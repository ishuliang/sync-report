package com.helianhealth.family.he.health.api.report.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author shenluw
 * @date 2021/9/23 13:51
 */
@Data
public class ReportExceptionDTO implements Serializable {

  private static final long serialVersionUID = -8487598606339659504L;

  /**
   * 报告结果状态 0正常 1有异常需要解读
   */
  Integer status;
  /**
   * 重要异常指标列表
   */
  List<ReportExceptionNode> nodeList;


  @Data
  public static class ReportExceptionNode implements Serializable {

    private static final long serialVersionUID = -3571637650812369520L;

    /**
     * 异常结果描述
     */
    String abnormalDescription;
    /**
     * 指标项描述
     */
    String description;


    /**
     * 诊断
     */
    String diacrisis;
    /**
     * 标准指标Id
     */
    String hlNodeId;
    /**
     * 标准指标名称
     */
    String hlNodeName;

    String itemMark;
    String itemNodeMark;
    /**
     * 指标id:可能为空
     */
    String nodeId;
    String nodeMark;
    /**
     * 指标名称
     */
    String nodeName;
    /**
     * 异常状态 0正常1异常2其他
     */
    Integer nodeStatus;
    /**
     * 指标结果
     */
    String nodeValue;
    /**
     * 范围
     */
    String nodeRange;
    /**
     * 单位
     */
    String nodeUnit;
    /**
     * 0数值1阴阳2文本
     */
    Integer noteType;
  }
}
