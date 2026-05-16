package com.helianhealth.family.he.health.api.member.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author lijun
 */
@Data
public class ArkMemberInfoBO implements Serializable {
    private static final long serialVersionUID = -4557488224316900828L;
    private Integer id;

    /**
     * 证件类型：1身份证 2出生证 3健康证
     */
    private Integer idCardType = 1;
    /**
     * 身份证号 aes加密
     */
    private String idCardNo;

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
     * 姓名
     */
    private String name;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 年龄
     */
    private Integer age;

    private String ageStr;

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
     * 客户标签
     */
    private String tags;

    /**
     * 疾病标签名称
     */
    private Set<String> icds;

    /**
     * 疾病标签id 包括人工分配的
     */
    private List<Integer> icdIds;

    /**
     * 管理医生姓名
     */
    private String principalDoctor;

    /**
     * 建档来源
     */
    private String source;

    /**
     * 最近体检时间
     */
    private Date lastExaminationTime;

    /**
     * 分配状态 0：待分配  1：已分配
     */
    private Integer distributeStatus;


    /**
     * 建档时间
     */
    private Date gmtCreate;

    /**
     * 匹配上的疾病模板
     */
    private Integer matchTemplate;

    /**
     * 最后体检医院名称
     */
    private String lastReportStationName;

    /**
     * 最后体检医院id
     */
    private String lastReportStation;

    /**
     * 管理医生id
     */
    private Integer doctorId;

    /**
     * 疾病标签：是否自动匹配
     */
    private Boolean icdAuto;

    /**
     * 宣教方案执行情况：1未生成 2已生成
     */
    private Integer propagandaStatus;
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
     * 重阳数据
     */
    private String positiveAlertStr = "";

    /**
     * 客户分配时间
     */
    private String distributeTime;

    private Integer isVip;

    private String vipLevel;

    /**
     * 最大的阳性等级
     */
    private String positiveLevel;

    /**
     * 体检报告ID
     */
    private String reportId;
}
