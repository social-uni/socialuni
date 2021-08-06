package com.socialuni.api.feignAPI;

import com.socialuni.api.model.RO.user.CenterMineUserDetailRO;
import com.socialuni.social.model.model.QO.user.SocialPhoneNumQO;
import com.socialuni.social.model.model.QO.user.SocialProviderLoginQO;
import com.socialuni.social.model.model.RO.ResultRO;
import com.socialuni.social.model.model.RO.user.login.SocialLoginRO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("login")
@FeignClient(name = "login", url = "${socialuni.server-url:https://api.socialuni.com}")
public interface SocialuniLoginAPI {

    //三方渠道登录，qq、wx、社交联盟，兼容各平台，h5、app、mp
    @PostMapping("providerLogin")
    ResultRO<SocialLoginRO<CenterMineUserDetailRO>> providerLogin(@RequestBody @Valid SocialProviderLoginQO loginData);

    @PostMapping("phoneLogin")
    ResultRO<SocialLoginRO<CenterMineUserDetailRO>> phoneLogin(@RequestBody @Valid SocialPhoneNumQO socialPhoneNumQO);
}