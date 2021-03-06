package com.socialuni.social.sdk.store;

import com.socialuni.social.entity.model.DO.talk.TalkDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.model.model.QO.community.talk.SocialUserTalkQueryQO;
import com.socialuni.social.sdk.redis.FollowRedis;
import com.socialuni.social.sdk.repository.community.TalkRepository;
import com.socialuni.social.sdk.utils.TalkRedis;
import com.socialuni.social.sdk.utils.TalkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TalkQueryStore {
    @Resource
    private TalkRepository talkRepository;
    @Resource
    private TalkRedis talkRedis;
    @Resource
    FollowRedis followRedis;

    public List<TalkDO> queryTalksTop10ByUserFollow(List<Integer> talkIds, Integer userId) {
        List<Integer> beUserIds = followRedis.queryUserFollowUserIds(userId);
        int page = talkIds.size() / 10;
        List<Integer> ids = talkRedis.queryUserFollowsTalkIds(userId, beUserIds, PageRequest.of(page, 10));
        return this.queryTalksByIds(ids);
    }

    public List<TalkDO> queryUserTalks(SocialUserTalkQueryQO queryQO, UserDO mineUser) {
        List<Integer> talkIds = queryQO.getTalkIds();
        Integer userId = queryQO.getUserId();

        if (ObjectUtils.isEmpty(talkIds)) {
            talkIds = Collections.singletonList(0);
        }
        List<TalkDO> talks;
        if (mineUser != null && mineUser.getId().equals(userId)) {
            talks = this.queryTalksTop10ByMine(talkIds, userId);
        } else {
            talks = this.queryTalksTop10ByUser(talkIds, userId);
        }
        return talks;
    }

    public List<TalkDO> queryTalksTop10ByUser(List<Integer> talkIds, Integer userId) {
        int page = talkIds.size() / 10;
        List<Integer> ids = talkRedis.queryUserTalkIds(userId, PageRequest.of(page, 10));
        return this.queryTalksByIds(ids);
    }

    //??????????????????????????????????????????????????????
    public List<TalkDO> queryTalksTop10ByMine(List<Integer> talkIds, Integer userId) {
        int page = talkIds.size() / 10;
        List<Integer> ids = talkRedis.queryMineTalkIds(userId, PageRequest.of(page, 10));
        return this.queryTalksByIds(ids);
    }

    public List<Integer> queryTalkIdsByTab(Integer mineUserId, String postTalkUserGender,
                                           Integer minAge, Integer maxAge, String adCode,
                                           String talkVisibleGender,
                                           String mineUserGender, List<Integer> tagIds, Pageable pageable, Integer devId, Date queryTime, Integer circleId, Boolean hasPeopleImgTalkNeedIdentity) {
//        log.info("queryUserTalkIdsByTab?????????" + new Date().getTime() / 1000);

//        ???????????????????????????, ?????????????????????????????????????????????????????????????????????????????????
        List<Integer> ids = talkRedis.queryTalkIdsByTab(postTalkUserGender, minAge, maxAge, adCode,
                talkVisibleGender, mineUserGender, tagIds, devId, circleId, hasPeopleImgTalkNeedIdentity);

        if (mineUserId != null) {
            List<Integer> mineTalkIds = talkRedis.queryMineTalkIdsByCom(mineUserId, circleId);
            ids.addAll(mineTalkIds);
        }

//        queryTime,

        //???id???????????????????????????10
        ids = talkRepository.queryTalkIdsByIds(ids, queryTime, pageable);
//        log.info("queryUserTalkIdsByTab?????????" + new Date().getTime() / 1000);
        return ids;
    }

    public List<TalkDO> queryTalksTop10ByGenderAgeAndLikeAdCodeAndTagIds(List<Integer> talkIds, Integer userId, String postTalkUserGender,
                                                                         Integer minAge, Integer maxAge, String adCode,
                                                                         List<Integer> tagIds, String talkVisibleGender,
                                                                         String mineUserGender, Integer devId, Date queryTime, Integer circleId, Boolean hasPeopleImgTalkNeedIdentity) {
        int page = talkIds.size() / 10;
        List<Integer> ids = this.queryTalkIdsByTab(userId, postTalkUserGender, minAge, maxAge, adCode,
                talkVisibleGender, mineUserGender, tagIds, PageRequest.of(page, 10), devId, queryTime, circleId, hasPeopleImgTalkNeedIdentity);
        return this.queryTalksByIds(ids);
    }

    //??????id????????????????????????talk??????
    public List<TalkDO> queryTalksByIds(List<Integer> ids) {
        List<TalkDO> talkDOS = new ArrayList<>();
        for (Integer id : ids) {
//            SocialTalkRO talkEO = TalkROFactory.newTalkRO(id);
//            talkDOS.add(talkEO);
            TalkDO talkDO = TalkUtils.get(id);
            talkDOS.add(talkDO);
        }
        return talkDOS;
    }
}