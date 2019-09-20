package arena

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.{KillSwitches, Materializer, UniqueKillSwitch}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink}
import javax.inject.{Inject, Singleton}
import play.api.libs.streams.ActorFlow

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

@Singleton
class ArenaService @Inject()(
    implicit val materializer: Materializer,
    implicit val system: ActorSystem
) {

  import ArenaService._

  private var waitingArenaId: Option[String] = None

  /** 接続してきたユーザが参加するアリーナと、ユーザの key を (必要なら生成して) 返す */
  def start(): (String, Arena) = synchronized {
    val arenaOption = for {
      waiting <- waitingArenaId
      arena <- arenaMap.get(waiting)
    } yield { arena }

    val userKey = UUID.randomUUID().toString

    arenaOption match {
      case Some(arena) =>
        waitingArenaId = None
        (userKey, arena)
      case None =>
        val arena = createArena()
        arenaMap.update(arena.arenaId, arena)
        battleManagerMap.update(arena.arenaId,
                                new BattleManager(arena, materializer, system))
        waitingArenaId = Some(arena.arenaId)
        (userKey, arena)
    }
  }

  private def createArena(): Arena = {
    val arenaId = UUID.randomUUID().toString

    // ClientInput をクライアントから受け付ける MergeHub
    // -> ClientInput を BattleManager に伝えて BattleState を返す ActorFlow
    // -> BattleState をクライアントに送る BroadcastHub
    val mergeSource = MergeHub.source[ClientInput](perProducerBufferSize = 16)
    val battleActor = ActorFlow.actorRef[ClientInput, BattleState](out =>
      BattleActor.props(out, arenaId))
    val broadcastSink = BroadcastHub.sink[BattleState](bufferSize = 256)

    val (sink, source) =
      mergeSource.via(battleActor).toMat(broadcastSink)(Keep.both).run()

    source.runWith(Sink.ignore)

    // ClientInput -> BattleState の Flow を作る
    // これにクライアントからの入力と出力を接続していく
    val bus: Flow[ClientInput, BattleState, UniqueKillSwitch] = Flow
      .fromSinkAndSource(sink, source)
      .joinMat(KillSwitches.singleBidi[BattleState, ClientInput])(Keep.right)
      .backpressureTimeout(3.seconds)

    Arena(arenaId, bus)
  }
}

object ArenaService {
  private val arenaMap: TrieMap[String, Arena] = TrieMap()
  private val battleManagerMap: TrieMap[String, BattleManager] = TrieMap()

  private[arena] def battleManager(arenaId: String): BattleManager =
    battleManagerMap(arenaId)
}
