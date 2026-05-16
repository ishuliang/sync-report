package com.helianhealth.family.he.admin.api.wgtj;

import lombok.Data;

import java.io.Serializable;

@Data
public class SyncReportParam implements Serializable {
    private static final long serialVersionUID = 5403150056457798747L;
    private String hospitalFid;
    private String hospitalName;
    private String month;
    private String fid;
    private String isPrint;
//    private String userId;
//    private String token;
//    private String stationId;
}
