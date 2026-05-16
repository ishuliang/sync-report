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
public class SportReportOriginalItemOutDto implements Serializable {

    /**
     * 推荐运动id
     */
    private Integer sportId;

    /**
     * 推荐运动名称
     */
    private String sportName;

    /**
     * 30分钟卡路里
     */
    private Double kcal;
}
