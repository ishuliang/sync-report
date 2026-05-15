package com.helianhealth.family.he.base.model;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * 结果类
 *
 * @author Li Junqing
 **/
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

  private static final long serialVersionUID = 8275170170237759378L;

  /**
   * 是否成功
   */
  private boolean success;

  /**
   * 返回数据
   */
  private T data;

  /**
   * 错误代码
   */
  private int errorCode;

  /**
   * 返回消息
   */
  private String message;

  public <T2> Result<T2> toOther() {
    return toOther(null);
  }

  public <T2> Result<T2> toOther(T2 data) {
    if (Objects.equals(ResultCode.NORMAL.getCode(), errorCode)) {
      return createWithData(data);
    } else {
      return createWithError(errorCode, message);
    }
  }

  public static <T> Result<T> createWithData(T data) {
    Result<T> result = new Result<>();
    result.setData(data);
    result.setSuccess(true);
    return result;
  }

  public static Result<Void> createWithVoid() {
    Result<Void> result = new Result<>();
    result.setSuccess(true);
    return result;
  }

  public static Result<Void> createWithVoid(IResultCode errorCode) {
    Result<Void> result = new Result<>();
    result.setSuccess(true);
    result.setErrorCode(errorCode.getCode());
    result.setMessage(errorCode.getMsg());
    return result;
  }

  public static <T> Result<T> createWithError(int errorCode, String message) {
    Result<T> result = new Result<>();
    result.setSuccess(false);
    result.setErrorCode(errorCode);
    result.setMessage(message);
    return result;
  }

  public static <T> Result<T> createWithError(int errorCode, String message, T data) {
    Result<T> result = new Result<>();
    result.setSuccess(false);
    result.setErrorCode(errorCode);
    result.setMessage(message);
    result.setData(data);
    return result;
  }

  public Result(int errorCode, String message, T data) {
    super();
    this.setSuccess(false);
    this.setData(data);
    this.setErrorCode(errorCode);
    this.setMessage(message);
  }

  public static <T> Result<T> createWithResultCode(IResultCode errorCode) {
    if (ResultCode.NORMAL.getCode() == (errorCode.getCode())) {
      return createWithData(null);
    } else {
      return createWithError(errorCode.getCode(), errorCode.getMsg());
    }
  }

  @Deprecated
  public static <T> Result<T> createWithError(Result<?> source) {
    if (Objects.equals(ResultCode.NORMAL.getCode(), source.errorCode)) {
      return createWithData(null);
    } else {
      return createWithError(source.errorCode, source.message);
    }
  }
}

