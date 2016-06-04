package com.zkjinshi.ritz.utils;

import com.zkjinshi.jni.aes.AESManager;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by laiqingquan on 16/2/3.
 * AES ECB模式
 */
public class AESUtil {

    //密钥算法
    public static final String KEY_ALGORITHM = "AES";

    //加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public final static String PAVO_KEY = AESManager.getInstance().getEncryptKey();

    /**
     * 转换密钥
     */
    private static Key toKey(byte[] key) {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    /**
     * 加密数据
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的数据
     * */
    public static String encrypt(String data, String key) throws GeneralSecurityException{
        //还原密钥
        Key k = toKey(key.getBytes());
        final byte[] jvb = new byte[16];
        Arrays.fill(jvb,(byte)0x00);
        //采用密钥作为初始化向量
        IvParameterSpec iv = new IvParameterSpec(jvb);
        //实例化Cipher对象，它用于完成实际的加密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化Cipher对象，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k, iv);
        //执行加密操作。加密后的结果通常都会用Base64编码进行传输
        return Base64Encoder.encode(cipher.doFinal(data.getBytes()));
    }

    /**
     * 解密数据
     * @param data 待解密数据
     * @param key  密钥
     * @return 解密后的数据
     * */
    public static String decrypt(String data, String key) throws GeneralSecurityException{
        Key k = toKey(Base64Decoder.decodeToBytes(key));
        //采用密钥作为初始化向量
        IvParameterSpec iv = new IvParameterSpec(Base64Decoder.decodeToBytes(key));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化Cipher对象，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k, iv);
        //执行解密操作
        return new String(cipher.doFinal(Base64Decoder.decodeToBytes(data)));
    }

}
