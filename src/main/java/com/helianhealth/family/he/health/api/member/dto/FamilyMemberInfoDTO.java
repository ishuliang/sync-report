package com.helianhealth.family.he.health.api.member.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class FamilyMemberInfoDTO implements Serializable {

  private static final long serialVersionUID = 2104548930850605237L;

  private Integer id;

  /**
   * 用户ID 管理员
   */
  private String userId;

  /**
   * 头像
   */
  private String avatar;

  /**
   * 关系
   */
  private Integer relation;

  /**
   * 关系名称
   */
  private String relationName;

  /**
   * 姓名
   */
  private String name;

  /**
   * 生日
   */
  private Date birthday;

  /**
   * 性别  1男 2女
   */
  private Integer gender;

  /**
   * 是否长期居住
   */
  private Integer liveWith;

  /**
   * 婚姻状况
   */
  private Integer married;

  /**
   * 血型
   */
  private String bloodType;

  /**
   * 身份证号
   */
  private String idCardNo;

  /**
   * 手机号码
   */
  private String mobile;

  /**
   * 血缘关系
   */
  List<FamilyBloodRelationDTO> bloodRelations;

  /**
   * 身高
   */
  private Double height;

  /**
   * 体重
   */
  private Double weight;

  /**
   * BMI
   */
  private Double bmi;

  /**
   * 是否需要健康评估 true 需要
   */
  private Boolean needAnswer;

  /**
   * 体质名称
   */
  private String constitutionName;

  /**
   * 过敏史
   */
  private Boolean allergy;

  /**
   * 疾病史
   */
  private Boolean disease;

  /**
   * 手术史
   */
  private Boolean operation;

  /**
   * 腰围(cm)
   */
  private Double waistline;
  /**
   * 臀围(cm)
   */
  private Double hipline;
  /**
   * 家庭编号
   */
  @Deprecated
  private Integer familyId;

  /**
   * 档案拥有人Id
   */
  private String ownerId;

  /**
   * 创建时间
   */
  private Date gmtCreate;

}
