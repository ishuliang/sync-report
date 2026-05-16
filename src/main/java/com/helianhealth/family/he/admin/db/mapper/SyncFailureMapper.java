package com.helianhealth.family.he.admin.db.mapper;

import com.helianhealth.family.he.admin.db.entity.SyncFailure;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SyncFailureMapper {

    int insert(SyncFailure failure);

    /** 查询所有未解决的失败明细 */
    List<SyncFailure> selectUnresolved();

    /** 标记为已解决 */
    int markResolved(@Param("id") Long id);

    /** 重试次数 +1 */
    int incrementRetry(@Param("id") Long id);
}
