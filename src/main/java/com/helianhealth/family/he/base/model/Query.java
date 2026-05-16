package com.helianhealth.family.he.base.model;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Li Junqing
 **/
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Query<T> implements Serializable {

  private static final long serialVersionUID = 2800291946468264236L;
  private T data;
  private int pageNum = 1;
  private int pageSize = 10;

  public Query(T data) {
    this.data = data;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Query;
  }

  public int getStart() {
    return (this.pageNum - 1) * this.pageSize;
  }
}
