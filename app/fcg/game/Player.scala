package fcg.game

import arena.ClientPlayer
import fcg.rule.{Card, Color, Rule, SpellCard}
import play.api.libs.json._

/** プレイヤーを表す
  *
  * @param hp           HP
  * @param attack       攻撃力
  * @param defense      防御力
  * @param regeneration リジェネ量 (負の場合は毒の量)
  * @param energies     現在保持している色ごとのエネルギー
  * @param generators   現在保持している色ごとのジェネレーター
  * @param deck         現在残っている山札
  * @param hand         手札
  * @param spellsCasted このターン使用した呪文のリスト
  */
case class Player(override val hp: Int,
                  override val attack: Int,
                  override val defense: Int,
                  regeneration: Int,
                  name: String,
                  energies: Map[Color, Int],
                  generators: Map[Color, Int],
                  deck: IndexedSeq[Card],
                  hand: IndexedSeq[Card],
                  spellsCasted: IndexedSeq[SpellCard])
    extends Entity {

  /** enemy による攻撃を受けたあとの状態を返す */
  def attacked(enemy: Entity): Player =
    gainHP(-(enemy.attack - defense).max(0))

  /** color 色のエネルギーを amount だけ得たあとの状態を返す */
  def gainEnergy(color: Color, amount: Int): Player =
    this.copy(energies = energies.updatedWith(color)(ene =>
      ene.map(e => (e + amount).max(0).min(Rule.MaxEnergy))))

  /** color 色のジェネレーターを generator 個得たあとの状態を返す */
  def gainGenerator(color: Color, generator: Int): Player =
    this.copy(generators = generators.updatedWith(color)(gen =>
      gen.map(_ + generator)))

  /** amount だけ体力を回復する (負ならダメージ) */
  def gainHP(amount: Int): Player =
    this.copy(hp = (hp + amount).min(Rule.PlayerMaxHP))

  /** amount だけ攻撃力を得る (負なら失う) */
  def gainAttack(amount: Int): Player =
    this.copy(attack = (attack + amount).max(0))

  /** amount だけ防御力を得る (負なら失う) */
  def gainDefense(amount: Int): Player =
    this.copy(defense = (defense + amount).max(0))

  /** amount だけリジェネを得る (負なら毒を得る) */
  def gainRegeneration(amount: Int): Player =
    this.copy(regeneration = regeneration + amount)

  /** 最大で amount 枚のカードを山札からドローする */
  def drawCard(amount: Int): Player = {
    val n = amount.min(deck.length).min(Rule.MaxHandSize - hand.length)
    this.copy(deck = deck.drop(n), hand = hand ++ deck.take(n))
  }

  /** ユーザ側から見える情報だけを集めた [[ClientPlayer]] を返す */
  def toClientPlayer(monster: Option[Monster]): ClientPlayer =
    ClientPlayer(hp,
                 attack,
                 defense,
                 regeneration,
                 name,
                 energies,
                 generators,
                 monster.map(_.toClientMonster),
                 spellsCasted.lastOption.map(_.id),
                 deck.length)
}

object Player {

  /** 指定したデッキを持ってゲームを開始した段階のプレイヤーを返す */
  def initialState(name: String, deck: IndexedSeq[Card]): Player =
    Player(
      Rule.PlayerInitialHP,
      0,
      0,
      0,
      name,
      Color.Colors.map(color => (color, 1)).toMap,
      Color.Colors.map(color => (color, 1)).toMap,
      deck.drop(Rule.InitialHandSize),
      deck.take(Rule.InitialHandSize),
      Vector()
    )

  private def energyMapToJson(e: Map[Color, Int]): JsObject =
    JsObject(Color.Colors.map(color =>
      color.englishName -> JsNumber(e.getOrElse(color, 0): Int)))

}
