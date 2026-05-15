package com.helianhealth.family.he.health.api.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author bellliu
 * @date 2022/10/18
 */
@Data
public class ReportPositiveDto implements Serializable {
    /**
     * 阳性等级
     */
    private Integer level;
    /**
     * 阳性项目列表
     */
    private List<ReportPositiveItemDto> itemList;
}
