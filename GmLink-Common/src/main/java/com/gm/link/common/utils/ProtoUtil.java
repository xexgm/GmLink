package com.gm.link.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author: xexgm
 */
public class ProtoUtil {

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16; // 128-bit IV

    public static int calculateChecksum(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return (int) crc32.getValue();
    }

    /**
     * GZIP压缩字节数组
     * @param data 原始数据
     * @return 压缩后的字节数组
     */
    public static byte[] compress(byte[] data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
            gzip.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("GZIP压缩失败", e);
        }
    }

    /**
     * GZIP解压缩字节数组
     * @param compressedData 压缩后的数据
     * @return 解压后的原始字节数组
     */
    public static byte[] decompress(byte[] compressedData) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzip = new GZIPInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzip.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("GZIP解压失败", e);
        }
    }

    /**
     * AES加密
     * @param data 原始数据
     * @param key 密钥（推荐32字节的256位密钥）
     * @return Base64编码的加密数据（格式：IV + 密文）
     */
    public static String encryptAES(byte[] data, byte[] key) {
        try {
            SecretKeySpec secretKey = validateKey(key);
            byte[] iv = generateIV();

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            byte[] encrypted = cipher.doFinal(data);
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new SecurityException("AES加密失败", e);
        }
    }

    /**
     * AES解密
     * @param encryptedData Base64编码的加密数据
     * @param key 加密使用的密钥
     * @return 原始字节数据
     */
    public static byte[] decryptAES(String encryptedData, byte[] key) {
        try {
            SecretKeySpec secretKey = validateKey(key);
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            return cipher.doFinal(combined, iv.length, combined.length - iv.length);
        } catch (Exception e) {
            throw new SecurityException("AES解密失败", e);
        }
    }

    private static SecretKeySpec validateKey(byte[] key) {
        // 自动处理不同长度的密钥（128/192/256位）
        byte[] validKey = new byte[32]; // 默认256位
        System.arraycopy(key, 0, validKey, 0, Math.min(key.length, validKey.length));
        return new SecretKeySpec(validKey, "AES");
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // 辅助方法：生成随机密钥
    public static byte[] generateAESKey(int keySize) {
        byte[] key = new byte[keySize/8];
        new SecureRandom().nextBytes(key);
        return key;
    }

}
