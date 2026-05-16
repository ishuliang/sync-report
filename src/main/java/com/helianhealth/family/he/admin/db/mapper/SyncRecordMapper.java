package com.helianhealth.family.he.admin.db.mapper;

import com.helianhealth.family.he.admin.db.entity.SyncRecord;

public interface SyncRecordMapper {

    /**
     * 插入一条记录（已存在则忽略，基于 UNIQUE(hospital_fid, month, shenfenzh)）
     * 返回影响行数：1=新插入，0=已跳过
     */
    int insertIgnore(SyncRecord record);
}
