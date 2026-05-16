package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author bellliu .
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveReportNodeDto implements Serializable {

    /**
     * 指标id:可能为空
     */
    private String nodeId;

    /**
     * 指标名称
     */
    private String nodeName;

    /**
     * 标准指标名称
     */
    private String hlNodeName;

    /**
     * 0定量 1定性
     */
    private Integer nodeType;

    /**
     * 标准指标Id
     */
    private String hlNodeId;

    /**
     * 指标结果
     */
    private String nodeValue;

    /**
     * 异常状态 0正常1异常2其他
     */
    private Integer nodeStatus;

    /**
     * 异常描述：偏高/偏低，阴/阳
     */
    private String nodeAbnormalDes;

    /**
     * 关联问卷
     */
    private String relationQuestionnaire;

    /**
     * 指标异常解读
     */
    private String nodeAbnormalInterpret;

    /**
     * 指标解析
     */
    private String nodeAnalysis;

    /**
     * 专业建议
     */
    private String nodeAdvise;

    /**
     * 单位
     */
    private String unit;

    /**
     * 范围
     */
    private String range;

    /**
     * 诊断
     */
    private String diacrisis;

}
