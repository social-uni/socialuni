package com.socialuni.demo.controller.socialuni;

import com.socialuni.api.feignAPI.SocialuniCommentAPI;
import com.socialuni.api.model.QO.comment.CenterCommentDeleteQO;
import com.socialuni.api.model.QO.comment.CenterCommentPostQO;
import com.socialuni.api.model.RO.talk.CenterCommentRO;
import com.socialuni.social.api.model.ResultRO;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CommentController implements SocialuniCommentAPI {
    @Resource
    SocialuniCommentAPI socialuniCommentAPI;

    public ResultRO<CenterCommentRO> postComment(CenterCommentPostQO centerCommentPostQO) {
        return socialuniCommentAPI.postComment(centerCommentPostQO);
    }

    public ResultRO<Void> deleteComment(CenterCommentDeleteQO commentDeleteQO) {
        return socialuniCommentAPI.deleteComment(commentDeleteQO);
    }
}