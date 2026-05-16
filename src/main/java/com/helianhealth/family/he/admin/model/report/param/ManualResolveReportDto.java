package com.helianhealth.family.he.admin.model.report.param;

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
