package fcg.rule.cards

import fcg.rule.{CardId, Color, Effect, MonsterCard}
import fcg.rule.Color._
import fcg.rule.Effect._

object MonsterCards {
  // White Monsters
  defineCard(1, White, "無し", 1, 3, 5, 1)
  defineCard(2, White, "見え猿", 6, 6, 9, 2)
  defineCard(3, White, "白龍", 13, 14, 9, 3)
  defineCard(4,
             White,
             "Awesome Face",
             1,
             16,
             0,
             1,
             ConsumeEnergyEveryTurn(White, 4),
             Poisoned(1))

  // Red Monsters
  defineCard(5, Red, "りんご", 1, 3, 0, 1, DealPoisonToPlayer(1))
  defineCard(6, Red, "毒コウモリ", 4, 7, 0, 2, DealPoisonToPlayer(2))
  defineCard(7, Red, "火蜥蜴", 6, 10, 0, 3, GainAttack(3))
  defineCard(8, Red, "牛頭馬頭", 10, 0, 0, 1, DealPoisonToPlayerEveryTurn(2))
  defineCard(9, Red, "赤龍", 13, 18, 0, 2)

  // Blue Monsters
  defineCard(10, Blue, "ぶどう", 1, 4, 2, 5)
  defineCard(11, Blue, "サワガニ", 3, 5, 2, 5, Freeze(1))
  defineCard(12, Blue, "守りヤドカリ", 6, 2, 11, 2, Freeze(1), GainDefense(2))
  defineCard(13, Blue, "青龍", 13, 14, 2, 9)

  // Green Monsters
  defineCard(14, Green, "きうい", 1, 3, 0, 10)
  defineCard(15, Green, "キノコ法師", 5, 4, 0, 8, BlessPlayer(1), GainHP(6))
  defineCard(16, Green, "狩人", 9, 7, 3, 6, GainAttack(3), GainHP(12))
  defineCard(17, Green, "緑龍", 13, 12, 1, 16)
  defineCard(
    18,
    Green,
    "貪欲大樹",
    20,
    15,
    8,
    20,
    GainHP(20),
    BlessPlayer(2),
    Draw(2),
    ConsumeEnergy(White, 10),
    ConsumeEnergy(Red, 10),
    ConsumeEnergy(Blue, 10),
    ConsumeEnergy(Yellow, 10)
  )

  // Yellow Monsters
  defineCard(19, Yellow, "れもん", 1, 3, 3, 3)
  defineCard(20, Yellow, "金インゴット", 4, 5, 5, 5, Draw(1))
  defineCard(21, Yellow, "ロボット", 8, 7, 7, 7, Draw(1))
  defineCard(22, Yellow, "黄龍", 13, 10, 10, 10)

  private def defineCard(cardId: CardId,
                         col: Color,
                         monsterName: String,
                         cost: Int,
                         at: Int,
                         df: Int,
                         initialHP: Int,
                         effectList: Effect*): Unit = {
    CardManager.addCard(new MonsterCard {
      val id: CardId = cardId
      val color: Color = col
      val name: String = monsterName
      val energyCost: Int = cost
      val attack: Int = at
      val defense: Int = df
      val hp: Int = initialHP
      val effects: Seq[Effect] = effectList.toSeq
    })
  }
}
