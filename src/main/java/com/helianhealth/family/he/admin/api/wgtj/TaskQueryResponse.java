package com.helianhealth.family.he.admin.api.wgtj;

import java.util.List;

public class TaskQueryResponse {
    private String code;
    private String message;
    private TaskData data;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public TaskData getData() { return data; }
    public void setData(TaskData data) { this.data = data; }

    public static class TaskData {
        private String jobid;
        private String jobState;
        private String zipPassword;
        private String dateType;
        private String tijianRq;
        private String yiyuanFid;
        private Integer priority;
        private List<String> files;

        public String getJobid() { return jobid; }
        public void setJobid(String jobid) { this.jobid = jobid; }
        public String getJobState() { return jobState; }
        public void setJobState(String jobState) { this.jobState = jobState; }
        public String getZipPassword() { return zipPassword; }
        public void setZipPassword(String zipPassword) { this.zipPassword = zipPassword; }
        public String getDateType() { return dateType; }
        public void setDateType(String dateType) { this.dateType = dateType; }
        public String getTijianRq() { return tijianRq; }
        public void setTijianRq(String tijianRq) { this.tijianRq = tijianRq; }
        public String getYiyuanFid() { return yiyuanFid; }
        public void setYiyuanFid(String yiyuanFid) { this.yiyuanFid = yiyuanFid; }
        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
        public List<String> getFiles() { return files; }
        public void setFiles(List<String> files) { this.files = files; }
    }
}
