package fcg.game

/** HP/ATK/DEF の情報を持つ戦闘可能なエンティティ (プレイヤーとモンスターのベースクラス) */
trait Entity {
  val hp: Int
  val attack: Int
  val defense: Int
}
