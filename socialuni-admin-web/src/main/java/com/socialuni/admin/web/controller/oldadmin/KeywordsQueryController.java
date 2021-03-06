package com.socialuni.admin.web.controller.oldadmin;

import com.socialuni.admin.web.model.KeywordsDetailVO;
import com.socialuni.admin.web.service.ViolationService;
import com.socialuni.social.api.model.ResultRO;
import com.socialuni.social.constant.CommonStatus;
import com.socialuni.social.constant.ContentStatus;
import com.socialuni.social.constant.ReportStatus;
import com.socialuni.social.entity.model.DO.base.BaseModelDO;
import com.socialuni.social.entity.model.DO.comment.CommentDO;
import com.socialuni.social.entity.model.DO.keywords.KeywordsDO;
import com.socialuni.social.entity.model.DO.keywords.KeywordsTriggerDetailDO;
import com.socialuni.social.entity.model.DO.message.MessageDO;
import com.socialuni.social.entity.model.DO.talk.TalkDO;
import com.socialuni.social.exception.SocialBusinessException;
import com.socialuni.social.sdk.mapper.TalkMapper;
import com.socialuni.social.sdk.repository.*;
import com.socialuni.social.sdk.repository.community.TalkRepository;
import com.socialuni.social.sdk.service.KeywordsService;
import com.socialuni.social.sdk.service.KeywordsTriggerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;


@RestController
@RequestMapping("keywords")
public class KeywordsQueryController {
    @Resource
    private ReportRepository reportRepository;
    @Resource
    private ViolationService violationService;
    @Resource
    private NotifyRepository notifyRepository;
    @Resource
    private KeywordsRepository keywordsRepository;
    @Resource
    private KeywordsTriggerDetailRepository keywordsTriggerDetailRepository;

    @Resource
    private TalkRepository talkRepository;
    @Resource
    private CommentRepository commentRepository;
    @Resource
    private MessageRepository messageRepository;
    @Resource
    private TalkMapper talkMapper;

    @Resource
    private KeywordsService keywordsService;

    @Resource
    private KeywordsTriggerService keywordsTriggerService;

    /*
     *?????????????????????
     *
     *@return*/


    @PostMapping("queryKeywords")
    public ResultRO<List<KeywordsDO>> queryKeywords() {
        //?????????????????????????????????
        List<KeywordsDO> wordDOs = keywordsRepository.findAllByStatusOrderByTextViolateRatioDesc(CommonStatus.enable);

        return new ResultRO<>(wordDOs);
    }

    /* * ????????????
     * ??????????????????????????????????????????
     * <p>
     * ????????????
     * <p>
     * ????????????????????????????????????????????????
     * ??????????????????????????????
     * ??????????????????
     * ?????????????????????
     * <p>
     * ?????????????????????????????? talk???comment???msg???n?????????????????????????????????????????????????????????id??????
     * ??????content
     * ??????????????????????????????????????????????????????????????????????????????
     * ?????????????????????????????????????????????
     * ????????????
     *
     * @param content
     * @return*/


    @PostMapping("queryKeyword")
    public ResultRO<KeywordsDetailVO> queryKeyword(@Valid @NotNull String content, @Valid @NotNull Integer count) {
        content = content.trim();
        if (StringUtils.isEmpty(content)) {
            throw new SocialBusinessException("????????????");
        }
        //??????talk???msg???comment ??? 3w???

        //????????????
        KeywordsDO keywordsDO = new KeywordsDO(content, "");
        //?????????????????????
        List<BaseModelDO> baseModelDOS = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, count);
        Page<TalkDO> talkModels = talkRepository.findByStatusNotInOrderByIdDesc(pageable, ContentStatus.auditStatus);
        Page<CommentDO> commentDOS = commentRepository.findByStatusNotInOrderByIdDesc(pageable, ContentStatus.auditStatus);
        Page<MessageDO> messageDOS = messageRepository.findByStatusNotInOrderByIdDesc(pageable, ContentStatus.auditStatus);

        baseModelDOS.addAll(talkModels.getContent());
        baseModelDOS.addAll(commentDOS.getContent());
        baseModelDOS.addAll(messageDOS.getContent());

        //???????????????
        List<KeywordsTriggerDetailDO> triggerDetailDOS = new ArrayList<>();
        //????????????
        List<KeywordsTriggerDetailDO> vioTriggerDetailDOS = new ArrayList<>();
        //??????????????????????????????
        List<KeywordsDO> keywordsDOS = Collections.singletonList(keywordsDO);
        //??????talk
        for (BaseModelDO modelDO : baseModelDOS) {
            String auditResult = modelDO.getStatus();
            // ??????10w??????????????????keywords
            //????????????keywordsTriggers
            List<KeywordsTriggerDetailDO> keywordsTriggers = keywordsTriggerService
                    .checkContentTriggerKeywords(modelDO, modelDO.getReportContentType(), keywordsDOS, true);

            for (KeywordsTriggerDetailDO keywordsTrigger : keywordsTriggers) {
                //????????????keyword??????????????????keyword
                keywordsService.calculateViolateRatioByReportStatus(auditResult, keywordsTrigger, keywordsDOS.get(0));
            }
            //?????????????????????????????????
            if (auditResult.equals(ReportStatus.violation)) {
                vioTriggerDetailDOS.addAll(keywordsTriggers);
                //???????????????????????????????????????
            } else {
                triggerDetailDOS.addAll(keywordsTriggers);
            }
        }

        //???????????????????????????
        KeywordsDetailVO keywordsDetailVO = new KeywordsDetailVO();
        //?????????????????????????????????db
        Optional<KeywordsDO> optionalViolateWordDO = keywordsRepository.findTopOneByText(content);
        optionalViolateWordDO.ifPresent(keywordsDetailVO::setKeywords);

        keywordsDetailVO.setTempKeywords(keywordsDO);
        keywordsDetailVO.setTriggerDetails(triggerDetailDOS);
        keywordsDetailVO.setVioTriggerDetails(vioTriggerDetailDOS);

        return new ResultRO<>(keywordsDetailVO);
    }


}
