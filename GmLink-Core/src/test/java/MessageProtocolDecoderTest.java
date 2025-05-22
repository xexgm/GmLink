import com.gm.link.common.domain.model.CompleteMessage;
import com.gm.link.common.domain.model.MessageBody;
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
        PacketBody body = PacketBody.newBuilder().setData("{\"content\":\"test\"}").build();

        // 2. 构造完整协议字节流
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(MAGIC);              // 魔数
        buf.writeShort(1);                  // 版本号
        buf.writeInt(header.toByteArray().length);  // 包头长度
        buf.writeInt(body.toByteArray().length);    // 包体长度
        buf.writeBytes(header.toByteArray());       // 包头内容
        buf.writeBytes(body.toByteArray());         // 包体内容

        // 3. 使用 EmbeddedChannel 测试
        EmbeddedChannel channel = new EmbeddedChannel(new MessageProtocolDecoder());
        channel.writeInbound(buf);

        // 4. 验证解码结果
        CompleteMessage msg = channel.readInbound();
        assertNotNull(msg);
        assertEquals(123, msg.getPacketHeader().getUid());
        assertEquals("test", msg.getMessageBody().getContent());
    }

    @Test
    public void testEncodeDecodeIntegration() {
        // 构建协议头
        PacketHeader header = PacketHeader.newBuilder()
                .setUid(10001)         // 用户ID
                .setAppId(1)    // 协议类型
                .setMessageType((short) 1)
                .build();

        // 构建消息体内容
        MessageBody body = new MessageBody();
        body.setContent("灵码测试消息");  // 根据你的 MessageBody 类实际结构调整
        body.setTimeStamp(System.currentTimeMillis());

        // 构建完整消息对象
        CompleteMessage testMessage = CompleteMessage.builder()
                .packetHeader(header)
                .messageBody(body)
                .build();

        // 1. 使用编码器生成数据
        MessageProtocolEncoder encoder = new MessageProtocolEncoder();
        EmbeddedChannel encodeChannel = new EmbeddedChannel(encoder);
        encodeChannel.writeOutbound(testMessage);  // 发送原始消息对象

        // 2. 获取编码后的字节流
        ByteBuf encodedBuf = encodeChannel.readOutbound();

        // 3. 用解码器解析
        EmbeddedChannel decodeChannel = new EmbeddedChannel(new MessageProtocolDecoder());
        decodeChannel.writeInbound(encodedBuf);

        // 4. 验证编解码结果一致性
        CompleteMessage decoded = decodeChannel.readInbound();
        assertEquals(testMessage, decoded);
    }

    @Test
    public void testKafkaProducerInitialization() {
        KafkaProducer<String, String> producer = KafkaProducerManager.getProducer();
        assertNotNull(producer);
    }

}
