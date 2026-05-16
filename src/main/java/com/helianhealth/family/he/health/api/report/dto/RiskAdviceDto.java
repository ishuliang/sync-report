package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAdviceDto implements Serializable {

    /**
     * 体检异常
     */
    private List<RiskAdv> reportRisk;

    /**
     * 生活异常
     */
    private List<RiskAdv> lifeRisk;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAdv implements Serializable {

        /**
         * 风险
         */
        private String risk;

        /**
         * 建议
         */
        private String adv;
    }
}
