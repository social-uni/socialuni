package com.socialuni.social.model.model.RO.app;

import lombok.Data;

import java.util.List;

/**
 * @author qinkaiyuan
 * @date 2019-10-30 14:08
 */
@Data
public class SocialDistrictRO {
    private Integer id;
    //省
    private String provinceName;
    //市
    private String cityName;
    //区县
    private String districtName;
    //统一标识
    private String adCode;
    private String adName;
    private Integer talkCount;
    private Integer count;
    private Double lon;
    private Double lat;
    private List<SocialDistrictRO> childs;
}
