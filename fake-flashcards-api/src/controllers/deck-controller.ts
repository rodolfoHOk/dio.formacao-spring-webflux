import { Request, Response } from 'express'
import { AuthModel } from '../models/auth-model'
import { deckService } from '../services/deck-service'

export const deckController = {
  index: async (req: Request, res: Response) => {
    const { token } = req.headers
    return deckService.getToken() === token
      ? res.status(200).json(deckService.list())
      : res.status(401).send()
  },

  auth: async (req: Request, res: Response) => {
    res.status(201).json(new AuthModel(deckService.getToken()))
  },
}
