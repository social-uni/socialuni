package com.socialuni.social.sdk.utils;

import com.socialuni.social.entity.model.DO.user.TokenDO;
import com.socialuni.social.exception.SocialNotLoginException;
import com.socialuni.social.exception.SocialNullUserException;
import com.socialuni.social.sdk.repository.CommonTokenRepository;
import com.socialuni.social.web.sdk.utils.SocialTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class SocialTokenDOUtil {
    private static CommonTokenRepository commonTokenRepository;

    @Resource
    public void setCommonTokenRepository(CommonTokenRepository commonTokenRepository) {
        SocialTokenDOUtil.commonTokenRepository = commonTokenRepository;
    }

    public static TokenDO getCommonTokenDO() {
        String token = SocialTokenUtil.getToken();
        return SocialTokenDOUtil.getCommonTokenDO(token);
    }

    public static TokenDO getCommonTokenDO(String token) {
        String userKey = SocialTokenUtil.getUserKeyByToken(token);
        if (userKey == null) {
            return null;
        }
        //解析token
        Integer userId = Integer.valueOf(userKey);
        TokenDO tokenDO = commonTokenRepository.findOneByToken(token);
        if (tokenDO == null) {
            throw new SocialNullUserException();
        }
        Date date = new Date();
        //如果当前时间大于时效时间，则时效了
        if (date.getTime() > tokenDO.getExpiredTime().getTime()) {
            //"用户凭证过期，请重新登录",必须用这个
            throw new SocialNotLoginException();
        }
        Integer doUserId = tokenDO.getUserId();
        if (!userId.equals(doUserId)) {
            log.error("绕过验证，错误的userId:{},{}", doUserId, userId);
            throw new SocialNullUserException();
        }
        //返回user
        return tokenDO;
    }
}