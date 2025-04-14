package com.gm.link.core.codec;

import com.gm.link.common.domain.protobuf.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.gm.link.common.constant.ProtoConstant.*;
import static com.gm.link.common.utils.ProtoUtil.calculateChecksum;

/**
 * @Author: xexgm
 * description: proto解码器
 */
public class MultiProtocolDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEADER_LENGTH) return;

        in.markReaderIndex();
        int magic = in.readInt();
        if (magic != MAGIC_NUMBER) {
            ctx.close();
            return;
        }

        int version = in.readShort();
        int cmd = in.readShort();

        // 根据 cmd 路由到不同的解码逻辑
        switch (cmd) {
            case 0x0001:
                decodeProtoMessage(in, out, version, cmd);
                break;
            case 0x0002:
                decodeProtoAck(in, out, version, cmd);
            case 0x0003:
                decodeProtoHB(in, out, version, cmd);
            default:
                throw new IllegalArgumentException("消息命令未知: " + cmd);
        }
    }

    private void decodeProtoMessage(ByteBuf in, List<Object> out, int version, int cmd) throws InvalidProtocolBufferException {
        ProtoMessage.Builder builder = ProtoMessage.newBuilder()
                .setMagic(MAGIC_NUMBER)
                .setVersion(version)
                .setCmd(cmd)
                .setMsgFeature(in.readInt());

        // 消息元数据
        builder.setMsgId(readLengthPrefixedString(in));
        builder.setSeqId(readLengthPrefixedString(in));
        builder.setTimestamp(in.readLong());
        builder.setFromUserId(readLengthPrefixedString(in));

        // 处理 oneof 字段
        short targetType = in.readShort();
        if (targetType > 0) {
            String target = readLengthPrefixedString(in);
            if (targetType == PRIVATE_RECEIVE_ID) {
                builder.setToUserId(target);
            } else if (targetType == GROUP_RECEIVE_ID) {
                builder.setToGroupId(target);
            }
        }

        // 消息属性
        builder.setMsgType(MessageType.forNumber(in.readShort()));
        builder.setMsgStatus(MessageStatus.forNumber(in.readShort()));
        builder.setFlags(in.readShort());

        // 可选字段 retryCount 处理
        if (in.readableBytes() > 4 + 4) { // 剩余数据足够包含retry_count
            builder.setRetryCount(in.readUnsignedByte());
        }

        // 数据校验
        int dataLength = in.readInt();
        int expectedChecksum = in.readInt();
        byte[] bodyBytes = new byte[dataLength];
        in.readBytes(bodyBytes);

        if (calculateChecksum(bodyBytes) != expectedChecksum) {
            throw new InvalidProtocolBufferException("ProtoMessage checksum mismatch");
        }

        builder.setBody(ByteString.copyFrom(bodyBytes));
        out.add(builder.build());
    }

    private void decodeProtoAck(ByteBuf in, List<Object> out, int version, int cmd) throws InvalidProtocolBufferException {
        ProtoAck.Builder builder = ProtoAck.newBuilder()
                .setMagic(MAGIC_NUMBER)
                .setVersion(version)
                .setCmd(cmd);

        // 解码ProtoAck字段
        builder.setOriginalMsgId(readLengthPrefixedString(in));
        builder.setStatus(in.readShort());

        // 处理可选字段error_msg
        if (in.readableBytes() >= 4 + 4) { // 剩余字节足够读取error_msg和check_sum
            short errorMsgLength = in.readShort();
            if (errorMsgLength > 0) {
                byte[] errorMsgBytes = new byte[errorMsgLength];
                in.readBytes(errorMsgBytes);
                builder.setErrorMsg(new String(errorMsgBytes, StandardCharsets.UTF_8));
            }
        }

        // 校验码验证
        int expectedChecksum = in.readInt();
        byte[] originalMsgBytes = builder.getOriginalMsgId().getBytes(StandardCharsets.UTF_8);
        if (calculateChecksum(originalMsgBytes) != expectedChecksum) {
            throw new InvalidProtocolBufferException("ProtoAck checksum mismatch");
        }

        out.add(builder.build());
    }

    private void decodeProtoHB(ByteBuf in, List<Object> out, int version, int cmd) throws InvalidProtocolBufferException {
        ProtoHB.Builder builder = ProtoHB.newBuilder()
                .setMagic(MAGIC_NUMBER)
                .setVersion(version)
                .setCmd(cmd);

        // 解码ProtoHB字段
        builder.setFromMachineId(readLengthPrefixedString(in));
        builder.setToMachineId(readLengthPrefixedString(in));
        builder.setTimestamp(in.readLong());

        // 校验码验证
        int expectedChecksum = in.readInt();
        String checkContent = builder.getFromMachineId() + builder.getToMachineId() + builder.getTimestamp();
        if (calculateChecksum(checkContent.getBytes()) != expectedChecksum) {
            throw new InvalidProtocolBufferException("ProtoHB checksum mismatch");
        }

        out.add(builder.build());
    }

    private String readLengthPrefixedString(ByteBuf in) {
        short length = in.readShort();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
