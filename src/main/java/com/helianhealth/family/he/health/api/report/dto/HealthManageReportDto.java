package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author bellliu
 * @date 2021/11/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthManageReportDto implements Serializable {

    /**
     * 最近的2份报告
     */
    private List<String> reportIdList;

    /**
     * 成员id
     */
    private Integer memberId;

    /**
     * 医院编号id
     */
    private String stationId;

    /**
     * 身份证-需要加密
     */
    private String idCard;

    /**
     * 专家点评内容
     */
    private String commentContent;

    /**
     * 专家点评人
     */
    private String commentName;

    /**
     * 运动参数
     */
    private SportReportDto sport;

    /**
     * 饮食参数
     */
    private FoodReportDto food;

    /**
     * 报告背景
     */
    private String reportBackground;

    /**
     * 公众号二维码
     */
    private String publicQrCode;

    /**
     * 公众号名称
     */
    private String wxName;

    /**
     * 前言
     */
    private String preface;

    /**
     * 结束语
     */
    private String conclusion;

    /**
     * 医院logo
     */
    private String logo;

    /**
     * 医院名称
     */
    private String instName;

    /**
     * 地址
     */
    private String instAddress;

    /**
     * 联系电话
     */
    private String instMobile;

}
