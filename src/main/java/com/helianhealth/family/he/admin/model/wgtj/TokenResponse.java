package com.helianhealth.family.he.admin.model.wgtj;

public class TokenResponse {
        private Integer code;
        private String message;
        private TokenData data;
        private Long timestamp;

        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public TokenData getData() { return data; }
        public void setData(TokenData data) { this.data = data; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }