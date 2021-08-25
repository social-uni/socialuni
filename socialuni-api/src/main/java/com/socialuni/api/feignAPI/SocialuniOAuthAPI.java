package com.socialuni.api.feignAPI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("oAuth")
@FeignClient(name = "oAuth", url = "${socialuni.server-url:https://api.socialuni.cn}")
public interface SocialuniOAuthAPI {
/*    @PostMapping("oAuthUserInfo")
    ResultRO<SocialLoginRO<CenterUserDetailRO>> oAuthUserInfo(OAuthUserInfoQO authVO);


    @PostMapping("oAuthUserPhoneNum")
    ResultRO<SocialLoginRO<CenterUserDetailRO>> oAuthUserPhoneNum(OAuthUserInfoQO authVO);

    @PostMapping("getUserInfo")
    ResultRO<SocialLoginRO<CenterUserDetailRO>> getUserInfo(OAuthUserInfoQO authVO);

    @PostMapping("getUserPhoneNum")
    ResultRO<OAuthUserPhoneNumRO> getOAuthUserPhoneNum();*/


   /* @PostMapping("applyOAuthUserPhoneNum")
    ResultRO<OAuthUserPhoneNumRO> applyOAuthUserPhoneNum(ApplyOAuthUserInfoQO authVO);



    @PostMapping("getOAuthUserPhoneNum")
    ResultRO<OAuthUserPhoneNumRO> getOAuthUserPhoneNum(GetOAuthUserInfoQO authVO);*/
}