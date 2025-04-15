package com.gm.link.core.config;

/**
 * @Author: xexgm
 */
public interface LifeCycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start() throws InterruptedException;

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 判断是否启动
     * @return
     */
    boolean isStarted();
}
