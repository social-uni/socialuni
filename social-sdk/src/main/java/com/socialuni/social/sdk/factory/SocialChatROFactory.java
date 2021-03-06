package com.socialuni.social.sdk.factory;


import com.socialuni.social.constant.ChatStatus;
import com.socialuni.social.constant.ChatType;
import com.socialuni.social.constant.ChatUserStatus;
import com.socialuni.social.constant.MessageStatus;
import com.socialuni.social.entity.model.DO.chat.ChatDO;
import com.socialuni.social.entity.model.DO.chat.ChatUserDO;
import com.socialuni.social.entity.model.DO.message.MessageDO;
import com.socialuni.social.entity.model.DO.message.MessageReceiveDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.model.model.RO.message.chat.ChatRO;
import com.socialuni.social.model.model.RO.message.message.SocialMessageRO;
import com.socialuni.social.sdk.constant.CommonConst;
import com.socialuni.social.sdk.constant.LoadMoreType;
import com.socialuni.social.sdk.manage.FollowManage;
import com.socialuni.social.sdk.repository.ChatRepository;
import com.socialuni.social.sdk.repository.MessageReceiveRepository;
import com.socialuni.social.sdk.repository.MessageRepository;
import com.socialuni.social.sdk.utils.SocialUserUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qinkaiyuan
 * @date 2018-11-18 20:48
 */
@Component
@Data//chatdo+chatUserdo
public class SocialChatROFactory {
    private static MessageReceiveRepository messageReceiveRepository;

    private static MessageRepository messageRepository;

    private static ChatRepository chatRepository;

    private static FollowManage followManage;

    @Resource
    public void setFollowManage(FollowManage followManage) {
        SocialChatROFactory.followManage = followManage;
    }

    @Resource
    public void setMessageReceiveRepository(MessageReceiveRepository messageReceiveRepository) {
        SocialChatROFactory.messageReceiveRepository = messageReceiveRepository;
    }

    @Resource
    public void setMessageRepository(MessageRepository messageRepository) {
        SocialChatROFactory.messageRepository = messageRepository;
    }

    @Resource
    public void setChatRepository(ChatRepository chatRepository) {
        SocialChatROFactory.chatRepository = chatRepository;
    }

    public SocialChatROFactory() {
    }

    //chat

    public static ChatRO getChatRO(ChatDO chatDO) {
        ChatRO chatRO = new ChatRO();

        chatRO.setId(chatDO.getId());
        chatRO.setType(chatDO.getType());
        if (ChatType.systemChats.contains(chatRO.getType())) {
            chatRO.setNickname(chatDO.getChatName());
            chatRO.setAvatar(chatDO.getAvatar());
        }
        chatRO.setTopFlag(false);
        chatRO.setStatus(chatDO.getStatus());
        chatRO.setUpdateTime(chatDO.getUpdateTime());
        chatRO.setTopLevel(chatDO.getTopLevel());
        chatRO.setUnreadNum(0);
        chatRO.setMessages(new ArrayList<>());
        chatRO.setLoadMore(LoadMoreType.noMore);
        chatRO.setNeedPayOpen(false);
        chatRO.setLastContent("会话已开启");
        return chatRO;
    }

    //初始查询的时候为99
    //用户未登录的情况，和群聊的情况会触发这里
    public static ChatRO getChatRO(ChatDO chatDO, Integer userId) {
        ChatRO chatRO = SocialChatROFactory.getChatRO(chatDO);
        //查询用户这个chatUser下的消息
        //已经确认过chat为可用的
        List<MessageDO> messageDOS = messageRepository.findTop31ByChatIdAndStatusAndIdNotInOrderByIdDesc(chatDO.getId(), ChatStatus.enable, CommonConst.emptyIds);
        if (messageDOS.size() > 30) {
            messageDOS.subList(1, 31);
            chatRO.setLoadMore(LoadMoreType.more);
        }
        List<SocialMessageRO> messageROS = SocialMessageROFactory.messageDOToVOS(messageDOS, userId);
        //最后一个的content
        if (messageROS.size() > 0) {
            String lastContent = messageROS.get(messageROS.size() - 1).getContent();
            chatRO.setLastContent(lastContent);
        }
        return chatRO;
    }

    //初始查询的时候为99
    /*public ChatVO(ChatDO chatDO, Integer userId) {
        this(chatDO);
        //查询用户这个chatUser下的消息
        //已经确认过chat为可用的
//        List<MessageDO> messageDOS = messageRepository.findTop30ByChatAndChatStatusAndStatusInOrderByCreateTimeDescIdDesc(chatDO, CommonStatus.normal, CommonStatus.otherCanSeeContentStatus);
        List<MessageDO> messageDOS = new ArrayList<>();
        this.messages = MessageVO.messageDOToVOS(messageDOS, userId);
    }*/


