import request from '../plugins/http/request'
import CircleCreateQO from '@/socialuni/model/community/circle/CircleCreateQO'
import ToastUtil from '@/socialuni/utils/ToastUtil'
import SocialCircleRO from '@/socialuni/model/community/circle/SocialCircleRO'
import CircleTypeRO from '@/socialuni/model/community/circle/CircleTypeRO'


export default class CircleAPI {
  static createCircleAPI (createQO: CircleCreateQO) {
    return request.post<SocialCircleRO>('circle/createCircle', createQO).then(res => {
      ToastUtil.toast('εε»Ίζε')
      return res
    })
  }

  static queryHotCirclesAPI () {
    return request.post<SocialCircleRO []>('circle/queryHotCircles')
  }

  static queryCircleTypesAPI () {
    return request.post<CircleTypeRO []>('circle/queryCircleTypes')
  }
}
