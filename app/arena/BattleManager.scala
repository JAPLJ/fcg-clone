package arena

import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Keep, Sink, Source}
import arena.ClientInput.Join
import fcg.game.GameState
import fcg.rule.{CardId, Rule}
import fcg.rule.cards.CardManager

/** バトル管理を行うクラス
  * 1 回の対戦につき (1 アリーナごとに) 1 個作られる */
class BattleManager(arena: Arena,
                    implicit val materializer: Materializer,
                    implicit val system: ActorSystem) {
  private var battleStateOption: Option[BattleState] = None
  def battleState: Option[BattleState] = battleStateOption

  // システムユーザーとしてシステムメッセージを送るためのキュー
  private val systemUserQueue =
    Source
      .queue[ClientInput](bufferSize = 8, OverflowStrategy.fail)
      .via(arena.bus)
      .toMat(Sink.ignore)(Keep.left)
      .run()

  // 対戦相手の接続待ちプレイヤー情報
  var waitingPlayer: Option[Join] = None

  def join(userKey: String,
           userName: String,
           deck: IndexedSeq[CardId]): Unit = {
    require(battleStateOption.isEmpty)
    waitingPlayer match {
      case Some(player1) =>
        val currentTime = System.currentTimeMillis()
        battleStateOption = Some(
          BattleState(
            GameState.gameStart(player1.userName,
                                player1.deck.flatMap(id =>
                                  CardManager.cardById(id)),
                                userName,
                                deck.flatMap(id => CardManager.cardById(id))),
            0,
            currentTime + Rule.BattleStartWait,
            player1.userKey,
            currentTime - Rule.CardUseWait,
            userKey,
            currentTime - Rule.CardUseWait
          ))
      case None =>
        waitingPlayer = Some(Join(userKey, userName, deck))
    }
  }

  def useCard(userKey: String, cardIndex: Int): Unit = ???
  def destroyMonster(userKey: String): Unit = ???
  def turnEnd(userKey: String): Unit = ???
}
