package com.socialuni.admin.web.controller.oldadmin;


import com.socialuni.admin.web.service.ViolationService;
import com.socialuni.social.api.model.ResultRO;
import com.socialuni.social.constant.CommonStatus;
import com.socialuni.social.entity.model.DO.keywords.KeywordsDO;
import com.socialuni.social.exception.SocialBusinessException;
import com.socialuni.social.sdk.constant.status.ConstBoolean;
import com.socialuni.social.sdk.mapper.TalkMapper;
import com.socialuni.social.sdk.repository.KeywordsRepository;
import com.socialuni.social.sdk.repository.KeywordsTriggerDetailRepository;
import com.socialuni.social.sdk.repository.NotifyRepository;
import com.socialuni.social.sdk.repository.ReportRepository;
import com.socialuni.social.sdk.repository.community.TalkRepository;
import com.socialuni.social.sdk.store.TalkQueryStore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("keywords")
public class KeywordsManageController {
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
    private TalkQueryStore talkQueryStore;
    @Resource
    private TalkMapper talkMapper;


    @PostMapping("addKeywords")
    public ResultRO<KeywordsDO> addKeywords(@Valid @NotNull String content, String cause) {
        content = content.trim();
        if (StringUtils.isEmpty(content)) {
            throw new SocialBusinessException("????????????");
        }
        Optional<KeywordsDO> optionalViolateWordDO = keywordsRepository.findTopOneByText(content);
        if (optionalViolateWordDO.isPresent()) {
            throw new SocialBusinessException("???????????????");
        }
        KeywordsDO keywordsDO = new KeywordsDO(content, cause);

        keywordsRepository.save(keywordsDO);
        return ResultRO.success(keywordsDO);
    }

    @PostMapping("closeKeywords")
    public ResultRO<KeywordsDO> closeKeywords(Integer id, String closeCause) {
        //???????????????
        Optional<KeywordsDO> optionalViolateWordDO = keywordsRepository.findById(id);
        //??????
        if (!optionalViolateWordDO.isPresent()) {
            throw new SocialBusinessException("?????????");
        }

        KeywordsDO keywordsDO = optionalViolateWordDO.get();
        keywordsDO = getKeywordsDOResultVO(keywordsDO, closeCause);
        keywordsRepository.save(keywordsDO);
        return ResultRO.success(keywordsDO);
    }

    private KeywordsDO getKeywordsDOResultVO(KeywordsDO keywordsDO, String closeCause) {

        keywordsDO.setStatus(CommonStatus.delete);
        keywordsDO.setOpenPinyin(ConstBoolean.close);
        keywordsDO.setOpenText(ConstBoolean.close);

        keywordsDO.setDeleteCause(closeCause);
        keywordsDO.setUpdateTime(new Date());
        return keywordsDO;
    }


    @PostMapping("openPinyinOrText")
    public ResultRO<KeywordsDO> openPinyinOrText(Integer id, String type, String cause) {
        //???????????????
        Optional<KeywordsDO> optionalViolateWordDO = keywordsRepository.findById(id);
        //??????
        if (!optionalViolateWordDO.isPresent()) {
            throw new SocialBusinessException("?????????");
        }

        KeywordsDO keywordsDO = getKeywordsDOResultVO(optionalViolateWordDO.get(), type, cause);
        keywordsRepository.save(keywordsDO);
        return ResultRO.success(keywordsDO);
    }

    //    @GetMapping("batchClosePinyinOrTexts"),?????????????????????
    public ResultRO<KeywordsDO> batchOpenPinyinOrTexts() {
        //???????????????
        List<KeywordsDO> optionalViolateWordDOs = keywordsRepository.findAllByStatusAndOpenPinyinIsTrueAndPinyinNormalNumGreaterThanAndPinyinViolateRatioLessThan(CommonStatus.enable, 19, 0.4);

        for (KeywordsDO keywordsDO : optionalViolateWordDOs) {
            getKeywordsDOResultVO(keywordsDO, "pinyin", "????????????");
        }

        keywordsRepository.saveAll(optionalViolateWordDOs);
        return ResultRO.success();
    }

    private KeywordsDO getKeywordsDOResultVO(KeywordsDO keywordsDO, String type, String cause) {
        //??????
        if (type.equals("text")) {
            keywordsDO.setOpenText(!keywordsDO.getOpenText());
            //??????
            if (keywordsDO.getOpenText()) {
                //????????????????????????
                keywordsDO.setReopenTextCause(cause);
            } else {
                //??????
                keywordsDO.setCloseTextCause(cause);
            }
        } else {
            //??????????????????
            keywordsDO.setOpenPinyin(!keywordsDO.getOpenPinyin());
            //??????
            if (keywordsDO.getOpenPinyin()) {
                //????????????????????????
                keywordsDO.setReopenPinyinCause(cause);
            } else {
                //??????
                //??????????????????
                keywordsDO.setClosePinyinCause(cause);
            }
        }

        //??????????????????????????????????????????
        if (keywordsDO.getOpenText() && keywordsDO.getOpenPinyin()) {
            //??????????????????????????????
            keywordsDO.setViolateNum(keywordsDO.getTextViolateNum() + keywordsDO.getPinyinViolateNum());
            keywordsDO.setNormalNum(keywordsDO.getTextNormalNum() + keywordsDO.getPinyinNormalNum());
            keywordsDO.setTotalNum(keywordsDO.getViolateNum() + keywordsDO.getNormalNum());

            //?????????????????????
            keywordsDO.setViolateRatio(keywordsDO.getViolateNum().doubleValue() / keywordsDO.getTotalNum().doubleValue());
            keywordsDO.setNormalRatio(1 - keywordsDO.getViolateRatio());
        } else if (keywordsDO.getOpenText()) {
            //????????????text
            keywordsDO.setViolateNum(keywordsDO.getTextViolateNum());
            keywordsDO.setNormalNum(keywordsDO.getTextNormalNum());
            keywordsDO.setTotalNum(keywordsDO.getTextTotalNum());
            //????????????????????????????????????????????????
            keywordsDO.setViolateRatio(keywordsDO.getTextViolateRatio());
            keywordsDO.setNormalRatio(keywordsDO.getTextNormalRatio());
        } else if (keywordsDO.getOpenPinyin()) {
            //??????????????????????????????????????????
            keywordsDO.setViolateNum(keywordsDO.getPinyinViolateNum());
            keywordsDO.setNormalNum(keywordsDO.getPinyinNormalNum());
            keywordsDO.setTotalNum(keywordsDO.getPinyinTotalNum());
            //????????????????????????????????????????????????
            keywordsDO.setViolateRatio(keywordsDO.getPinyinViolateRatio());
            keywordsDO.setNormalRatio(keywordsDO.getPinyinNormalRatio());
        } else {
            return getKeywordsDOResultVO(keywordsDO, cause);
        }
        keywordsDO.setUpdateTime(new Date());
        return keywordsDO;
    }

}
