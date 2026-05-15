package com.helianhealth.family.he.health.api.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: zhangkai
 * @createDate: 2022/8/8
 * @version: 1.0
 */
@Data
public class ESReviewUserDTO implements Serializable {

    private String sequenceId;

    /**
     * 预约id
     */
    private String reserveId;

    /**
     * 用户真实姓名
     */
    private String name;

    private String phone;

    private String idCard;

    /**
     * 0:未知、1:男、2:女
     */
    private String gender;

    private String birthday;

    private String age;

    private String address;

    private String knowledge;

    private String userType;

    private String vipLevel;

    private String workType;

    /**
     * 本次体检的类型，如婚检
     */
    private String examType;

    private String dept;

    private String companyId;

    private String companyName;

    private String batchId;

    private String batchName;

    private String companyTime;

    /**
     * 总检结论:每一条数据只包含一条结论。
     */
    private String summary;

    private String suggest;

    /**
     * 总检结论$总检建议$重症级别@
     */
    private String conclusion;

    private String level;

    private String summaryer;

    private String editor;

    /**
     * 检查时间
     * 精确到秒，格式：yyyy-MM-dd HH:mm:ss
     */
    private String checkTime;

    /**
     * 报告总检时间/发放日期
     * 精确到秒，格式：yyyy-MM-dd HH:mm:ss
     */
    private String lastUpdateTime;
}
