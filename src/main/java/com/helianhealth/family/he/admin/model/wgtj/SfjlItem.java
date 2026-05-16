package com.helianhealth.family.he.admin.model.wgtj;

import lombok.Data;

import java.util.Map;

@Data
public class SfjlItem {
    /** 随访扩展字段，因随访类型不同内容各异，用 Map 接收 */
    private Map<String, Object> ext;
    private String suifangDw;
    private String suifangRq;
    private String createTime;
    private String suifangLx;
    private String updateTime;
    private String suifangYs;
    private String jlid;
    private String suifangBz;
}
