package com.helianhealth.family.he.health.api.member.param;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author lijun
 */
@Data
public class ArkMemberInfoParam implements Serializable {
    private static final long serialVersionUID = 5816567770154736127L;
    /**
     *
     */
    private Integer id;

    /**
     * 证件类型：1身份证 2出生证 3健康证
     */
    private Integer idCardType;

    /**
     * 身份证号 aes加密
     */
    private String idCardNo;
    /**
     * 出生证号
     */
    private String birthCertificateNumber;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 渠道编号 比如医院 HL99999
     */
    private String stationId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 姓名
     */
    private String name;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 性别 1男 2女
     */
    private Integer gender;

    /**
     * 是否长期居住
     */
    private Integer liveWith;

    /**
     * 婚姻状况 婚姻状况 0未婚／1已婚（含再婚）／2离异／3丧偶／
     */
    private Integer married;

    /**
     * 血型
     */
    private String bloodType;

    /**
     * 过敏史
     */
    private Boolean allergy;

    /**
     * 疾病史
     */
    private Boolean disease;

    /**
     * 手术史
     */
    private Boolean operation;

    /**
     * 身高
     */
    private Double height;

    /**
     * 体重
     */
    private Double weight;

    /**
     * 工作单位
     */
    private String company;

    /**
     * 客户标签 多个逗号分割
     */
    private String tags;

    /**
     * 疾病标签 多个逗号分割
     */
    private String icds;

    /**
     * 手动添加的疾病标签
     */
    private String customizeIcds;

    /**
     * 管理医生id
     */
    private Integer principalDoctor;

    /**
     * 体检报告ID
     */
    private String reportId;

    /**
     * VIP级别
     */
    private String vipLevel;
    private String reportLevel;
    private String summaryer;

    /**
     * 体检类型
     */
    private String examType;

    /**
     * 建档来源： 1: 体检建档 2: 后台建档 3: 微信建档
     */
    private Integer source;

    /**
     * 疾病标签：是否自动匹配
     */
    private Boolean icdAuto;

    /**
     * 最近体检时间
     */
    private LocalDate lastExaminationTime;

    /**
     * 最近体检医院
     */
    private String lastReportStation;

    private Integer creator;

    private String dept;
}
