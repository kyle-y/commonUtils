package com.example.administrator.common.commonUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2017/1/17.
 */

public class EncryptUtils {

//========================================MD5加密,不可逆算法=========================================

    /**
     * MD5加密，将目标字符串加密后转换为16进制字符串
     * @param strObj 需要加密的字符串
     * @return
     */
    public static String GetMD5Code(String strObj) {
        if(strObj == null || strObj.isEmpty()) return "";
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = ByteArrayUtils.byte2HexString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

//========================================非对称RSA加解密=================================================

    public static final int KEY_MODE_PUBLIC = 1;
    public static final int KEY_MODE_PRIVATE = 2;

    /**
     * 通过指定的密钥长度，生成非对称的密钥对
     * @param keySize 推荐使用1024/2048 不允许低于1024
     * @return KeyPair 公私密钥对
     */
    public static KeyPair generateRSAKeyPair(int keySize) {
        KeyPair ret = null;
        try {
            //1.准备生成
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            //2.初始化，设置密钥长度
            generator.initialize(keySize);
            //3.生成，并且返回
            ret = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 获取公钥
     * @param keyPair 密钥对
     * @return （使用Base64编码后的字符串形式）
     */
    public static String getPublicKey(KeyPair keyPair) {
        String ret = null;
        byte[] publicKey = keyPair.getPublic().getEncoded();
        ret = CommonUtils.Base64Encode(publicKey);
        return ret;
    }

    /**
     * 获取私钥
     * @param keyPair 密钥对
     * @return （使用Base64编码后的字符串形式）
     */
    public static String getPrivateKey(KeyPair keyPair) {
        String ret = null;
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        ret = CommonUtils.Base64Encode(privateKey);
        return ret;
    }

    /**
     * 使用RSA非对称加密
     * @param data    要加密的数据
     * @param key     使用Base64编码过的key对象
     * @param keyMode 使用加密的方式（公钥还是私钥）
     * @return 返回加密过的数据，并用Base64编码过的字符串结果
     */
    public static String rsaEncrptWithBase64(byte[] data, String key, Integer keyMode) {
        String ret = null;
        int mode = KEY_MODE_PRIVATE;
        if (keyMode == KEY_MODE_PUBLIC) {
            mode = KEY_MODE_PUBLIC;
        }
        if (data != null && data.length > 0 && key != null && key.length() > 0) {
            byte[] keyData = CommonUtils.Base64Decode(key);//获取key对象的字节数组
            byte[] encryptBytes = null;
            switch (mode) {
                case KEY_MODE_PRIVATE:
                    //使用私钥加密
                    PrivateKey privateKey = loadPrivate(keyData);//载入私钥
                    encryptBytes = rsaEncrypt(data, privateKey);//使用私钥对数据进行加密
                    break;
                case KEY_MODE_PUBLIC:
                    PublicKey publicKey = loadPublic(keyData);//载入公钥
                    encryptBytes = rsaEncrypt(data, publicKey);//使用公钥对数据进行加密
                    break;
            }
            //将加密后的数据编码
            ret = CommonUtils.Base64Encode(encryptBytes);
        }
        return ret;
    }

    /**
     * RSA 非对称解密
     * <p/>
     * 注意：公钥加密的数据只能使用私钥解密，
     * 同样私钥加密过的数据只能使用公钥来解密。
     *
     * @param data    要解密的数据
     * @param key     解密使用的key（密钥）
     * @param keyMode 要使用的密钥的模式，公钥还是私钥
     * @return 解密后的数据
     */
    public static byte[] rsaDecrptWithBase64(byte[] data, String key, Integer keyMode) {
        byte[] ret = null;
        int mode = KEY_MODE_PUBLIC;
        if (keyMode == KEY_MODE_PRIVATE) {
            mode = KEY_MODE_PRIVATE;
        }
        if (data != null && data.length > 0 && key != null && key.length() > 0) {
            byte[] keyData = CommonUtils.Base64Decode(key);//获取key对象的字节数组
            switch (mode) {
                case KEY_MODE_PRIVATE:
                    //使用私钥解密
                    PrivateKey privateKey = loadPrivate(keyData);//载入私钥
                    ret = rsaDecrypt(data, privateKey);//使用私钥对数据进行解密
                    break;
                case KEY_MODE_PUBLIC:
                    PublicKey publicKey = loadPublic(keyData);//载入公钥
                    ret = rsaDecrypt(data, publicKey);//使用公钥对数据进行解密
                    break;
            }
        }
        return ret;
    }

    //载入公钥
    public static PublicKey loadPublic(byte[] keyData) {
        PublicKey ret = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyData);
            ret = keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return ret;
    }
    //载入私钥
    public static PrivateKey loadPrivate(byte[] keyData) {
        PrivateKey ret = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyData);
            ret = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 使用非对称RSA加密
     *
     * @param data 要加密的原始数据（字节数组）
     * @param key  密钥（公钥或者私钥）
     * @return 返回加密过后的数据（字节数组）
     */
    public static byte[] rsaEncrypt(byte[] data, Key key) {
        return rsa(Cipher.ENCRYPT_MODE, data, key);
    }

    /**
     * 非对称RSA解密
     *
     * @param data 使用密钥加密过的字节数组
     * @param key  密钥（如果原始数据是用公钥加密过的，那么此处就要用私钥来解密；否则用公钥来解）
     * @return 返回解密后的数据（字节数组）
     */
    public static byte[] rsaDecrypt(byte[] data, Key key) {
        return rsa(Cipher.DECRYPT_MODE, data, key);
    }

    /**
     * 非对称RSA加密与解密-基本方法
     *
     * @param mode 加密还是解密的模式
     * @param data 数据
     * @param key  密钥
     * @return 加密或解密之后的字节数组
     */
    private static byte[] rsa(int mode, byte[] data, Key key) {
        byte[] ret = null;
        if (data != null
                && data.length > 0
                && key != null
                ) {
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(mode, key);
                ret = cipher.doFinal(data);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    //========================================对称DES加解密=================================================
    /**
     * 使用对称加密DES方式对数据进行加密
     *
     * @param data    要加密的数据（字节数组）
     * @param keyData 加密用的密钥（字节数组）长度必须为8个字节否则返回null
     * @return 返回加密过的数据（字节数组）
     */
    public static byte[] desEncrypt(byte[] data, byte[] keyData) {
        return des(Cipher.ENCRYPT_MODE, data, keyData);
    }

    /**
     * 解密使用对称加密DES方式加密过的数据
     *
     * @param data    加密过的数据（字节数组）
     * @param keyData 解密用的密钥（字节数组）长度必须为8个字节否则返回null
     * @return 返回解密后的字节数组
     */
    public static byte[] desDecrypt(byte[] data, byte[] keyData) {
        return des(Cipher.DECRYPT_MODE, data, keyData);
    }

    /**
     * DES加密和解密的基本方法
     * @param mode
     * @param data
     * @param keyData
     * @return
     */
    private static byte[] des(int mode, byte[] data, byte[] keyData) {
        byte[] ret = null;
        if (data != null &&
                data.length > 0 &&
                keyData != null &&
                keyData.length == 8) {
            try {
                Cipher cipher = Cipher.getInstance("DES");
                //3、准备Key对象
                //3.1、DES 使用DESKeySpec,内部构造指定8个字节密码即可
                DESKeySpec keySpec = new DESKeySpec(keyData);

                //3.2、DeSKeySpec 需要转换成Key对象，才可以继续使用
                //需要使用SecretKeyFactory来处理
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                //3.3 生成Key对象
                SecretKey key = keyFactory.generateSecret(keySpec);
                //2、设置Cipher是加密还是解密，模式
                //同时对于对称加密还需要设置密码Key对象
                //参数2使用Key对象
                cipher.init(mode, key);

                ret = cipher.doFinal(data);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    //===================================对称AES加解密(单一密码形式)=====================================
    /**
     * 使用对称AES加密方式对数据进行加密
     *
     * @param data    要加密的数据
     * @param keyData 加密使用的密钥，密钥的长度必须是16个字节否则加密不成功返回null
     * @return 返回加密后的数据
     */
    public static byte[] aesEncrypt(byte[] data, byte[] keyData) {
        return aesSingle(Cipher.ENCRYPT_MODE, data, keyData);
    }

    /**
     * 使用对称AES加密方式对数据进行解密
     *
     * @param data    加密过的数据
     * @param keyData 解密使用的密钥，密钥的长度必须是16个字节否则解密不成功返回null
     * @return 返回解密后的数据
     */
    public static byte[] aesDecrypt(byte[] data, byte[] keyData) {
        return aesSingle(Cipher.DECRYPT_MODE, data, keyData);
    }

    /**
     * AES加解密基本方法
     * @param mode
     * @param data
     * @param keyData
     * @return
     */
    private static byte[] aesSingle(int mode, byte[] data, byte[] keyData) {
        byte[] ret = null;
        if (data != null &&
                data.length > 0 &&
                keyData != null &&
                keyData.length == 16  //126bit aes
                ) {

            try {
                Cipher cipher = Cipher.getInstance("AES");
                //AES 方式一 单一密码的情况 不同于 DES
                SecretKeySpec keySpec = new SecretKeySpec(keyData, "AES");
                cipher.init(mode, keySpec);

                ret = cipher.doFinal(data);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }

        }
        return ret;
    }

    //===================================对称AES加解密(带加密模式形式)=====================================

    /**
     * 使用超高强度的AES对称加密方式对数据进行加密
     *
     * @param data    要加密的数据
     * @param keyData 加密使用的密钥 长度必须为16字节
     * @param ivData  加强强度所使用的密钥 长度必须为16字节
     * @return 返回加密后的数据
     */
    public static byte[] aesEncrypt(byte[] data, byte[] keyData, byte[] ivData) {
        return aesWithIv(Cipher.ENCRYPT_MODE, data, keyData, ivData);
    }

    /**
     * 使用超高强度的AES对称加密方式对加密过的数据进行解密
     *
     * @param data    要解密的数据
     * @param keyData 加密时使用的密钥 长度必须为16字节
     * @param ivData  加强强度时所使用的密钥 长度必须为16字节
     * @return 返回解密后的数据
     */
    public static byte[] aesDecrypt(byte[] data, byte[] keyData, byte[] ivData) {
        return aesWithIv(Cipher.DECRYPT_MODE, data, keyData, ivData);
    }
    /**
     * AES带加密模式加解密基本算法
     * @param mode    加解密的模式
     * @param data    数据
     * @param keyData 密钥
     * @param ivData  用于AES/CBC/PKCS5Padding 这个有加密模式的算法
     * @return
     */
    private static byte[] aesWithIv(int mode, byte[] data, byte[] keyData, byte[] ivData) {
        byte[] ret = null;
        if (data != null
                && data.length > 0
                && keyData != null
                && keyData.length == 16
                && ivData != null
                && ivData.length == 16
                ) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                //密码部分设置成“AES”即可
                SecretKeySpec keySpec = new SecretKeySpec(keyData, "AES");
                //准备Iv参数
                IvParameterSpec iv = new IvParameterSpec(ivData);
                //设置密码以及IV参数
                cipher.init(mode, keySpec, iv);
                ret = cipher.doFinal(data);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
