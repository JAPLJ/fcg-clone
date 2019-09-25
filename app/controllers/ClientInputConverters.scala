package controllers

import arena.ClientInput.{DestroyMonster, UseCard}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object ClientInputConverters {

  implicit val ReadsUseCard: Reads[UseCard] =
    (__ \ "type").read[String](verifying[String](_ == "use_card")) ~>
      ((__ \ "user_key").read[String] ~
        (__ \ "card_index").read[Int])(UseCard.apply _)

  implicit val ReadsDestroyMonster: Reads[DestroyMonster] =
    (__ \ "type")
      .read[String](verifying[String](_ == "destroy_monster")) ~>
      (__ \ "user_key").read[String].map(DestroyMonster.apply)
}
