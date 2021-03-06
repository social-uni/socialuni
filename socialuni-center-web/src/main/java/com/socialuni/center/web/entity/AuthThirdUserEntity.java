package com.socialuni.center.web.entity;


import com.socialuni.api.model.RO.user.CenterMineUserDetailRO;
import com.socialuni.social.entity.model.DO.dev.DevAccountDO;
import com.socialuni.center.web.factory.RO.user.CenterMineUserDetailROFactory;
import com.socialuni.center.web.manage.ThirdUserAuthManage;
import com.socialuni.center.web.manage.ThirdUserManage;
import com.socialuni.center.web.manage.ThirdUserTokenManage;
import com.socialuni.social.entity.model.DO.dev.ThirdUserDO;
import com.socialuni.center.web.utils.UnionIdDbUtil;
import com.socialuni.social.entity.model.DO.user.SocialUserPhoneDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.exception.SocialBusinessException;
import com.socialuni.social.model.model.RO.user.SocialMineUserDetailRO;
import com.socialuni.social.sdk.constant.AuthType;
import com.socialuni.social.sdk.redis.SocialUserPhoneRedis;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//授权用户信息
@Service
public class AuthThirdUserEntity {
    @Resource
    ThirdUserTokenManage thirdUserTokenManage;
    @Resource
    private ThirdUserManage thirdUserManage;
    @Resource
    private ThirdUserAuthManage thirdUserAuthManage;
    @Resource
    SocialUserPhoneRedis socialUserPhoneRedis;

    /*public ThirdUserTokenDO authThirdUser(DevAccountDO threeDevDO, UserDO userDO, String oAuthType) {
        //只是记录一个授权记录
        ThirdUserDO threeUserDO = thirdUserManage.getOrCreate(threeDevDO.getId(), userDO.getId());

        thirdUserAuthManage.getOrCreate(threeUserDO, oAuthType);
        //授权成功
        //token有效期三个月
        //生成userToken
        ThirdUserTokenDO tokenDO = thirdUserTokenManage.create(userDO.getId(), threeDevDO.getId());
        return tokenDO;
    }*/

    //登录和 绑定手机号和微信手机号三个地方使用
    public CenterMineUserDetailRO authThirdUser(UserDO mineUser, String authType, DevAccountDO devAccountDO, SocialMineUserDetailRO socialMineUserDetailRO) {
        CenterMineUserDetailRO centerMineUserDetailRO = CenterMineUserDetailROFactory.getMineUserDetail(socialMineUserDetailRO, mineUser);
        String uid = UnionIdDbUtil.createUserUid(mineUser.getId(), mineUser, devAccountDO);
        centerMineUserDetailRO.setId(uid);

        //只是记录一个授权记录
        ThirdUserDO threeUserDO = thirdUserManage.getOrCreate(devAccountDO.getId(), mineUser.getId(), centerMineUserDetailRO.getId());

        if (AuthType.phone.equals(authType)) {
            thirdUserAuthManage.getOrCreate(threeUserDO, AuthType.user);
            //只有为手机号授权时才返回手机号
            SocialUserPhoneDO socialUserPhoneDO = socialUserPhoneRedis.findUserPhoneByUserId(mineUser.getId());
            if (socialUserPhoneDO == null) {
                throw new SocialBusinessException("用户未绑定手机号，请先绑定手机号");
            }
            centerMineUserDetailRO.setPhoneNum(socialUserPhoneDO.getPhoneNum());
        }
        thirdUserAuthManage.getOrCreate(threeUserDO, authType);

        return centerMineUserDetailRO;
    }
}
