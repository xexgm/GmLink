package com.gm.link.common.domain.model;

import com.gm.link.common.domain.protobuf.PacketBody;
import com.gm.link.common.domain.protobuf.PacketHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xexgm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Deprecated
public class CompleteMessage {

    PacketHeader packetHeader;

    PacketBody packetBody;
}
