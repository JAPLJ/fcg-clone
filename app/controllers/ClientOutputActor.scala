package controllers

import akka.actor.{Actor, ActorRef, Props}
import arena.BattleState
import fcg.game.GameState.PlayerSide.{Player1, Player2}

class ClientOutputActor(out: ActorRef, userKey: String) extends Actor {

  override def receive: Receive = {
    case battleState: BattleState =>
      val sideOption = if (userKey == battleState.player1Key) {
        Some(Player1)
      } else if (userKey == battleState.player2Key) {
        Some(Player2)
      } else {
        None
      }
      sideOption.foreach(side => out ! battleState.toClientBattleState(side))
  }
}

object ClientOutputActor {
  def props(out: ActorRef, userKey: String): Props =
    Props(new ClientOutputActor(out, userKey))
}
