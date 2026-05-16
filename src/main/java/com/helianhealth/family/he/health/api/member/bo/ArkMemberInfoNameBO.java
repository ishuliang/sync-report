package com.helianhealth.family.he.health.api.member.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author bellliu
 * @date 2022/4/12
 */
@Data
public class ArkMemberInfoNameBO implements Serializable {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 姓名
     */
    private String name;
}
