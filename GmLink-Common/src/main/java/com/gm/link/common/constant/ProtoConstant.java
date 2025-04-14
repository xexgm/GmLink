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
}
