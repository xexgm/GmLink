package com.gm.link.core.register;

/**
 * @Author: xexgm
 * description: 注册中心接口
 */
public interface RegisterCenterProcessor {

    /**
     * 注册中心初始化
     */
    void init();

    /**
     * 订阅注册中心实例变化
     * 自定义监听器在调用时实现
     */
    void subscribeServiceChange(RegisterCenterListener listener);
}
