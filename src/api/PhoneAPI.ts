import http from '@/plugins/http'
import SocialUserRO from '@/model/social/SocialUserRO'
import SocialPhoneNumQO from '@/model/phone/SocialPhoneNumQO'
import UserVO from '@/model/user/UserVO'
import RefreshWxSessionKeyQO from '@/model/phone/RefreshWxSessionKeyQO'
import BindWxPhoneNumQO from '@/model/phone/BindWxPhoneNumQO'

export default class PhoneAPI {
  static bindSocialPhoneNum () {
    return http.post<SocialUserRO>('phone/bindSocialPhoneNum')
  }

  static sendAuthCodeAPI (phoneNum: string) {
    return http.post('phone/sendAuthCode', { phoneNum })
  }

  static bindPhoneNumAPI (phoneNum: string, authCode: string) {
    const phoneNumObj: SocialPhoneNumQO = new SocialPhoneNumQO(phoneNum, authCode)
    return http.post<UserVO>('phone/bindPhoneNum', phoneNumObj).then(res => {
      return res.data
    })
  }

  //微信绑定手机号使用
  static bindWxPhoneNumAPI (bindWxPhoneNumQO: BindWxPhoneNumQO) {
    return http.post<UserVO>('phone/bindWxPhoneNum', bindWxPhoneNumQO)
  }

  //微信绑定手机号使用
  static refreshWxSessionKeyAPI (code: string) {
    return http.post('phone/refreshWxSessionKey', new RefreshWxSessionKeyQO(code))
  }
}
