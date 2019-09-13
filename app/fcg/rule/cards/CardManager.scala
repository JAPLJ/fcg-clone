package fcg.rule.cards

import fcg.rule.{Card, CardId}

/** カード一覧を管理するオブジェクト */
object CardManager {
  private var cardByIdMap: Map[CardId, Card] = Map()

  private[cards] def addCard(card: Card): Unit = {
    require(!cardByIdMap.contains(card.id))
    cardByIdMap += card.id -> card
  }

  /** 指定した ID を持つカードを返す */
  def cardById(id: CardId): Option[Card] = cardByIdMap.get(id)
}
