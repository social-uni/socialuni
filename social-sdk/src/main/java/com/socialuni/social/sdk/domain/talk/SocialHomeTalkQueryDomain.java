package com.socialuni.social.sdk.domain.talk;

import com.socialuni.social.constant.CommonStatus;
import com.socialuni.social.constant.GenderType;
import com.socialuni.social.entity.model.DO.circle.SocialCircleDO;
import com.socialuni.social.entity.model.DO.tag.TagDO;
import com.socialuni.social.entity.model.DO.talk.TalkDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.exception.SocialBusinessException;
import com.socialuni.social.exception.SocialParamsException;
import com.socialuni.social.model.model.QO.community.talk.SocialHomeTabTalkQueryBO;
import com.socialuni.social.model.model.QO.community.talk.SocialHomeTabTalkQueryQO;
import com.socialuni.social.model.model.RO.community.talk.SocialTalkRO;
import com.socialuni.social.sdk.constant.GenderTypeQueryVO;
import com.socialuni.social.sdk.constant.GenderTypeVO;
import com.socialuni.social.sdk.constant.TalkTabType;
import com.socialuni.social.sdk.entity.talk.SocialFollowUserTalksQueryEntity;
import com.socialuni.social.sdk.entity.talk.SocialHomeTalkQueryEntity;
import com.socialuni.social.sdk.factory.SocialTalkROFactory;
import com.socialuni.social.sdk.repository.community.SocialCircleRepository;
import com.socialuni.social.sdk.repository.community.TagRepository;
import com.socialuni.social.sdk.store.SocialTagRedis;
import com.socialuni.social.sdk.utils.DevAccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SocialHomeTalkQueryDomain {
    @Resource
    private SocialHomeTalkQueryEntity socialHomeTalkQueryEntity;
    @Resource
    private SocialFollowUserTalksQueryEntity socialFollowUserTalksQueryEntity;
    @Resource
    private SocialCircleRepository socialCircleRepository;
    @Resource
    private SocialTagRedis socialTagRedis;
    @Resource
    TagRepository tagRepository;


    public SocialHomeTabTalkQueryBO checkAndGetHomeTalkQueryBO(SocialHomeTabTalkQueryQO queryQO, UserDO mineUser) {
        SocialHomeTabTalkQueryBO socialHomeTabTalkQueryBO = new SocialHomeTabTalkQueryBO();
        //talk
        socialHomeTabTalkQueryBO.setTalkIds(queryQO.getTalkIds());

        //tagNames???tagIds
        List<Integer> tagIds = queryQO.getTagIds();
        if (tagIds == null) {
            tagIds = new ArrayList<>();
            List<String> tagNames = queryQO.getTagNames();
            if (tagNames == null) {
                tagNames = new ArrayList<>();
            }
            for (String tagName : tagNames) {
                TagDO tagDO = tagRepository.findFirstByName(tagName);
                if (tagDO == null || !tagDO.getStatus().equals(CommonStatus.enable)) {
                    throw new SocialBusinessException("????????????????????????");
                }
                tagIds.add(tagDO.getId());
            }
            queryQO.setTagIds(tagIds);
        }
        String tabType = queryQO.getHomeTabType();
        socialHomeTabTalkQueryBO.setHomeTabType(tabType);
        socialHomeTabTalkQueryBO.setAdCode(queryQO.getAdCode());
        socialHomeTabTalkQueryBO.setLon(queryQO.getLon());
        socialHomeTabTalkQueryBO.setLat(queryQO.getLat());
        socialHomeTabTalkQueryBO.setMinAge(queryQO.getMinAge());
        socialHomeTabTalkQueryBO.setQueryTime(queryQO.getQueryTime());
        socialHomeTabTalkQueryBO.setMaxAge(queryQO.getMaxAge());
        socialHomeTabTalkQueryBO.setDevId(DevAccountUtils.getDevIdAllowNull());

        String circleName = queryQO.getCircleName();
        if (TalkTabType.circle_type.equals(tabType)) {
            if (StringUtils.isNotEmpty(circleName)) {
                SocialCircleDO socialCircleDO = socialCircleRepository.findFirstByName(queryQO.getCircleName());
                socialHomeTabTalkQueryBO.setCircleId(socialCircleDO.getId());
            }
        }
        String queryQOGender = queryQO.getGender();

        //??????query???GenderType????????????
        GenderTypeQueryVO genderTypeQueryVO = GenderTypeVO.GenderTypeQueryMap.get(queryQOGender);
        if (genderTypeQueryVO == null) {
            throw new SocialParamsException("?????????????????????");
        }
        //???????????????????????????
        if (mineUser != null) {
            if (GenderType.onlyGenders.contains(queryQOGender)) {
//                throw new SocialParamsException("?????????????????????????????????????????????");
                String mineUserGender = mineUser.getGender();
                //??????????????????????????????????????????????????????????????????
                if (queryQOGender.equals(GenderType.onlyBoy) && !GenderType.boy.equals(mineUserGender)) {
                    throw new SocialParamsException("?????????????????????");
                } else if (queryQOGender.equals(GenderType.onlyGirl) && !GenderType.girl.equals(mineUserGender)) {
                    throw new SocialParamsException("?????????????????????");
                }
            }
        }
        socialHomeTabTalkQueryBO.setTalkVisibleGender(genderTypeQueryVO.getTalkVisibleGender());
        socialHomeTabTalkQueryBO.setTalkUserGender(genderTypeQueryVO.getTalkUserGender());
        socialHomeTabTalkQueryBO.setHasPeopleImgTalkNeedIdentity(queryQO.getHasPeopleImgTalkNeedIdentity());

        return socialHomeTabTalkQueryBO;
    }

    //???????????????tab???????????????
    public List<SocialTalkRO> queryHomeTabTalks(SocialHomeTabTalkQueryQO queryQO, UserDO mineUser) {
        //??????gender??????,??????BO
        SocialHomeTabTalkQueryBO queryBO = this.checkAndGetHomeTalkQueryBO(queryQO, mineUser);

        String tabType = queryBO.getHomeTabType();
        if (!TalkTabType.values.contains(tabType)) {
            throw new SocialParamsException("?????????tab??????");
        }
        //???????????????talk
        List<TalkDO> talkDOS;
        //????????????????????????
        if ((TalkTabType.follow_type.equals(tabType))) {
            talkDOS = socialFollowUserTalksQueryEntity.queryUserFollowTalks(queryBO.getTalkIds(), mineUser);
        } else {
            talkDOS = socialHomeTalkQueryEntity.queryHomeTalks(queryBO, mineUser);
        }
        //?????????rolist
        List<SocialTalkRO> socialTalkROs = SocialTalkROFactory.newHomeTalkROs(mineUser, talkDOS, queryBO);
        return socialTalkROs;
    }
}
