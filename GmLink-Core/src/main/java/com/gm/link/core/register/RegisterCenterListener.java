package com.gm.link.core.register;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.Set;

/**
 * @Author: xexgm
 * description: 注册实例变化监听器
 */
public interface RegisterCenterListener {

    /**
     * 有实例变化时调用此方法，更新本地缓存的实例信息
     */
    void onInstancesChange(Set<Instance> newInstances);
}
