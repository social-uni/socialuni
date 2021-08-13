export default class LoginUserVO {
  phoneNum: string = null
  authCode: string = null

  constructor(phoneNum: string, authCode?: string) {
    this.phoneNum = phoneNum
    this.authCode = authCode
  }
}
