export interface Card {
  id: number;
  name: string;
  maxSameCards: number;
  color: string;
  energyCost: number;
  effectDescriptions: string[];
  inDeck: number;
}

export interface MonsterCard extends Card {
  hp: number;
  attack: number;
  defense: number;
}

export interface SpellCard extends Card { }
