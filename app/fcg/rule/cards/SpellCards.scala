package fcg.rule.cards

import fcg.rule.{CardId, Color, Effect, Rule, SpellCard}
import fcg.rule.Color._
import fcg.rule.Effect._

object SpellCards {
  // ジェネレーター
  defineCard(101, White, "ジェネレーター白", 0, GainGenerator(White, 1))
  defineCard(102, Red, "ジェネレーター赤", 0, GainGenerator(Red, 1))
  defineCard(103, Blue, "ジェネレーター青", 0, GainGenerator(Blue, 1))
  defineCard(104, Green, "ジェネレーター緑", 0, GainGenerator(Green, 1))
  defineCard(105, Yellow, "ジェネレーター黄", 0, GainGenerator(Yellow, 1))
  defineCard(106,
             White,
             "ジェネレーター虹",
             7,
             GainGenerator(White, 1),
             GainGenerator(Red, 1),
             GainGenerator(Blue, 1),
             GainGenerator(Green, 1),
             GainGenerator(Yellow, 1))

  // チャージ
  defineCard(107, White, "チャージ白", 0, GainEnergy(White, 5))
  defineCard(108, Red, "チャージ赤", 0, GainEnergy(Red, 5))
  defineCard(109, Blue, "チャージ青", 0, GainEnergy(Blue, 5))
  defineCard(110, Green, "チャージ緑", 0, GainEnergy(Green, 5))
  defineCard(111, Yellow, "チャージ黄", 0, GainEnergy(Yellow, 5))
  defineCard(112,
             White,
             "チャージ虹",
             0,
             GainEnergy(White, 1),
             GainEnergy(Red, 1),
             GainEnergy(Blue, 1),
             GainEnergy(Green, 1),
             GainEnergy(Yellow, 1))

  // White Spells
  defineCard(113, White, "電磁鍛造カタナ", 6, GainAttack(5), AdditionalDamage(5))
  defineCard(114, White, "カタナブレードツルギ", 8, GainAttack(6), AdditionalDamage(8))
  defineCard(115, White, "妖刀", 11, GainAttack(11), GetPoison(3))
  defineCard(116, White, "努力値 A", 6, GainMonsterAttack(8))
  defineCard(117, White, "努力値 B", 6, GainMonsterDefense(10))

  // Red Spells
  defineCard(118, Red, "だいばくはつ", 9, KillMonster(), DealDamage(10))
  defineCard(119, Red, "サクリファイス", 8, PermanentMonster())
  defineCard(120,
             Red,
             "審判",
             3,
             DealDamage(10),
             DealSelfDamage(10),
             DealMonsterDamage(10),
             DealSelfMonsterDamage(10))
  defineCard(121, Red, "天誅", 25, AdditionalDamage(35))
  defineCard(122, Red, "解凍", 0, AntiFreeze())

  // Blue Spells
  defineCard(123, Blue, "洪水", 8, KillMonster(), GainHP(10))
  defineCard(124, Blue, "凍結", 3, Freeze(2))
  defineCard(125, Blue, "水の守り", 5, GainDefense(4))
  defineCard(126,
             Blue,
             "エクサルマティオー",
             5,
             GainAttack(-20),
             GainOpponentAttack(-20),
             GainDefense(-15),
             GainOpponentDefense(-15))
  defineCard(127, Blue, "聖水", 15, GainHP(20), GainAttack(3), GainDefense(3))

  // Green Spells
  defineCard(128, Green, "竜巻", 7, AbsorbHP(), KillMonster())
  defineCard(129, Green, "毒矢", 6, DealPoisonToPlayer(3))
  defineCard(130, Green, "ヒール", 6, GainHP(20))
  defineCard(131, Green, "リジェネレーション", 8, BlessPlayer(3))
  defineCard(132, Green, "癒やしの手", 1, Cure(10))
  defineCard(133, Green, "劇薬", 2, GainHP(30), GetPoison(10))

  // Yellow Spells
  defineCard(134, Yellow, "処刑", 7, KillMonster(), Draw(1))
  defineCard(135, Yellow, "タイムワープ", 4, Draw(2))
  defineCard(136, Yellow, "大図書館", 9, Draw(Rule.MaxHandSize))
  defineCard(137, Yellow, "インファイト", 3, GainDefense(-30), GainOpponentDefense(-30))
  defineCard(138, Yellow, "準備万端", 10, GainAttack(4), GainDefense(2), Draw(1))

  private def defineCard(cardId: CardId,
                         col: Color,
                         spellName: String,
                         cost: Int,
                         effectList: Effect*): Unit = {
    CardManager.addCard(new SpellCard {
      val id: CardId = cardId
      val color: Color = col
      val name: String = spellName
      val energyCost: Int = cost
      val effects: Seq[Effect] = effectList.toSeq
    })
  }
}
