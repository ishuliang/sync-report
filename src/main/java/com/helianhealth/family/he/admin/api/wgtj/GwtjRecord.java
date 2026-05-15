package com.helianhealth.family.he.admin.api.wgtj;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.util.List;

@Data
public class GwtjRecord {

    @SerializedName("dangan_info")
    private DanganInfo danganInfo;

    @SerializedName("tijian_info")
    private TijianInfo tijianInfo;

    @SerializedName("qyjl_list")
    private List<QyjlItem> qyjlList;

    @SerializedName("jzjl_list")
    private List<JzjlItem> jzjlList;

    @SerializedName("jws_list")
    private List<JwsItem> jwsList;

    @SerializedName("sfjl_list")
    private List<SfjlItem> sfjlList;
}
