package com.gm.link.core.codec;

import com.gm.link.common.domain.protobuf.ProtoAck;
import com.gm.link.common.domain.protobuf.ProtoHB;
import com.gm.link.common.domain.protobuf.ProtoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

import static com.gm.link.common.constant.ProtoConstant.MAGIC_NUMBER;
import static com.gm.link.common.utils.ProtoUtil.calculateChecksum;


/**
 * @Author: xexgm
 * description: proto编码器
 */
public class MultiProtocolEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {
        if (msg instanceof ProtoMessage) {
            encodeProtoMessage((ProtoMessage) msg, byteBuf);
        } else if (msg instanceof ProtoAck) {
            encodeProtoAck((ProtoAck) msg, byteBuf);
        } else if (msg instanceof ProtoHB) {
            encodeProtoHB((ProtoHB) msg, byteBuf);
        }
    }

    private void encodeProtoMessage(ProtoMessage msg, ByteBuf out) {
        // 协议头
        out.writeInt(MAGIC_NUMBER); // magic魔数
        out.writeShort(msg.getVersion()); // 版本
        out.writeShort(msg.getCmd()); // 命令字

        out.writeInt(msg.getMsgFeature()); // 业务标识

        // 消息元数据
        writeLengthPrefixedString(out, msg.getMsgId());
        writeLengthPrefixedString(out, msg.getSeqId());
        out.writeLong(msg.getTimestamp());
        writeLengthPrefixedString(out, msg.getFromUserId());

        // 处理 oneof字段
        if (msg.hasToUserId()) {
            // 私聊,写入私聊对象id
            writeLengthPrefixedString(out, msg.getToUserId());
        } else if (msg.hasToGroupId()) {
            writeLengthPrefixedString(out, msg.getToGroupId());
        } else {
            // 无目标标识
            out.writeShort(0);
        }

        // 消息属性
        out.writeShort(msg.getMsgType().getNumber());
        out.writeShort(msg.getMsgStatus().getNumber());
        out.writeShort(calculateFlags(msg));

        // 重试次数可选字段处理
        if (msg.hasRetryCount()) {
            out.writeByte(msg.getRetryCount());
        }

        // 数据校验
        byte[] bodyBytes = msg.getBody().toByteArray();
        int checksum = calculateChecksum(bodyBytes);
        out.writeInt(bodyBytes.length);
        out.writeInt(checksum);
        out.writeBytes(bodyBytes);
    }

    private void writeLengthPrefixedString(ByteBuf out, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        out.writeShort(bytes.length);
        out.writeBytes(bytes);
    }

    private int calculateFlags(ProtoMessage msg) {
        int flags = 0;
        if ((msg.getFlags() & 0x01) != 0) flags |= 0x0001;
        if ((msg.getFlags() & 0x02) != 0) flags |= 0x0002;
        if ((msg.getFlags() & 0x04) != 0) flags |= 0x0004;
        return flags;
    }

    private void encodeProtoAck(ProtoAck msg, ByteBuf out) {
        out.writeInt(msg.getMagic());
        out.writeShort(msg.getVersion());
        out.writeShort(msg.getCmd());

        // 编码ProtoAck字段
        writeLengthPrefixedString(out, msg.getOriginalMsgId());
        out.writeShort(msg.getStatus());

        // 处理可选字段error_msg
        if (msg.hasErrorMsg()) {
            writeLengthPrefixedString(out, msg.getErrorMsg());
        } else {
            out.writeShort(0); // 长度0表示无error_msg
        }

        // 计算校验码
        byte[] originalMsgBytes = msg.getOriginalMsgId().getBytes(StandardCharsets.UTF_8);
        out.writeInt(calculateChecksum(originalMsgBytes));
    }

    private void encodeProtoHB(ProtoHB msg, ByteBuf out) {
        out.writeInt(msg.getMagic());
        out.writeShort(msg.getVersion());
        out.writeShort(msg.getCmd());

        // 编码ProtoHB字段
        writeLengthPrefixedString(out, msg.getFromMachineId());
        writeLengthPrefixedString(out, msg.getToMachineId());
        out.writeLong(msg.getTimestamp());

        // 计算校验码
        String checkContent = msg.getFromMachineId() + msg.getToMachineId() + msg.getTimestamp();
        out.writeInt(calculateChecksum(checkContent.getBytes()));
    }



}
