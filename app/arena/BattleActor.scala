package arena

import akka.actor.{Actor, ActorRef, Props}
import arena.ClientInput.{DestroyMonster, Join, TurnEnd, UseCard}

/** ユーザの入力を指定のアリーナ ID に対応する [[BattleManager]] に伝達し、結果を返す Actor */
class BattleActor(out: ActorRef, arenaId: String) extends Actor {

  def manager: BattleManager = ArenaService.battleManager(arenaId)

  override def receive: Receive = {
    case Join(userKey, userName) =>
      manager.join(userKey, userName)
      out ! manager.battleState
    case UseCard(userKey, cardIndex) =>
      manager.useCard(userKey, cardIndex)
      out ! manager.battleState
    case DestroyMonster(userKey) =>
      manager.destroyMonster(userKey)
      out ! manager.battleState
    case TurnEnd(userKey) =>
      manager.turnEnd(userKey)
      out ! manager.battleState
  }
}

object BattleActor {
  def props(out: ActorRef, arenaId: String): Props =
    Props(new BattleActor(out, arenaId))
}
