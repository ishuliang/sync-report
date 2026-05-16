package com.helianhealth.family.he.health.api.member.param;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lijun
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchParam implements Serializable {
    private static final long serialVersionUID = -3935090982797649307L;

    /**
     * 证件类型：1身份证 2出生证 3健康证
     */
    private Integer idCardType = null;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 姓名
     */
    private String name;

    /**
     * 工作单位
     */
    private String company;

    /**
     * ..
     */
    private List<String> companyList;

    /**
     * 疾病标签
     */
    private String icd;


    private List<String> icds;

    /**
     * 管理医生id
     */
    private Integer principalDoctor;

    /**
     * 体检类型
     */
    private String examType;

    /**
     * 建档来源： 1: 体检建档 2: 后台建档 3: 微信建档
     */
    private Integer source;

    /**
     * 建档日期：开始日期
     */
    private Date createStartDate;

    /**
     * 建档日期：结束日期
     */
    private Date createEndDate;


    private String distributeBegin;
    private String distributeEnd;

    /**
     * 体检开始时间 yyyy-MM-dd
     */
    private String lastExamBegin;
    /**
     * 体检结束时间  yyyy-MM-dd
     */
    private String lastExamEnd;
    /**
     * 性别 1男 2女
     */
    private Integer gender;

    /**
     * 最小年龄
     */
    private Integer minAge;

    /**
     * 最大年龄
     */
    private Integer maxAge;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 分配状态 0：待分配 1：已分配
     */
    private Integer distributeStatus;

    /**
     * 医院id
     */
    private String stationId;


    /**
     * 基础服务付费用户:1是 0否(ark_order存在记录即为付费)
     */
    private Integer hasBaseOrder;

    /**
     * 是否已服务:1是 0否(存在干预计划即为已服务)
     */
    private Integer hasMeddlePlan;
    /**
     * 是否来源阳性预警上报
     */
    private Integer positiveAdd;

    /**
     * 有服务包且对应的体检单是否有报告（服务包体检对应的报告）
     */
    private Integer hasReport;

    private Integer isVip;

    private List<String> vipList;

    /**
     * 是否有重阳数据
     */
    private Integer isHeavyPositive;

    private Integer positiveLevel;

    /**
     * 报告ID
     */
    private String reportId;

    private Integer doctorId;

    /**
     *  管理疾病ID
     */
    private String manageDiseaseId;
    /**
     *  管理分组ID
     */
    private Integer groupId;

    private String orderBy;

    private Boolean asc;
}
