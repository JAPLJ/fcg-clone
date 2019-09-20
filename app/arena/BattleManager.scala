package arena

import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Keep, Sink, Source}

/** バトル管理を行うクラス
  * 1 回の対戦につき (1 アリーナごとに) 1 個作られる */
class BattleManager(arena: Arena,
                    implicit val materializer: Materializer,
                    implicit val system: ActorSystem) {
  private var battleStateOption: Option[BattleState] = None
  def battleState: BattleState = battleStateOption.get

  // システムユーザーとしてシステムメッセージを送るためのキュー
  private val systemUserQueue =
    Source
      .queue[ClientInput](bufferSize = 8, OverflowStrategy.fail)
      .via(arena.bus)
      .toMat(Sink.ignore)(Keep.left)
      .run()

  def join(userKey: String, userName: String): Unit = ???
  def useCard(userKey: String, cardIndex: Int): Unit = ???
  def destroyMonster(userKey: String): Unit = ???
  def turnEnd(userKey: String): Unit = ???
}
