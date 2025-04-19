package com.gm.link.common.enums;

import lombok.Getter;

/**
 * @Author: xexgm
 */
@Getter
public enum MessageType {
    // 包头数据类型标识，0-登录，1-心跳，2-ack，3-强制下线,4-私聊，5-群聊，6-系统消息，待扩展

    LOGIN_MESSAGE((short) 0),
    HEARTBEAT_MESSAGE((short) 1),
    ACK_MESSAGE((short) 2),
    FORCE_OFFLINE_MESSAGE((short) 3),
    PRIVATE_CHAT_MESSAGE((short) 4),
    GROUP_CHAT_MESSAGE((short) 5),
    SYSTEM_MESSAGE((short) 6);

    short type;

    MessageType(short type) {
        this.type = type;
    }

    public static MessageType fromType(short type) {
        for (MessageType typeEnum : values()) {
            if (typeEnum.getType() == type) {
                return typeEnum;
            }
        }
        return null;
    }
}
