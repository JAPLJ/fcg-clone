package arena

import akka.stream.UniqueKillSwitch
import akka.stream.scaladsl.Flow
import fcg.rule.CardId

/** ユーザからの入力 */
sealed trait ClientInput

object ClientInput {

  /** アリーナ参加時 */
  case class Join(userKey: String, userName: String, deck: IndexedSeq[CardId])
      extends ClientInput

  /** カード使用時 */
  case class UseCard(userKey: String, cardIndex: Int) extends ClientInput

  /** 自モンスター破壊時 */
  case class DestroyMonster(userKey: String) extends ClientInput

  /** ゲーム開始時 (システムユーザーのみ) */
  case class GameStart(userKey: String) extends ClientInput

  /** ターン終了時 (システムユーザーのみ) */
  case class TurnEnd(userKey: String) extends ClientInput
}

/** バトルが行われるアリーナ */
case class Arena(arenaId: String,
                 bus: Flow[ClientInput, BattleState, UniqueKillSwitch])
