package com.socialuni.social.sdk.service.comment;


import com.socialuni.social.sdk.constant.ErrorMsg;
import com.socialuni.social.sdk.constant.config.AppConfigStatic;
import com.socialuni.social.exception.SocialBusinessException;
import com.socialuni.social.entity.model.DO.keywords.IllegalWordDO;
import com.socialuni.social.sdk.repository.IllegalWordRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IllegalWordService {

    @Resource
    IllegalWordRepository illegalWordRepository;

    public void checkHasIllegals(String content) {
        List<IllegalWordDO> illegals = AppConfigStatic.getIllegals();

        List<IllegalWordDO> triggerWords = new ArrayList<>();

        StringBuilder words = new StringBuilder();

        //如果有触发违规关键词
        for (IllegalWordDO illegal : illegals) {
            String wordText = illegal.getWord();
            //关键词不为空，且包含
            if (StringUtils.isNotEmpty(wordText) && StringUtils.containsIgnoreCase(content, wordText)) {
                illegal.setTriggerCount(illegal.getTriggerCount() + 1);
                illegal.setUpdateTime(new Date());
                words.append(illegal.getWord()).append("，");
                triggerWords.add(illegal);
            }
        }
        //保存触发的，更新他们的次数
        if (triggerWords.size() > 0) {
            illegalWordRepository.saveAll(triggerWords);
            String wordMsg = words.substring(0, words.length() - 1);
            String errorMsg = MessageFormat.format(ErrorMsg.illegalWordMsg, wordMsg);
            throw new SocialBusinessException(errorMsg);
        }
    }
}
