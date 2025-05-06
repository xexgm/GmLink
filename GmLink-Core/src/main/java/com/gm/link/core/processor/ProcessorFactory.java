package com.gm.link.core.processor;

import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.enums.MessageType;

import static com.gm.link.common.enums.MessageType.*;

/**
 * @Author: xexgm
 */
public class ProcessorFactory {

    public static AbstractMessageProcessor<CompleteMessage> getProcessor(MessageType messageType) {
        return switch (messageType) {
            case LOGIN_MESSAGE -> LoginProcessor.getInstance();
            case HEARTBEAT_MESSAGE -> HeartBeatProcessor.getInstance();
            default -> throw new IllegalArgumentException("不支持的消息类型");
        };
     }
}
