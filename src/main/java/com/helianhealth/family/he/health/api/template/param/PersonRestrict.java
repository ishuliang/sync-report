package com.helianhealth.family.he.health.api.template.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lijun
 */
@Data
public class PersonRestrict implements Serializable {
    private static final long serialVersionUID = -8652676038358062953L;
    /**
     * 0: 不限 1：男 2：女
     */
    Integer sex;
    Integer age;
}
