package com.helianhealth.family.he.admin.api.member.param;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 三方/精简场景下的建档入参，字段为 {@link com.helianhealth.family.he.health.api.member.param.ArkMemberInfoParam} 的子集，
 * 服务端会转换为完整建档对象再调用既有建档逻辑。
 *
 * @author yufeng
 */
@Data
public class CustomerArchiveCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 证件类型：1身份证 2出生证 3健康证
     */
    @NotNull(message = "证件类型不能为空")
    @Min(value = 1, message = "证件类型取值 1～3")
    @Max(value = 3, message = "证件类型取值 1～3")
    private Integer idCardType;

    /**
     * 身份证号（证件类型为身份证等时使用）
     */
    @NotBlank(message = "身份证号不能为空")
    private String idCardNo;

    /**
     * 出生证号（证件类型为出生证时使用，可选）
     */
    private String birthCertificateNumber;

    /**
     * 性别：1男 2女
     */
    @NotNull(message = "性别不能为空")
    @Min(value = 1, message = "性别取值 1 或 2")
    @Max(value = 2, message = "性别取值 1 或 2")
    private Integer gender;

    /**
     * 手机号码
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /**
     * 年龄
     */
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能为负")
    @Max(value = 150, message = "年龄不合法")
    private Integer age;

    /**
     * 工作单位
     */
    private String company;

    /**
     * 身高（cm）
     */
    private Double height;

    /**
     * 体重（kg）
     */
    private Double weight;

    /**
     * 婚姻状况：0未婚／1已婚（含再婚）／2离异／3丧偶
     */
    @Min(value = 0, message = "婚姻状况取值 0～3")
    @Max(value = 3, message = "婚姻状况取值 0～3")
    private Integer married;

    /**
     * 最近体检医院
     */
    private String lastReportStation;

    /**
     * 疾病标签是否自动匹配
     */
    private Boolean icdAuto;

    /**
     * 客户标签，多个逗号分割
     */
    private String tags;

    /**
     * 疾病标签，多个逗号分割
     */
    private String icds;
}
