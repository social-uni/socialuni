package com.socialuni.sdk.factory;

import com.socialuni.sdk.model.DO.tag.TagDO;
import com.socialuni.social.model.model.RO.community.tag.TagRO;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class SocialTagROFactory {
    public static TagRO getTagRO(TagDO tagDO) {
        TagRO tagRO = new TagRO();
        tagRO.setId(tagDO.getId());
        tagRO.setName(StringUtils.substring(tagDO.getName(), 0, 4));
        tagRO.setCount(tagDO.getCount());
        tagRO.setTalkCount(tagDO.getTalkCount());
        tagRO.setAvatar(tagDO.getAvatar());
        tagRO.setDescription( tagDO.getDescription());
        return tagRO;
    }

    public static List<TagRO> tagDOToROS(List<TagDO> DOs) {
        return ListConvertUtil.toList(SocialTagROFactory::getTagRO, DOs);
    }
}
