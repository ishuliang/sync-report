-- ============================================================
-- 同步任务相关表（SQLite 方言）
-- 数据库文件：./sync.db（不存在会自动创建）
-- 使用方式：MyBatisUtil 启动时自动执行此脚本（IF NOT EXISTS 保证幂等）
-- ============================================================

CREATE TABLE IF NOT EXISTS sync_task (
    id              INTEGER     PRIMARY KEY AUTOINCREMENT,
    hospital_fid    VARCHAR(64) NOT NULL,
    hospital_name   VARCHAR(128),
    month           VARCHAR(7)  NOT NULL,
    task_id         VARCHAR(64),
    zip_pwd         VARCHAR(128),
    status          VARCHAR(16) NOT NULL,
    total_count     INTEGER     DEFAULT 0,
    success_count   INTEGER     DEFAULT 0,
    fail_count      INTEGER     DEFAULT 0,
    error_msg       TEXT,
    retry_times     INTEGER     DEFAULT 0,
    create_time     DATETIME    DEFAULT (datetime('now', '+8 hours')),
    update_time     DATETIME    DEFAULT (datetime('now', '+8 hours')),
    UNIQUE(hospital_fid, month)
);

-- 已有数据库的存量列迁移（列已存在时 MyBatisUtil 会忽略此错误）
ALTER TABLE sync_task ADD COLUMN zip_pwd VARCHAR(128);

CREATE INDEX IF NOT EXISTS idx_sync_task_status ON sync_task(status);

CREATE TABLE IF NOT EXISTS sync_failure (
    id              INTEGER     PRIMARY KEY AUTOINCREMENT,
    task_id_ref     INTEGER,
    hospital_fid    VARCHAR(64),
    month           VARCHAR(7),
    customer_name   VARCHAR(64),
    customer_idno   VARCHAR(32),
    stage           VARCHAR(32),
    payload         TEXT,
    error_msg       TEXT,
    retry_times     INTEGER     DEFAULT 0,
    resolved        INTEGER     DEFAULT 0,
    create_time     DATETIME    DEFAULT (datetime('now', '+8 hours'))
);

CREATE INDEX IF NOT EXISTS idx_sync_failure_resolved ON sync_failure(resolved);
CREATE INDEX IF NOT EXISTS idx_sync_failure_task_ref ON sync_failure(task_id_ref);

CREATE TABLE IF NOT EXISTS sync_record (
    id           INTEGER     PRIMARY KEY AUTOINCREMENT,
    hospital_fid VARCHAR(64) NOT NULL,
    month        VARCHAR(7)  NOT NULL,
    xingming     VARCHAR(64),
    shenfenzh    VARCHAR(32),
    tijian_date  VARCHAR(32),
    json_content TEXT,
    create_time  DATETIME    DEFAULT (datetime('now', '+8 hours'))
);

CREATE INDEX IF NOT EXISTS idx_sync_record_fid_month ON sync_record(hospital_fid, month);
