package com.socialuni.social.model.model.RO.message.message;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MsgDeleteVO {
    @NotNull
    private Integer msgId;
    private String deleteReason;
    private Boolean violation;
}