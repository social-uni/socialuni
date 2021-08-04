package com.socialuni.sdk.model.EO;

import com.socialuni.sdk.model.DO.user.SocialUserFansDetailDO;
import com.socialuni.sdk.model.DO.user.UserDO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SocialProviderLoginEO {
    UserDO mineUser;
    //创建或返回
    SocialUserFansDetailDO socialUserFansDetailDO;

    public SocialProviderLoginEO(UserDO mineUser, SocialUserFansDetailDO socialUserFansDetailDO) {
        this.mineUser = mineUser;
        this.socialUserFansDetailDO = socialUserFansDetailDO;
    }
}
