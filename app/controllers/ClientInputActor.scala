package controllers

import akka.actor.{Actor, ActorRef, Props}
import arena.ClientInput.Join

class ClientInputActor(out: ActorRef, userKey: String, userName: String)
    extends Actor {

  override def receive: Receive = ???

  override def preStart(): Unit = out ! Join(userKey, userName)
}

object ClientInputActor {
  def props(out: ActorRef, userKey: String, userName: String): Props =
    Props(new ClientInputActor(out, userKey, userName))
}
