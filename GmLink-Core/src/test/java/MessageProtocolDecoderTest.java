
import com.gm.link.common.domain.protobuf.CompleteMessage;
import com.gm.link.common.domain.protobuf.PacketBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import com.gm.link.core.codec.MessageProtocolDecoder;
import com.gm.link.core.codec.MessageProtocolEncoder;
import com.gm.link.core.kafka.KafkaProducerManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Test;

import static com.gm.link.common.constant.ProtoConstant.MAGIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @Author: xexgm
 */
@Slf4j
public class MessageProtocolDecoderTest {
    @Test
    public void testDecode() throws Exception {
        // 1. 构造测试数据
        PacketHeader header = PacketHeader.newBuilder().setUid(123).build();
        PacketBody body = PacketBody.newBuilder().setContent("test").build();

        // 2. 构造完整协议字节流
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(MAGIC);
        buf.writeShort(1);
        buf.writeInt(header.toByteArray().length);
        buf.writeInt(body.toByteArray().length);
        buf.writeBytes(header.toByteArray());
        buf.writeBytes(body.toByteArray());

        // 3. 使用 EmbeddedChannel 测试
        EmbeddedChannel channel = new EmbeddedChannel(new MessageProtocolDecoder());
        channel.writeInbound(buf);

        // 4. 验证解码结果
        CompleteMessage msg = channel.readInbound();
        assertNotNull(msg);
        assertEquals(123, msg.getPacketHeader().getUid());
        assertEquals("test", msg.getPacketBody().getContent());
    }

    @Test
    public void testEncodeDecodeIntegration() {
        // 构建协议头
        PacketHeader header = PacketHeader.newBuilder()
                .setUid(10001)
                .setAppId(1)
                .setMessageType(1)
                .build();

        // 构建消息体内容
        PacketBody body = PacketBody.newBuilder()
                .setContent("编解码集成测试消息")
                .setTimeStamp(System.currentTimeMillis())
                .setFromUserId(10001)
                .setToId(10002)
                .setMessageType(4)
                .build();

        // 构建完整消息对象
        CompleteMessage testMessage = CompleteMessage.newBuilder()
                .setPacketHeader(header)
                .setPacketBody(body)
                .build();

        // 1. 使用编码器生成数据
        MessageProtocolEncoder encoder = new MessageProtocolEncoder();
        EmbeddedChannel encodeChannel = new EmbeddedChannel(encoder);
        encodeChannel.writeOutbound(testMessage);

        // 2. 获取编码后的字节流
        ByteBuf encodedBuf = encodeChannel.readOutbound();

        // 3. 用解码器解析
        EmbeddedChannel decodeChannel = new EmbeddedChannel(new MessageProtocolDecoder());
        decodeChannel.writeInbound(encodedBuf);

        // 4. 验证编解码结果一致性
        CompleteMessage decoded = decodeChannel.readInbound();
        assertNotNull(decoded);
        assertEquals(header, decoded.getPacketHeader());
        assertEquals(body, decoded.getPacketBody());
    }

    @Test
    public void testKafkaProducerInitialization() {
        KafkaProducer<String, byte[]> producer = KafkaProducerManager.getProducer();
        assertNotNull(producer);
    }

}
