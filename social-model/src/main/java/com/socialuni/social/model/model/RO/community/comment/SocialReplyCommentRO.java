package com.socialuni.social.model.model.RO.community.comment;

import com.socialuni.social.model.model.RO.user.base.SocialUserRO;
import lombok.Data;

@Data
public class SocialReplyCommentRO {
    private Integer id;
    //如果这个评论 有parent，就代表已经是一个子评论，就不用把他设置为parent而是用它的parentId，他是否有parent
//    private Integer no;

    private SocialUserRO user;
}
