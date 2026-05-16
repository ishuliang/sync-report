package com.helianhealth.family.he.health.api.template.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lijun
 */
@Data
public class ChildTemplate implements Serializable {
    private static final long serialVersionUID = 4875804988261585375L;
    private String nodeId;
    private Integer operate;
    private BigDecimal highestValue;
    private BigDecimal lowestValue;
    private String keywords;
    private PersonRestrict pr = new PersonRestrict();
}
