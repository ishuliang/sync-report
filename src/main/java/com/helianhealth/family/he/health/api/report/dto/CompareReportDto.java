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
public class CompareReportDto implements Serializable {

    /**
     * 左报告日期
     */
    private String leftDt;

    /**
     * 右报告日期
     */
    private String rightDt;

    /**
     * 对比指标
     */
    private List<CompareNode> nodeList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompareNode implements Serializable {
        /**
         * 指标id
         */
        private String nodeId;

        /**
         * 指标名
         */
        private String nodeName;

        /**
         * 左值
         */
        private String leftValue;

        /**
         * 右值
         */
        private String rightValue;

        /**
         * 趋势
         */
        private String trend;

        /**
         * 参考值
         */
        private String range;
    }
}
