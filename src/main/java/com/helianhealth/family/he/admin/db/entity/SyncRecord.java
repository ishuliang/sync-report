package com.helianhealth.family.he.admin.db.entity;

import lombok.Data;

import java.util.Date;

/**
 * temp 目录 JSON 文件解析记录：每条 GwtjRecord 对应一行
 */
@Data
public class SyncRecord {

    private Long id;

    /** 医院 fid（从文件夹名解析） */
    private String hospitalFid;

    /** 同步月份 yyyy-MM（从文件夹名解析） */
    private String month;

    /** dangan_info.xingming 姓名 */
    private String xingming;

    /** dangan_info.shenfenZh 身份证号 */
    private String shenfenzh;

    /** tijian_info.createTime 体检日期 */
    private String tijianDate;

    /** 完整的 GwtjRecord JSON 字符串 */
    private String jsonContent;

    private Date createTime;
}
