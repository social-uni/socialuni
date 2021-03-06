package com.socialuni.social.sdk.factory.community;

import com.socialuni.social.entity.model.DO.circle.SocialCircleDO;
import com.socialuni.social.model.model.RO.community.circle.SocialCircleRO;
import com.socialuni.social.model.model.RO.community.tag.TagRO;
import com.socialuni.social.sdk.factory.ListConvertUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SocialCircleROFactory {
    public static SocialCircleRO getCircleRO(SocialCircleDO circleDO) {
        SocialCircleRO circleRO = new SocialCircleRO();
        circleRO.setId(circleDO.getId());
        circleRO.setName(StringUtils.substring(circleDO.getName(), 0, 4));
        circleRO.setCount(circleDO.getCount());
        circleRO.setTalkCount(circleDO.getTalkCount());
        circleRO.setAvatar(circleDO.getAvatar());
        circleRO.setDescription( circleDO.getDescription());
        return circleRO;
    }

    public static List<SocialCircleRO> circleDOToROS(List<SocialCircleDO> DOs) {
        return ListConvertUtil.toList(SocialCircleROFactory::getCircleRO, DOs);
    }
}
