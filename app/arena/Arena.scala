package arena

import akka.stream.UniqueKillSwitch
import akka.stream.scaladsl.Flow
import fcg.rule.{CardId, Color}

/** ユーザからの入力 */
sealed trait ClientInput

object ClientInput {

  /** アリーナ参加時 */
  case class Join(userKey: String, userName: String) extends ClientInput

  /** カード使用時 */
  case class UseCard(userKey: String, cardIndex: Int) extends ClientInput

  /** 自モンスター破壊時 */
  case class DestroyMonster(userKey: String) extends ClientInput

  /** ターン終了時 (システムユーザーのみ) */
  case class TurnEnd(userKey: String) extends ClientInput
}

/** ユーザ側から見えるモンスターの状態 */
case class ClientMonster(hp: Int,
                         attack: Int,
                         defense: Int,
                         regeneration: Int,
                         frozen: Int,
                         baseCardId: CardId)

/** ユーザ側から見えるユーザの状態 */
case class ClientPlayer(hp: Int,
                        attack: Int,
                        defense: Int,
                        regeneration: Int,
                        name: String,
                        energies: Map[Color, Int],
                        generators: Map[Color, Int],
                        monster: Option[ClientMonster],
                        lastSpell: Option[CardId],
                        deckRemain: Int)

/** ユーザから見えるゲームの状態 ([[ClientPlayer]] に加え、自プレイヤーは手札の情報も見える) */
case class ClientGameState(player: ClientPlayer,
                           opponent: ClientPlayer,
                           playerHand: Seq[CardId])

/** バトルが行われるアリーナ */
case class Arena(arenaId: String,
                 bus: Flow[ClientInput, BattleState, UniqueKillSwitch])
