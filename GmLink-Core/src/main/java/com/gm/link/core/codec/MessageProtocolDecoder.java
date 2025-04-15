package com.gm.link.core.codec;

import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.domain.model.MessageBody;
import com.gm.link.common.domain.protobuf.PacketBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.common.utils.JsonUtil;
import com.gm.link.common.utils.ProtoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static com.gm.link.common.constant.ProtoConstant.*;

/**
 * @Author: xexgm
 */
@Slf4j
public class MessageProtocolDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 长度不足包边界，返回
        if (in.readableBytes() < FIXED_PACKET_BOUNDARY) {
            return;
        }

        // 标记当前读取指针位置，便于后续重置
        in.markReaderIndex();

        // 读取固定字段
        short magic = in.readShort();
        short version = in.readShort();
        int headerLength = in.readInt();
        int dataLength = in.readInt();

        if (magic != MAGIC) {
            ctx.close();
            return;
        }

        if (version != VERSION) {
            ctx.close();
            return;
        }

        // 数据不足包头和包体，返回
        if (in.readableBytes() < headerLength + dataLength) {
            in.resetReaderIndex();
            return;
        }

        // 读取包头和包体
        ByteBuf headerBuf = in.readBytes(headerLength);
        ByteBuf dataBuf = in.readBytes(dataLength);

        // 解析包头和包体，构造 byte数组
        byte[] headerBytes = new byte[headerLength];
        byte[] dataBytes = new byte[dataLength];
        // 缓冲区读到 byte 数组里
        headerBuf.readBytes(headerBytes);
        dataBuf.readBytes(dataBytes);

        // parseFrom
        PacketHeader packetHeader = PacketHeader.parseFrom(headerBytes);

        log.info("[DecodeMessageProtocol] 解析 uid: {} 发送的数据", packetHeader.getUid());

        // 对 dataBytes 先解密
        if (packetHeader.getEncryption() == 1) {
            // bytes 先转string，解密完转 bytes
            String dataBytesString = dataBytes.toString();
            dataBytes = ProtoUtil.decryptAES(dataBytesString, DEFAULT_SECRETKEY.getBytes());
        }
        // 解压缩
        if (packetHeader.getCompression() == 1) {
            dataBytes = ProtoUtil.decompress(dataBytes);
        }

        // 处理 data  二进制数组 -> protobuf对象 -> json字符串 -> MessageBody
        PacketBody packetBody = PacketBody.parseFrom(dataBytes);
        String dataJson = packetBody.getData();
        // 异常处理
        MessageBody messageBody = null;
        try {
            messageBody = JsonUtil.fromJson(dataJson, MessageBody.class);
        } catch (Exception e) {
            ctx.writeAndFlush("Invalid JSON format");
            throw new RuntimeException(e);
        }

        out.add(CompleteMessage.builder().packetHeader(packetHeader).messageBody(messageBody).build());
    }
}
