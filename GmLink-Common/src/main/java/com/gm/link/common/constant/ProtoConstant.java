package com.gm.link.common.constant;

/**
 * @Author: xexgm
 */
public interface ProtoConstant {

    int MAGIC_NUMBER = 0xDEADBEEF;

    // magic(4) + version(2) + cmd(2)
    int HEADER_LENGTH = 4 + 2 + 2;

    short PRIVATE_RECEIVE_ID = 9;

    short GROUP_RECEIVE_ID = 10;

    // magic(2) + version(2) + packet_headerLength(4) + packet_dataLength(4)
    int FIXED_PACKET_BOUNDARY = 12;

    short MAGIC = 0xABC;

    short VERSION = 1;

    String DEFAULT_SECRETKEY = "SECRETKEYBYXEXGM";
}
