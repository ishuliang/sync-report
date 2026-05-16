package com.helianhealth.family.he.health.api.report.dto;

import com.helianhealth.family.he.health.api.member.dto.FamilyMemberInfoDTO;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 报告状态确认
 *
 * @author lee
 */
@Data
public class ReportCheckDTO implements Serializable {

  private static final long serialVersionUID = 505413105995088640L;


  /**
   * 没有报告的成员
   */
  private List<FamilyMemberInfoDTO> hasNoReportMembers;


  /**
   * 是否全部包含报告
   */
  private Boolean allHashReport;
}
