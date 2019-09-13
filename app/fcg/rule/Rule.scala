package fcg.rule

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
}
