package com.helianhealth.family.he.health.api.member.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lijun
 */
@Data
public class ImportFailCustomerBO implements Serializable {
    private static final long serialVersionUID = 9021189197840097024L;
    /**
     * 客户姓名
     */
    private String name;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 身高
     */
    private Double height;
    /**
     * 体重
     */
    private Double weight;
    /**
     * 婚姻状况
     */
    private String married;
    /**
     * 工作单位
     */
    private String company;
    /**
     * 失败原因
     */
    private String failReason;
}
