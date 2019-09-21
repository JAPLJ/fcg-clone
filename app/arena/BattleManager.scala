package arena

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Keep, Sink, Source}
import arena.ClientInput.{GameStart, Join, TurnEnd}
import fcg.game.GameState
import fcg.game.GameState.PlayerSide
import fcg.game.GameState.PlayerSide.{Player1, Player2}
import fcg.rule.{CardId, MilliSec, Rule}
import fcg.rule.cards.CardManager

import scala.concurrent.ExecutionContext

/** バトル管理を行うクラス
  * 1 回の対戦につき (1 アリーナごとに) 1 個作られる */
class BattleManager(arena: Arena,
                    implicit val materializer: Materializer,
                    implicit val system: ActorSystem) {
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private var battleStateOption: Option[BattleState] = None
  def battleState: Option[BattleState] = battleStateOption
  private def updateBattleState(f: BattleState => BattleState): Unit =
    battleState match {
      case Some(state) => battleStateOption = Some(f(state))
    }

  // システムユーザーとしてシステムメッセージを送るためのキュー
  private val systemUserQueue =
    Source
      .queue[ClientInput](bufferSize = 8, OverflowStrategy.fail)
      .via(arena.bus)
      .toMat(Sink.ignore)(Keep.left)
      .run()

  // システムユーザー識別用の key
  private val systemUserKey = UUID.randomUUID().toString

  // 対戦相手の接続待ちプレイヤー情報
  private var waitingPlayer: Option[Join] = None

  // ターン内で使われたカードの枚数
  private val cardUsedInThisTurn = new AtomicLong(0)

  // ターン終了条件 (ターンのデフォルト長さ + 使用されたカード枚数に応じた延長分) をチェックして、
  // 満たされていればターン終了のシステムメッセージを送る
  private def checkTurnEnd(): Unit = {
    if (cardUsedInThisTurn.getAndDecrement() == 0) {
      cardUsedInThisTurn.set(0L)
      systemUserQueue.offer(TurnEnd(systemUserKey))
    } else {
      system.scheduler.scheduleOnce(Rule.CardUseWait, () => checkTurnEnd())
    }
  }

  private def checkCardUseWait(side: PlayerSide,
                               currentTime: MilliSec): Boolean =
    (for { state <- battleState } yield {
      side match {
        case Player1 =>
          currentTime >= state.player1LastCast + Rule.CardUseWait.toMillis
        case Player2 =>
          currentTime >= state.player2LastCast + Rule.CardUseWait.toMillis
      }
    }).getOrElse(false)

  /** バトルにプレイヤーを参加させる (2人揃ったら [[BattleState]] を初期化する) */
  def join(userKey: String, userName: String, deck: IndexedSeq[CardId]): Unit =
    synchronized {
      require(battleStateOption.isEmpty)
      waitingPlayer match {
        case Some(player1) =>
          val currentTime = System.currentTimeMillis()
          battleStateOption = Some(
            BattleState(
              GameState.gameStart(player1.userName,
                                  player1.deck.flatMap(id =>
                                    CardManager.cardById(id)),
                                  userName,
                                  deck.flatMap(id => CardManager.cardById(id))),
              0,
              currentTime + Rule.BattleStartWait.toMillis,
              player1.userKey,
              currentTime - Rule.CardUseWait.toMillis,
              userKey,
              currentTime - Rule.CardUseWait.toMillis,
            ))
          system.scheduler
            .scheduleOnce(
              Rule.BattleStartWait,
              () => systemUserQueue.offer(GameStart(systemUserKey)): Unit)
        case None =>
          waitingPlayer = Some(Join(userKey, userName, deck))
      }
    }

  /** 2人揃って所定の時間が経ったときにゲームを開始させる */
  def gameStart(userKey: String): Unit = synchronized {
    if (userKey == systemUserKey && battleState.nonEmpty) {
      updateBattleState(st => {
        require(st.currentTurn == 0)
        st.copy(currentTurn = 1,
                nextTurnStartTime = System
                  .currentTimeMillis() + Rule.InitialTurnDuration.toMillis)
      })
      system.scheduler.scheduleOnce(Rule.InitialTurnDuration,
                                    () => checkTurnEnd())
    }
  }

  private def validateUserKey(
      userKey: String): Option[(BattleState, PlayerSide)] =
    for {
      state <- battleState
      playerSide <- userKey match {
        case state.player1Key => Some(Player1)
        case state.player2Key => Some(Player2)
        case _                => None
      }
    } yield { (state, playerSide) }

  /** カードを使用する */
  def useCard(userKey: String, cardIndex: Int): Unit = synchronized {
    val currentTime = System.currentTimeMillis()
    for {
      (state, playerSide) <- validateUserKey(userKey)
      if checkCardUseWait(playerSide, currentTime)
      if state.gameState.canCast(playerSide, cardIndex)
    } {
      val (lastUsed1, lastUsed2): (MilliSec, MilliSec) = playerSide match {
        case Player1 => (currentTime, lastUsed2)
        case Player2 => (lastUsed1, currentTime)
      }
      updateBattleState(
        st =>
          st.copy(gameState = st.gameState.castCard(playerSide, cardIndex),
                  player1LastCast = lastUsed1,
                  player2LastCast = lastUsed2))
    }
  }

  /** 自分のモンスターを破壊する */
  def destroyMonster(userKey: String): Unit = synchronized {
    for {
      (_, playerSide) <- validateUserKey(userKey)
    } {
      updateBattleState(
        st => st.copy(gameState = st.gameState.destroyMonster(playerSide)))
    }
  }

  /** ターン終了時の処理を行う */
  def turnEnd(userKey: String): Unit = synchronized {
    if (userKey == systemUserKey && battleState.nonEmpty) {
      val currentTime = System.currentTimeMillis()
      updateBattleState(st =>
        st.copy(
          gameState = st.gameState.turnEnd(),
          currentTurn = st.currentTurn + 1,
          nextTurnStartTime = currentTime + Rule.InitialTurnDuration.toMillis))
      system.scheduler.scheduleOnce(Rule.InitialTurnDuration,
                                    () => checkTurnEnd())
    }
  }
}
