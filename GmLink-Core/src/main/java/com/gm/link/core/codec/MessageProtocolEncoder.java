package com.gm.link.core.codec;

import com.gm.link.common.constant.ProtoConstant;
import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.domain.model.MessageBody;
import com.gm.link.common.domain.protobuf.PacketBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.common.utils.JsonUtil;
import com.gm.link.common.utils.ProtoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import static com.gm.link.common.constant.ProtoConstant.DEFAULT_SECRETKEY;

/**
 * @Author: xexgm
 */
@Slf4j
public class MessageProtocolEncoder extends MessageToByteEncoder<CompleteMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CompleteMessage message, ByteBuf out) throws Exception {
        log.info("[Outbound-encodeData] 发送者uid: " + message.getPacketHeader().getUid());
        // 依次序列化
        // 包边界
        out.writeShort(ProtoConstant.MAGIC);
        out.writeShort(ProtoConstant.VERSION);

        // 从完整message中拆出 包头和包体
        PacketHeader packetHeader = message.getPacketHeader();
        MessageBody messageBody = message.getMessageBody();

        byte[] headerBytes = packetHeader.toByteArray();

        // MessageBody -> json字符串 -> protobuf对象 -> 二进制数组
        String dataJson = JsonUtil.toJson(messageBody);
        PacketBody packetBody = PacketBody.newBuilder()
                .setData(dataJson)
                .build();
        byte[] dataBytes = packetBody.toByteArray();

        // 序列化包头长度
        out.writeInt(headerBytes.length);

        // 判断是否压缩和加密，先压缩，再加密
        byte compression = (byte) packetHeader.getCompression();
        if (compression == 1) {
            // 使用 gzip 压缩
            dataBytes = ProtoUtil.compress(dataBytes);
        }
        byte encryption = (byte) packetHeader.getEncryption();
        if (encryption == 1) {
            // 使用 aes 加密
            dataBytes = ProtoUtil.encryptAES(dataBytes, DEFAULT_SECRETKEY.getBytes()).getBytes();
        }

        // 序列化包体长度
        out.writeInt(dataBytes.length);
        log.info("[HandledDataLength] 压缩+加密完 包体长度: " + dataBytes.length);

        // 序列化包头
        out.writeBytes(headerBytes);
        // 序列化包体
        out.writeBytes(dataBytes);
    }
}
