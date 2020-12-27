import http from '@/plugins/http'
import ChatReadVO from '@/model/chat/ChatReadVO'
import ChatRemoveVO from '@/model/chat/ChatRemoveVO'
import UserVO from '@/model/user/UserVO'
import ChatVO from '@/model/chat/ChatVO'

export default class ChatAPI {
  static getChatAPI (user: UserVO) {
    return http.post<ChatVO>('chat/queryChat', user)
  }

  static getChatsAPI () {
    return http.post('chat/queryChats')
  }

  static readChat (chatId: number, messageIds: number[]) {
    const chatRead: ChatReadVO = new ChatReadVO(chatId, messageIds)
    return http.post('chat/readChat', chatRead)
  }

  static removeChat (chatId: number) {
    const chat: ChatRemoveVO = new ChatRemoveVO(chatId)
    return http.post('chat/removeChat', chat)
  }

  static openChat (chatId: number, needPayOpen = false) {
    return http.post<ChatVO>('chat/openChat', { id: chatId, needPayOpen })
  }
}
