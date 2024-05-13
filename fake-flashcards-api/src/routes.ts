import express from 'express'
import { deckController } from './controllers/deck-controller'

export const router = express.Router()

router.get('/decks', deckController.index)

router.post('/auth', deckController.auth)
