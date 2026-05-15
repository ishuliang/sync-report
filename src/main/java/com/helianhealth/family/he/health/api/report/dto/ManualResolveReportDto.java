package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author bellliu .
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualResolveReportDto implements Serializable {

    private Integer memberId;
    private String stationId;
    private String stationName;

    private ResolveReportDto resolveReportDto;

    /**
     * 单位信息
     */
    private CompanyInfoDTO companyInfo;
    /**
     * 批次信息
     */
    private BatchInfoDTO batchInfo;

    /**
     * 结论+建议+等级
     */
    private List<Conclusion> conclusions;

    @Data
    public static class Conclusion implements Serializable {
        /**
         * 结论
         */
        private String summary;

        /**
         * 建议
         */
        private String suggest;

        /**
         * 等级
         */
        private Integer level;
    }
}
