package fcg.game

import fcg.game.GameState.GameStatus.{Draw, InGame, Player1Wins, Player2Wins}
import fcg.game.GameState.PlayerSide.{Player1, Player2}
import fcg.game.GameState.{GameStatus, PlayerSide}
import fcg.rule.{Card, Color, MonsterCard, Rule, SpellCard}

/** ゲームの状態を表す */
case class GameState private (player1: Player,
                              player2: Player,
                              monster1: Option[Monster],
                              monster2: Option[Monster]) {

  /** プレイヤーのサイド (1P/2P) を入れ替えた状態を作る (ユーティリティ) */
  def swapSide: GameState = GameState(player2, player1, monster2, monster1)

  /** side で指定された側のプレイヤーを 1P にしたうえで、
    * 操作 f を適用し、再び 1P を side 側に戻す (ユーティリティ) */
  def makeSwap(side: PlayerSide)(f: GameState => GameState): GameState =
    side match {
      case Player1 => f(this)
      case Player2 => f(swapSide).swapSide
    }

  /** side 側のプレイヤーが手札の cardIndex 番目のカードを使用できるかどうかを判定する */
  def canCast(side: PlayerSide, cardIndex: Int): Boolean = {
    val (player, monster) = side match {
      case Player1 => (player1, monster1)
      case Player2 => (player2, monster2)
    }

    val card = player.hand(cardIndex)
    if (player.energies(card.color) < card.energyCost) {
      // エネルギーが足りていない場合は使用不可
      false
    } else if (card.isInstanceOf[MonsterCard] && monster.nonEmpty) {
      // モンスターを既に場に出している場合にモンスターカードを使用するのも不可
      false
    } else {
      true
    }
  }

  /** side 側のプレイヤーが手札の cardIndex 番目のカードを使用したあとのステートを返す
    * @note 必ず当該カードが使用可能であることを precondition として要求する */
  def castCard(side: PlayerSide, cardIndex: Int): GameState = makeSwap(side) {
    state =>
      require(canCast(side, cardIndex))
      val player = state.player1
      val card = player.hand(cardIndex)

      // エネルギーを消費し、手札から当該カードを除去する (Player のアップデート)
      val nextPlayer =
        player
          .gainEnergy(card.color, -card.energyCost)
          .copy(hand = player.hand diff Seq(card))

      // モンスターなら召喚し、呪文ならこのターン唱えた呪文のリストに追加する
      val castState = card match {
        case monsterCard: MonsterCard =>
          state.copy(
            player1 = nextPlayer,
            monster1 = Some(Monster.initialState(monsterCard))
          )
        case spellCard: SpellCard =>
          state.copy(
            player1 = nextPlayer.copy(
              spellsCasted = nextPlayer.spellsCasted.appended(spellCard))
          )
      }

      // カード発動時の効果を適用して、モンスターの死亡判定を行った結果を返す
      card.effects
        .foldLeft(castState) { (s, effect) =>
          effect.onCast(s)
        }
        .removeKilledMonster(Player1)
        .removeKilledMonster(Player2)
  }

  // ターン終了時の処理のうち、最初に処理すべきものを side 側についてのみ行ったあとのステートを返す
  private def preTurnEndSingleSide(side: PlayerSide): GameState =
    makeSwap(side) { state =>
      val player = state.player1

      // 1. プレイヤーとモンスターによる攻撃を行う
      val opponentAttacked =
        (player +: state.monster1.toList).foldLeft(state.player2) { (p, e) =>
          e match {
            case monster: Monster if monster.frozen > 0 =>
              p // 凍結しているモンスターは攻撃できない
            case _ => p.attacked(e)
          }
        }

      // 2. リジェネによる回復を行う
      val nextHP = (player.hp + player.regeneration).min(Rule.PlayerMaxHP)

      // 3. エネルギーのジェネレーターによる生成を行う
      val nextEnergies = Color.Colors
        .map(
          color =>
            (color,
             (player.energies(color) + player.generators(color))
               .min(Rule.MaxEnergy)))
        .toMap

      // 4. カードをドローする (山札がない/手札がいっぱいのとき、毒1を受ける)
      val (nextHand, nextDeck) = if (player.hand.length == Rule.MaxHandSize) {
        (player.hand, player.deck)
      } else {
        (player.hand ++ player.deck.take(1), player.deck.drop(1))
      }
      val nextRegeneration =
        if (player.deck.isEmpty || player.hand.length == Rule.MaxHandSize) {
          player.regeneration - 1
        } else {
          player.regeneration
        }

      // 5. モンスターのリジェネ回復と凍結ターン減衰
      val nextState = state
        .copy(
          player1 = player.copy(hp = nextHP,
                                energies = nextEnergies,
                                hand = nextHand,
                                deck = nextDeck,
                                regeneration = nextRegeneration),
          player2 = opponentAttacked
        )
        .applyMonster(Player1) { monster =>
          monster.copy(hp = monster.hp + monster.regeneration,
                       frozen = (monster.frozen - 1).max(0))
        }

      // 5. 呪文とモンスターのターン終了時効果を発動させる
      val monsterEffects = for {
        monster <- state.monster1 if monster.frozen == 0
      } yield { monster.baseCard.effects }
      (player.spellsCasted.flatMap(_.effects) ++ monsterEffects.getOrElse(
        Seq()))
        .foldLeft(nextState) { (s, effect) =>
          effect.onTurnEnd(s)
        }
    }

  // モンスターの死亡判定を side 側で行ったあとのステートを返す
  private def removeKilledMonster(side: PlayerSide): GameState =
    makeSwap(side) { state =>
      if (state.monster1.forall(_.hp <= 0)) {
        state.copy(monster1 = None)
      } else {
        state
      }
    }

  /** 現在の状態でターンを終了させ、その後のステートを返す */
  def turnEnd(): GameState = {
    this
      .preTurnEndSingleSide(Player1)
      .preTurnEndSingleSide(Player2)
      .removeKilledMonster(Player1)
      .removeKilledMonster(Player2)
  }

  /** 現在の決着状況 (or 未決着) を返す */
  def status: GameStatus = {
    val player1Dead = player1.hp <= 0
    val player2Dead = player2.hp <= 0
    if (player1Dead && player2Dead) {
      Draw
    } else if (player1Dead) {
      Player2Wins
    } else if (player2Dead) {
      Player1Wins
    } else {
      InGame
    }
  }

  /** side 側のモンスターに f を適用する */
  def applyMonster(side: PlayerSide)(f: Monster => Monster): GameState =
    makeSwap(side) { state =>
      state.monster1
        .map(monster => state.copy(monster1 = Some(f(monster))))
        .getOrElse(state)
    }

  /** side 側のプレイヤーに f を適用する */
  def applyPlayer(side: PlayerSide)(f: Player => Player): GameState =
    makeSwap(side) { state =>
      state.copy(player1 = f(player1))
    }
}

object GameState {

  /** 指定のデッキを持ったふたりのプレイヤーでゲームを開始し、初期状態を返す */
  def gameStart(deck1: IndexedSeq[Card], deck2: IndexedSeq[Card]): GameState =
    GameState(Player.initialState(deck1),
              Player.initialState(deck2),
              None,
              None)

  // プレイヤーのサイド (1P/2P) を表すための case object
  sealed trait PlayerSide
  object PlayerSide {
    case object Player1 extends PlayerSide
    case object Player2 extends PlayerSide
  }

  // ゲームの決着状況を表すための case object
  sealed trait GameStatus
  object GameStatus {
    case object InGame extends GameStatus
    case object Player1Wins extends GameStatus
    case object Player2Wins extends GameStatus
    case object Draw extends GameStatus
  }
}
