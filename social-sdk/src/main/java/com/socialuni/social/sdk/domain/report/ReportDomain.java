package com.socialuni.social.sdk.domain.report;

import com.socialuni.social.entity.model.DO.talk.TalkDO;
import com.socialuni.social.model.model.QO.community.talk.SocialTalkImgAddQO;
import com.socialuni.social.sdk.config.SocialAppConfig;
import com.socialuni.social.sdk.constant.*;
import com.socialuni.social.sdk.domain.BaseModelService;
import com.socialuni.social.model.model.QO.SocialReportAddQO;
import com.socialuni.social.api.model.ResultRO;
import com.socialuni.social.sdk.constant.config.AppConfigStatic;
import com.socialuni.social.constant.ContentStatus;
import com.socialuni.social.sdk.constant.status.UserStatus;
import com.socialuni.social.sdk.factory.ReportFactory;
import com.socialuni.social.entity.model.DO.ReportDO;
import com.socialuni.social.entity.model.DO.ReportDetailDO;
import com.socialuni.social.entity.model.DO.base.BaseModelDO;
import com.socialuni.social.entity.model.DO.keywords.KeywordsTriggerDetailDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.sdk.repository.*;
import com.socialuni.social.sdk.service.KeywordsService;
import com.socialuni.social.sdk.service.KeywordsTriggerService;
import com.socialuni.social.sdk.utils.QQUtil;
import com.socialuni.social.sdk.utils.SocialUserUtil;
import com.socialuni.social.sdk.utils.WxUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @author qinkaiyuan
 * @date 2020-03-19 20:05
 */
@Service
public class ReportDomain {
    @Resource
    private ReportDetailRepository reportDetailRepository;
    @Resource
    private JusticeValueOrderRepository justiceValueOrderRepository;
    @Resource
    private KeywordsTriggerDetailRepository keywordsTriggerDetailRepository;
    @Resource
    private ReportRepository reportRepository;
    @Resource
    private KeywordsTriggerService keywordsTriggerService;
    @Resource
    private UserRepository userRepository;

    @Resource
    private KeywordsRepository keywordsRepository;

    @Resource
    private KeywordsService keywordsService;

    @Resource
    private ReportFactory reportFactory;
    @Resource
    BaseModelService baseModelService;

    @Transactional
    public void checkImgCreateReport(TalkDO talkDO, List<SocialTalkImgAddQO> imgs) {
        if (imgs.size() > 0) {
            for (SocialTalkImgAddQO img : imgs) {
                String imgFullUrl = SocialAppConfig.getStaticResourceUrl() + img.getSrc();
                WxUtil.checkImgSecPost(imgFullUrl);
                QQUtil.checkImgSecPost(imgFullUrl);
            }
        }
    }

    //根据是否违规生成举报信息，只需要basedo基础信息就够
    @Transactional
    public BaseModelDO checkKeywordsCreateReport(BaseModelDO modelDO) {
        //不为空才校验内容
        if (StringUtils.isNotEmpty(modelDO.getContent())) {
            //网易三方审查
//            AntispamDO antispamDO = WangYiUtil.checkWYContentSecPost(modelDO);

            // 校验是否触发关键词
            List<KeywordsTriggerDetailDO> keywordsTriggers = keywordsTriggerService
                    .checkContentTriggerKeywords(modelDO, modelDO.getReportContentType(), AppConfigStatic.getKeywordDOs(), false);


//            if (!CollectionUtils.isEmpty(keywordsTriggers) || antispamDO.hasViolate()) {
            //如果触发了关键词
            if (!CollectionUtils.isEmpty(keywordsTriggers)) {
                String reportCause;
                ReportDO reportDO;
            /*    if (antispamDO.hasViolate()) {
                    reportCause = antispamDO.getCause();
                    reportDO = reportFactory.createReportDO(reportCause, modelDO, ReportSourceType.antispam, antispamDO.getId());
                } else {*/
                reportCause = "系统自动审查";
                reportDO = reportFactory.createReportDO(reportCause, modelDO, ReportSourceType.systemAutoCheck, modelDO.getDevId());
//                }

                //这里之后才能校验
                // 设置model
                //可以放到 report的 store 中
                //保存数据
                reportDO = reportRepository.save(reportDO);
                //生成举报详情
                ReportDetailDO reportDetailDO =
                        new ReportDetailDO(reportCause, ViolateType.pornInfo, reportDO, modelDO);

                reportDetailRepository.save(reportDetailDO);


                Integer reportId = reportDO.getId();

                if (keywordsTriggers.size() > 0) {
                    //为触发记录关联 report
                    keywordsTriggers.forEach(keywordsTriggerDetailDO -> {
                        keywordsTriggerDetailDO.setReportId(reportId);
                    });

                    //保存触发记录
                    keywordsTriggerDetailRepository.saveAll(keywordsTriggers);
                }


                //更新 model
                modelDO.setStatus(ContentStatus.preAudit);
                modelDO.setUpdateTime(new Date());
                baseModelService.save(modelDO);
            }
        }
        return modelDO;
    }


