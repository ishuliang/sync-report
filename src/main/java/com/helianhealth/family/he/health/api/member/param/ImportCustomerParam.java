package com.helianhealth.family.he.health.api.member.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lijun
 */
@Data
public class ImportCustomerParam implements Serializable {
    private static final long serialVersionUID = -8184292091681812849L;
    /**
     * 客户id
     */
    private Integer id;
    /**
     * 客户姓名
     */
    private String name;

    /**
     * 证件类型：1身份证 2出生证 3健康证
     */
    private Integer idCardType = 1;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 性别：1男2女
     */
    private Integer gender;

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

    private String birthday;

    /**
     * 婚姻状况
     */
    private String married;
    /**
     * 工作单位
     */
    private String company;

    /**
     * 医院id
     */
    private String stationId;


    private Integer creator;
    @Override
    public int hashCode() {
        return idCardNo == null ? 0 : idCardNo.hashCode();
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null) {
            return false;
        }
        if (getClass() != another.getClass()) {
            return false;
        }
        ImportCustomerParam that = (ImportCustomerParam) another;
        return this.idCardNo.equals(that.getIdCardNo());
    }

}
