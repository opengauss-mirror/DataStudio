/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.security;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class EncryptionUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class EncryptionUtil {
    private SecureUtil encryptionDecryption;

    /**
     * Instantiates a new encryption util.
     *
     * @param encryptionDecryption the encryption decryption
     */
    public EncryptionUtil(SecureUtil encryptionDecryption) {
        this.encryptionDecryption = encryptionDecryption;
    }

    /**
     * Gets the encrypted string.
     *
     * @return the encrypted string
     */
    public String getEncryptedString() {
        return encryptionDecryption.getEncryptedString();
    }

    /**
     * Sets the encrypted string.
     *
     * @param encryptedString the new encrypted string
     */
    public void setEncryptedString(String encryptedString) {
        encryptionDecryption.setEncryptedString(encryptedString);
    }

    /**
     * Encrypt.
     *
     * @param strToEncrypt the str to encrypt
     * @return boolean true if success
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final boolean encrypt(String strToEncrypt) throws UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException, DataStudioSecurityException {
        setEncryptedString(encryptString(strToEncrypt, "UTF-8"));
        return true; // return for tests
    }

    /**
     * Encrypt.
     *
     * @param strToEncrypt the str to encrypt
     * @param boolean true if success
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final boolean encrypt(char[] strToEncrypt) throws UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException, DataStudioSecurityException {
        setEncryptedString(encryptString(strToEncrypt, "UTF-8"));
        return true; // return for tests
    }

    /**
     * Encrypt string.
     *
     * @param strToEncrypt the str to encrypt
     * @param encodingCharSet the encoding char set
     * @return returns encrypted string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final String encryptString(String strToEncrypt, String encodingCharSet) throws UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, DataStudioSecurityException {
        return Base64.encodeBase64String(encryptByteArray(strToEncrypt.getBytes(encodingCharSet)));
    }

    /**
     * Encrypt string.
     *
     * @param strToEncrypt the str to encrypt
     * @param encodingCharSet the encoding char set
     * @return returns encrypted string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final String encryptString(char[] strToEncrypt, String encodingCharSet) throws UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, DataStudioSecurityException {
        String result = Base64.encodeBase64String(encryptByteArray(getBytes(strToEncrypt, encodingCharSet)));
        return result;
    }

    /**
     * Encrypt byte array.
     *
     * @param bytesToEncrypt the bytes to encrypt
     * @return the byte[]
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final byte[] encryptByteArray(byte[] bytesToEncrypt) throws UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, DataStudioSecurityException {
        byte[] ivBytes = encryptionDecryption.getIVSalt();
        IvParameterSpec iv;
        if (null != ivBytes) {
            iv = new IvParameterSpec(ivBytes);
        } else {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR);
        }
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        } catch (NoSuchAlgorithmException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (NoSuchPaddingException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, encryptionDecryption.getRandomKey(), iv);
        } catch (InvalidKeyException e) {
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, e);
        }

        return cipher.doFinal(bytesToEncrypt);
    }

    /**
     * description: convert char[] to bytes[]
     *
     * @param chars the input chars
     * @param encodeCharSet the convert charset
     * @return byte[] the byte array
     */
    public static byte[] getBytes(char[] chars, String encodeCharSet) {
        Charset cs = Charset.forName(encodeCharSet);
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        byte[] result = new byte[bb.limit()];
        bb.get(result, 0, result.length);
        return result;
    }

    /**
     * description: convert byte[] to char[]
     *
     * @param bytes the input bytes
     * @param encodeCharSet the convert charset
     * @return char[] the char array
     */
    public static char[] getChars(byte[] bytes, String encodeCharset) {
        Charset cs = Charset.forName(encodeCharset);
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    /**
     * description: remove space from chars
     *
     * @param chars the char array to dispose
     * @return char[] remove space array
     */
    public static char[] trimChars(char[] chars) {
        int startPos = 0;
        int endPos = chars.length - 1;
        for (; startPos < chars.length ; startPos ++) {
            if (!Character.isSpaceChar(chars[startPos])) {
                break;
            }
        }
        for (; endPos > startPos; endPos -= 1) {
            if (!Character.isSpaceChar(chars[startPos])) {
                break;
            }
        }
        if (endPos <= startPos) {
            return new char[0];
        }
        char[] results = new char[endPos - startPos + 1];
        System.arraycopy(chars, startPos, results, 0, results.length);
        return results;
    }
}
