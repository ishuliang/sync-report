package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoDTO implements Serializable {


    private String companyId;
    private String companyName;
    private String contactName;
    private String contactPhone;


}
