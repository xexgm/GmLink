package com.gm.link.core.config;

import com.gm.link.common.utils.NetUtil;
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
    public static String SERVICENAME = DEFAULT_SERVICE_NAME;

    /**
     * 运行环境
     */
    public static String ENV = DEFAULT_ENV;

    /**
     * 机器id
     */
    public static Integer MACHINE_ID;

    /**
     * 机器ip
     */
    public static String LINK_HOST = NetUtil.getLocalIp();

}
