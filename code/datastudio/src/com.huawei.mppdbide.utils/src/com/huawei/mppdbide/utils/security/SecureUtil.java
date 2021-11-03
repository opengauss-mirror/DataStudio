/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.security;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.DSFolderDeleteUtility;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.AESAlgorithmUtility.KeyPartFileCreateOption;

/**
 * 
 * Title: class
 * 
 * Description: The Class SecureUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class SecureUtil {
    
    private static final String TEMP_FILE = "temp.txt";
    private static final String CONFIG = String.format(Locale.ENGLISH, "%s%s", 
        EnvirnmentVariableValidator.validateAndGetFileSeperator(), "config");
    private static final String PROFILE_DUMMY = EnvirnmentVariableValidator.validateAndGetFileSeperator() + "profile" + 
        EnvirnmentVariableValidator.validateAndGetFileSeperator() + "dummy";
    private ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();

    private SecretKeySpec randomKey;

    private byte[] key;

    private String decryptedString;

    private String encryptedString;

    private EncryptionUtil encryption;
    private DecryptionUtil decryption;
    private AESAlgorithmUtility aesAlgorithmUtil;

    private String packagePath;
    private String strRootKey;

    private String strWorkKey;
    private static final Object INSTANCE_LOCK = new Object();

    /**
     * Sets the package path.
     *
     * @param path the new package path
     */
    public void setPackagePath(String path) {
        this.packagePath = path;
    }

    /**
     * Gets the security folder.
     *
     * @return the security folder
     */
    public String getSecurityFolder() {
        return packagePath + CONFIG;
    }

    /**
     * Instantiates a new secure util.
     */
    public SecureUtil() {
        // Making this class singleton.
        this.encryption = new EncryptionUtil(this);
        this.decryption = new DecryptionUtil(this);
        this.aesAlgorithmUtil = new AESAlgorithmUtility(this);
    }

    /**
     * Gets the aes algorithm util.
     *
     * @return the aes algorithm util
     */
    public AESAlgorithmUtility getAesAlgorithmUtil() {
        return this.aesAlgorithmUtil;
    }

    /**
     * Sets the aes algorithm util.
     *
     * @param aesAlgorithmUtil the new aes algorithm util
     */
    public void setAesAlgorithmUtil(AESAlgorithmUtility aesAlgorithmUtil) {
        this.aesAlgorithmUtil = aesAlgorithmUtil;
    }

    /**
     * Sets the str root key.
     *
     * @param strRootKey the new str root key
     */
    public void setStrRootKey(String strRootKey) {
        this.strRootKey = strRootKey;
    }

    /**
     * Sets the str work key.
     *
     * @param strWorkKey the new str work key
     */
    public void setStrWorkKey(String strWorkKey) {
        this.strWorkKey = strWorkKey;
    }

    /**
     * Creates the secure salt.
     *
     * @throws DataStudioSecurityException the data studio security exception
     * @throws DatabaseOperationException the database operation exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected String createSecureSalt() throws DataStudioSecurityException, DatabaseOperationException, IOException {
        BigInteger bigRandomNumber = getAesAlgorithmUtil().getRandom();
        return bigRandomNumber.toString();
    }

    /**
     * Run pre encryption task.
     *
     * @throws DataStudioSecurityException the data studio security exception
     */
    public void runPreEncryptionTask() throws DataStudioSecurityException {
        File file = new File(packagePath);
        
        // Check if folder has write permission
        boolean canWrite = Files.isWritable(file.toPath());
        if (!canWrite) {
            return;
        }
        
        try {
            ISetFilePermission set = FilePermissionFactory.getFilePermissionInstance();
            set.createFileWithPermission(getSecurityFolder(), true, null, true);
            set.createFileWithPermission(getSecurityFolder() +
                EnvirnmentVariableValidator.validateAndGetFileSeperator() + "temp", true, null, true);
            set.createFileWithPermission(getSecurityFolder() +
                EnvirnmentVariableValidator.validateAndGetFileSeperator() + "dropin", true, null, true);
            set.createFileWithPermission(getSecurityFolder() +
                EnvirnmentVariableValidator.validateAndGetFileSeperator() + "profile", true, null, true);
            String salt = createSecureSalt();
            aesAlgorithmUtil.setPbkdf2Salt(salt);
            KeyPartFileCreateOption keyPartFileCreate = getAesAlgorithmUtil()
                    .createSecurityFolder(getSecurityFolder());
            if (keyPartFileCreate.equals(KeyPartFileCreateOption.CREATED)) {
                genarateRootKey();
            }
        } catch (IOException ex) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), ex);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, ex);
        } catch (IllegalArgumentException ex) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), ex);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, ex);
        } catch (DatabaseOperationException ex) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), ex);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, ex);
        }
    }

    private void genarateRootKey()
        throws IOException, DataStudioSecurityException, DatabaseOperationException {
        // RootKey based on XOR operation from the Different SEC_PART
        try {
            setStrRootKey(getAesAlgorithmUtil().generateXORResult(getSecurityFolder()));
            getAesAlgorithmUtil().setKey(this.strRootKey);
            // Get the Working Key and write in 4th Key File
            String strWorkingKeyRandom = getAesAlgorithmUtil().getRandom().toString();
            String strWorkingKey = null;
            strWorkingKey = getAesAlgorithmUtil().generatePBKDF(strWorkingKeyRandom, true);
            encryption.encrypt(strWorkingKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalBlockSizeException 
                    | BadPaddingException ex) {
            MPPDBIDELoggerUtility
                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), ex);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, ex);
        }
        
        withPermission.createFileWithPermission(String.format(Locale.ENGLISH, "%s%s", 
            getSecurityFolder(), PROFILE_DUMMY), true, null, true);
        getAesAlgorithmUtil().writeValueInFile(String.format(Locale.ENGLISH, "%s%s", 
            getSecurityFolder(), PROFILE_DUMMY), TEMP_FILE, encryption.getEncryptedString());
        setStrWorkKey(encryption.getEncryptedString());
    }

    /**
     * Encrypt prd.
     *
     * @param prd the prd
     * @return the string
     * @throws DataStudioSecurityException the data studio security exception
     */
    public String encryptPrd(char[] prd) throws DataStudioSecurityException {

        MPPDBIDELoggerUtility.securityInfo("Encrypting password to be saved");
        File keyDir = new File(String.format(Locale.ENGLISH, "%s%s", getSecurityFolder(), PROFILE_DUMMY));
        // Read the Folder Name where user wants to create the All KEY Files

        try {
            setStrWorkKey(getAesAlgorithmUtil().readWKResult(keyDir, TEMP_FILE));
            setStrRootKey(getAesAlgorithmUtil().generateXORResult(getSecurityFolder()));

            getAesAlgorithmUtil().setKey(this.strRootKey);
            decryption.decrypt(this.strWorkKey);

            // Set the WK for prd Encryption
            getAesAlgorithmUtil().setKey(decryption.getDecryptedString());
            char[] trimChars = EncryptionUtil.trimChars(prd);
            encryption.encrypt(trimChars);
            clearPassword(trimChars);
        } catch (IOException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (NoSuchAlgorithmException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (InvalidKeySpecException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (IllegalBlockSizeException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (BadPaddingException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        }
        return encryption.getEncryptedString();

    }

    /**
     * Decrypt prd.
     *
     * @param string the string
     * @return the char[]
     * @throws DataStudioSecurityException the data studio security exception
     */
    public char[] decryptPrd(String string) throws DataStudioSecurityException {
        try {
            return decryptPswdHandleError(string);
        } catch (DataStudioSecurityException exe) {
            resetSecurityDetails();
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        }
    }

    /**
     * Decrypt pswd handle error.
     *
     * @param string the string
     * @return the char[]
     * @throws DataStudioSecurityException the data studio security exception
     */
    private char[] decryptPswdHandleError(String string) throws DataStudioSecurityException {
        MPPDBIDELoggerUtility.securityInfo("Decrypting saved password");
        File keyDir = new File(String.format(Locale.ENGLISH, "%s%s", getSecurityFolder(), PROFILE_DUMMY));
        try {

            this.strWorkKey = this.aesAlgorithmUtil.readWKResult(keyDir, TEMP_FILE);

            this.strRootKey = this.aesAlgorithmUtil.generateXORResult(getSecurityFolder());
            getAesAlgorithmUtil().setKey(this.strRootKey);
            decryption.decrypt(this.strWorkKey);

            // Set the WK for prd Encryption
            getAesAlgorithmUtil().setKey(decryption.getDecryptedString());

            decryption.decrypt(string);
        } catch (IOException exe) {
            MPPDBIDELoggerUtility
                    .securityError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (NoSuchAlgorithmException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (IllegalBlockSizeException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (BadPaddingException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (InvalidKeySpecException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (Exception exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        }

        return decryption.getDecryptedString().toCharArray();
    }

    /**
     * Reset security details.
     *
     * @throws DataStudioSecurityException the data studio security exception
     */
    private void resetSecurityDetails() throws DataStudioSecurityException {
        deleteSecurityFolder();
        runPreEncryptionTask();
    }

    /**
     * Delete security folder.
     */
    public void deleteSecurityFolder() {
        Path securityFolderPath = Paths.get(getSecurityFolder());
        if (!Files.exists(securityFolderPath)) {
            return;
        }
        try {
            Files.walkFileTree(securityFolderPath, new DSFolderDeleteUtility());
        } catch (IOException exception) {
            MPPDBIDELoggerUtility
                    .securityError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR));
        }
    }

    /**
     * Gets the random key.
     *
     * @return the random key
     */
    public SecretKeySpec getRandomKey() {
        return this.randomKey;
    }

    /**
     * Sets the random key.
     *
     * @param randomKey the new random key
     */
    public void setRandomKey(SecretKeySpec randomKey) {
        this.randomKey = randomKey;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public byte[] getKey() {
        if (this.key == null) {
            return new byte[0];
        }
        return (byte[]) this.key.clone();
    }

    /**
     * Sets the key.
     *
     * @param key the new key
     */
    public void setKey(byte[] key) {
        this.key = (byte[]) key.clone();
    }

    /**
     * Gets the decrypted string.
     *
     * @return the decrypted string
     */
    public String getDecryptedString() {
        return this.decryptedString;
    }

    /**
     * Sets the decrypted string.
     *
     * @param decryptedString the new decrypted string
     */
    public void setDecryptedString(String decryptedString) {
        this.decryptedString = decryptedString;
    }

    /**
     * Gets the encrypted string.
     *
     * @return the encrypted string
     */
    public String getEncryptedString() {
        return this.encryptedString;
    }

    /**
     * Sets the encrypted string.
     *
     * @param encryptedString the new encrypted string
     */
    public void setEncryptedString(String encryptedString) {
        this.encryptedString = encryptedString;
    }

    /**
     * Encrypt byte array.
     *
     * @param bytesToEncrypt the bytes to encrypt
     * @return the byte[]
     * @throws DataStudioSecurityException the data studio security exception
     */
    public byte[] encryptByteArray(byte[] bytesToEncrypt) throws DataStudioSecurityException {
        try {
            MPPDBIDELoggerUtility.securityInfo("Encrypting files to be autosaved");
            File keyDir = new File(String.format(Locale.ENGLISH, "%s%s", getSecurityFolder(), PROFILE_DUMMY));
            setStrWorkKey(getAesAlgorithmUtil().readWKResult(keyDir, TEMP_FILE));
            setStrRootKey(getAesAlgorithmUtil().generateXORResult(getSecurityFolder()));

            getAesAlgorithmUtil().setKey(this.strRootKey);
            decryption.decrypt(this.strWorkKey);

            getAesAlgorithmUtil().setKey(decryption.getDecryptedString());

            return this.encryption.encryptByteArray(bytesToEncrypt);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException | NoSuchAlgorithmException
                | IOException exe) {
            resetSecurityDetails();
            MPPDBIDELoggerUtility
                    .securityError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        } catch (DataStudioSecurityException exe) {
            resetSecurityDetails();
            throw exe;
        }
    }

    /**
     * Decrypt byte array.
     *
     * @param bytesToDecrypt the bytes to decrypt
     * @return the byte[]
     * @throws DataStudioSecurityException the data studio security exception
     */
    public byte[] decryptByteArray(byte[] bytesToDecrypt) throws DataStudioSecurityException {
        MPPDBIDELoggerUtility.securityInfo("Decrypting autosaved files");
        try {
            File keyDir = null;
            keyDir = new File(String.format(Locale.ENGLISH, "%s%s", getSecurityFolder(), PROFILE_DUMMY));
            this.strWorkKey = this.aesAlgorithmUtil.readWKResult(keyDir, TEMP_FILE);
            this.strRootKey = this.aesAlgorithmUtil.generateXORResult(getSecurityFolder());

            this.aesAlgorithmUtil.setKey(this.strRootKey);
            decryption.decrypt(this.strWorkKey);

            this.aesAlgorithmUtil.setKey(decryption.getDecryptedString());

            return this.decryption.decryptByteArray(bytesToDecrypt);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException | NoSuchAlgorithmException
                | IOException exe) {
            resetSecurityDetails();
            MPPDBIDELoggerUtility
                    .securityError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        }
    }

    /**
     * Encrypt string.
     *
     * @param str the str
     * @param charSet the char set
     * @return the string
     * @throws DataStudioSecurityException the data studio security exception
     */
    public String encryptString(String str, String charSet) throws DataStudioSecurityException {
        try {
            return this.encryption.encryptString(str, charSet);
        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exe);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, exe);
        }
    }

    /**
     * Decrypt string.
     *
     * @param str the str
     * @param charSet the char set
     * @return the string
     * @throws DataStudioSecurityException the data studio security exception
     */
    public String decryptString(String str, String charSet) throws DataStudioSecurityException {
        try {
            return this.decryption.decryptString(str, charSet);
        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, e);
        }
    }

    /**
     * Gets the IV salt.
     *
     * @return the IV salt
     */
    public byte[] getIVSalt() {
        return this.aesAlgorithmUtil.getIVSalt();
    }

    /**
     * Clear password.
     *
     * @param clearProfilePrd the clear profile prd
     */
    public static void clearPassword(char[] clearProfilePrd) {
        if (null == clearProfilePrd) {
            return;
        }

        for (int i = 0; i < clearProfilePrd.length; i++) {
            clearProfilePrd[i] = 0;
        }
        MemoryCleaner.cleanUpMemory();
    }

    /**
     * description: clean key of string info
     *
     * @param key then clean String key
     */
    public static void cleanKeyString(String key) {
        if (key == null || "".equals(key)) {
            return;
        }
        return;
    }
}
