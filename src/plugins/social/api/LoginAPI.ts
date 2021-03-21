import ProviderUserVO from '@/plugins/uni/model/login/ProviderUserVO'
import socialHttp from '@/plugins/social/http'
import UserVO from '@/model/user/UserVO'


export default class LoginAPI {
  static providerLoginAPI (loginData: ProviderUserVO) {
    return socialHttp.post('user/providerLogin', loginData)
  }

  //微信绑定手机号使用
  static bindPhoneNumAPI (loginData: ProviderUserVO) {
    return socialHttp.post<UserVO>('user/bindPhoneNumByWx', loginData)
  }
}
