package controllers

import arena.{ClientBattleState, ClientGameState, ClientMonster, ClientPlayer}
import fcg.rule.{CardId, Color, MilliSec}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json.JsValueWrapper

object ClientOutputConverters {

  implicit val WritesClientMonster: Writes[ClientMonster] = (
    (__ \ "hp").write[Int] ~
      (__ \ "attack").write[Int] ~
      (__ \ "defense").write[Int] ~
      (__ \ "regeneration").write[Int] ~
      (__ \ "frozen").write[Int] ~
      (__ \ "base_card_id").write[Int]
  )(unlift(ClientMonster.unapply))

  implicit val WritesEnergyMap: Writes[Map[Color, Int]] =
    (o: Map[Color, Int]) =>
      Json.obj(Color.Colors.map { color =>
        color.englishName -> JsNumber(o.getOrElse(color, 0): Int): (String,
                                                                    JsValueWrapper)
      }.toSeq: _*)

  implicit val WritesClientPlayer: Writes[ClientPlayer] = (
    (__ \ "hp").write[Int] ~
      (__ \ "attack").write[Int] ~
      (__ \ "defense").write[Int] ~
      (__ \ "regeneration").write[Int] ~
      (__ \ "name").write[String] ~
      (__ \ "energies").write[Map[Color, Int]] ~
      (__ \ "generators").write[Map[Color, Int]] ~
      (__ \ "monster").writeOptionWithNull[ClientMonster] ~
      (__ \ "last_spell").writeOptionWithNull[CardId] ~
      (__ \ "deck_remain").write[Int]
  )(unlift(ClientPlayer.unapply))

  implicit val WritesClientGameState: Writes[ClientGameState] = (
    (__ \ "player").write[ClientPlayer] ~
      (__ \ "opponent").write[ClientPlayer] ~
      (__ \ "player_hand").write[Seq[CardId]]
  )(unlift(ClientGameState.unapply))

  implicit val WritesClientBattleState: Writes[ClientBattleState] = (
    (__ \ "game_state").write[ClientGameState] ~
      (__ \ "current_turn").write[Int] ~
      (__ \ "next_turn_start_time").write[MilliSec] ~
      (__ \ "player_key").write[String]
  )(unlift(ClientBattleState.unapply))
}
