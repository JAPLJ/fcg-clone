package arena

import fcg.game.GameState
import fcg.game.GameState.PlayerSide
import fcg.game.GameState.PlayerSide.{Player1, Player2}
import fcg.rule.{CardId, Color, MilliSec}

/** ゲーム状態を表す [[GameState]] に加え、
  * ターン数・時間・プレイヤー識別用のキー情報も持ったステート */
case class BattleState(gameState: GameState,
                       currentTurn: Int,
                       nextTurnStartTime: MilliSec,
                       player1Key: String,
                       player1LastCast: MilliSec,
                       player2Key: String,
                       player2LastCast: MilliSec) {

  /** side 側のユーザに送る [[ClientBattleState]] に変換する */
  def toClientBattleState(side: PlayerSide): ClientBattleState =
    ClientBattleState(gameState.toClientGameState(side),
                      currentTurn,
                      nextTurnStartTime,
                      side match {
                        case Player1 => player1Key
                        case Player2 => player2Key
                      })
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

/** ユーザから見えるゲームの状態 ([[ClientPlayer]] に加え、自プレイヤーの手札とキー情報も見える) */
case class ClientGameState(player: ClientPlayer,
                           opponent: ClientPlayer,
                           playerHand: Seq[CardId])

/** ユーザから見えるバトルの全状態 (これが実際にユーザに送られる) */
case class ClientBattleState(gameState: ClientGameState,
                             currentTurn: Int,
                             nextTurnStartTime: MilliSec,
                             playerKey: String)