    //chatuser
    //推消息时，查列表时  的基础
    public static ChatRO getChatRO(ChatDO chat, ChatUserDO chatUserDO) {
        ChatRO chatRO = SocialChatROFactory.getChatRO(chat);

        //暂时不支持删除系统群聊
        //不使用chatUserDO的id是因为 推送message时，需要赋值chatVoId，此时获取chatUserDO的id比较麻烦。每次推送都要读一次数据库，不如在操作chatUser时读取一次好
        //可以使用了是因为，解决了notifyvo获取chatUserId的问题
        //不能使用，因为发送消息时，不知道这个chatId是chatUser还是chat，统一按chat处理
//        this.id = chatUserDO.getId();
        //根据类型区分不同nick和ava
        //如果群聊则直接使用
        if (!ChatType.systemChats.contains(chatRO.getType())) {
            UserDO receiveUser = SocialUserUtil.get(chatUserDO.getReceiveUserId());

            chatRO.setNickname(receiveUser.getNickname());
            chatRO.setNickname(receiveUser.getAvatar());
            chatRO.setReceiveUserId(receiveUser.getId());
            chatRO.setUnreadNum(chatUserDO.getUnreadNum());
//            this.vipFlag = receiveUser.getVipFlag();
            //不为系统群聊才有记录了未读数量，才有未读数量
        }
        chatRO.setTopFlag(chatUserDO.getTopFlag());
        chatRO.setUpdateTime(chatUserDO.getUpdateTime());
        chatRO.setStatus(chatUserDO.getStatus());
        //chat的最后一条消息时间大家都一样，把最后一条删除也是最后一条的时间
        //只有为待开启才判断是否需要支付开启
        if (chatRO.getStatus().equals(ChatUserStatus.waitOpen)) {
            chatRO.setLastContent("会话待开启");
            //查询对方是否关注了自己，只有未关注的情况，才能支付
            boolean hasFollow = followManage.userHasFollowBeUser(chatRO.getReceiveUserId(), chatUserDO.getUserId());
            //只在这一个地方判断，只有私聊的时候，且只有私聊的这里才会触发
            chatRO.setNeedPayOpen(!hasFollow);
        }
        return chatRO;
    }

    //3个地方使用，开启会话时，如果是一个已关闭的则获取之前的，所以需要获取列表
    //初始查询列表时需要列表，
    // 还有查看用户详情页面,查看时有时候不为已开启，所以需要判断
    public static ChatRO getChatRO(ChatDO chatDO, ChatUserDO chatUserDO, boolean queryMsgFlag) {
        ChatRO chatRO = SocialChatROFactory.getChatRO(chatDO, chatUserDO);
        //系统群聊读取message表
        //查询用户这个chatUser下的消息
        List<MessageReceiveDO> messageReceiveDOS = messageReceiveRepository.findTop31ByChatUserIdAndChatUserStatusAndStatusAndMessageIdNotInOrderByIdDesc(chatUserDO.getId(), ChatUserStatus.enable, MessageStatus.enable, CommonConst.emptyIds);
        if (messageReceiveDOS.size() > 30) {
            messageReceiveDOS.subList(1, 31);
            chatRO.setLoadMore(LoadMoreType.more);
        }
        List<SocialMessageRO> messages = SocialMessageROFactory.messageReceiveDOToVOS(messageReceiveDOS);
        //最后一个的content
        if (messages.size() > 0) {
            String lastContent = messages.get(messages.size() - 1).getContent();
            chatRO.setLastContent(lastContent);
            chatRO.setLoadMore(LoadMoreType.more);
        }
        return chatRO;
    }


    public static List<ChatRO> chatUserDOToVOS(List<ChatUserDO> chatUsers) {
        //查询的时候chat列表展示不为当前用户的
        return chatUsers.stream().map((ChatUserDO chatUserDO) -> {
            ChatDO chat = chatUserDO.getChat();
            if (chat.getType().equals(ChatType.system_group)) {
                return SocialChatROFactory.getChatRO(chat, chatUserDO.getUserId());
            } else {
                return SocialChatROFactory.getChatRO(chat, chatUserDO, true);
            }
        }).collect(Collectors.toList());
    }

    //用户未登陆时
    public static List<ChatRO> chatDOToVOS(List<ChatDO> chats) {
        Integer userId = null;
        //查询的时候chat列表展示不为当前用户的
        return chats.stream().map((ChatDO chatDO) -> SocialChatROFactory.getChatRO(chatDO, userId)).collect(Collectors.toList());
    }

    /*public static List<ChatVO> chatDOToVOS(List<ChatDO> chats, Integer userId) {
        //查询的时候chat列表展示不为当前用户的
        return chats.stream().map((ChatDO chatDO) -> new ChatVO(chatDO, userId)).collect(Collectors.toList());
    }*/


    //推送部分

    //给用户推送消息
    public static ChatRO getChatRO(ChatDO chat, MessageDO messageDO) {
        ChatRO chatRO = SocialChatROFactory.getChatRO(chat);
        //没user ，没记录未读数量，所以设置为1
        chatRO.setUnreadNum(1);
        List<SocialMessageRO> messageROS = Collections.singletonList(SocialMessageROFactory.getMessageRO(messageDO, null));
        chatRO.setMessages(messageROS);
        chatRO.setLastContent(chatRO.getMessages().get(0).getContent());
        return chatRO;
    }

    //推送单个消息的chat，推送单个消息
    public static ChatRO getChatRO(ChatDO chat, ChatUserDO chatUser, MessageReceiveDO messageReceiveDO) {
        ChatRO chatRO = SocialChatROFactory.getChatRO(chat, chatUser);
        List<SocialMessageRO> messageROS = Collections.singletonList(SocialMessageROFactory.getMessageRO(messageReceiveDO));
        chatRO.setMessages(messageROS);
        chatRO.setLastContent(chatRO.getMessages().get(0).getContent());
        return chatRO;
        //todo 不能推送所有未读的，因为有些未读的可能已经推送过了，但用户没看而已，再推送就会重复，解决这个问题需要标识哪些是已经推送过了，websocket中记录，目前前台通过重连充重新查询chats解决
//        List<MessageReceiveDO> messageReceiveDOS = messageReceiveRepository.findByChatUserAndMessageStatusAndReceiveUserAndStatusAndIsReadFalseOrderByCreateTimeDescIdDesc(messageReceiveDO.getChatUser(), CommonStatus.enable, messageReceiveDO.getReceiveUser(), CommonStatus.enable);
//        this.messages = MessageVO.messageDOToVOS(messageReceiveDOS);
    }

}
