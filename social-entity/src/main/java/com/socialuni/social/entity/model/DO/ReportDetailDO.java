package com.socialuni.social.entity.model.DO;


import com.socialuni.social.constant.ReportStatus;
import com.socialuni.social.entity.model.DO.base.BaseModelDO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 举报信息
 */@Entity
@Data@Table(name = "report_detail")
public class ReportDetailDO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    //应该就是contentId
    private Integer modelId;

    private Integer receiveUserId;

    /**
     * 举报类型，政治敏感，暴露，暴力等
     */
    private String reportType;

    private Date createTime;
    private Date updateTime;

    private Integer reportId;

    /**
     * 举报时的备注信息
     */
    private String content;
    private String cause;

    /**
     * 举报的状态，待审核，已通过，未通过，和report状态完全联动
     */
    private String status;

    public ReportDetailDO() {
    }

    //    todo 这里有个坑 content 改为了 cause
    public ReportDetailDO(String cause, String reportType, ReportDO reportDO, BaseModelDO model) {
        this.cause = cause;

        this.modelId = model.getId();
        this.content = model.getContent();

        this.receiveUserId = model.getUserId();
        this.reportId = reportDO.getId();
        this.reportType = reportType;

        this.createTime = new Date();
        this.status = ReportStatus.audit;
    }

    public ReportDetailDO(String cause, String reportType, ReportDO reportDO, BaseModelDO model, Integer requestUserId) {
        this(cause, reportType, reportDO, model);
        this.userId = requestUserId;
    }
}
