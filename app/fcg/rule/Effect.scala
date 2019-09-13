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

  /** この効果を持つカードに書かれる説明文を返す */
  def description: String
}

object Effect {

  //
  // 毒・リジェネ
  //

  /** 出現時にモンスターは [[poison]] ぶんの毒を受ける */
  case class Poisoned(poison: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainRegeneration(-poison) }

    override def description: String = s"毒 $poison"
  }

  /** 出現時に自プレイヤーは [[poison]] ぶんの毒を受ける */
  case class GetPoison(poison: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainRegeneration(-poison) }

    override def description: String = s"服毒 $poison"
  }

  /** 出現時に相手プレイヤーに [[poison]] ぶんの毒を与える */
  case class DealPoisonToPlayer(poison: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainRegeneration(-poison) }

    override def description: String = s"毒付与 $poison"
  }

  /** ターン終了時に相手プレイヤーに [[poison]] ぶんの毒を与える */
  case class DealPoisonToPlayerEveryTurn(poison: Int) extends Effect {
    override def onTurnEnd(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainRegeneration(-poison) }

    override def description: String = s"猛毒付与 $poison"
  }

  /** 出現時にモンスターは [[regeneration]] ぶんのリジェネを受ける */
  case class Blessed(regeneration: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainRegeneration(regeneration) }

    override def description: String = s"祝福 $regeneration"
  }

  /** 出現時に自プレイヤーに [[regeneration]] ぶんのリジェネを与える */
  case class BlessPlayer(regeneration: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainRegeneration(regeneration) }

    override def description: String = s"リジェネ $regeneration"
  }

  //
  // エネルギー
  //

  /** ターン終了時に [[color]] 色のエネルギーを [[consumption]] だけ消費する */
  case class ConsumeEnergyEveryTurn(color: Color, consumption: Int)
      extends Effect {
    override def onTurnEnd(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainEnergy(color, -consumption) }

    override def description: String = s"常時${color.name}エネルギー消費 $consumption"
  }

  /** 出現時に [[color]] 色のエネルギーを [[consumption]] だけ消費する */
  case class ConsumeEnergy(color: Color, consumption: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainEnergy(color, -consumption) }

    override def description: String = s"${color.name}エネルギー消費 $consumption"
  }

  /** 出現時に [[color]] 色のエネルギー増加量を [[generator]] だけ増やす */
  case class GainGenerator(color: Color, generator: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainGenerator(color, generator) }

    override def description: String = s"${color.name}ジェネレーター獲得 $generator"
  }

  /** 出現時に [[color]] 色のエネルギーを [[amount]] だけ得る */
  case class GainEnergy(color: Color, amount: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainEnergy(color, amount) }

    override def description: String = s"${color.name}エネルギー獲得 $amount"
  }

  //
  // ダメージ
  //

  /** 出現時に相手プレイヤーに [[damage]] ぶんのダメージを与える */
  case class DealDamage(damage: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainHP(-damage) }

    override def description: String = s"即時ダメージ $damage"
  }

  /** 出現時に自プレイヤーに [[damage]] ぶんのダメージを与える */
  case class DealSelfDamage(damage: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainHP(-damage) }

    override def description: String = s"即時自傷ダメージ $damage"
  }

  /** 出現時に相手モンスターに [[damage]] ぶんのダメージを与える */
  case class DealMonsterDamage(damage: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player2) { _.gainHP(-damage) }

    override def description: String = s"即時モンスターダメージ $damage"
  }

  /** 出現時に自モンスターに [[damage]] ぶんのダメージを与える */
  case class DealSelfMonsterDamage(damage: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainHP(-damage) }

    override def description: String = s"即時モンスター自傷ダメージ $damage"
  }

  /** ターン終了時に相手プレイヤーに [[damage]] ぶんのダメージを与える */
  case class AdditionalDamage(damage: Int) extends Effect {
    override def onTurnEnd(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainHP(-damage) }

    override def description: String = s"追加ダメージ $damage"
  }

  //
  // モンスター操作
  //

  /** 出現時に相手のモンスターを除去する */
  case class KillMonster() extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player2) { _.removed }

    override def description: String = s"除去"
  }

  /** 出現時に相手モンスターの HP と同じだけ自プレイヤーが回復する */
  case class AbsorbHP() extends Effect {
    override def onCast(state: GameState): GameState =
      state.monster2
        .map { monster =>
          state.applyPlayer(Player1) { _.gainHP(monster.hp) }
        }
        .getOrElse(state)

    override def description: String = s"HP 吸収"
  }

  /** 出現時に自モンスターを除去し、その HP・攻撃力・防御力・リジェネ値を自プレイヤーが得る */
  case class PermanentMonster() extends Effect {
    override def onCast(state: GameState): GameState =
      state.monster1
        .map { monster =>
          state.applyPlayer(Player1) { player =>
            player
              .gainHP(monster.hp)
              .gainAttack(monster.attack)
              .gainDefense(monster.defense)
              .gainRegeneration(monster.regeneration)
          }
        }
        .getOrElse(state)
        .applyMonster(Player1) { _.removed }

    override def description: String = s"生贄"
  }

  /** 出現時に相手モンスターに [[turns]] ターンの凍結を与える */
  case class Freeze(turns: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player2) { _.freeze(turns) }

    override def description: String = s"凍結 $turns"
  }

  /** 出現時に自モンスターの凍結状態を解除する */
  case class AntiFreeze() extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { monster =>
        monster.freeze(-monster.frozen)
      }

    override def description: String = s"凍結解除"
  }

  //
  // ステータス変化
  //

  private def upOrDown(amount: Int): String =
    if (amount > 0) s"UP $amount" else s"DOWN ${-amount}"

  /** 出現時に自プレイヤーに [[attack]] ぶんの攻撃力を与える */
  case class GainAttack(attack: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainAttack(attack) }

    override def description: String = s"攻撃力${upOrDown(attack)}"
  }

  /** 出現時に相手プレイヤーに [[attack]] ぶんの攻撃力を与える */
  case class GainOpponentAttack(attack: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainAttack(attack) }

    override def description: String = s"敵攻撃力${upOrDown(attack)}"
  }

  /** 出現時に自プレイヤーに [[defense]] ぶんの防御力を与える */
  case class GainDefense(defense: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainDefense(defense) }

    override def description: String = s"防御力${upOrDown(defense)}"
  }

  /** 出現時に相手プレイヤーに [[defense]] ぶんの防御力を与える */
  case class GainOpponentDefense(defense: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player2) { _.gainDefense(defense) }

    override def description: String = s"敵防御力${upOrDown(defense)}"
  }

  /** 出現時に自モンスターに [[attack]] ぶんの攻撃力を与える */
  case class GainMonsterAttack(attack: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainAttack(attack) }

    override def description: String = s"モンスター攻撃力${upOrDown(attack)}"
  }

  /** 出現時に自モンスターに [[defense]] ぶんの防御力を与える */
  case class GainMonsterDefense(defense: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyMonster(Player1) { _.gainDefense(defense) }

    override def description: String = s"モンスター防御力${upOrDown(defense)}"
  }

  /** 出現時に自プレイヤーに [[hp]] ぶんの HP を与える */
  case class GainHP(hp: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.gainHP(hp) }

    override def description: String = s"回復 $hp"
  }

  //
  // 他
  //

  /** 出現時に [[amount]] 枚のカードをドローする */
  case class Draw(amount: Int) extends Effect {
    override def onCast(state: GameState): GameState =
      state.applyPlayer(Player1) { _.drawCard(amount) }

    override def description: String = s"ドロー $amount"
  }
}
