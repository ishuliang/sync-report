package com.helianhealth.family.he.health.api.report.dto;

import com.helianhealth.family.he.health.api.health.bo.IcdBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author bellliu .
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveReportDto implements Serializable {

    /**
     * 类型：0深对接报告；1手动pdf；2手动文本
     */
    private Integer type;

    /**
     * 档案id
     */
    private Integer memberId;

    /**
     * 报告所属医院
     */
    private String reportStationName;

    /* ***************************** 结构化报告 ***************************** */

    /**
     * 总检
     */
    private String summary;

    /**
     * 建议
     */
    private String suggest;

    /**
     * 医院名称
     */
    private String stationName;

    /**
     * 姓名
     */
    private String customerName;

    /**
     * 体检日期 yyyy-MM-dd
     */
    private String examDate;

    /**
     * 报告id
     */
    private String reportId;

    /**
     * 报告是否结构化
     */
    private boolean isReportStructured;

    /**
     * 阳性结论列表
     */
    List<ReportConclusionItemDto> conclusionList;

    /**
     * 检查项类表
     */
    List<ResolveReportItemDto> reportItemList;

    /* ***************************** pdf报告 ***************************** */

    /**
     * 内容：类型0和2是null，类型1是url
     */
    private String detail;

    public boolean getIsReportStructured() {
        return !(Objects.isNull(conclusionList) || CollectionUtils.isEmpty(conclusionList));
    }
    /**
     * 疾病标签（铜仁医院：标签绑在报告上）
     */
    private List<IcdBO> icds;
}
