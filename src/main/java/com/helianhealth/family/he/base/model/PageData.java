package com.helianhealth.family.he.base.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lenovo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageData<T> implements Serializable {

  private static final long serialVersionUID = -5559738108027112213L;

  private long total;

  private List<T> list;


  public static <T> PageData<T> empty() {
    return new PageData<>(0, Collections.emptyList());
  }


  public static <T> PageData<T> of(List<T> data, long totalNum) {
    PageData<T> pageData = new PageData<>();
    pageData.setList(data);
    pageData.setTotal(totalNum);
    return pageData;
  }

  /**
   * 逻辑分页
   *
   * @param <T>
   * @return
   */
  public static <T, E> PageData<T> logic(List<E> data, Query query, Function<E, T> convert) {
    List<T> ret = new ArrayList<>();
    for (int i = 0; i < query.getPageSize(); i++) {
      int index = query.getPageSize() * (query.getPageNum() - 1) + i;
      if (index < data.size() && index >= 0) {
        E info = data.get(index);
        ret.add(convert.apply(info));
      }
    }
    return PageData.of(ret, data.size());
  }
}
