package com.helianhealth.family.he.health.api.report.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author shenluw
 * @date 2021/9/23 13:59
 */
@Data
public class HistoryReportDTO implements Serializable {

  private static final long serialVersionUID = -7628195755989164685L;
  /**
   * 异常结果说明
   */
  String exceptionResult;
  /**
   * 异常指标列表
   */
  List<HistoryNode> nodeList;
  /**
   * 可以提工单的reportIdList
   */
  List<String> reportIdList;

  @Data
  public static class HistoryNode implements Serializable {

    private static final long serialVersionUID = 7196008559564015603L;

    /**
     * 异常结果描述
     */
    String abnormalDescription;
    /**
     * 指标项描述
     */
    String description;
    /**
     * 标准指标Id
     */
    String hlNodeId;
    /**
     * 标准指标名称
     */
    String hlNodeName;
    /**
     * 0数值1阴阳2文本
     */
    Integer noteType;
    /**
     * 历史指标
     */
    List<HistoryNodeValue> list;
  }

  @Data
  public static class HistoryNodeValue implements Serializable {

    private static final long serialVersionUID = 601588869908472149L;
    /**
     * 日期
     */
    String date;
    /**
     * 异常结果:偏高,偏低,阴性,阳性
     */
    String exceptionValue;
    /**
     * 指标结果-数值类型需要显示
     */
    String nodeValue;
  }
}
