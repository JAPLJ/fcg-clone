package controllers

import akka.actor.{Actor, ActorRef, Props}
import arena.ClientInput.{DestroyMonster, Join, UseCard}
import fcg.rule.CardId
import play.api.libs.json.JsValue

class ClientInputActor(out: ActorRef,
                       userKey: String,
                       userName: String,
                       deck: IndexedSeq[CardId])
    extends Actor {

  import ClientInputConverters._

  override def receive: Receive = {
    case input: JsValue if input.asOpt[UseCard].isDefined =>
      out ! input.as[UseCard]
    case input: JsValue if input.asOpt[DestroyMonster].isDefined =>
      out ! input.as[DestroyMonster]
  }

  override def preStart(): Unit = out ! Join(userKey, userName, deck)
}

object ClientInputActor {
  def props(out: ActorRef,
            userKey: String,
            userName: String,
            deck: IndexedSeq[CardId]): Props =
    Props(new ClientInputActor(out, userKey, userName, deck))
}
