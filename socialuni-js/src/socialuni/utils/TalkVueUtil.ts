import StorageUtil from './StorageUtil'
import TalkTabVO from '../model/talk/TalkTabVO'
import TalkTabType from '../constant/TalkTabType'

const talkTabFollowDefault = new TalkTabVO()
talkTabFollowDefault.name = TalkTabType.follow_name
talkTabFollowDefault.type = TalkTabType.follow_type

const talkTabHomeDefault = new TalkTabVO()
talkTabHomeDefault.name = TalkTabType.home_name
talkTabHomeDefault.type = TalkTabType.home_type

const talkTabCityDefault = new TalkTabVO()
talkTabCityDefault.name = TalkTabType.city_name
talkTabCityDefault.type = TalkTabType.city_type

export default class TalkVueUtil {
  static readonly TalkTabsKey: string = 'talkTabs'
  static readonly TalkTabsDefault: TalkTabVO [] = [
    new TalkTabVO(TalkTabType.follow_name, TalkTabType.follow_type),
    new TalkTabVO(TalkTabType.home_name, TalkTabType.home_type),
    new TalkTabVO(TalkTabType.city_name, TalkTabType.city_type),
    new TalkTabVO('处对象', TalkTabType.circle_type),
    new TalkTabVO('闺蜜', TalkTabType.circle_type),
    new TalkTabVO('扩列', TalkTabType.circle_type),
    new TalkTabVO('生活', TalkTabType.circle_type)
  ]


  static readonly talkTabIndexKey: string = 'talkTabIndex'
  static readonly talkTabIndexDefault: number = 1

  static readonly talkTabTypeKey: string = 'talkTabType'
  static readonly talkTabTypeDefault: string = TalkTabType.home_type


  static getTalkTabs (): TalkTabVO [] {
    const homeTypeTalks: TalkTabVO [] = StorageUtil.getObj(TalkVueUtil.TalkTabsKey) || TalkVueUtil.TalkTabsDefault
    if (homeTypeTalks.length < 4) {
      return TalkVueUtil.TalkTabsDefault
    }
    return homeTypeTalks
  }

  static getCurTalkTabIndex (): number {
    let index = StorageUtil.getObj(TalkVueUtil.talkTabIndexKey)
    if (index !== 0) {
      index = index || TalkVueUtil.talkTabIndexDefault
    }
    return index
  }
}
