package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author bellliu
 * @date 2021/11/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDiseaseDto implements Serializable {

    /**
     * ICD编码
     */
    private String icd;
    /**
     * 名称
     */
    private String name;

    @Override
    public String toString() {
        return this.name;
    }
}
