import { DeckModel, CardModel } from '../models/deck-model'

export const deckService = {
  list: (): DeckModel[] => {
    return [
      new DeckModel(
        'Deck capitais do Brasil',
        'Aprenda de forma rápida as capitais do Brasil',
        'Joãozinho SP',
        [
          new CardModel('São Paulo', 'São Paulo'),
          new CardModel('Rio de Janeiro', 'Rio de Janeiro'),
          new CardModel('Minas Gerais', 'Belo Horizonte'),
          new CardModel('Espírito Santo', 'Vitória'),
        ]
      ),
      new DeckModel(
        'Deck capitais dos Países da América Latina',
        'Aprenda de forma rápida as capitais do Países da América Latina',
        'Maria RS',
        [
          new CardModel('Brasil', 'Brasília'),
          new CardModel('Argentina', 'Buenos Aires'),
          new CardModel('Paraguai', 'Assunção'),
          new CardModel('Uruguai', 'Montevidéu'),
        ]
      ),
    ]
  },

  getToken: (): string => {
    return 'FAKE_TOKEN_TO_REQUEST'
  },
}
