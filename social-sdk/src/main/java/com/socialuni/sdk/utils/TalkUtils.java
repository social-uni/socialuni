package com.socialuni.sdk.utils;

import com.socialuni.sdk.exception.SocialParamsException;
import com.socialuni.sdk.model.DO.talk.TalkDO;
import com.socialuni.sdk.repository.TalkRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TalkUtils {
    private static TalkRepository talkRepository;

    @Resource
    public void setTalkRepository(TalkRepository talkRepository) {
        TalkUtils.talkRepository = talkRepository;
    }

    public static TalkDO get(Integer talkId) {
        TalkDO talkDO = talkRepository.findOneById(talkId);
        if (talkDO == null){
            throw new SocialParamsException("不存在的动态内容");
        }
        return talkDO;
    }
}
