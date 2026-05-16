package com.helianhealth.family.he.admin.api.member.param;

import lombok.Data;

/**
 * @author tx
 * @date 2022/10/27 18:07
 */
@Data
public class PickDiseaseParam {

    /**
     * 客户id
     */
    private Integer memberId;

    /**
     * 疾病模板id
     */
    private Integer diseaseId;

}
