package com.helianhealth.family.he.health.api.member.bo;

import com.helianhealth.family.he.health.api.member.bo.ImportFailCustomerBO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lijun
 */
@Data
public class ImportJobBO implements Serializable {
    private static final long serialVersionUID = -4864591374293743993L;
    /**
     * 自增主键
     */
    private Integer id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 任务状态(0:未执行,1:执行中,2:执行成功,3:执行失败)
     */
    private Integer status;

    /**
     * 失败数据
     */
    private List<ImportFailCustomerBO> failData;

    /**
     * 导入成功数据量
     */
    private Integer successCount;

    /**
     * 导入失败数据量
     */
    private Integer failCount;
}
