/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.utils.security;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
 * Description: The Class DecryptionUtil.
 *
 * @since 3.0.0
 */
public class DecryptionUtil {
    private SecureUtil encryptionDecryption;

    /**
     * Instantiates a new decryption util.
     *
     * @param encryptionDecryption the encryption decryption
     */
    public DecryptionUtil(SecureUtil encryptionDecryption) {
        this.encryptionDecryption = encryptionDecryption;
    }

    /**
     * Gets the decrypted string.
     *
     * @return the decrypted string
     */
    public String getDecryptedString() {
        return encryptionDecryption.getDecryptedString();
    }

    /**
     * Sets the decrypted string.
     *
     * @param decryptedString the new decrypted string
     */
    public void setDecryptedString(String decryptedString) {
        encryptionDecryption.setDecryptedString(decryptedString);
    }

    /**
     * Decrypt.
     *
     * @param strToDecrypt the str to decrypt
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final void decrypt(String strToDecrypt) throws UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException, DataStudioSecurityException {
        setDecryptedString(decryptString(strToDecrypt, StandardCharsets.UTF_8.name()));
    }

    /**
     * Decrypt string. returns decrypted string
     *
     * @param strToDecrypt the str to decrypt
     * @param charSet the char set
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final String decryptString(String strToDecrypt, String charSet) throws UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, DataStudioSecurityException {
        return new String(decryptByteArray(getBase4DecodeBase(strToDecrypt)), charSet);
    }

    private byte[] getBase4DecodeBase(String strToDecrypt) {
        return Base64.decodeBase64(strToDecrypt);
    }

    /**
     * Decrypt byte array.
     *
     * @param bytesToDecrypt the bytes to decrypt
     * @return the byte[]
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException the bad padding exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final byte[] decryptByteArray(byte[] bytesToDecrypt) throws UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, DataStudioSecurityException {
        byte[] ivByts = encryptionDecryption.getIVSalt();
        IvParameterSpec ivParam;
        if (null != ivByts) {
            ivParam = new IvParameterSpec(ivByts);
        } else {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR);
        }

        Cipher ciphr;
        try {
            ciphr = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        } catch (NoSuchAlgorithmException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (NoSuchPaddingException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        }

        try {
            ciphr.init(Cipher.DECRYPT_MODE, encryptionDecryption.getRandomKey(), ivParam);
        } catch (InvalidKeyException e) {
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, e);
        }
        return ciphr.doFinal(bytesToDecrypt);
    }
}
