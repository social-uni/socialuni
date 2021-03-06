package com.socialuni.social.sdk.service.comment;


import com.socialuni.social.sdk.domain.comment.SocialCommentDeleteDomain;
import com.socialuni.social.sdk.domain.comment.SocialCommentPostDomain;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.sdk.utils.SocialUserUtil;
import com.socialuni.social.model.model.QO.community.comment.SocialCommentDeleteQO;
import com.socialuni.social.model.model.QO.community.comment.SocialCommentPostQO;
import com.socialuni.social.api.model.ResultRO;
import com.socialuni.social.model.model.RO.community.comment.SocialCommentRO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qinkaiyuan
 * @date 2020-05-16 20:11
 */
@Service
@Slf4j
public class SocialCommentService {
    @Resource
    SocialCommentPostDomain socialPostCommentDomain;
    @Resource
    SocialCommentDeleteDomain socialCommentDeleteDomain;

    public ResultRO<SocialCommentRO> postComment(SocialCommentPostQO socialCommentPostQO) {
        UserDO mineUser = SocialUserUtil.getMineUserAllowNull();

        SocialCommentRO socialCommentRO = socialPostCommentDomain.postComment(mineUser, socialCommentPostQO);

        return new ResultRO<>(socialCommentRO);
    }

    public ResultRO<Void> deleteComment(SocialCommentDeleteQO commentDeleteQO) {
        UserDO mineUser = SocialUserUtil.getMineUserAllowNull();
        socialCommentDeleteDomain.deleteComment(mineUser, commentDeleteQO);
        return new ResultRO<>();
    }
}
