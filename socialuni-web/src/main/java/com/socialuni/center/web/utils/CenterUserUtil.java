package com.socialuni.center.web.utils;

import com.socialuni.center.web.exception.SocialUserBannedException;
import com.socialuni.center.web.model.DO.ThirdUserDO;
import com.socialuni.center.web.model.DO.ThirdUserTokenDO;
import com.socialuni.center.web.repository.ThirdUserRepository;
import com.socialuni.social.sdk.constant.status.UserStatus;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.sdk.utils.SocialUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CenterUserUtil {
    private static ThirdUserRepository thirdUserRepository;

    @Resource
    public void setThirdUserRepository(ThirdUserRepository thirdUserRepository) {
        CenterUserUtil.thirdUserRepository = thirdUserRepository;
    }

    public static Integer getMineUserId() {
        UserDO user = CenterUserUtil.getMineUser();
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    public static UserDO getMineUser() {
        ThirdUserTokenDO tokenDO = CenterTokenUtil.getThirdUserTokenDO();
        return getMineUser(tokenDO);
    }

    public static boolean isMine(Integer userId) {
        Integer mineUserId = CenterUserUtil.getMineUserId();
        return userId.equals(mineUserId);
    }

    public static UserDO getMineUser(String token) {
        ThirdUserTokenDO tokenDO = CenterTokenUtil.getThirdUserTokenDO(token);
        return getMineUser(tokenDO);
    }

    private static UserDO getMineUser(ThirdUserTokenDO tokenDO) {
        if (tokenDO == null) {
            return null;
        }
        UserDO user = SocialUserUtil.get(tokenDO.getUserId());
        if (user.getStatus().equals(UserStatus.violation)) {
            throw new SocialUserBannedException(user);
        }
        //返回user
        return user;
    }


    public static ThirdUserDO getMineThirdUser() {
        Integer devId = DevAccountUtils.getDevId();
        Integer userId = CenterUserUtil.getMineUserId();
        ThirdUserDO thirdUserDO = thirdUserRepository.findByDevIdAndUserId(devId, userId);
        return thirdUserDO;
    }

    public static String getMineUserPhoneNum() {
        UserDO userDO = CenterUserUtil.getMineUser();
        return SocialUserUtil.getUserPhoneNum(userDO.getId());
    }

    public static UserDO get(String userId) {
        Integer id = UnionIdDbUtil.getUserIdByUid(userId);
        return SocialUserUtil.get(id);
    }
}