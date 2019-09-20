package controllers

import akka.actor.{Actor, ActorRef, Props}

class ClientOutputActor(out: ActorRef, userKey: String) extends Actor {

  override def receive: Receive = ???
}

object ClientOutputActor {
  def props(out: ActorRef, userKey: String): Props =
    Props(new ClientOutputActor(out, userKey))
}
