import { Action, Module, VuexModule } from 'vuex-class-modules'

import UserAPI from '../api/UserAPI'
import { socialUserModule } from './index'
import UserStorageUtil from '../utils/UserStorageUtil'
import AlertUtil from '../utils/AlertUtil'
import ToastUtil from '../utils/ToastUtil'
import UserService from '../service/UserService'
import TokenUtil from '../utils/TokenUtil'
import SocialLoginRO from '../model/social/SocialLoginRO'
import CenterUserDetailRO from '../model/social/CenterUserDetailRO'

//用来存储当前用户的一些信息
@Module({ generateMutationSetters: true })
export default class SocialUserModule extends VuexModule {
  user: CenterUserDetailRO = UserStorageUtil.get() || null

  get hasPhoneNum () {
    return this.user && this.user.phoneNum
  }

  removeUser () {
    this.user = null
    UserStorageUtil.remove()
  }

  setUser (user: CenterUserDetailRO) {
    this.user = user
    UserStorageUtil.set(user)
  }

  get hasUser (): boolean {
    return !!this.user
  }

  get userId () {
    if (this.user) {
      return this.user.id
    }
    return null
  }

  loginOut () {
    return AlertUtil.confirm('是否退出登录').then(() => {
      UserService.clearUserInfoCom()
      ToastUtil.toast('用户退出')
    })
  }

  loginAfterSetUserAndToken (loginRO: SocialLoginRO<CenterUserDetailRO>) {
    TokenUtil.set(loginRO.token)
    this.setUser(loginRO.user)
  }

  @Action
  destroyAccount () {
    return AlertUtil.confirm('是否注销账号，7天内不再登录，账号将彻底清空无法使用').then(() => {
      UserAPI.destroyAccountAPI().then(() => {
        UserService.clearUserInfoCom()
        ToastUtil.toast('注销成功')
      })
    })
  }

  /**
   * 调用后台仅获取user信息
   */
  @Action
  getMineUserAction () {
    return UserAPI.getMineUserInfoAPI().then((res: any) => {
      socialUserModule.setUser(res.data)
    })
  }
}
