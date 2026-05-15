package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author bellliu
 * @date 2021/11/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportLiveOutDto implements Serializable {
    /**
     * 指标日期
     */
    private String questionnaireDate;

    /**
     * 完成度 0-1
     */
    private Double questionnaireFinish;

    /**
     * 生活分
     */
    private Integer liveScore;

    /**
     * 底部内容文案->小于60分会出现
     */
    private String liveContent;

    /**
     * 饮食标题
     */
    private String dietTitle;

    /**
     * 饮食内容
     */
    private String dietContent;
    /**
     * 饮食危险因素
     */
    private List<String> dietFactor;

    /**
     * 运动标题
     */
    private String sportTitle;
    /**
     * 运动内容
     */
    private String sportContent;
    /**
     * 运动危险因素
     */
    private List<String> sportFactor;

    /**
     * 吸烟标题
     */
    private String smokeTitle;

    /**
     * 吸烟内容
     */
    private String smokeContent;

    /**
     * 吸烟危险因素
     */
    private List<String> smokeFactor;

    /**
     * 饮酒标题
     */
    private String drinkTitle;

    /**
     * 饮酒内容
     */
    private String drinkContent;

    /**
     * 饮酒危险因素
     */
    private List<String> drinkFactor;

}
