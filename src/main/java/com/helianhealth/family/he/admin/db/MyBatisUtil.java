package com.helianhealth.family.he.admin.db;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * MyBatis 全局工具类：负责 SqlSessionFactory 单例 + 启动时自动建表。
 * <p>
 * 用法：
 * <pre>
 *     try (SqlSession session = MyBatisUtil.openSession()) {
 *         SyncTaskMapper mapper = session.getMapper(SyncTaskMapper.class);
 *         ...
 *     }
 * </pre>
 */
@Slf4j
public final class MyBatisUtil {

    private static final SqlSessionFactory FACTORY;

    static {
        try (InputStream is = Resources.getResourceAsStream("mybatis-config.xml")) {
            FACTORY = new SqlSessionFactoryBuilder().build(is);
            initSchema();
        } catch (Exception e) {
            log.error("MyBatis 初始化失败", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private MyBatisUtil() { }

    /** 打开自动提交的会话；简单 CRUD 场景够用 */
    public static SqlSession openSession() {
        return FACTORY.openSession(true);
    }

    /** 打开手动提交的会话；批量写入时使用，需调用方自行 commit/rollback */
    public static SqlSession openManualSession() {
        return FACTORY.openSession(false);
    }

    /** 启动时执行 schema.sql；CREATE TABLE IF NOT EXISTS 保证幂等 */
    private static void initSchema() {
        try (SqlSession session = FACTORY.openSession();
             InputStream is = Resources.getResourceAsStream("db/schema.sql")) {
            Connection conn = session.getConnection();
            String raw = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            // 先逐行去掉 -- 注释，再按 ; 拆分
            StringBuilder cleaned = new StringBuilder();
            for (String line : raw.split("\\r?\\n")) {
                String l = line.trim();
                if (l.isEmpty() || l.startsWith("--")) continue;
                cleaned.append(line).append('\n');
            }
            try (Statement stmt = conn.createStatement()) {
                for (String s : cleaned.toString().split(";")) {
                    String sql = s.trim();
                    if (sql.isEmpty()) continue;
                    try {
                        stmt.execute(sql);
                    } catch (Exception ex) {
                        // ALTER TABLE ADD COLUMN 在列已存在时会报错，对迁移语句容错处理
                        if (sql.toUpperCase().contains("ALTER TABLE")) {
                            log.warn("DDL 已忽略（可能列已存在）: {}", sql);
                        } else {
                            throw ex;
                        }
                    }
                }
            }
            conn.commit();
            log.info("数据库初始化完成（sync_task / sync_failure）");
        } catch (Exception e) {
            log.error("数据库建表失败：{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
