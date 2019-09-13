package fcg.rule

import fcg.game.GameState
import fcg.game.GameState.PlayerSide.{Player1, Player2}

/** モンスターの特殊効果や、呪文による効果を表す trait
  * 効果はすべて 1P 側が発動した場合を実装する */
sealed trait Effect {

  /** 出現時 (呪文の場合は唱えられた瞬間) に発動する効果を実装するメソッド */
  def onCast(state: GameState): GameState = state

  /** ターン終了時に発動する効果を実装するメソッド */
  def onTurnEnd(state: GameState): GameState = state
}

object Effect {

  /** 出現時にモンスターは [[poison]] ぶんの毒を受ける */
  case class Poisoned(poison: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainRegeneration(-poison) }
  }

  /** 出現時に自プレイヤーは [[poison]] ぶんの毒を受ける */
  case class GetPoison(poison: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainRegeneration(-poison) }
  }

  /** ターン終了時に [[color]] 色のエネルギーを [[consumption]] だけ消費する */
  case class ConsumeEnergyEveryTurn(color: Color, consumption: Int)
      extends Effect {
    override def onTurnEnd(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainEnergy(color, -consumption) }
  }

  /** 出現時に [[color]] 色のエネルギーを [[consumption]] だけ消費する */
  case class ConsumeEnergy(color: Color, consumption: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainEnergy(color, -consumption) }
  }

  /** 出現時に [[color]] 色のエネルギー増加量を [[generator]] だけ増やす */
  case class GainGenerator(color: Color, generator: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainGenerator(color, generator) }
  }

  /** 出現時に [[color]] 色のエネルギーを [[amount]] だけ得る */
  case class GainEnergy(color: Color, amount: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainEnergy(color, amount) }
  }

  /** 出現時に相手プレイヤーに [[damage]] ぶんのダメージを与える */
  case class DealDamage(damage: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainHP(-damage) }
  }

  /** 出現時に自プレイヤーに [[damage]] ぶんのダメージを与える */
  case class DealSelfDamage(damage: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainHP(-damage) }
  }

  /** 出現時に相手モンスターに [[damage]] ぶんのダメージを与える */
  case class DealMonsterDamage(damage: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player2) { _.gainHP(-damage) }
  }

  /** 出現時に自モンスターに [[damage]] ぶんのダメージを与える */
  case class DealSelfMonsterDamage(damage: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainHP(-damage) }
  }

  /** ターン終了時に相手プレイヤーに [[damage]] ぶんのダメージを与える */
  case class AdditionalDamage(damage: Int) extends Effect {
    override def onTurnEnd(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainHP(-damage) }
  }

  /** 出現時に相手のモンスターを除去する */
  case class KillMonster() extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player2) { _.removed }
  }

  /** 出現時に相手モンスターの HP と同じだけ自プレイヤーが回復する */
  case class AbsorbHP() extends Effect {
    override def onCast(state: GameState): GameState =
      state.monster2
        .map { monster =>
          state.applyPlayer(Player1) { _.gainHP(monster.hp) }
        }
        .getOrElse(state)
  }

  /** 出現時に自モンスターを除去し、その HP・攻撃力・防御力を自プレイヤーが得る */
  case class PermanentMonster() extends Effect {
    override def onCast(state: GameState): GameState =
      state.monster1
        .map { monster =>
          state.applyPlayer(Player1) { player =>
            player
              .gainHP(monster.hp)
              .gainAttack(monster.attack)
              .gainDefense(monster.defense)
          }
        }
        .getOrElse(state)
        .applyMonster(Player1) { _.removed }
  }

  /** 出現時に相手プレイヤーに [[poison]] ぶんの毒を与える */
  case class DealPoisonToPlayer(poison: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainRegeneration(-poison) }
  }

  /** ターン終了時に相手プレイヤーに [[poison]] ぶんの毒を与える */
  case class DealPoisonToPlayerEveryTurn(poison: Int) extends Effect {
    override def onTurnEnd(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainRegeneration(-poison) }
  }

  /** 出現時に自プレイヤーに [[attack]] ぶんの攻撃力を与える */
  case class GainAttack(attack: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainAttack(attack) }
  }

  /** 出現時に相手プレイヤーに [[attack]] ぶんの攻撃力を与える */
  case class GainOpponentAttack(attack: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainAttack(attack) }
  }

  /** 出現時に自プレイヤーに [[defense]] ぶんの防御力を与える */
  case class GainDefense(defense: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainDefense(defense) }
  }

  /** 出現時に相手プレイヤーに [[defense]] ぶんの防御力を与える */
  case class GainOpponentDefense(defense: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainDefense(defense) }
  }

  /** 出現時に自モンスターに [[attack]] ぶんの攻撃力を与える */
  case class GainMonsterAttack(attack: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainAttack(attack) }
  }

  /** 出現時に自モンスターに [[defense]] ぶんの防御力を与える */
  case class GainMonsterDefense(defense: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainDefense(defense) }
  }

  /** 出現時にモンスターは [[regeneration]] ぶんのリジェネを受ける */
  case class Blessed(regeneration: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainRegeneration(regeneration) }
  }

  /** 出現時に自プレイヤーに [[regeneration]] ぶんのリジェネを与える */
  case class BlessPlayer(regeneration: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainRegeneration(regeneration) }
  }

  /** 出現時に自プレイヤーに [[hp]] ぶんの HP を与える */
  case class GainHP(hp: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainHP(hp) }
  }

  /** 出現時に [[amount]] 枚のカードをドローする */
  case class Draw(amount: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.drawCard(amount) }
  }

  /** 出現時に相手モンスターに [[turns]] ターンの凍結を与える */
  case class Freeze(turns: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player2) { _.freeze(turns) }
  }

  /** 出現時に自モンスターの凍結状態を解除する */
  case class AntiFreeze() extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { monster =>
        monster.freeze(-monster.frozen)
      }
  }
}
