package com.socialuni.social.sdk.repository;


import com.socialuni.social.entity.model.DO.user.TokenDO;
import com.socialuni.social.sdk.redis.redisKey.RedisKeysConst;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonTokenRepository extends JpaRepository<TokenDO, Integer> {
    @Cacheable(cacheNames = RedisKeysConst.tokenByToken, key = "#token")
    TokenDO findOneByToken(String token);

    @CachePut(cacheNames = RedisKeysConst.tokenByToken, key = "#tokenDO.token")
    TokenDO save(TokenDO tokenDO);


//    @CachePut(cacheNames = RedisKeysConst.userById, key = "#account.userId")
//    UserAccountDO save(SocialUserAccountDO account);

 /*   Optional<SocialUserAccountDO> findFirstOneByQqMpOpenIdOrderByIdAsc(String qqMpOpenId);
    Optional<SocialUserAccountDO> findFirstOneByWxMpOpenIdOrderByIdAsc(String wxMpOpenId);

    Optional<SocialUserAccountDO> findFirstOneByQqUnionIdOrderByIdAsc(String unionId);

    Optional<SocialUserAccountDO> findFirstOneByWxUnionIdOrderByIdAsc(String unionId);

    Optional<SocialUserAccountDO> findOneByUserId(Integer userId);
*/
}

