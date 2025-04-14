package com.gm.link.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

import static com.gm.link.common.enums.MsgFeature.GROUP_CHAT;
import static com.gm.link.common.enums.MsgFeature.PRIVATE_CHAT;


/**
 * @Author: xexgm
 */
public class ProtocolEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {

    }

    private void encodeProtoMessage(ProtoMessage msg, ByteBuf buf) {
        // 协议头
        buf.writeInt(msg.getMagic()); // magic魔数
        buf.writeInt(msg.getVersion()); // 版本
        buf.writeInt(msg.getCmd()); // 命令字
        buf.writeInt(msg.getMsgFeature()); // 业务标识

        // 消息元数据
        writeString(buf, msg.getMsgId());
        writeString(buf, msg.getSeqId());
        buf.writeLong(msg.getTimestamp());
        writeString(buf, msg.getFromUserId());
        if (msg.getMsgFeature() == PRIVATE_CHAT.getValue()) {
            // 私聊,写入私聊对象id
            writeString(buf, msg.getToUserId());
        } else if (msg.getMsgFeature() == GROUP_CHAT.getValue()) {
            writeString(buf, msg.getToGroupId());
        }

        // 消息属性
    }


    private void writeString(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }
}
