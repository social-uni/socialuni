package com.socialuni.social.model.model.QO.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SocialUserIdQO {
    @NotNull
    private Integer userId;
}