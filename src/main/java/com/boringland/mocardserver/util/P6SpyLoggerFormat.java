package com.boringland.mocardserver.util;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义P6SpyLogger日志打印
 * @author 41990
 */
@Slf4j
public class P6SpyLoggerFormat implements MessageFormattingStrategy {

    /**
     * 重写日志格式方法
     * @param connectionId 连接ID
     * @param now 当前时间
     * @param elapsed 执行耗时（单位：ms）
     * @param category 执行分组
     * @param prepared 预编译sql语句
     * @param sql 执行的真实SQL语句，已替换占位
     * @param url 连接URL
     * @return
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        /*return !"".equals(sql.trim())
                ? String.format("当前时间：%s | SQL耗时：%s ms | 连接信息：%s-connection %s \n预编译SQL：%s \n实际SQL：%s", now, elapsed, category, connectionId, prepared.replace("\n", " "), sql.replace("\n" , " "))
                : "";*/
        return !"".equals(sql.trim())
                ? String.format("当前时间：%s | SQL耗时：%s ms | 连接信息：%s-connection %s \n实际SQL：%s", now, elapsed, category, connectionId, sql.replace("\n" , " "))
                : "";
    }
}
