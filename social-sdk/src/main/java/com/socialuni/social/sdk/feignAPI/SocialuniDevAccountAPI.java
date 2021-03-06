package com.socialuni.social.sdk.feignAPI;

import com.socialuni.social.entity.model.DO.dev.DevAccountDO;
import com.socialuni.social.sdk.model.QO.dev.DevAccountQueryQO;
import com.socialuni.social.api.model.ResultRO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author qinkaiyuan
 * @date 2021-07-28 11:09
 * 前端初始化内容
 */
@RequestMapping("devAccount")
@FeignClient(name = "devAccount", url = "${socialuni.server-url:https://api.socialuni.cn}")
public interface SocialuniDevAccountAPI {
    //同步创建生产环境的开发者账号
    @PostMapping("queryDevAccount")
    ResultRO<DevAccountDO> queryDevAccount(@RequestBody @Valid DevAccountQueryQO devAccountQueryQO);

    @PostMapping("queryDevAccountProvider")
    ResultRO<DevAccountDO> queryDevAccountProvider(@RequestBody @Valid DevAccountQueryQO devAccountQueryQO);
}
