package com.helianhealth.family.he.admin.db.mapper;

import com.helianhealth.family.he.admin.db.entity.SyncTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SyncTaskMapper {

    /** 新增任务记录，返回自增主键写入 task.id */
    int insert(SyncTask task);

    /** 全字段更新（按主键） */
    int update(SyncTask task);

    /** 按 (hospitalFid, month) 唯一键查询 */
    SyncTask selectByHospitalAndMonth(@Param("hospitalFid") String hospitalFid,
                                      @Param("month") String month);

    /** 查询所有失败任务，便于人工或自动重跑 */
    List<SyncTask> selectFailed();

    /** 仅更新远端 taskId 和 ZIP 密码（获取到后立即持久化） */
    int updateMeta(@Param("id") Long id,
                   @Param("taskId") String taskId,
                   @Param("zipPwd") String zipPwd);

    /** 仅更新状态与错误信息（轻量更新） */
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("errorMsg") String errorMsg);

    /** 查询已成功但 total_count=0 的任务（用于数据补偿重跑） */
    List<SyncTask> selectSuccessWithZeroTotal();

    /** 查询所有 total_count=0 的任务（不限状态，用于全量补偿重跑） */
    List<SyncTask> selectAllWithZeroTotal();
}
