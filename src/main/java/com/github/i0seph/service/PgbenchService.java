package com.github.i0seph.service;

import java.util.Random;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;



@Service
public class PgbenchService {

    private final JdbcTemplate jdbcTemplate;
    private static final Random random = new Random();

    @Value("${pgbench.scale:1}")
    private String scaleStr;

    public PgbenchService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String getMtime() {
        int pgbenchScale = Integer.parseInt(scaleStr);
        int bid = random.nextInt(pgbenchScale) + 1;
        int tid = random.nextInt(pgbenchScale * 10) + 1;
        int aid = random.nextInt(pgbenchScale * 100000) + 1;
        int delta = random.nextInt(1000) + 1;

        java.sql.Timestamp mtime;

        jdbcTemplate.update("UPDATE pgbench_accounts SET abalance = abalance + ? WHERE aid = ?", delta, aid);

        Long balance = jdbcTemplate.queryForObject(
		"SELECT abalance FROM pgbench_accounts WHERE aid = ?",
		Long.class, aid);

        jdbcTemplate.update("UPDATE pgbench_tellers SET tbalance = tbalance + ? WHERE tid = ?", delta, tid);
        jdbcTemplate.update("UPDATE pgbench_branches SET bbalance = bbalance + ? WHERE bid = ?", delta, bid);
        String insertSql = "INSERT INTO pgbench_history (tid, bid, aid, delta, mtime) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) returning mtime";
        mtime = jdbcTemplate.queryForObject(insertSql, java.sql.Timestamp.class, tid, bid, aid, delta);

        return mtime.toString() + ":scale=" + scaleStr;
    }
}
