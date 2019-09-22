package controllers

import akka.actor.{Actor, ActorRef, Props}
import arena.ClientInput.Join
import fcg.rule.CardId

class ClientInputActor(out: ActorRef,
                       userKey: String,
                       userName: String,
                       deck: IndexedSeq[CardId])
    extends Actor {

  override def receive: Receive = ???

  override def preStart(): Unit = out ! Join(userKey, userName, deck)
}

object ClientInputActor {
  def props(out: ActorRef,
            userKey: String,
            userName: String,
            deck: IndexedSeq[CardId]): Props =
    Props(new ClientInputActor(out, userKey, userName, deck))
}
