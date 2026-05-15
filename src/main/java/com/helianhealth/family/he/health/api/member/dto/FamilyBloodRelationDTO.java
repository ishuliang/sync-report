package com.helianhealth.family.he.health.api.member.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 家庭成员血缘关系
 *
 * @author lee
 */
@Data
public class FamilyBloodRelationDTO implements Serializable {

  private static final long serialVersionUID = -451059122021370865L;

  /**
   * 成员id
   */
  private Integer memberId;

  /**
   * 成员名称
   */
  private String memberName;

  /**
   * 关系id
   */
  @Deprecated
  private Integer relationId;

  /**
   * 关系名称
   */
  @Deprecated
  private String relationName;

}
