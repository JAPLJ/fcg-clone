package fcg.rule

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.{Duration, FiniteDuration}

object Rule {

  /** ゲーム開始時のプレイヤーの体力 */
  val PlayerInitialHP: Int = 100

  /** プレイヤーの最大体力 (これより大きな値にはならない) */
  val PlayerMaxHP: Int = 200

  /** ゲーム開始時の手札の枚数 */
  val InitialHandSize: Int = 3

  /** 手札の最大枚数 */
  val MaxHandSize: Int = 6

  /** 全てのモンスターが確実に死ぬ最大のダメージ (除去はこの分のダメージとして扱う) */
  val MaxDamage: Int = 10000

  /** デッキに入れられる同名カードの最大枚数 (デフォルト値) */
  val DefaultMaxSameCards: Int = 7

  /** エネルギーの最大保持数 */
  val MaxEnergy: Int = 99

  /** 初ターン開始までの待ち時間 */
  val BattleStartWait: FiniteDuration = Duration(3, TimeUnit.SECONDS)

  /** ターンの長さの初期値 */
  val InitialTurnDuration: FiniteDuration = Duration(5, TimeUnit.SECONDS)

  /** 連続カード使用に必要な待ち時間 */
  val CardUseWait: FiniteDuration = Duration(500, TimeUnit.MILLISECONDS)

  /** デッキの最小カード枚数 */
  val MinDeckSize: Int = 25

  /** デッキの最大カード枚数 */
  val MaxDeckSize: Int = 100
}
