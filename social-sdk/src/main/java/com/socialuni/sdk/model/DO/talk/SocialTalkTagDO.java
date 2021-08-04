package com.socialuni.sdk.model.DO.talk;

import lombok.Data;

import javax.persistence.*;

/**
 * @author qinkaiyuan
 * @date 2019-11-07 15:20
 */
@Entity
@Table(name = "talk_tag")
@Data
public class SocialTalkTagDO {
    //此类为talk子类，只能包含基础数据类型
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer talkId;
    private Integer tagId;
}
