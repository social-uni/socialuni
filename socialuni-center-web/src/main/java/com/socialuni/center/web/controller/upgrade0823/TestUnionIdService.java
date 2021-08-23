package com.socialuni.center.web.controller.upgrade0823;

import com.socialuni.center.web.factory.DO.UnionIdDOFactory;
import com.socialuni.center.web.model.DO.ThirdUserAuthDO;
import com.socialuni.center.web.model.DO.ThirdUserDO;
import com.socialuni.center.web.model.DO.ThirdUserTokenDO;
import com.socialuni.center.web.model.DO.UnionIdDO;
import com.socialuni.center.web.repository.ThirdUserAuthRepository;
import com.socialuni.center.web.repository.ThirdUserRepository;
import com.socialuni.center.web.repository.ThirdUserTokenRepository;
import com.socialuni.center.web.repository.UnionIdRepository;
import com.socialuni.social.api.model.ResultRO;
import com.socialuni.social.constant.ContentType;
import com.socialuni.social.entity.model.DO.user.SocialUserAccountDO;
import com.socialuni.social.sdk.constant.AuthType;
import com.socialuni.social.sdk.domain.phone.SocialBindUserSocialuniAccountDomain;
import com.socialuni.social.sdk.repository.SocialUserAccountRepository;
import com.socialuni.social.sdk.repository.UserRepository;
import com.socialuni.social.web.sdk.utils.SocialTokenUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TestUnionIdService {
    @Resource
    UserRepository userRepository;
    @Resource
    UnionIdRepository unionIdRepository;
    @Resource
    ThirdUserRepository thirdUserRepository;
    @Resource
    ThirdUserAuthRepository thirdUserAuthRepository;
    @Resource
    ThirdUserTokenRepository thirdUserTokenRepository;
    @Resource
    SocialBindUserSocialuniAccountDomain socialBindUserSocialuniAccountDomain;

    @Async
    public void unionid() {
        final Integer devId = 1;
        List<Integer> userIds = userRepository.findAllUserIds();
        List<UnionIdDO> unionIdDOS = new ArrayList<>();
        List<ThirdUserDO> thirdUserDOS = new ArrayList<>();
        List<ThirdUserAuthDO> thirdUserAuthDOS = new ArrayList<>();
        List<ThirdUserTokenDO> thirdUserTokenDOS = new ArrayList<>();
        Date date = new Date();
        for (Integer userId : userIds) {
            UnionIdDO unionIdDO = UnionIdDOFactory.createUnionDO(ContentType.user, userId, devId, date, userId);
            unionIdDOS.add(unionIdDO);

            ThirdUserDO threeUserDO = new ThirdUserDO(devId, userId, unionIdDO.getUnionId());
            thirdUserDOS.add(threeUserDO);

            ThirdUserAuthDO threeUserAuthUserDO = new ThirdUserAuthDO(threeUserDO, AuthType.user);
            ThirdUserAuthDO threeUserAuthDO = new ThirdUserAuthDO(threeUserDO, AuthType.phone);
            thirdUserAuthDOS.add(threeUserAuthUserDO);
            thirdUserAuthDOS.add(threeUserAuthDO);

            String appToken = SocialTokenUtil.generateTokenByUserKey(unionIdDO.getUnionId());
            ThirdUserTokenDO thirdToken = new ThirdUserTokenDO(userId, appToken, devId, unionIdDO.getUnionId());
            thirdUserTokenDOS.add(thirdToken);

            socialBindUserSocialuniAccountDomain.bindUserSocialAccount(userId, appToken, unionIdDO.getUnionId());
        }
        unionIdRepository.saveAll(unionIdDOS);
        thirdUserRepository.saveAll(thirdUserDOS);
        thirdUserAuthRepository.saveAll(thirdUserAuthDOS);
        thirdUserTokenRepository.saveAll(thirdUserTokenDOS);
    }
}