    @Transactional
    public ResultRO<String> userReportContent(SocialReportAddQO socialReportAddQO, BaseModelDO modelDO, Integer requestUserId, Integer devId) {
        //这里之后才能校验

        // 设置model

        //这里之后才能校验
        // 设置model
        //可以放到 report的 store 中
        ReportDO reportDO = reportFactory.createReportDO(socialReportAddQO.getContent(), modelDO, ReportSourceType.userReport, devId);

        //保存数据
        reportDO = reportRepository.save(reportDO);

        //生成举报详情
        ReportDetailDO reportDetailDO =
                new ReportDetailDO(socialReportAddQO.getContent(), socialReportAddQO.getReportType(), reportDO, modelDO, requestUserId);

        reportDetailRepository.save(reportDetailDO);


        ResultRO<String> resultRO = new ResultRO<>();

        //只有用户举报的才修改用户状态
        Integer receiveUserId = modelDO.getUserId();

        //系统自动审查，则只修改动态为预审查


        //用户举报其他用户的逻辑
        UserDO receiveUser = SocialUserUtil.get(receiveUserId);


        Integer modelReportNum = modelDO.getReportNum() + 1;
        modelDO.setReportNum(modelReportNum);
        //被1个人举报就进入审核中,这里做判断是因为阀值以后可能会调整
        Integer reportCountHide = (Integer) AppConfigConst.appConfigMap.get(AppConfigConst.reportCountHideKey);
        //大于阀值，更改动态和用户状态，否则只增加举报此数
        if (modelReportNum >= reportCountHide) {
            modelDO.setStatus(ContentStatus.audit);
            resultRO.setData(ErrorMsg.reportSubmitHide);

            //如果被举报的用户是官方，则不修改官方的用户状态、只存在于官方自己举报自己时，也不能修改自己的用户状态
            if (!receiveUser.getType().equals(UserType.system)) {
                //只有用户为正常时，才改为待审核，如果用户已被封禁则不改变状态
                if (receiveUser.getStatus().equals(UserStatus.enable)) {
                    receiveUser.setStatus(UserStatus.audit);
                }
            }
            //记录用户的被举报此数
        } else {
            resultRO.setData(ErrorMsg.reportSubmit);
        }
        //todo 存到userDetail表
//        receiveUser.setReportNum(receiveUser.getReportNum() + 1);

        userRepository.save(receiveUser);

        //有关
        //必须要单独保存，涉及到缓存
        //被1个人举报就进入审核中,这里做判断是因为阀值以后可能会调整
        //更新 model
        modelDO.setUpdateTime(new Date());
        baseModelService.save(modelDO);
//     todo  测试，不在这里保存，使用 report 的级联保存是否可以
//        baseModelService.save(modelDO);

        // 校验是否触发关键词
        /*List<KeywordsTriggerDetailDO> keywordsTriggers = keywordsTriggerService
                .checkContentTriggerKeywords(modelDO, ReportContentType.comment);

        String reportCause = "系统自动审查";

        Integer reportId = reportDO.getId();

        //为触发记录关联 report
        keywordsTriggers.forEach(keywordsTriggerDetailDO -> {
            keywordsTriggerDetailDO.setReportId(reportId);
        });

        //保存触发记录
        keywordsTriggerDetailRepository.saveAll(keywordsTriggers);*/

        //必须要单独保存，涉及到缓存
        return resultRO;
    }
}
