package com.helianhealth.family.he.health.api.template.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lijun
 */
@Data
public class TagCalcParam implements Serializable {
    private static final long serialVersionUID = -3541764162168472521L;
    private List<RuleTemplate> ruleTemplates;
    private List<UserInfo> users;
}
