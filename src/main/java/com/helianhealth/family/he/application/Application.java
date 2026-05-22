package com.helianhealth.family.he.application;

import com.helianhealth.family.he.admin.service.syncreport.CompensationService;
import com.helianhealth.family.he.admin.service.syncreport.GwtjReportService;

public class Application {
    public static void main(String[] args) throws Exception {
//         GwtjReportService service = new GwtjReportService();
//         service.syncReport("yy_220106001002",
//                 "正阳社区卫生服务中心",
//                 "2022-12",
//                 "HL07731",
//                 "0",
//                 "1655",
//                 "d4030aabb095fb0eb76599a81d68fa041655",
//                 "HL07731");
//         service.syncReport();

        // 补偿
        // new CompensationService().compensate();
        // 重新下载数据来同步
         new CompensationService().compensateByDataPrepare();

        // 合并 sync.db → sync-all.db
        // new DbMergeTask().run();

        // 扫描 temp/ 目录 JSON 导入 sync_record 表
//        new TempJsonImportTask().run();
    }

}
