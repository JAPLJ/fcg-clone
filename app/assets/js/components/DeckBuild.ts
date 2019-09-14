import axios from "axios";
import { Card, MonsterCard, SpellCard } from "./Card";

import { Component, Vue } from "vue-property-decorator";

@Component({
  template: require('./DeckBuild.html')
})
export default class DeckBuild extends Vue {
  monsterCards: MonsterCard[] = [];
  spellCards: SpellCard[] = [];

  /** カードの色を表すクラス名を返す */
  get colorOf(): (card: Card) => string {
    return (card => {
      return "col-" + card.color;
    });
  }

  /** マウント直前にカード情報をサーバから取得する */
  mounted(): void {
    axios.get<MonsterCard[]>("/api/monster_cards")
      .then(response => (this.monsterCards = response.data));
    axios.get<SpellCard[]>("/api/spell_cards")
      .then(response => (this.spellCards = response.data));
  }
}
