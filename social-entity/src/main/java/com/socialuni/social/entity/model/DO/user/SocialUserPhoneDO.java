package com.socialuni.social.entity.model.DO.user;

import com.socialuni.social.entity.model.DO.user.base.SocialUserPhoneBaseDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;


@Entity
@Table(name = "s_user_phone",
        uniqueConstraints = {
                //每个渠道都是唯一的
                @UniqueConstraint(columnNames = {"phoneNum", "status"}),
                @UniqueConstraint(columnNames = {"userId", "status"})
        }
)
@Data
@NoArgsConstructor
public class SocialUserPhoneDO extends SocialUserPhoneBaseDO implements Serializable {
    public SocialUserPhoneDO(Integer userId, String phoneCountryCode, String phoneNum) {
       super(userId,phoneCountryCode,phoneNum);
    }
}
