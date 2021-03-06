package com.socialuni.social.sdk.factory;

import com.socialuni.social.sdk.factory.user.base.SocialUserROFactory;
import com.socialuni.social.model.model.RO.community.comment.SocialCommentRO;
import com.socialuni.social.constant.ContentStatus;
import com.socialuni.social.sdk.dao.CommentDao;
import com.socialuni.social.entity.model.DO.comment.CommentDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.sdk.repository.CommentRepository;
import com.socialuni.social.sdk.utils.SocialUserUtil;
import com.socialuni.social.utils.SystemUtil;
import com.socialuni.social.model.model.RO.user.base.SocialUserRO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SocialCommentROFactory {
    private static CommentRepository commentRepository;
    private static CommentDao commentDao;

    @Resource
    public void setCommentDao(CommentDao commentDao) {
        SocialCommentROFactory.commentDao = commentDao;
    }

    @Resource
    public void setCommentRepository(CommentRepository commentRepository) {
        SocialCommentROFactory.commentRepository = commentRepository;
    }

    public static SocialCommentRO newTalkCommentRO(UserDO mineUser, CommentDO comment, boolean showAll) {
        SocialCommentRO socialCommentRO = new SocialCommentRO();
        socialCommentRO.setId(comment.getId());
        socialCommentRO.setNo(comment.getNo());

        UserDO commentUser = SocialUserUtil.get(comment.getUserId());
        SocialUserRO commentUserRO = SocialUserROFactory.getUserRO(commentUser);
        socialCommentRO.setUser(commentUserRO);

        socialCommentRO.setContent(comment.getContent());
        socialCommentRO.setHugNum(comment.getHugNum());
        socialCommentRO.setReportNum(comment.getReportNum());
        socialCommentRO.setCreateTime(comment.getCreateTime());
        socialCommentRO.setChildCommentNum(comment.getChildCommentNum());

        List<SocialCommentRO> socialCommentROS = SocialCommentROFactory.getCommentChildCommentROs(mineUser, comment.getId(), showAll);
        socialCommentRO.setChildComments(socialCommentROS);

        log.debug("??????????????????" + SystemUtil.getCurrentTimeSecond());
        if (!ObjectUtils.isEmpty(comment.getReplyCommentId())) {
            socialCommentRO.setReplyComment(ReplyCommentROFactory.newReplyCommentRO(comment.getReplyCommentId()));
        }
        return socialCommentRO;
    }

    public static List<SocialCommentRO> getTalkCommentROs(UserDO mineUser, Integer talkId, Boolean showAllComment) {
        //10??????
        log.debug("????????????comment" + new Date().getTime() / 1000);
        List<CommentDO> commentDOS;
        if (showAllComment) {
            commentDOS = commentDao.queryTalkDetailComments(talkId);
        } else {
            commentDOS = commentDao.queryTalkComments(talkId);
        }
        List<SocialCommentRO> commentVOS = SocialCommentROFactory.newTalkCommentVOs(mineUser, commentDOS, showAllComment);
        log.debug("????????????comment??????" + new Date().getTime() / 1000);
        return commentVOS;
    }

    public static List<SocialCommentRO> getCommentChildCommentROs(UserDO mineUser, Integer commentId, Boolean showAllComment) {
        //10??????
        log.debug("????????????comment" + new Date().getTime() / 1000);
        List<CommentDO> commentDOS;
        if (showAllComment) {
            commentDOS = commentDao.queryCommentDetailChildComments(commentId);
        } else {
            commentDOS = commentDao.queryCommentChildComments(commentId);
        }
        List<SocialCommentRO> commentVOS = SocialCommentROFactory.newTalkCommentVOs(mineUser, commentDOS, showAllComment);
        log.debug("????????????comment??????" + new Date().getTime() / 1000);
        return commentVOS;
    }


    private static List<SocialCommentRO> newTalkCommentVOs(UserDO mineUser, List<CommentDO> commentDOS, boolean showAll) {
        return commentDOS.stream()
                //?????????????????????????????????????????????
                .filter(talkCommentDO -> {
                    // ???????????? null && ????????????????????????
                    return (mineUser != null && talkCommentDO.getUserId().equals(mineUser.getId()))
                            //????????????????????????????????????
                            || !ContentStatus.preAudit.equals(talkCommentDO.getStatus());
                })
                .map(talkCommentDO -> SocialCommentROFactory.newTalkCommentRO(mineUser, talkCommentDO, showAll)).collect(Collectors.toList());
    }
}
