package com.socialuni.demo.controller;

import com.socialuni.sdk.model.QO.phone.SocialBindWxPhoneNumQO;
import com.socialuni.sdk.service.phone.SocialPhoneService;
import com.socialuni.sdk.url.user.SocialuniPhoneUrl;
import com.socialuni.social.model.model.QO.user.SocialPhoneNumQO;
import com.socialuni.social.model.model.RO.ResultRO;
import com.socialuni.social.model.model.RO.user.SocialMineUserDetailRO;
import com.socialuni.social.model.model.RO.user.phone.SocialSendAuthCodeQO;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SocialPhoneController implements SocialuniPhoneUrl {

    @Resource
    SocialPhoneService socialPhoneService;

    @Override
    public ResultRO<Void> sendAuthCode(SocialSendAuthCodeQO authCodeQO) {
        return socialPhoneService.sendAuthCode(authCodeQO);
    }

    @Override
    public ResultRO<SocialMineUserDetailRO> bindPhoneNum(SocialPhoneNumQO phoneNumQO) {
        return socialPhoneService.bindPhoneNum(phoneNumQO);
    }

    @Override
    public ResultRO<SocialMineUserDetailRO> bindWxPhoneNum(SocialBindWxPhoneNumQO socialBindWxPhoneNumQO) {
        return socialPhoneService.bindWxPhoneNum(socialBindWxPhoneNumQO);
    }
}
