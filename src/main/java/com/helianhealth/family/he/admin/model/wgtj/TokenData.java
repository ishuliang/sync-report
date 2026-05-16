package com.helianhealth.family.he.admin.model.wgtj;

public class TokenData {
        private Long expireTime;
        private String expireTimeFormatted;
        private String appKey;
        private String accessToken;
        private String tokenType;

        public Long getExpireTime() { return expireTime; }
        public void setExpireTime(Long expireTime) { this.expireTime = expireTime; }
        public String getExpireTimeFormatted() { return expireTimeFormatted; }
        public void setExpireTimeFormatted(String expireTimeFormatted) { this.expireTimeFormatted = expireTimeFormatted; }
        public String getAppKey() { return appKey; }
        public void setAppKey(String appKey) { this.appKey = appKey; }
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    }