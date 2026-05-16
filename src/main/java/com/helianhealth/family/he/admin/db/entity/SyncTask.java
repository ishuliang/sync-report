package com.helianhealth.family.he.admin.db.entity;

import lombok.Data;

import java.util.Date;

/**
 * 同步任务记录：每个 (医院, 月份) 一条
 */
@Data
public class SyncTask {

    private Long id;

    /** 医院 fid（卫健委侧标识） */
    private String hospitalFid;

    /** 医院名称 */
    private String hospitalName;

    /** 同步月份 yyyy-MM */
    private String month;

    /** 卫健委侧任务 ID（dataPrepare 接口返回） */
    private String taskId;

    /** 下载 ZIP 文件的解压密码 */
    private String zipPwd;

    /** PENDING / RUNNING / SUCCESS / FAILED */
    private String status;

    /** 本次同步总条数 */
    private Integer totalCount;

    /** 推送成功条数 */
    private Integer successCount;

    /** 推送失败条数 */
    private Integer failCount;

    /** 错误信息（任务级失败时填写） */
    private String errorMsg;

    /** 当前任务被重试次数（失败后再次执行时累加） */
    private Integer retryTimes;

    private Date createTime;

    private Date updateTime;
}
