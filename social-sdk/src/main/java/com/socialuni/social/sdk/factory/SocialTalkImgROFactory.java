package com.socialuni.social.sdk.factory;

import com.socialuni.social.entity.model.DO.talk.SocialTalkImgDO;
import com.socialuni.social.exception.SocialParamsException;
import com.socialuni.social.model.model.RO.community.talk.SocialTalkImgRO;
import com.socialuni.social.sdk.config.SocialAppConfig;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SocialTalkImgROFactory {


    public static SocialTalkImgRO newTalkImgRO(SocialTalkImgDO socialTalkImgDO) {
        SocialTalkImgRO socialTalkImgRO = new SocialTalkImgRO();
        socialTalkImgRO.setId(socialTalkImgDO.getId());

        socialTalkImgRO.setSrc(SocialAppConfig.getStaticResourceUrl()  + socialTalkImgDO.getSrc());
        socialTalkImgRO.setAspectRatio(socialTalkImgDO.getAspectRatio());
        return socialTalkImgRO;
    }


    public static List<SocialTalkImgRO> newTalkImgROS(List<SocialTalkImgDO> imgDOS) {
        return imgDOS.stream().map(SocialTalkImgROFactory::newTalkImgRO).collect(Collectors.toList());
    }
}
