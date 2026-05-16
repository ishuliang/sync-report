package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author bellliu
 * @date 2021/11/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportBaseInfoDto implements Serializable {
    /**
     * 姓名
     */
    private String customerName;
    /**
     * 性别
     */
    private String sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 单位
     */
    private String unit;
    /**
     * 问卷编号
     */
    private String reportId;
    /**
     * examDate
     */
    private String examDate;
    /**
     * 问卷日期
     */
    private String questionnaireDate;
    /**
     * 报告生成日期
     */
    private String reportDate;
    /**
     * 公众号二维码
     */
    private String publicQrCode;
    /**
     * 公众号名称
     */
    private String wxName;
    /**
     * 报告背景
     */
    private String reportBackground;
    /**
     * 前言
     */
    private String preface;
    /**
     * 结束语
     */
    private String conclusion;
    /**
     * 医院log
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
    /**
     * 专家点评内容
     */
    private String commentContent;
    /**
     * 专家点评人
     */
    private String commentName;
    /**
     * 婚姻状态 0未婚 1已婚 2离异 3丧偶 null 未知
     */
    private Integer marry;
}
