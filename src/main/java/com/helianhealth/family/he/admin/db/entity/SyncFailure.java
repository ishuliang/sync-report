package com.helianhealth.family.he.admin.db.entity;

import lombok.Data;

import java.util.Date;

/**
 * 同步失败明细：用于精准重试
 */
@Data
public class SyncFailure {

    private Long id;

    /** 关联 sync_task.id */
    private Long taskIdRef;

    private String hospitalFid;

    private String month;

    /** 失败时的客户姓名（便于排查） */
    private String customerName;

    /** 失败时的客户身份证号（便于幂等） */
    private String customerIdno;

    /** 失败阶段：DATA_PREPARE / TASK_QUERY / DOWNLOAD / PUSH_GATEWAY */
    private String stage;

    /** 失败时的请求 JSON，便于重推 */
    private String payload;

    private String errorMsg;

    private Integer retryTimes;

    /** 0 未处理，1 重试成功 */
    private Integer resolved;

    private Date createTime;

    private Date lastRetryTime;

    private Date resolvedTime;
}
