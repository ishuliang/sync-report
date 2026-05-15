package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author bellliu
 * @date 2021/11/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamReportOutDto implements Serializable {

    /**
     * 基础项目
     */
    private List<String> baseExamNameList;

    /**
     * 推荐项目
     */
    private List<String> recommendExamNameList;
}
