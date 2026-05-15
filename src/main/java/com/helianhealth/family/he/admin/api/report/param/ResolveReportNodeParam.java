package com.helianhealth.family.he.admin.api.report.param;

import lombok.Data;

/**
 * @author tx
 * @date 2022/11/4 13:30
 */
@Data
public class ResolveReportNodeParam {

    /**
     * 指标项id:可能为空
     */
    private String nodeId;

    /**
     * 指标项名称
     */
    private String nodeName;

    /**
     * 指标结果
     */
    private String nodeValue;

    /**
     * 范围
     */
    private String range;

    /**
     * 单位
     */
    private String unit;

    /**
     * 状态。0 - 正常；1 - 异常
     */
    private Integer nodeStatus;

    /**
     * 类型。0 - 定性；1 - 定量
     */
    private Integer nodeType;

}
