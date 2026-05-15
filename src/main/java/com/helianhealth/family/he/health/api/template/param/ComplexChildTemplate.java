package com.helianhealth.family.he.health.api.template.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ComplexChildTemplate implements Serializable {
    private static final long serialVersionUID = 8579007589397646704L;
    private ChildTemplate childTemplate;
    private List<String> nodes;
    private List<MathOperator> operators;
    private Double unitConvert;
}
