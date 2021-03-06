package com.socialuni.admin.web.controller;

import com.socialuni.social.sdk.constant.platform.SocialuniSupportProviderType;
import com.socialuni.social.sdk.utils.DevAccountUtils;
import com.socialuni.social.entity.model.DO.dev.DevAccountDO;
import com.socialuni.social.entity.model.DO.dev.DevAccountProviderDO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
public class DevAccountRO {
    private Long id;
    private String secretKey;
    private String token;
    private Long devNum;
    //通用名称，创建应用专属tag时使用
    private String appName;
    private String phoneNum;
    private String type;
    private String realName;
    private String identityNum;
    private Integer callApiCount;

    private String status;
    private Date createTime;
    private Date updateTime;

    private String wxMpAppName;
    private String wxMpAppId;
    private String qqMpAppName;
    private String qqMpAppId;


    public DevAccountRO(DevAccountDO devAccountDO) {
        this.devNum = devAccountDO.getDevNum();

        this.type = devAccountDO.getType();
        this.appName = devAccountDO.getAppName();
        this.realName = devAccountDO.getRealName();

        String phoneNum = devAccountDO.getPhoneNum();
        this.phoneNum = phoneNum.substring(0, 3) + "*****" + phoneNum.substring(8);

        if (StringUtils.isNotEmpty(devAccountDO.getSecretKey())) {
            this.secretKey = devAccountDO.getSecretKey().substring(0, 5) + "*****************";
        }

        for (String supportProviderType : SocialuniSupportProviderType.supportProviderTypes) {
            DevAccountProviderDO wxDevAccountProviderDO = DevAccountUtils.getDevAccountProviderDOByDevAndMpType(devAccountDO.getId(), supportProviderType);
            if (wxDevAccountProviderDO != null) {
                if (SocialuniSupportProviderType.wx.equals(supportProviderType)) {
                    this.wxMpAppId = wxDevAccountProviderDO.getAppId();
                    this.wxMpAppName = wxDevAccountProviderDO.getAppName();
                } else if (SocialuniSupportProviderType.qq.equals(supportProviderType)) {
                    this.qqMpAppId = wxDevAccountProviderDO.getAppId();
                    this.qqMpAppName = wxDevAccountProviderDO.getAppName();
                }
            }
        }
    }
}
