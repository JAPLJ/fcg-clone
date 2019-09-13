package fcg.rule

/** 全てのカードに共通する要素をもつ trait */
trait Card {

  /** カード ID */
  val id: CardId

  /** カード名 */
  val name: String

  /** カードの属性 */
  val color: Color

  /** [[color]] 色のエネルギーの消費量 */
  val energyCost: Int

  /** カードの持っている特殊効果のリスト */
  val effects: Seq[Effect]
}

/** モンスターカードを表す trait */
trait MonsterCard extends Card {
  // HP, 攻撃力, 防御力は召喚時 (呪文や特殊効果による変更を受ける前) のもの
  val hp: Int
  val attack: Int
  val defense: Int
}

/** 呪文カードを表す trait */
trait SpellCard extends Card
