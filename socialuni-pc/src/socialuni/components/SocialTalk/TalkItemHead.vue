<template>
  <view class="card-title pb-10" @click="toUserDetailVue">
    <image
      class="card-title-avatar"
      mode="aspectFill"
      :src="talk.user.avatar"
    />
    <view class="row-between flex-auto">
      <view>
        <view class="h25 row-col-center">
          <text class="text-md">{{ talk.user.nickname }}</text>
          <!--          <text class="text-md" :class="{'color-red':talk.user.vipFlag}">{{ talk.user.nickname }}</text>-->
          <template v-if="!talk.globalTop">
            <div v-if="talk.user.gender==='girl'" class="box-nn q-tag-error ml-sm">
              {{ talk.user.age }}
              <q-icon class="ml-nn"
                      size="12"
                      :icon="getGenderIcon(talk.user)"/>
            </div>
            <div v-else class="box-nn q-tag-blue ml-sm">
              {{ talk.user.age }}
              <q-icon class="ml-nn"
                      size="12"
                      :icon="getGenderIcon(talk.user)"/>
            </div>
          </template>

          <!--          <view v-if="!talk.globalTop" class="ml-5 cu-tag sm radius text-sm row-col-center"
                          :class="[getGenderBgColor(talk.user)]">
                      {{ talk.user.age }}
                      <q-icon class="ml-nn"
                              size="12"
                              :icon="getGenderIcon(talk.user)"/>
                    </view>-->
          <!--          <view v-if="talk.user.vipFlag" class="ml-5 cu-tag bg-red radius sm text-sm font-bold"
                          @click.stop="openVip">
                      VIP
                    </view>-->
          <!--    如果爱心值不为0，且大于正义值显示爱心值-->
          <!--          <view v-else-if="talk.user.loveValue&& talk.user.loveValue>talk.user.justiceValue"
                          class="ml-5 cu-capsule radius"
                          @click.stop="toLoveValuePage">
                      <view class='cu-tag bg-red sm'>
                        <q-icon icon="heart"/>
                      </view>
                      <view class="cu-tag bg-white bd-red bd-r-radius sm">
                        {{ talk.user.loveValue }}
                      </view>
                    </view>-->
          <!--    如果正义值不为0，且大于等于爱心值显示正义值-->
          <!--          <view v-else-if="talk.user.justiceValue&& talk.user.justiceValue >= talk.user.loveValue"
                          class="ml-5 cu-capsule radius"
                          @click.stop="hintJusticeInfo">
                      <view class='cu-tag bg-green sm'>
                        <q-icon icon="mdi-sword-cross"/>
                      </view>
                      <view class="cu-tag bg-white bd-green bd-r-radius sm">
                        {{
                          talk.user.justiceValue > 1000 ? Math.floor(talk.user.justiceValue / 1000) + 'k' : talk.user.justiceValue
                        }}
                      </view>
                    </view>-->
        </view>
        <view class="text-gray text-sm h25 row-col-center">
          最新回复：{{ talk.updateTime| formatTime }}
          <view v-if="talk.globalTop" class="ml-5 sm cu-tag round bg-red light">
            官方
          </view>
          <!--              自己的帖子，或者系统管理员可以删除帖子-->
          <text v-if="isMine"
                class="ml-5 color-blue1 bg-click"
                @click.stop="confirmDeleteTalk">
            删除
          </text>
        </view>
      </view>
      <!--                不为自己且未关注-->
      <view v-if="talkTabType!==followType&&!isMine&&!isUserDetail" class="col-center">
        <button v-if="!talk.hasFollowed" class="cu-btn round bd-theme bg-white text-theme px-smm"
                @click.stop="addFollow">
          关注
        </button>
        <view v-else class="box-xs" @click.stop="addFollow">已关注</view>
      </view>
    </view>
  </view>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator'
import TalkVO from '../../model/talk/TalkVO'
import FollowAddVO from '../../model/FollowAddVO'
import PagePath from '../../const/PagePath'
import TalkAPI from '../../api/TalkAPI'
import UserUtil from '../../utils/UserUtil'
import JsonUtils from '../../utils/JsonUtil'
import FollowAPI from '../../api/FollowAPI'
import PageUtil from '../../utils/PageUtil'
import MsgUtil from '../../utils/MsgUtil'
import TalkTabType from '../../const/TalkTabType'
import RouterUtil from '../../utils/RouterUtil'
import QIcon from '../../../qing-ui/components/QIcon/QIcon.vue'
import CenterUserDetailRO from '../../model/social/CenterUserDetailRO'
import AlertUtil from '../../utils/AlertUtil'
import ToastUtil from '../../utils/ToastUtil'
import SocialuniConfig from '../../config/SocialuniConfig'
import { socialUserStore } from '../../store'

@Component({
  components: { QIcon }
})
export default class TalkItemHead extends Vue {
  @Prop() talkProp!: TalkVO
  @Prop() talkTabType: string

  // 因为需要修改关注状态，所以需要克隆
  talk: TalkVO = JsonUtils.deepClone(this.talkProp)
  @socialUserStore.State('user') user: CenterUserDetailRO
  followBtnDisabled = false
  isUserDetail = false
  followType: string = TalkTabType.follow_type

  @Watch('talkProp')
  talkPropWatch () {
    this.talk = JsonUtils.deepClone(this.talkProp)
  }

  created () {
    if (RouterUtil.getCurrentPageURI() === PagePath.userDetail) {
      this.isUserDetail = true
    }
  }

  toLoveValuePage () {
    if (this.user) {
      if (SocialuniConfig.authApp) {
        PageUtil.toLoveValuePage()
      }
    } else {
      MsgUtil.unLoginMessage()
    }
  }

  hintJusticeInfo () {
    ToastUtil.toastLong('正义值，正确举报会增加正义值')
  }

  // 自己不为null，且是自己
  get isMine (): boolean {
    return this.user && this.user.id === this.talk.user.id
  }

  toUserDetailVue () {
    if (RouterUtil.getCurrentPageURI() !== PagePath.userDetail) {
      RouterUtil.navigateTo(PagePath.userDetail + '?userId=' + this.talk.user.id)
    }
  }

  confirmDeleteTalk () {
    AlertUtil.confirm('是否确定删除此条动态，此操作无法恢复').then(() => {
      this.$emit('deleteTalk', this.talk.id)
      TalkAPI.deleteTalkAPI(this.talk.id)
    })
  }

  openVip () {
    PageUtil.toVipPage()
  }

  addFollow () {
    if (this.user) {
      if (!this.followBtnDisabled) {
        const followAdd: FollowAddVO = new FollowAddVO(this.talk.user.id)
        if (this.talk.hasFollowed) {
          AlertUtil.confirm('是否取消关注用户：' + this.talk.user.nickname).then(() => {
            this.followBtnDisabled = true
            this.talk.hasFollowed = false
            FollowAPI.cancelFollowAPI(followAdd).finally(() => {
              this.followBtnDisabled = false
            })
          })
        } else {
          this.followBtnDisabled = true
          this.talk.hasFollowed = true
          FollowAPI.addFollowAPI(followAdd).finally(() => {
            this.followBtnDisabled = false
          })
        }
      }
    } else {
      MsgUtil.unLoginMessage()
    }
  }

  getGenderIcon (user: CenterUserDetailRO) {
    return UserUtil.getGenderIcon(user)
  }

  getGenderBgColor (user: CenterUserDetailRO) {
    return UserUtil.getGenderBgColor(user)
  }
}
</script>
