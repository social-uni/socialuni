package com.socialuni.admin.web.controller;

import com.socialuni.admin.web.model.RO.UserIdentityAuditRO;
import com.socialuni.social.api.model.ResultRO;
import com.socialuni.social.constant.UserIdentityAuthStatus;
import com.socialuni.social.entity.model.DO.user.SocialUserIdentityAuthDO;
import com.socialuni.social.entity.model.DO.user.SocialUserIdentityAuthImgDO;
import com.socialuni.social.entity.model.DO.user.UserDO;
import com.socialuni.social.sdk.repository.user.identity.SocialUserIdentityAuthImgRepository;
import com.socialuni.social.sdk.repository.user.identity.SocialUserIdentityAuthRepository;
import com.socialuni.social.sdk.utils.SocialUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("userIdentity")
@Slf4j
public class UserIdentityAuditController {
    @Resource
    SocialUserIdentityAuthRepository socialUserIdentityAuthRepository;
    @Resource
    SocialUserIdentityAuthImgRepository socialUserIdentityAuthImgRepository;

    /**
     * 查询cos图片审核历史详情
     */
    @PostMapping("queryUserIdentityAuthAuditList")
    public ResultRO<List<UserIdentityAuditRO>> queryAuditUserIdentityList() {
        List<SocialUserIdentityAuthDO> list = socialUserIdentityAuthRepository.findByStatusIn(UserIdentityAuthStatus.auditList);
        List<UserIdentityAuditRO> list1 = new ArrayList<>();
        for (SocialUserIdentityAuthDO socialUserIdentityAuthDO : list) {
            SocialUserIdentityAuthImgDO socialUserIdentityAuthImgDO = socialUserIdentityAuthImgRepository.findFirstById(socialUserIdentityAuthDO.getUserIdentityImgId());

            UserDO userDO = SocialUserUtil.get(socialUserIdentityAuthDO.getUserId());

            UserIdentityAuditRO userIdentityAuditRO = new UserIdentityAuditRO(socialUserIdentityAuthDO, socialUserIdentityAuthImgDO, userDO);
            list1.add(userIdentityAuditRO);
        }
        //展示自拍和id照片信息，还有
        return ResultRO.success(list1);
    }

    /**
     * 查询cos图片审核历史详情
     */
    @PostMapping("auditUserIdentityList")
    public ResultRO<List<UserIdentityAuditRO>> auditUserIdentityList(@RequestBody List<UserIdentityAuditRO> audits) {
        for (UserIdentityAuditRO audit : audits) {
            SocialUserIdentityAuthDO socialUserIdentityAuthDO = socialUserIdentityAuthRepository.getOne(audit.getId());
            if (audit.getSuccess()) {
                socialUserIdentityAuthDO.setStatus(UserIdentityAuthStatus.enable);
            } else {
                socialUserIdentityAuthDO.setStatus(UserIdentityAuthStatus.delete);
            }
            socialUserIdentityAuthDO.setUpdateTime(new Date());
            socialUserIdentityAuthRepository.save(socialUserIdentityAuthDO);
        }
        //展示自拍和id照片信息，还有
        return ResultRO.success();
    }
}
