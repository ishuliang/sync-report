package com.helianhealth.family.he.admin.api.wgtj;

import java.util.List;

public class HospitalQueryResponse {
        private Integer code;
        private String message;
        private List<HospitalData> data;
        private Long timestamp;

        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<HospitalData> getData() { return data; }
        public void setData(List<HospitalData> data) { this.data = data; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }