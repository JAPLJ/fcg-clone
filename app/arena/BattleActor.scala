package arena

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import arena.ClientInput.{DestroyMonster, GameStart, Join, TurnEnd, UseCard}

/** ユーザの入力を指定のアリーナ ID に対応する [[BattleManager]] に伝達し、結果を返す Actor */
class BattleActor(out: ActorRef, arenaId: String) extends Actor {

  def manager: BattleManager = ArenaService.battleManager(arenaId)

  private def emitBattleState(): Unit = {
    manager.battleState.foreach(state => out ! state)
    if (manager.battleState.exists(_.isDone)) {
      self ! PoisonPill
      ArenaService.destroyArena(arenaId)
    }
  }

  override def receive: Receive = {
    case Join(userKey, userName, deck) =>
      manager.join(userKey, userName, deck)
      emitBattleState()
    case UseCard(userKey, cardIndex) =>
      manager.useCard(userKey, cardIndex)
      emitBattleState()
    case DestroyMonster(userKey) =>
      manager.destroyMonster(userKey)
      emitBattleState()
    case GameStart(userKey) =>
      manager.gameStart(userKey)
      emitBattleState()
    case TurnEnd(userKey) =>
      manager.turnEnd(userKey)
      emitBattleState()
  }
}

object BattleActor {
  def props(out: ActorRef, arenaId: String): Props =
    Props(new BattleActor(out, arenaId))
}
