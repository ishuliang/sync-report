package com.helianhealth.family.he.health.api.member.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArkMemberInfoNumBO implements Serializable {
    private static final long serialVersionUID = -3344629219203269955L;

    /**
     * 医生id
     */
    private Integer principalDoctor;

    /**
     * 数据统计
     */
    private Integer count;

}
