package com.socialuni.api.feignAPI.community;

import com.socialuni.social.model.model.QO.community.HugAddQO;
import com.socialuni.social.model.model.RO.ResultRO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("hug")
@FeignClient(name = "hug", url = "${socialuni.server-url:https://api.socialuni.com}")
public interface SocialuniHugAPI {
    @PostMapping("addHug")
    ResultRO<Void> addHug(@RequestBody @Valid HugAddQO addVO);
}