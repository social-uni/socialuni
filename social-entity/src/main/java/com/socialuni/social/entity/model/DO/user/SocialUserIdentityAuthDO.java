package com.socialuni.social.entity.model.DO.user;

import com.socialuni.social.entity.model.DO.base.CommonContentBaseDO;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * 登录相关，只有登录时才用得到的表
 * 自己表示字段，其他表示关联的表内字段
 */
@Data
@Entity
@Table(name = "s_user_identity_auth",
        //查询条件索引
        /* indexes = {
                 //关联需要键索引，索引列不能为空
                 @Index(columnList = "userId"),
         },*/
        uniqueConstraints = {
                //每个渠道都是唯一的
                @UniqueConstraint(columnNames = "userId"),
        }
)
public class SocialUserIdentityAuthDO extends CommonContentBaseDO implements Serializable {
    private Integer userId;
    private Integer userIdentityImgId;
    private Integer age;
    private String birth;
    private String name;
    private String nation;
    private String sex;
    //认证次数
    private Integer authNum;

    public SocialUserIdentityAuthDO() {
        this.authNum = 0;
    }
}