<template>
  <view class="card-title pb-10" @click="toUserDetailVue">
    <image
      class="size40 bd-radius mr-sm"
      mode="aspectFill"
      :src="talk.user.avatar"
    />
    <view class="row-between flex-auto">
      <view class="col-center">
        <view class="h20 row-col-center">
          <text class="text-df font-bold">{{ talk.user.nickname }}</text>
          <!--          <text class="text-md" :class="{'color-red':talk.user.vipFlag}">{{ talk.user.nickname }}</text>-->
          <template v-if="!talk.globalTop">
            <social-gender-tag class="ml-xs" :user="talk.user"></social-gender-tag>
            <div v-if="talk.user.identityAuth" class="q-tag-success q-box-nn" @click.stop="toIdentityAuth">
              <q-icon size="14" icon="level"/>
            </div>
            <!--              <q-icon class="color-blue" size="18" icon="level" @click.native.stop="toIdentityAuth"/>-->
          </template>

          <!--          <view v-if="!talk.globalTop" class="ml-5 cu-tag sm radius text-sm row-col-center"
                          :class="[getGenderBgColor(talk.user)]">
                      {{ talk.user.age }}
                      <q-icon class="ml-nm"
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

        <!--        <view class="color-sub text-sm h20 row-col-end" v-if="talk.user.identityAuth || talk.globalTop ||isMine">-->
        <view class="color-sub text-sm h20 row-col-end line-h1">
          {{ talk.updateTime| formatTime }}
          <view v-if="talk.globalTop" class="ml-5 sm cu-tag round bg-red light">
            官方
          </view>
          <!--              自己的帖子，或者系统管理员可以删除帖子-->
          <text v-if="isMine"
                class="ml-5 color-blue1 bg-click row-col-end line-h1"
                @click.stop="confirmDeleteTalk">
            删除
          </text>
        </view>
      </view>
      <!--                不为自己且未关注-->
      <view v-if="talkTabType!==followType&&!isMine&&!isUserDetail" class="col-center">
        <div v-if="!talk.hasFollowed" class="color-content chunk q-box-nm bd-radius" @click.stop="addFollow">关注</div>
        <div v-else class="color-content" @click.stop="addFollow">已关注</div>
      </view>
    </view>
  </view>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator'
import TalkVO from '../../model/talk/TalkVO'
import FollowAddVO from '../../model/FollowAddVO'
import PagePath from '../../constant/PagePath'
import TalkAPI from '../../api/TalkAPI'
import UserUtil from '../../utils/UserUtil'
import JsonUtils from '../../utils/ObjectUtil'
import FollowAPI from '../../api/FollowAPI'
import PageUtil from '../../utils/PageUtil'
import MsgUtil from '../../utils/MsgUtil'
import TalkTabType from '../../constant/TalkTabType'
import RouterUtil from '../../utils/RouterUtil'
import QIcon from '../../../qing-ui/components/QIcon/QIcon.vue'
import CenterUserDetailRO from '../../model/social/CenterUserDetailRO'
import AlertUtil from '../../utils/AlertUtil'
import ToastUtil from '../../utils/ToastUtil'
import SocialuniConfig from '../../config/SocialuniConfig'
import { socialUserStore } from '../../store'
import SocialGenderTag from '@/socialuni/components/SocialGenderTag/SocialGenderTag.vue'

@Component({
  components: {
    SocialGenderTag,
    QIcon
  }
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

  toIdentityAuth () {
    MsgUtil.identityAuthHint()
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
      TalkAPI.deleteTalkAPI(this.talk.id).then(() => {
        ToastUtil.toast('删除成功')
      })
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

  getGenderIcon (user
    :
    CenterUserDetailRO
  ) {
    return UserUtil.getGenderIcon(user)
  }

  getGenderBgColor (user
    :
    CenterUserDetailRO
  ) {
    return UserUtil.getGenderBgColor(user)
  }
}
</script>
