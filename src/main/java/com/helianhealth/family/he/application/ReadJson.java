package com.helianhealth.family.he.application;

import com.helianhealth.family.he.admin.service.syncreport.GwtjReportService;

public class ReadJson {
    public static void main(String[] args) {
        GwtjReportService gwtjReportService = new GwtjReportService();
gwtjReportService.processJsonFile("/Users/ishuliang/IdeaProjects/SyncReport/temp/yy_220106001002_2022-05/data_20260516132426258463_001.json",
                "yy_220106001002", "正阳社区卫生服务中心", "HL07731", "1", "1", "1");

    }
}
