package com.helianhealth.family.he.health.api.report.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class ReportListDTO implements Serializable {

  private static final long serialVersionUID = -32841837443287527L;
  private String reportId;
  private String memberName;
  private String stationName;
  /**
   * 1 体检报告 2 智能解读
   */
  private Integer type;
  /**
   * 状态(1:未审核,2:已审核,3:用户已取消,4:平台已取消)
   */
  private Integer status;
  private Date examTime;
  private Date gmtCreatetime;
  /**
   * (0, "不显示"), (1, "可以提交"), (2, "审核中"),
   */
  private Integer workOrderStatus;

  /**
   * 是否需要解读 true 是
   */
  private Boolean interpretation;

  /**
   * 报告类型 1 image 2 pdf 3 结构化数据
   */
  private Integer reportType;

  /**
   * 是否是健管端上传
   *
   * <p>false 老报告，不在自己库里 true 在自己库里</p>
   */
  private boolean healthUpload;

  private String reportCode;
}
