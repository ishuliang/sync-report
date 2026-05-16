package com.helianhealth.family.he.health.api.template.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lijun
 */
@Data
public class RuleTemplate implements Serializable {
    private static final long serialVersionUID = 8760849920039297333L;
    private String id;
    private boolean isStandard;
    private String examDiseaseId;
    private Integer examDiseaseRiskLevel;
    private Integer needMatchConditionNumber;
    private List<ChildTemplate> cts;
    private List<ComplexChildTemplate> complexCts;
}
