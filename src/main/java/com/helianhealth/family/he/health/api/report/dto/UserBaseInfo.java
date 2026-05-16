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
public class UserBaseInfo implements Serializable {

    /**
     * 1男2女0未知
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 出身日期
     */
    private String birthDay;

    /**
     * 单位
     */
    private String company;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 身高
     */
    private Double height;

    /**
     * 体重
     */
    private Double weight;

    /**
     * 婚姻状态 0未婚 1已婚 2离异 3丧偶 null 未知
     */
    private Integer marry;
}
