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
    private String serviceName = DEFAULT_SERVICE_NAME;

    /**
     * 监听端口
     */
    private int port = LISTENING_PORT;

    /**
     * 运行环境
     */
    private String env = DEFAULT_ENV;

    // netty 配置信息
    private NettyConfig nettyConfig = new NettyConfig();

}
