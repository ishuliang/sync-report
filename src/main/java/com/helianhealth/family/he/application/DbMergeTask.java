package com.helianhealth.family.he.application;

import java.sql.*;

/**
 * 数据库合并工具：将 sync.db 中的 sync_task 数据合并到 sync-all.db。
 * <p>
 * 合并策略：以 sync-all.db 为主，(hospital_fid, month) 已存在则跳过，缺失则插入。
 * <p>
 * 使用方式：直接在 Application.main 中调用 new DbMergeTask().run()
 */
public class DbMergeTask {

    /** 源库路径（单次同步库） */
    private static final String SRC_URL = "jdbc:sqlite:./sync.db";
    /** 目标库路径（汇总库） */
    private static final String DST_URL = "jdbc:sqlite:./sync-all.db";

    /** sync_task 建表 DDL（与 schema.sql 保持一致） */
    private static final String CREATE_SYNC_TASK = "CREATE TABLE IF NOT EXISTS sync_task (\n"
            + "    id            INTEGER     PRIMARY KEY AUTOINCREMENT,\n"
            + "    hospital_fid  VARCHAR(64) NOT NULL,\n"
            + "    hospital_name VARCHAR(128),\n"
            + "    month         VARCHAR(7)  NOT NULL,\n"
            + "    task_id       VARCHAR(64),\n"
            + "    zip_pwd       VARCHAR(128),\n"
            + "    status        VARCHAR(16) NOT NULL,\n"
            + "    total_count   INTEGER     DEFAULT 0,\n"
            + "    success_count INTEGER     DEFAULT 0,\n"
            + "    fail_count    INTEGER     DEFAULT 0,\n"
            + "    error_msg     TEXT,\n"
            + "    retry_times   INTEGER     DEFAULT 0,\n"
            + "    create_time   DATETIME    DEFAULT (datetime('now', '+8 hours')),\n"
            + "    update_time   DATETIME    DEFAULT (datetime('now', '+8 hours')),\n"
            + "    UNIQUE(hospital_fid, month)\n"
            + ")";

    private static final String CREATE_INDEX =
            "CREATE INDEX IF NOT EXISTS idx_sync_task_status ON sync_task(status)";

    /** 查询源库全量 */
    private static final String SELECT_ALL =
            "SELECT hospital_fid, hospital_name, month, task_id, zip_pwd, "
            + "status, total_count, success_count, fail_count, error_msg, "
            + "retry_times, create_time, update_time FROM sync_task ORDER BY id";

    /** 目标库检查 (hospital_fid, month) 是否已存在 */
    private static final String CHECK_EXISTS =
            "SELECT COUNT(1) FROM sync_task WHERE hospital_fid = ? AND month = ?";

    /** 目标库插入（跳过已存在行） */
    private static final String INSERT_IGNORE =
            "INSERT OR IGNORE INTO sync_task "
            + "(hospital_fid, hospital_name, month, task_id, zip_pwd, "
            + " status, total_count, success_count, fail_count, error_msg, "
            + " retry_times, create_time, update_time) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public void run() throws Exception {
        System.out.println("========== DbMergeTask 开始 ==========");

        try (Connection src = DriverManager.getConnection(SRC_URL);
             Connection dst = DriverManager.getConnection(DST_URL)) {

            // ── 第一步：确保目标库表结构存在 ──────────────────────────
            ensureTargetSchema(dst);

            // ── 第二步：验证：打印两库当前数量 ───────────────────────
            int srcTotal = countTable(src);
            int dstBefore = countTable(dst);
            System.out.printf("[验证] sync.db    sync_task 记录数：%d%n", srcTotal);
            System.out.printf("[验证] sync-all.db sync_task 记录数（合并前）：%d%n", dstBefore);

            if (srcTotal == 0) {
                System.out.println("[提示] 源库无数据，无需合并，退出。");
                return;
            }

            // ── 第三步：逐行合并 ───────────────────────────────────────
            int inserted = 0;
            int skipped = 0;

            dst.setAutoCommit(false);
            try (PreparedStatement checkStmt = dst.prepareStatement(CHECK_EXISTS);
                 PreparedStatement insertStmt = dst.prepareStatement(INSERT_IGNORE);
                 Statement srcStmt = src.createStatement();
                 ResultSet rs = srcStmt.executeQuery(SELECT_ALL)) {

                while (rs.next()) {
                    String hospitalFid = rs.getString("hospital_fid");
                    String month = rs.getString("month");

                    // 检查目标库是否已存在该 (hospital_fid, month)
                    checkStmt.setString(1, hospitalFid);
                    checkStmt.setString(2, month);
                    try (ResultSet cr = checkStmt.executeQuery()) {
                        if (cr.next() && cr.getInt(1) > 0) {
                            System.out.printf("  [跳过] %s / %s（目标库已存在）%n", hospitalFid, month);
                            skipped++;
                            continue;
                        }
                    }

                    // 插入到目标库
                    insertStmt.setString(1, hospitalFid);
                    insertStmt.setString(2, rs.getString("hospital_name"));
                    insertStmt.setString(3, month);
                    insertStmt.setString(4, rs.getString("task_id"));
                    insertStmt.setString(5, rs.getString("zip_pwd"));
                    insertStmt.setString(6, rs.getString("status"));
                    insertStmt.setObject(7, rs.getObject("total_count"));
                    insertStmt.setObject(8, rs.getObject("success_count"));
                    insertStmt.setObject(9, rs.getObject("fail_count"));
                    insertStmt.setString(10, rs.getString("error_msg"));
                    insertStmt.setObject(11, rs.getObject("retry_times"));
                    insertStmt.setString(12, rs.getString("create_time"));
                    insertStmt.setString(13, rs.getString("update_time"));
                    insertStmt.executeUpdate();

                    System.out.printf("  [插入] %s / %s  status=%s total=%s%n",
                            hospitalFid, month,
                            rs.getString("status"),
                            rs.getObject("total_count"));
                    inserted++;
                }

                dst.commit();
            } catch (Exception e) {
                dst.rollback();
                throw e;
            } finally {
                dst.setAutoCommit(true);
            }

            // ── 第四步：合并后验证 ─────────────────────────────────────
            int dstAfter = countTable(dst);
            System.out.println("========================================");
            System.out.printf("[结果] 源库总计：%d 条%n", srcTotal);
            System.out.printf("[结果] 新增插入：%d 条%n", inserted);
            System.out.printf("[结果] 已存在跳过：%d 条%n", skipped);
            System.out.printf("[结果] sync-all.db 合并后记录数：%d 条%n", dstAfter);
            System.out.println("========== DbMergeTask 完成 ==========");
        }
    }

    /** 确保目标库 sync_task 表和索引存在 */
    private void ensureTargetSchema(Connection dst) throws SQLException {
        try (Statement stmt = dst.createStatement()) {
            stmt.execute(CREATE_SYNC_TASK);
            stmt.execute(CREATE_INDEX);
            System.out.println("[初始化] sync-all.db 表结构已就绪");
        }
    }

    /** 统计表记录数 */
    private int countTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(1) FROM sync_task")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
