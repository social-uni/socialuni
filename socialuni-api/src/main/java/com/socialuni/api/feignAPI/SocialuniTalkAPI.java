package com.socialuni.api.feignAPI;

import com.socialuni.social.model.model.QO.community.talk.*;
import com.socialuni.social.model.model.RO.ResultRO;
import com.socialuni.social.model.model.RO.community.talk.SocialTalkRO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("talk")
@FeignClient(name = "talk", url = "${socialuni.server-url:https://api.socialuni.com}")
public interface SocialuniTalkAPI {
    @GetMapping("queryTalks")
    ResultRO<List<SocialTalkRO>> queryHomeTalks();

    @PostMapping("queryTalks")
    ResultRO<List<SocialTalkRO>> queryHomeTabTalks(@RequestBody @Valid SocialHomeTalkQueryQO queryVO);

    @PostMapping("postTalk")
    ResultRO<Void> postTalk(@RequestBody @Valid SocialTalkPostQO talkVO);

    @PostMapping("deleteTalk")
    ResultRO<Void> deleteTalk(@RequestBody @Valid SocialTalkDeleteQO talkVO);

    @PostMapping("queryTalkInfo")
    ResultRO<SocialTalkRO> queryTalkDetail(@RequestBody @Valid SocialTalkIdQO socialTalkIdQO);

    @PostMapping("queryUserTalks")
    ResultRO<List<SocialTalkRO>> queryUserTalks(@RequestBody @Valid SocialUserTalkQueryQO queryVO);
}