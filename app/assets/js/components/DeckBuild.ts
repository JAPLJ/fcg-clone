import axios from "axios";
import { Card, MonsterCard, SpellCard } from "./Card";

import { Component, Vue } from "vue-property-decorator";

@Component({
  template: require('./DeckBuild.html')
})
export default class DeckBuild extends Vue {
  monsterCards: MonsterCard[] = [];
  spellCards: SpellCard[] = [];

  userInputDeckCode: string = "";
  userInputErrors: string = "";

  /** カードの色を表すクラス名を返す */
  get colorOf(): (card: Card) => string {
    return (card => {
      return "col-" + card.color;
    });
  }

  /** 現在のデッキを表すデッキコードを返す */
  get deckCode(): string {
    function toCode(card: Card): string {
      if (card.inDeck == 0) {
        return "";
      } else {
        return `${card.inDeck}.${card.id}`;
      }
    }
    return this.monsterCards.map(toCode).concat(this.spellCards.map(toCode))
      .filter(s => s.length > 0).join('.');
  }

  /** 現在のデッキに入っているカードの枚数を返す */
  get numCardsInDeck(): number {
    const monsterCards = this.monsterCards.reduce((p, card) => p + card.inDeck, 0);
    return this.spellCards.reduce((p, card) => p + card.inDeck, monsterCards);
  }

  /** 現在のデッキのエラー状況を返す */
  get deckErrors(): Array<string> {
    let errors: Array<string> = [];
    const totalCards = this.numCardsInDeck;

    function checkCard(card: Card): void {
      if (!Number.isSafeInteger(card.inDeck)) {
        errors.push(`id ${card.id}: 枚数が整数でないか、絶対値が大きすぎます。`);
      } else if (!(0 <= card.inDeck && card.inDeck <= card.maxSameCards)) {
        errors.push(`id ${card.id}: 枚数が 0 枚以上 ${card.maxSameCards} 枚以下ではありません。`);
      }
    }
    this.monsterCards.forEach(checkCard);
    this.spellCards.forEach(checkCard);

    if (errors.length == 0 && !(25 <= totalCards && totalCards <= 100)) {
      errors.push('カードの合計枚数が 25 枚以上 100 枚以下ではありません。');
    }
    return errors;
  }

  /** 存在するカードの ID の集合を返す */
  get validCardIds(): Set<number> {
    let ids = new Set<number>();
    this.monsterCards.forEach(c => ids.add(c.id));
    this.spellCards.forEach(c => ids.add(c.id));
    return ids;
  }

  /** ユーザの入力したデッキコードを読み込む */
  loadDeckCode($event: Event): void {
    let inputDeck = new Map<number, number>();
    this.userInputErrors = "";

    const splitted = this.userInputDeckCode.split('.');
    if (splitted.length % 2 != 0) {
      this.userInputErrors = 'フォーマットエラー';
      return;
    }

    for (let i = 0; i < splitted.length; i++) {
      if (!/^\+?[1-9]\d*$/.test(splitted[i]) || !Number.isSafeInteger(parseFloat(splitted[i]))) {
        this.userInputErrors = '正整数でないか、大きすぎる値が含まれています。';
        return;
      }
    }

    for (let i = 0; i < splitted.length; i += 2) {
      const num = parseInt(splitted[i]);
      const id = parseInt(splitted[i + 1]);
      if (!this.validCardIds.has(id)) {
        this.userInputErrors = `id ${id} のカードは存在しません。`;
        return;
      }
      if (inputDeck.has(id)) {
        this.userInputErrors = `id ${id} のカードの情報が重複しています。`;
        return;
      }
      inputDeck.set(id, num);
    }

    function setInDeck(card: Card): void {
      const num = inputDeck.get(card.id);
      if (num) {
        card.inDeck = num;
      } else {
        card.inDeck = 0;
      }
    }
    this.monsterCards.forEach(setInDeck);
    this.spellCards.forEach(setInDeck);
  }

  /** マウント直前にカード情報をサーバから取得する */
  mounted(): void {
    axios.get<MonsterCard[]>("/api/monster_cards")
      .then(response => (this.monsterCards = response.data));
    axios.get<SpellCard[]>("/api/spell_cards")
      .then(response => (this.spellCards = response.data));
  }
}
