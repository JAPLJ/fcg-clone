package fcg.game

import arena.ClientMonster
import fcg.rule.{MonsterCard, Rule}

/** モンスターを表す
  *
  * @param hp HP
  * @param attack 攻撃力
  * @param defense 防御力
  * @param regeneration リジェネ量 (負の場合は毒の量)
  * @param frozen 凍結ターン数 (0 の場合は凍結状態でない)
  * @param baseCard モンスターのベースになっているカード
  */
case class Monster(override val hp: Int,
                   override val attack: Int,
                   override val defense: Int,
                   regeneration: Int,
                   frozen: Int,
                   baseCard: MonsterCard)
    extends Entity {

  /** enemy による攻撃を受けたあとの状態を返す */
  def attacked(enemy: Entity): Monster =
    this.copy(hp = hp - (enemy.attack - defense).max(0))

  /** 除去効果を受けたあとの状態を返す */
  def removed: Monster =
    this.copy(hp = -Rule.MaxDamage)

  /** amount だけ体力を回復する (負ならダメージ) */
  def gainHP(amount: Int): Monster =
    this.copy(hp = (hp + amount).min(Rule.PlayerMaxHP))

  /** amount だけ攻撃力を得る (負なら失う) */
  def gainAttack(amount: Int): Monster =
    this.copy(attack = (attack + amount).max(0))

  /** amount だけ防御力を得る (負なら失う) */
  def gainDefense(amount: Int): Monster =
    this.copy(defense = (defense + amount).max(0))

  /** amount だけリジェネを得る (負なら毒を得る) */
  def gainRegeneration(amount: Int): Monster =
    this.copy(regeneration = regeneration + amount)

  /** turns ターンの凍結を受ける */
  def freeze(turns: Int): Monster =
    this.copy(frozen = frozen + turns)

  /** ユーザ側から見える情報だけを集めた [[ClientMonster]] を返す */
  def toClientMonster: ClientMonster =
    ClientMonster(hp, attack, defense, regeneration, frozen, baseCard.id)
}

object Monster {

  /** 指定のカードで表されるモンスターを召喚したばかりの状態を作って返す */
  def initialState(card: MonsterCard): Monster =
    Monster(card.hp, card.attack, card.defense, 0, 0, card)
}
