import { Action, Module, VuexModule } from 'vuex-class-modules'
import { socialCircleModule, socialUserModule } from './index'
import CommentAddVO from '../model/comment/CommentAddVO'
import CommentVO from '../model/comment/CommentVO'
import TalkAPI from '../api/TalkAPI'
import TalkVO from '../model/talk/TalkVO'
import MsgUtil from '../utils/MsgUtil'
import CommonUtil from '../utils/CommonUtil'
import TalkTabVO from '../model/talk/TalkTabVO'
import TalkVueUtil from '../utils/TalkVueUtil'
import TalkFilterUtil from '../utils/TalkFilterUtil'
import TalkTabType from '@/socialuni/constant/TalkTabType'
import StorageUtil from '@/socialuni/utils/StorageUtil'


@Module({ generateMutationSetters: true })
export default class SocialTalkModule extends VuexModule {
  // filter内容
  userMinAge: number = TalkFilterUtil.getMinAgeFilter()
  userMaxAge: number = TalkFilterUtil.getMaxAgeFilter()
  userGender: string = TalkFilterUtil.getGenderFilter()
  talkTabs: TalkTabVO [] = TalkVueUtil.getTalkTabs()
  currentTabIndex: number = TalkVueUtil.getCurTalkTabIndex()

  // state
  currentContent: null
  talk: TalkVO = null
  comment: CommentVO = null
  replyComment: CommentVO = null
  inputContentFocus = false

  // talk和评论的举报删除相关操作的dialog的显示
  commentActionShow = false
  reportDialogShow = false
  reportContentType = ''

  @Action
  addComment ({ content }) {
    // 使输入框失去焦点，隐藏
    const commentAdd: CommentAddVO = new CommentAddVO(content, this.talk.id)
    const tempComment: CommentVO = commentAdd.toComment()
    tempComment.user = socialUserModule.user
    if (this.comment) {
      commentAdd.commentId = this.comment.id
      if (this.replyComment) {
        commentAdd.replyCommentId = this.replyComment.id
        tempComment.replyComment = this.replyComment
      }
    }
    if (this.comment) {
      this.comment.childComments.unshift(tempComment)
      this.comment.childCommentNum++
    } else {
      this.talk.comments.unshift(tempComment)
      this.talk.commentNum++
    }
    TalkAPI.addCommentAPI(commentAdd).then((res: any) => {
      if (this.comment) {
        this.comment.childComments.splice(0, 1, res.data)
      } else {
        this.talk.comments.splice(0, 1, res.data)
      }
    }).catch(() => {
      if (this.comment) {
        this.comment.childComments.splice(0, 1)
      } else {
        this.talk.comments.splice(0, 1)
      }
    })
  }

  @Action
  setTalk (talk) {
    const user = socialUserModule.user
    if (user && user.phoneNum) {
      this.talk = talk
      this.comment = null
      this.replyComment = null
      this.currentContent = talk.content
      this.inputContentFocusEvent()
    } else {
      MsgUtil.unBindPhoneNum()
    }
  }

  @Action
  setComment ({
    talk,
    comment
  }) {
    if (socialUserModule.user) {
      this.talk = talk
      this.comment = comment
      this.replyComment = null
      this.currentContent = comment.content
      this.inputContentFocusEvent()
    } else {
      MsgUtil.unLoginMessage()
    }
  }

  @Action
  setReplyComment ({
    talk,
    comment,
    replyComment
  }) {
    if (socialUserModule.user) {
      this.talk = talk
      this.comment = comment
      this.replyComment = replyComment
      this.currentContent = replyComment.content
      this.inputContentFocusEvent()
    } else {
      MsgUtil.unLoginMessage()
    }
  }

  @Action
  inputContentFocusEvent () {
    MsgUtil.cantPopupPromptToast()
    // 需要有延迟，要不然无法成功切换
    CommonUtil.delayTime(200).then(() => {
      this.inputContentFocus = true
    })
  }

  @Action
  inputContentBlur () {
    // 需要有延迟，要不然无法触发按钮事件
    CommonUtil.delayTime(100).then(() => {
      this.inputContentFocus = false
    })
  }

  setFilterData (genderFilter: string, minAge: number, maxAge: number) {
    this.userGender = genderFilter
    this.userMinAge = minAge
    this.userMaxAge = maxAge
    TalkFilterUtil.setFilterData(genderFilter, minAge, maxAge)
  }

  @Action
  getTalkTabs () {
    // this.talkTabs = TalkAPI.queryHomeTalkTabsAPI()
    this.updateCircleByTabIndex()
  }

  updateCircleByTabIndex () {
    const curTab = this.talkTabs.find((item, index) => index === this.currentTabIndex)
    if (curTab.type === TalkTabType.circle_type) {
      socialCircleModule.setCircleName(curTab.name)
    } else {
      socialCircleModule.setCircleName(null)
    }/*
    //不处理，前三个切来切去，不能修改上次使用的
    else {
      this.setCircleName(null)
    }*/
    return curTab
  }

  setCurrentTabIndex (currentTabIndex: number) {
    this.currentTabIndex = currentTabIndex
  }

  setCurTabIndexUpdateCircle (currentTabIndex: number) {
    this.setCurrentTabIndex(currentTabIndex)
    return this.updateCircleByTabIndex()
  }

  //tab选中当前的圈子
  setCircleNameUpdateCurTabIndex (circleName: string) {
    if (circleName) {
      const circleTabIndex = this.talkTabs.findIndex(item => (item.type === TalkTabType.circle_type) && item.name === circleName)
      let circleTab
      if (circleTabIndex > -1) {
        circleTab = this.talkTabs[circleTabIndex]
        //从当前位置删除
        this.talkTabs.splice(circleTabIndex, 1)
      } else {
        circleTab = new TalkTabVO(circleName, TalkTabType.circle_type)
      }
      circleTab.firstLoad = false
      //添加到第四个位置
      this.talkTabs.splice(3, 0, circleTab)
      this.talkTabs = this.talkTabs.slice(0, 9)
      return this.setCurTabIndexUpdateCircle(3)
    }
    return this.setCurTabIndexUpdateCircle(1)
  }

  saveLastTalkTabs (talkTabs: TalkTabVO [], talkTabIndex: number, talkTabType: string) {
    //缓存记录本次推出时的默认值
    // TalkVueUtil.setTalkTabsAll(talkTabs, talkTabIndex, talkTabType)
    if (talkTabs.length) {
      StorageUtil.setObj(TalkVueUtil.TalkTabsKey, talkTabs)
    }
    StorageUtil.setObj(TalkVueUtil.talkTabIndexKey, talkTabIndex)
    StorageUtil.setObj(TalkVueUtil.talkTabTypeKey, talkTabType)
  }

  get curTab () {
    return this.talkTabs[this.currentTabIndex]
  }

  get curTabIsCircle () {
    return this.curTab.type === TalkTabType.circle_type
  }
}
