package com.socialuni.social.model.model.QO.community;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * TODO〈一句话功能简述〉
 * TODO〈功能详细描述〉
 *
 * @author qinkaiyuan
 * @since TODO[起始版本号]
 */
@Data
public class FollowAddVO {
    /**
     * 被关注的用户id
     */
    @NotNull(message = "入参为空异常")
    private Integer beUserId;
}
