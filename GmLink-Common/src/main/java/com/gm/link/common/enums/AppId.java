package com.gm.link.common.enums;

import lombok.Getter;

/**
 * @Author: xexgm
 * decription: 业务线id，LinkServer也是一个业务，标识所属业务模块
 */
@Getter
public enum AppId {

    // 长连接中台业务，比如心跳、登录，等等
    LINK_SERVER(1);

    int id;

    AppId(int id) {
        this.id = id;
    }
}
