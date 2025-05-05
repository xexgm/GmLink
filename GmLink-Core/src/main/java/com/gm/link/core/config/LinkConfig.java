package com.gm.link.core.config;

import lombok.Data;

import static com.gm.link.common.constant.LinkConfigConstant.*;

/**
 * @Author: xexgm
 */
@Data
public class LinkConfig {
    /**
     * 服务名称
     */
    public static String serviceName = DEFAULT_SERVICE_NAME;

    /**
     * 运行环境
     */
    public static String env = DEFAULT_ENV;

    /**
     * 机器id
     */
    public static Integer MACHINE_ID;

    // todo 机器ip
    public static String LINK_HOST = "";

}
