package com.helianhealth.family.he.health.api.template.param;

import lombok.Getter;

/**
 * @author lijun
 */

@Getter
public enum MathOperator {
    /**
     * 加
     */
    ADDITION("+"),
    /**
     * 减
     */
    SUBDUCTION("-"),
    /**
     * 乘
     */
    MULTIPLICATION("*"),
    /**
     * 除
     */
    DIVISION("/");

    /**
     * 操作符
     */
    private String operator;

    MathOperator(String operator) {
        this.operator = operator;
    }

    public static MathOperator getEnumByOperator(String operator) {
        for (MathOperator value : MathOperator.values()) {
            if (value.operator.equals(operator)) {
                return value;
            }
        }
        return null;
    }
}
