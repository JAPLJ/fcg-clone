package arena

import fcg.game.GameState
import fcg.rule.MilliSec

/** ゲーム状態を表す [[GameState]] に加え、
  * ターン数・時間・プレイヤー識別用のキー情報も持ったステート */
case class BattleState(gameState: GameState,
                       currentTurn: Int,
                       nextTurnStartTime: MilliSec,
                       player1Key: String,
                       player1LastCast: MilliSec,
                       player2Key: String,
                       player2LastCast: MilliSec)
