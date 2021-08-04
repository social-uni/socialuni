package com.socialuni.sdk.domain.phone;

import com.socialuni.sdk.constant.ProviderLoginType;
import com.socialuni.sdk.constant.platform.PlatformType;
import com.socialuni.sdk.entity.user.SocialBindUserProviderAccountEntity;
import com.socialuni.sdk.model.DO.user.UserDO;
import com.socialuni.social.model.model.QO.user.SocialProviderLoginQO;
import com.socialuni.social.model.model.RO.user.login.SocialLoginRO;
import com.socialuni.social.model.model.RO.user.SocialUserDetailRO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SocialBindUserSocialuniAccountDomain {
    @Resource
    SocialBindUserProviderAccountEntity socialBindUserProviderAccountEntity;

    public void bindUserSocialAccount(UserDO mineUser, SocialLoginRO socialLoginRO) {
        SocialProviderLoginQO loginQO = new SocialProviderLoginQO();
        loginQO.setProvider(ProviderLoginType.socialuni);
        loginQO.setPlatform(PlatformType.mp);
        loginQO.setCode(socialLoginRO.getToken());

        SocialUserDetailRO centerUserDetailRO = socialLoginRO.getUser();

//        loginQO.setUnionId(centerUserDetailRO.getId());
//        loginQO.setOpenId(centerUserDetailRO.getId());

        socialBindUserProviderAccountEntity.bindProviderAccount(mineUser, loginQO);
    }
}