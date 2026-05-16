package com.helianhealth.family.he.base.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public enum ResultCode implements IResultCode {
  /**
   * 正常
   */
  NORMAL(0, "", 0),
  /**
   * 正常(三诺)
   */
  SINO_NORMAL(200, "", 0),

  MESSAGE_MENTION_TIPS(1000, "已经提醒过2次了~请尝试电话联系吧", 0),

  PARAMS_EMPTY(1, "", 1),

  /** ========== 系统错误码 ========== **/

  /**
   * Token过期
   */
  NOT_FOUND(404, "不存在的API", 1),
  TOKEN_ERROR(401, "登录已失效,请重新登录", 1),
  SIGN_ERROR(20029, "签名失败", 2),
  NOT_AUTH(20030, "权限不足", 2),
  /**
   * 系统错误
   */

  ERROR_SYSTEM(10001, "系统错误,请稍后再试！", 1),
  TIME_OUT(10002, "请求超时，请稍后再试！", 1),
  PARAM_INVALID(10003, "参数非法，请检查后重试", 1),
  REQUEST_FREQUENT(10004, "您的操作过于频繁，请稍后再试", 1),

  /**
   * 账号存在风险
   */
  RISK_ERROR(10005, "账号存在风险！", 1),

  /**
   *
   */
  IDEMPOTENT_ERROR(10015, "不能重复提交，请稍后重试！", 1),

  /**
   * 数据库查询失败
   */
  ERROR_SYSTEM_DATABASE_QUERY(10011, "数据库查询失败！", 1),

  /**
   * 数据库存储失败
   */
  ERROR_SYSTEM_DATABASE_INSERT(10012, "数据库存储失败！", 1),

  /**
   * 数据库更新失败
   */
  ERROR_SYSTEM_DATABASE_UPDATE(10013, "数据库更新失败！", 1),

  /** ========== 应用错误码 ========== **/

  /**
   * 传入的参数有误，验证不能通过
   */
  PARAM_WRONG(20000, "传入的参数有误，验证不能通过！", 2),

  /**
   * 验证码已过期
   */
  VALID_CODE_EXPIRE_ERROR(20002, "验证码已过期！", 2),

  /**
   * 发送验证码过于频繁
   */
  SMS_VALID_CODE_REQUEST_FREQUENT(20006, "发送验证码过于频繁！", 2),
  /**
   * 短信验证码失效
   */
  INVALID_SMS_VALID_CODE(20007, "短信验证码失效！", 2),

  /**
   * 数据为空
   */
  DATA_EMPTY(2000001,"数据为空",2),
  /**
   * 该用户不存在，请注册
   */
  USER_NOT_EXISTS_ERROR(20003, "账号或密码不正确，请重试！", 2),

  /**
   * 密码修改失败
   */
  PASSWORD_CHANGE_FAILED_ERROR(20004, "密码修改失败！", 2),
  /**
   * 账号已被冻结
   */
  ACCOUNT_HAS_BEEN_FROZEN_ERROR(20009, "账号已被停用！", 2),
  /**
   * 优惠券异常
   */
  RECEIVE_COUPON_RECEIVE_ERROR(20282, "已经领取优惠券", 2),

  /**
   * 密码错误
   */
  PASSWORD_FAILED_ERROR(20005, "账号或密码不正确，请重试！", 2),

  BIZ_ERROR(20234, "业务异常！", 2),

  SERVICE_NOT_SUPPORT(20410, "接口不支持", 2),

  AREA_CODE_ERROR(20217, "区域编码有误", 2),

  DATA_NOT_FOUND(20470, "数据不存在", 2),
  RECORD_ALREADY_EXIST(20135, "记录已经存在", 2),
  EACRD_EXCHANGE_LIMIT(20407, "E卡兑换次数超限", 2),
  DATA_UNEXPECTED(20408, "数据不符预期", 2),
  DATA_NO_PERMISSION(20409, "无权操作", 2),
  STATUS_ERROR(20195, "操作状态有误！", 2),
  ECARD_EXCHANGE_ERROR(20411, "e卡兑换失败", 2),
  /**
   * 验证码不匹配
   */
  VALICODE_NOT_EQUEL_ERROR(20014, "验证码不匹配！", 2),

  /**
   * 信息获取失败
   */
  DATA_REQUIRD_ERROR(20008, "信息获取失败！", 2),

  HNC_INVALID_PHONE_NUM(20104, "非法手机号码", 2),

  GET_BIND_USERINFO_FAIL(20314, "获取用户信息失败", 2),

  PHONE_BIND_OTHER_WX(20315, "该手机号已绑定微信号【%s】，如您需重新绑定，请联系客服处理", 2),


  UPDATE_WX_USERINFO_FAIL(20313, "更新用户信息失败", 2),


  WX_BIND_OTHER_PHONE(20316, "该微信号已绑定手机号【%s】，如您需重新绑定，请联系客服处理", 2),

  WX_MINI_APP_SESSION_KEY_EXPIRED(20284, "小程序sessionKey已过期", 2),

  ECARD_UNBOUND(20401, "e卡未绑定", 2),
  ECARD_BINDED(20402, "e卡已绑定", 2),
  ECARD_CONSUME(20403, "e卡已用完", 2),
  ECARD_EXPIRE(20404, "e卡已过期", 2),
  ECARD_DISABLE(20405, "e卡已禁用", 2),

  EACRD_NOT_SUPPORT_BUY(20406, "不支持购买", 2),
  ECARD_TEMPLATE_EXPIRE(20412, "e卡模板已过期", 2),

  RECODE_NOT_FOUND(20116, "信息不存在", 2),

  REST_MONEY_NOT_ENOUGH(20120, "余额不足", 2),

  USER_REFUND_RESEND(20370, "重复发起退款申请", 2),


  NOT_ALLOW_REFUND_APPLY(20371, "非已付款订单，用户发起退款", 2),

  ORDER_NOT_EXISTS(20119, "订单不存在", 2),

  GET_GOLD_COUPON_DETAIL_ERROR(20317, "用户体验金兑换明细查询操作失败!", 2),

  GOLD_COUPON_AVAILABLE_SHOW_ERROR(20318, "体验金可兑换的优惠券展示操作失败!",
      2),

  GOLD_COUPON_CONVERT_OPERATION_ERROR(20319, "用户体验金兑换商品失败!",
      2),

  REPORT_IS_DELETE_OR_NOT_ERROR(20160, "体检报告不存在", 2),
  COMMON_PDF_REPORT_NO_RECORD(20345, "ERROR_PDF_REPORT_NO_RECORD", "PDF报告正在生成中", 2),
  COMMON_PDF_REPORT_NO_USER(20346, "ERROR_PDF_REPORT_NO_USER", "PDF报告基本信息不存在", 2),
  COMMON_PDF_REPORT_NO_ITEM(20347, "ERROR_PDF_REPORT_NO_ITEM", "PDF报告项目信息不存在", 2),

  OPERATION_FAILED(20107, "操作失败", 2),

  WX_GET_OPENID_ERROR(20210, "获取微信失败", 2),

  TIME_ERROR(20295, "当前不在提现时间段", 2),

  TRANSFER_ERROR(20288, "转账错误", 2),

  AUDIT_ORDER_ALREADY_EXIST(20364, "报销助力进行中，请流程结束后再重新发起～", 2),

  GET_REDUCE_DETAIL_PAGE_FAILED(20365, "获取砍价详情页失败", 2),

  /**
   * 该手机号已注册
   */
  ACCOUNT_HAS_BEEN_EXISTS_ERROR(20011, "该手机号已注册！", 2),

  ALSP_ALREADY_ADD(20283, "商品已经加入自有库", 2),

  TODAY_SIGN_ERROR(20141, "重复操作", 2),

  LOGIN_TIMES_ERROR(20300, "TODAY_SIGN_ERROR", "登录错误次数过多,请30分钟后再试！", 2),

  TEMP_RULE_COUNT_EXCEED(20310,"TEMP_RULE_COUNT_EXCEED","模板规则数量超限",2),

  TEMP_NAME_REPEAT(20311,"TEMP_NAME_REPEAT","模板名称已存在",2),

  MULTI_TEMP_REPEAT(20321,"MULTI_TEMP_REPEAT","已存在该多病种组合模板",2),

  TEMP_NOT_EXIST(20338,"TEMP_NOT_EXIST","模板不存在",2),

  JOB_NOT_EXIST(20312,"JOB_NOT_EXIST","任务不存在",2),

  MEMBER_EXISTED(20340,"MEMBER_EXISTED","客户已存在",2),

  MEMBER_NOT_EXIST(20334,"MEMBER_NOT_EXIST","客户不存在",2),

  MEDDLE_GENERATE_FAILURE(20997, "MEDDLE_GENERATE_FAILURE", "创建干预计划失败", 2),

  MEDDLE_DATE_ILLEGAL(20998, "MEDDLE_DATE_ILLEGAL", "干预时间小于当前时间，请重新设置！", 2),

  DISEASE_DELETE_FAIL(20999, "DISEASE_DELETE_FAIL", "该疾病存在历史数据，无法删除！", 2),
  HL_STATION_SYNC_FAIL(20354, "HL_STATION_SYNC_FAIL", "禾连机构不可同步知识库", 2),
  TEMPLATE_IS_EMPTY(20898, "TEMPLATE_IS_EMPTY", "没有找到已启用的模板", 2),
  ITEM_MISS(21011, "ITEM_MISS", "指标项缺失", 2),
  INDICATOR_USING(21010, "INDICATOR_USING", "指标项被使用中，无法删除", 2),
  NODE_ITEM_EXISTED(22010, "NODE_ITEM_EXISTED", "指标项已存在", 2),

  TEMP_HAS_BINDED(20996, "TEMP_HAS_BINDED", "当前已被关联，暂无法删除", 2),

  NEED_STATION_ID(21001, "NEED_STATION_ID", "缺少stationId字段", 2),

  TEMP_NAME_ALREADY_EXISTS(21003, "TEMP_NAME_ALREADY_EXISTS", "模版名称已存在", 2),

  BASE_STATION_CANNOT_SYNC(21005, "BASE_STATION_CANNOT_SYNC", "当前机构是禾连健康，不支持同步", 2),

  NEED_TEMP_NAME(21007, "NEED_TEMP_NAME", "需要传递干预模版名称", 2),

  PKG_SERVICE_USED(21100, "PKG_SERVICE_USED", "该项目已被服务包绑定，请先解绑", 2),

  /**
   * 企微相关
   */
  WORK_WECHAT_NOT_BOUND(21101, "WORK_WECHAT_NOT_BOUND", "健管师暂未绑定企业微信", 2),

  /**
   * 微信相关
   */
  WECHAT_GET_PHONE_ERROR(21110, "WECHAT_GET_PHONE_ERROR", "获取用户手机号失败", 2),

  /**
   * 自动配置
   */
  AUTO_MEDDLE_GROUP_REPEAT(21200, "AUTO_MEDDLE_GROUP_REPEAT", "客户分组不能重复", 2),
  AUTO_MEDDLE_ICD_REPEAT(21201, "AUTO_MEDDLE_ICD_REPEAT", "疾病风险标签不能重复", 2),

  /**
   * 疾病标签
   */
  DISEASE_ID_EMPTY(21202, "DISEASE_ID_EMPTY", "请先添加疾病标签", 2),

  /**
   * 团检报告配置
   */
  PREFACE_TOO_LONG(21130, "PREFACE_TOO_LONG", "前言不能超过20000字", 2),
  CONCLUSION_TOO_LONG(21131, "CONCLUSION_TOO_LONG", "结束语不能超过20000字", 2),
  HIGH_RISK_EMPTY(21132, "HIGH_RISK_EMPTY", "请配置高危预警专项分析", 2),
  BIZ_ERROR_NOT_MATCH_NUM_LIMIT(21133, "BIZ_ERROR_NOT_MATCH_NUM_LIMIT", "未达到最低报告限制：5", 2),
  HIGH_RISK_WRONG(21134, "HIGH_RISK_WRONG", "高危预警专项分析配置错误", 2),

  /**
   * 角色相关
   */
  USER_ROLE_ALREADY_BIND(21120, "USER_ROLE_ALREADY_BIND", "用户和角色已绑定，请勿重复绑定", 2),
  ROLE_NAME_REPEAT(21121, "ROLE_NAME_REPEAT", "角色名称重复", 2),
  ROLE_NAME_TOO_LONG(21122, "ROLE_NAME_TOO_LONG", "角色名称上限20字", 2),
  ROLE_DESCRIPTION_TOO_LONG(21123, "ROLE_DESCRIPTION_TOO_LONG", "角色说明上限50字", 2),

  /**
   * 饮食
   */
  DIET_WEIGHT_EMPTY(21140, "DIET_WEIGHT_EMPTY", "推荐重量需要在1-2000克之间", 2),
  DIET_DESCRIPTION_TOO_LONG(21141, "DIET_DESCRIPTION_TOO_LONG", "饮食说明不能超过10000字", 2),
  DIET_PLAN_TYPE_EMPTY(21142, "DIET_PLAN_TYPE_EMPTY", "请至少选择一个方案。", 2),
    NO_REPORT_DATA(30011, "NO_REPORT_DATA", "没有查询到任何体检报告数据", 2);


  private final String msg;

  private final int code;

  /**
   * 错误类型（0:一般结果码；1：系统级的错误；2：应用级的错误）
   */
  private final int type;

  @Deprecated
  ResultCode(int code, String codeStr, String msg, int type) {
    this.msg = msg;
    this.code = code;
    this.type = type;
  }

  ResultCode(int code, String msg, int type) {
    this.msg = msg;
    this.code = code;
    this.type = type;
  }


  private final static ResultCode[] all = ResultCode.values();

  public static ResultCode of(Integer v) {
    for (int i = 0; i < all.length; i++) {
      if (Objects.equals(all[i].getCode(), v)) {
        return all[i];
      }
    }
    return null;
  }

  public static ResultCode of(Integer v, ResultCode defaultValue) {
    ResultCode of = of(v);
    if (of == null) {
      return defaultValue;
    }
    return of;
  }

  public static boolean contains(Integer v) {
    for (int i = 0; i < all.length; i++) {
      if (Objects.equals(all[i].getCode(), v)) {
        return true;
      }
    }
    return false;
  }

  public static void check(Integer v) {
    for (int i = 0; i < all.length; i++) {
      if (Objects.equals(all[i].getCode(), v)) {
        return;
      }
    }
    throw new IllegalArgumentException(String.format("ResultCode: value %s not found", v));
  }

  //自检返回码是否重复
  static {
    ResultCode[] values = ResultCode.values();
    Map<Integer, ResultCode> m = new HashMap<>();
    for (ResultCode rc : values) {
      int code = rc.getCode();
      if (m.containsKey(code)) {
        throw new SecurityException("返回状态码存在重复code值," + m.get(code) + "," + rc);
      } else {
        m.put(code, rc);
      }
    }
  }
}
