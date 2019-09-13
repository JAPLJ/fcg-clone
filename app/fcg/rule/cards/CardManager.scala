package fcg.rule.cards

import fcg.rule.{Card, CardId, MonsterCard, SpellCard}

import scala.collection.mutable.ArrayBuffer

/** カード一覧を管理するオブジェクト */
object CardManager {
  private var cardByIdMap: Map[CardId, Card] = Map()
  private val monsterCardsSet: ArrayBuffer[MonsterCard] = ArrayBuffer()
  private val spellCardsSet: ArrayBuffer[SpellCard] = ArrayBuffer()

  private[cards] def addCard(card: Card): Unit = {
    require(!cardByIdMap.contains(card.id))
    cardByIdMap += card.id -> card
    card match {
      case monsterCard: MonsterCard => monsterCardsSet += monsterCard
      case spellCard: SpellCard     => spellCardsSet += spellCard
    }
  }

  /** 指定した ID を持つカードを返す */
  def cardById(id: CardId): Option[Card] = cardByIdMap.get(id)

  /** モンスターカードのリスト */
  def monsterCards: Seq[MonsterCard] = monsterCardsSet.toSeq

  /** 呪文カードのリスト */
  def spellCards: Seq[SpellCard] = spellCardsSet.toSeq
}
