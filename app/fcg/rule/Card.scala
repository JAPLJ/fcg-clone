package fcg.rule

/** 全てのカードに共通する要素をもつ trait */
trait Card extends Ordered[Card] {

  /** カード ID */
  val id: CardId

  /** カード名 */
  val name: String

  /** このカードをデッキに最大で何枚入れられるか */
  val maxSameCards: Int = Rule.DefaultMaxSameCards

  /** カードの属性 */
  val color: Color

  /** [[color]] 色のエネルギーの消費量 */
  val energyCost: Int

  /** カードの持っている特殊効果のリスト */
  val effects: Seq[Effect]

  override def compare(that: Card): CardId = this.id - that.id
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
