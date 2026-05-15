package com.helianhealth.family.he.health.api.template.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lijun
 */
@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -8798430121275858130L;
    private String id;
    private List<String> reportIds;
    private Integer sex;
    private Integer age;
}
