package com.socialuni.social.entity.model.DO.dev;

import com.socialuni.social.constant.CommonStatus;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author qinkaiyuan
 * @date 2019-02-14 22:03
 */
@Entity
@Table(name = "dev_auth_code")
@Data
public class DevAuthCodeDO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String phoneNum;
    private String authCode;
    private Date createTime;
    private String ip;
    //    private Integer userId;
    private String status;

    public DevAuthCodeDO() {
    }

    public DevAuthCodeDO(String phoneNum, String authCode, String ip) {
        this.phoneNum = phoneNum;
        this.authCode = authCode;
        this.ip = ip;
        this.createTime = new Date();
        this.status = CommonStatus.enable;
    }
}
