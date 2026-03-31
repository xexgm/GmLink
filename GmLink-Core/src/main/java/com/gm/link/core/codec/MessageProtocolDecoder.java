package com.gm.link.core.codec;

import static com.gm.link.common.constant.ProtoConstant.DEFAULT_SECRETKEY;
import static com.gm.link.common.constant.ProtoConstant.FIXED_PACKET_BOUNDARY;
import static com.gm.link.common.constant.ProtoConstant.MAGIC;
import static com.gm.link.common.constant.ProtoConstant.VERSION;
import java.util.List;

import com.gm.link.common.domain.protobuf.CompleteMessage;
import com.gm.link.common.domain.protobuf.PacketBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.common.utils.ProtoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

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

        // header data: 
        PacketHeader packetHeader = PacketHeader.parseFrom(headerBytes);

        log.info("[DecodeMessageProtocol] 解析包头: {} ", packetHeader);

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

        // body data: 二进制数组 -> protobuf对象
        PacketBody packetBody = PacketBody.parseFrom(dataBytes);

        out.add(CompleteMessage.newBuilder().setPacketHeader(packetHeader).setPacketBody(packetBody).build());
    }
}
