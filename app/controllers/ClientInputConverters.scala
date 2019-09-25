package controllers

import arena.ClientInput.{DestroyMonster, UseCard}
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object ClientInputConverters {

  implicit val ReadsUseCard: Reads[UseCard] =
    (JsPath \ 'type).read[String](verifying[String](_ == "use_card")) andKeep
      ((JsPath \ 'user_key).read[String] and
        (JsPath \ 'card_index).read[Int])(UseCard.apply _)

  implicit val ReadsDestroyMonster: Reads[DestroyMonster] =
    (JsPath \ 'type)
      .read[String](verifying[String](_ == "destroy_monster")) andKeep
      (JsPath \ 'user_key).read[String].map(DestroyMonster.apply)
}
