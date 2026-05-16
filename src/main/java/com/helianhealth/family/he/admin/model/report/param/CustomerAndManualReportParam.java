package com.helianhealth.family.he.admin.model.report.param;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 三方建档并手动录入报告的一体化入参。
 * <p>
 * 建档部分使用精简 {@link CustomerArchiveCreateParam}；报告部分仍为 {@link ManualFillReportParam}。
 * 失败重试时可原样再次提交（客户已存在时仍会继续写入报告）。
 * </p>
 *
 * @author yufeng
 */
@Data
public class CustomerAndManualReportParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "渠道号不能为空")
    private String stationId;
    /**
     * 建档参数（精简字段，服务端转换为 {@link com.helianhealth.family.he.health.api.member.param.ArkMemberInfoParam}）
     */
    @NotNull(message = "archiveCreateParam 不能为空")
    @Valid
    private CustomerArchiveCreateParam archiveCreateParam;

    /**
     * 报告参数，与 {@code ArkAdminReportService#manualFillReport} 一致；
     * 未传 {@link ManualFillReportParam#getMemberId()} 时由建档结果自动回填
     */
    @NotNull(message = "manualFillReportParam 不能为空")
    @Valid
    private ManualFillReportParam manualFillReportParam;
}
