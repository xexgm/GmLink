package com.gm.link.common.utils;

import java.util.zip.CRC32;

/**
 * @Author: xexgm
 */
public class ProtoUtil {

    public static int calculateChecksum(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return (int) crc32.getValue();
    }
}
