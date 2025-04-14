package com.gm.link.common.enums;

import lombok.Getter;

/**
 * @Author: xexgm
 * description: 消息特征，如 私聊、群聊、客服、弹幕，等等
 */
@Getter
public enum MsgFeature {

    PRIVATE_CHAT(1),
    GROUP_CHAT(2);

    private int value;

    MsgFeature(int value) {
        this.value = value;
    }
}
