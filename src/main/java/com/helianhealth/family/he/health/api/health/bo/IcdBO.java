package com.helianhealth.family.he.health.api.health.bo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author shenluw
 * @date 2021/11/23 11:27
 */
@Data
@AllArgsConstructor
public class IcdBO implements Serializable {

  private static final long serialVersionUID = 4619314604326572565L;

  /**
   * 疾病ICD
   */
  private String icdId;


  /**
   * 疾病名称
   */
  private String icdName;

  /**
   * 忽略该字段，前端区分疾病0 和干预模版1的
   */
  private Integer type;
  public IcdBO() {

  }

  public IcdBO(String icdId, String icdName) {
    this.icdId = icdId;
    this.icdName = icdName;
  }
}
