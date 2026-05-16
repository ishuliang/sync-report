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
public class SportReportOutDto implements Serializable {

    /**
     * 运动推荐热量-可能为null
     */
    private Integer kcal;

    /**
     * 运动频次
     */
    private String frequency;

    /**
     * 运动时间
     */
    private String time;

    /**
     * 运动强度
     */
    private String intensity;

    /**
     * 运动注意事项
     */
    private String precautions;

    /**
     * 推荐运动列表
     */
    private List<SportReportItemOutDto> sportRecommendList;

    /**
     * 普通项目列表
     */
    private List<SportReportOriginalItemOutDto> sportList;

}
