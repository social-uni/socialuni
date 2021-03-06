package com.socialuni.social.sdk.domain.talk;

import com.socialuni.social.constant.CommonStatus;
import com.socialuni.social.constant.GenderType;
import com.socialuni.social.entity.model.DO.DistrictDO;
import com.socialuni.social.entity.model.DO.circle.SocialCircleDO;
import com.socialuni.social.entity.model.DO.tag.TagDO;
import com.socialuni.social.entity.model.DO.talk.SocialTalkCircleDO;
import com.socialuni.social.entity.model.DO.talk.SocialTalkImgDO;
import com.socialuni.social.entity.model.DO.talk.SocialTalkTagDO;
import com.socialuni.social.entity.model.DO.talk.TalkDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.exception.SocialBusinessException;
import com.socialuni.social.exception.SocialParamsException;
import com.socialuni.social.model.model.QO.community.talk.SocialTalkPostQO;
import com.socialuni.social.model.model.RO.community.talk.SocialTalkRO;
import com.socialuni.social.sdk.constant.CommonConst;
import com.socialuni.social.sdk.constant.TalkOperateType;
import com.socialuni.social.sdk.domain.report.ReportDomain;
import com.socialuni.social.sdk.factory.SocialTalkROFactory;
import com.socialuni.social.sdk.factory.TalkImgDOFactory;
import com.socialuni.social.sdk.manage.talk.SocialTalkCreateManage;
import com.socialuni.social.sdk.model.TalkAddValidateRO;
import com.socialuni.social.sdk.repository.community.*;
import com.socialuni.social.sdk.service.content.ModelContentCheck;
import com.socialuni.social.sdk.service.tag.TagService;
import com.socialuni.social.sdk.utils.DistrictStoreUtils;
import com.socialuni.social.sdk.utils.TalkRedis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SocialTalkPostDomain {
    @Resource
    TalkRepository talkRepository;
    @Resource
    TagService tagService;

    @Resource
    TagRepository tagRepository;
    @Resource
    ReportDomain reportDomain;
    @Resource
    TalkRedis talkRedis;
    @Resource
    TalkTagRepository talkTagRepository;
    @Resource
    SocialCircleRepository socialCircleRepository;
    @Resource
    TalkImgRepository talkImgRepository;
    @Resource
    SocialTalkCreateManage socialTalkCreateManage;
    @Resource
    SocialTalkCircleRepository socialTalkCircleRepository;
    @Resource
    ModelContentCheck modelContentCheck;

    public SocialTalkRO postTalk(UserDO mineUser, SocialTalkPostQO talkPostQO) {
        modelContentCheck.checkUserAndLongContent(talkPostQO.getContent(), mineUser);


        //????????????????????????
//        modelContentCheck.checkUserAndContent(addVO.getContent(), requestUser);
        //???????????????????????????
        TalkAddValidateRO talkAddValidateRO = this.paramsValidate(mineUser, talkPostQO);
        TalkDO talkDO = this.saveEntity(mineUser, talkPostQO, talkAddValidateRO.getDistrict(), talkAddValidateRO.getTags(), talkAddValidateRO.getCircle());
        reportDomain.checkKeywordsCreateReport(talkDO);
        //???????????????????????????
        //        reportDomain.checkImgCreateReport(talkDO, talkPostQO.getImgs());
        SocialTalkRO socialTalkRO = SocialTalkROFactory.getTalkRO(talkDO, mineUser);
        return socialTalkRO;
    }


    public TalkAddValidateRO paramsValidate(UserDO mineUser, SocialTalkPostQO talkVO) {

        //??????????????????
        String adCode = talkVO.getAdCode();
        //????????????????????????????????????adcode?????????????????????????????????
        if (adCode == null) {
            adCode = CommonConst.chinaDistrictCode;
        }
        //??????adcode???????????????
        DistrictDO districtDO = DistrictStoreUtils.findFirstOneByAdCode(adCode);
        if (districtDO == null) {
            throw new SocialParamsException("???????????????????????????");
        }

        String talkVisibleGender = talkVO.getVisibleGender();
        //??????????????????????????? ?????????DO
        //????????????
        List<Integer> tagIds = talkVO.getTagIds();

        if (tagIds == null) {
            tagIds = new ArrayList<>();
        }
        List<String> tagNames = talkVO.getTagNames();
        for (String tagName : tagNames) {
            TagDO tagDO = tagRepository.findFirstByName(tagName);
            if (tagDO == null || !tagDO.getStatus().equals(CommonStatus.enable)) {
                throw new SocialBusinessException("????????????????????????");
            }
            tagIds.add(tagDO.getId());
        }
        talkVO.setTagIds(tagIds);

        List<TagDO> list = tagService.checkAndUpdateTagCount(mineUser, tagIds, TalkOperateType.talkAdd, talkVisibleGender);

        String circleName = talkVO.getCircleName();
        SocialCircleDO socialCircleDO = null;
        if (StringUtils.isNotEmpty(circleName)) {
            socialCircleDO = socialCircleRepository.findFirstByName(talkVO.getCircleName());
            if (socialCircleDO != null) {
                socialCircleDO.setCount(socialCircleDO.getCount() + 1);
                socialCircleDO = socialCircleRepository.save(socialCircleDO);
            }
        }

        TalkAddValidateRO talkAddValidateRO = new TalkAddValidateRO(districtDO, list, socialCircleDO);
        return talkAddValidateRO;
    }

    public TalkDO saveEntity(UserDO userDO, SocialTalkPostQO socialTalkPostQO, DistrictDO district, List<TagDO> tags, SocialCircleDO socialCircleDO) {
        String talkVisibleGender = socialTalkPostQO.getVisibleGender();
        //?????????????????????????????????
        if (!talkVisibleGender.equals(GenderType.all)) {
            //????????????????????????????????????tag????????????????????????
            if (talkVisibleGender.equals(GenderType.girl)) {
                //??????????????????????????????????????????????????????????????????tag???????????????
                TagDO genderTag = tagRepository.findFirstByName(GenderType.girlTagName);
                tags.add(genderTag);
            } else if (talkVisibleGender.equals(GenderType.boy)) {
                //??????????????????????????????????????????????????????????????????tag???????????????
                TagDO genderTag = tagRepository.findFirstByName(GenderType.boyTagName);
                tags.add(genderTag);
            }
            //?????????????????????
        }

        TalkDO talkDO = socialTalkCreateManage.createTalkDO(userDO, socialTalkPostQO, district);

        List<SocialTalkTagDO> list = new ArrayList<>();

        for (TagDO tagDO : tags) {
            SocialTalkTagDO socialTalkTagDO = new SocialTalkTagDO();
            socialTalkTagDO.setTagId(tagDO.getId());
            socialTalkTagDO.setTalkId(talkDO.getId());
            list.add(socialTalkTagDO);
        }
        talkTagRepository.saveAll(list);

        List<SocialTalkImgDO> imgDOS = TalkImgDOFactory.newTalkImgDOS(socialTalkPostQO.getImgs());

        for (SocialTalkImgDO imgDO : imgDOS) {
            imgDO.setContentId(talkDO.getId());
            imgDO.setUserId(talkDO.getUserId());
        }

        List<SocialTalkImgDO> talkImgDOS = talkImgRepository.saveAll(imgDOS);

        if (socialCircleDO != null) {
            SocialTalkCircleDO socialTalkCircleDO = new SocialTalkCircleDO();
            socialTalkCircleDO.setTalkId(talkDO.getId());
            socialTalkCircleDO.setCircleId(socialCircleDO.getId());
            socialTalkCircleRepository.save(socialTalkCircleDO);
        }

        return talkDO;
    }
}
