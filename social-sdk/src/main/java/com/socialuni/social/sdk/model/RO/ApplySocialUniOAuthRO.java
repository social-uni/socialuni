package com.socialuni.social.sdk.model.RO;

import com.socialuni.social.model.model.RO.user.base.SocialUserRO;
import lombok.Data;

@Data
public class ApplySocialUniOAuthRO {
    private String token;
    private SocialUserRO user;
}
