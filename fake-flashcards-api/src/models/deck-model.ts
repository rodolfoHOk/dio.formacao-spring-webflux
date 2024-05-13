export class DeckModel {
  name: string
  info: string
  author: string
  cards: CardModel[]

  constructor(name: string, info: string, author: string, cards: CardModel[]) {
    this.name = name
    this.info = info
    this.author = author
    this.cards = cards
  }
}

export class CardModel {
  ask: string
  answer: string

  constructor(ask: string, answer: string) {
    this.ask = ask
    this.answer = answer
  }
}
