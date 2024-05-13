export class AuthModel {
  token: string
  expiresIn: number

  constructor(token: string) {
    this.token = token
    this.expiresIn = 30
  }
}
