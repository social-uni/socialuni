package com.socialuni.social.sdk.domain;

import com.socialuni.social.entity.model.DO.ReportDO;
import com.socialuni.social.entity.model.DO.base.BaseModelDO;
import com.socialuni.social.entity.model.DO.comment.CommentDO;
import com.socialuni.social.entity.model.DO.message.MessageDO;
import com.socialuni.social.entity.model.DO.talk.TalkDO;
import com.socialuni.social.entity.model.DO.user.UserImgDO;
import com.socialuni.social.exception.SocialBusinessException;
import com.socialuni.social.sdk.repository.*;
import com.socialuni.social.sdk.repository.community.TalkRepository;
import com.socialuni.social.sdk.utils.TalkRedis;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class BaseModelService {
    @Resource
    private TalkRepository talkRepository;
    @Resource
    private CommentRepository commentRepository;
    @Resource
    private UserImgRepository userImgRepository;
    @Resource
    private MessageRepository messageRepository;
    @Resource
    private ReportRepository reportRepository;
    @Resource
    private TalkRedis talkRedis;

    public BaseModelDO save(BaseModelDO model) {
        if (model instanceof TalkDO) {
            TalkDO talkDO = (TalkDO) model;
            return talkRedis.save(talkDO);
        } else if (model instanceof CommentDO) {
            CommentDO commentDO = (CommentDO) model;
            return commentRepository.save(commentDO);
        } else if (model instanceof MessageDO) {
            MessageDO messageDO = (MessageDO) model;
            return messageRepository.save(messageDO);
        } else {
            throw new SocialBusinessException("错误的内容类型");
        }
    }

    public Optional<ReportDO> findReportByModel(BaseModelDO model) {
        if (model instanceof TalkDO) {
            TalkDO talkDO = (TalkDO) model;
            return reportRepository.findFirstOneByTalkId(talkDO.getId());
        } else if (model instanceof CommentDO) {
            CommentDO commentDO = (CommentDO) model;
            return reportRepository.findFirstOneByCommentId(commentDO.getId());
        } else if (model instanceof MessageDO) {
            MessageDO messageDO = (MessageDO) model;
            return reportRepository.findFirstOneByMessageId(messageDO.getId());
        } else if (model instanceof UserImgDO) {
            UserImgDO userImgDO = (UserImgDO) model;
            return reportRepository.findFirstOneByUserImgId(userImgDO.getId());
        } else {
            throw new SocialBusinessException("错误的内容类型");
        }
    }
}
