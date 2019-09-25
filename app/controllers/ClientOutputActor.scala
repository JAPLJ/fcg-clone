package controllers

import akka.actor.{Actor, ActorRef, Props}
import arena.BattleState

class ClientOutputActor(out: ActorRef, userKey: String) extends Actor {

  override def receive: Receive = {
    case battleState: BattleState =>

  }
}

object ClientOutputActor {
  def props(out: ActorRef, userKey: String): Props =
    Props(new ClientOutputActor(out, userKey))
}
