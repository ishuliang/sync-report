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
public class SportReportItemOutDto implements Serializable {

    /**
     * 推荐运动id
     */
    private Integer sportId;

    /**
     * 推荐运动名称
     */
    private String sportName;

    /**
     * 推荐运动图片地址
     */
    private String ossUrl;

    /**
     * 推荐运动时长
     */
    private Integer minute;
}
