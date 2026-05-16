package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author bellliu
 * @date 2021/11/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReportOutDto implements Serializable {
    /**
     * 身高
     */
    private Double height;
    /**
     * 体重
     */
    private Double weight;
    /**
     * BMI
     */
    private Double bmi;
    /**
     * 体型
     */
    private String size;
    /**
     * 建议
     */
    private String suggest;
    /**
     * 体重分析
     */
    private String weightAnalysis;
    /**
     * 饮食原则
     */
    private String dietaryPrinciples;
    /**
     * 主要事项
     */
    private String precautions;
    /**
     * 午餐热量
     */
    private Integer mealKcal;
    /**
     * 碳水化合物
     */
    private Integer carbohydrate;
    /**
     * 蛋白质
     */
    private Integer protein;
    /**
     * 脂肪
     */
    private Integer fat;

    /**
     * 早餐-分类
     */
    private List<FoodReportItemDto> breakFastKind;
    /**
     * 上午加餐-分类
     */
    private List<FoodReportItemDto> morningExtraMealKind;
    /**
     * 午餐-分类
     */
    private List<FoodReportItemDto> lunchKind;
    /**
     * 下午加餐-分类
     */
    private List<FoodReportItemDto> noonExtraMealKind;
    /**
     * 晚餐-分类
     */
    private List<FoodReportItemDto> dinnerKind;
    /**
     * 晚上加餐-分类
     */
    private List<FoodReportItemDto> nightExtraMealKind;

    /**
     * 早餐
     */
    private List<FoodReportItemDto> breakFast;
    /**
     * 上午加餐
     */
    private List<FoodReportItemDto> morningExtraMeal;
    /**
     * 午餐
     */
    private List<FoodReportItemDto> lunch;
    /**
     * 下午加餐
     */
    private List<FoodReportItemDto> noonExtraMeal;
    /**
     * 晚餐
     */
    private List<FoodReportItemDto> dinner;
    /**
     * 晚上加餐
     */
    private List<FoodReportItemDto> nightExtraMeal;
}
