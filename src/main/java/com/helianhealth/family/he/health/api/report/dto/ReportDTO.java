package com.helianhealth.family.he.health.api.report.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * @description
 * @Author RHS
 * @date 2020/8/26 16:59
 */
@Data
public class ReportDTO implements Serializable {

  private static final long serialVersionUID = 505413105995088640L;
  private Integer type;
  private Integer status;
  private String mark;
  private String reportId;
  private String memberName;
  private String stationName;
  private Date examTime;
  private Date gmtCreatetime;
  private List<String> imageUrls;
}
