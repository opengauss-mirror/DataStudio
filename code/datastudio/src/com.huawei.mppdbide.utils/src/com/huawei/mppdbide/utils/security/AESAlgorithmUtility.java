/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.input.BoundedInputStream;

import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.DSFolderDeleteUtility;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class AESAlgorithmUtility.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AESAlgorithmUtility {
    /**
     * The Constant PBKDF2_ALGORITHM.
     */
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";   
    
    private static final String ENVIRON_FILE = EnvirnmentVariableValidator.validateAndGetFileSeperator() 
        + "environ.txt";
    private static final String LOCALFILE = EnvirnmentVariableValidator.validateAndGetFileSeperator() + "local.txt";
    private static final String TEMP_FILE = EnvirnmentVariableValidator.validateAndGetFileSeperator() + "temp.txt";
    private static final String TEMP_WORKSPACE = EnvirnmentVariableValidator.validateAndGetFileSeperator() + "temp"
        + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "workspace";
    private static final String DROPIN_USER = EnvirnmentVariableValidator.validateAndGetFileSeperator() + "dropin"
        + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "user";
    private static final String PROFILE_DUMMY = EnvirnmentVariableValidator.validateAndGetFileSeperator() + "profile"
        + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "dummy";
    private static final String CHARSET_NAME = "UTF-8";
    
    private static String keyPart3 = "18634072297704355082300964104946051172";
    
    private String pbkdf2Salt = null;
    private SecureUtil secureUtil;
    private ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();

    public void setPbkdf2Salt(String pbkdf2Salt) {
        this.pbkdf2Salt = pbkdf2Salt;
    }

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum KeyPartFileCreateOption.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public enum KeyPartFileCreateOption {

        /**
         * The created.
         */
        CREATED,
        /**
         * The not created.
         */
        NOT_CREATED,
        /**
         * The exception.
         */
        EXCEPTION
    }

    /**
     * Instantiates a new AES algorithm utility.
     *
     * @param encryptionDecryption the encryption decryption
     */
    public AESAlgorithmUtility(SecureUtil encryptionDecryption) {
        this.secureUtil = encryptionDecryption;
    }

    /**
     * Creates the security folder.
     *
     * @param path the path
     * @return the key part file create option
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws DatabaseOperationException the database operation exception
     */
    public final KeyPartFileCreateOption createSecurityFolder(String path)
            throws IOException, DatabaseOperationException {

        KeyPartFileCreateOption keyPartFileCreate = KeyPartFileCreateOption.NOT_CREATED;
        String strFileName = null;
        String secPath = "";
        int counter = 0;

        if (Files.exists(Paths.get(secPath))) {
            for (int count = 1; count <= 2; count++) {
                secPath = getSecPath(path, count);
                strFileName = getSecFileName(count);
                if (Files.exists(Paths.get(String.format(Locale.ENGLISH, "%s%s%s", secPath, '/', strFileName)))) {
                    counter++;
                }
            }
            if (Files.exists(Paths.get(String.format(Locale.ENGLISH, "%s%s%s", 
                path, PROFILE_DUMMY, TEMP_FILE)))) {
                counter++;
            }
            
            // If files inside security folder is not equal to 3 (key part) + 1
            // (working key) delete the folder
            if (counter != 3) {
                deleteSecurityFolder(path);
            }  
        }
        
        for (int count = 1; count <= 2; count++) {
            secPath = getSecPath(path, count);
            withPermission.createFileWithPermission(secPath, true, null, true);
        }

        counter = 0;
        for (int count = 1; count <= 2; count++) {
            try {
                setKey(this.generatePBKDF(keyPart3, false));
                String encryptedBigRandomNumberStr = secureUtil.encryptString(getRandom().toString(),
                        CHARSET_NAME);
                if (count == 1) {
                    encryptedBigRandomNumberStr = encryptedBigRandomNumberStr + pbkdf2Salt;
                }
                secPath = getSecPath(path, count);
                strFileName = getSecFileName(count);
                boolean isFileCreated = writeValueInFile(secPath, strFileName, encryptedBigRandomNumberStr);
                if (isFileCreated) {
                    counter++;
                }
            } catch (DataStudioSecurityException | IllegalArgumentException | NoSuchAlgorithmException
                    | InvalidKeySpecException e) {
                MPPDBIDELoggerUtility.error("Error encrypting ");
            }
        }

        if (counter == 2) {
            keyPartFileCreate = KeyPartFileCreateOption.CREATED;
        }

        return keyPartFileCreate;
    }

    private String getSecFileName(int count) {
        String secFileName = null;
        switch (count) {
            case 1: {
                secFileName = "environ.txt";
                break;
            }
            case 2: {
                secFileName = "local.txt";
                break;
            }
            case 3: {
                secFileName = "temp.txt";
                break;
            }
            default: {
                break;
            }
        }
        return secFileName;
	}

    /**
     * the deleteSecurityFolder
     * 
     * @param path the path
     * @throws IOException  the IOException
     */
    public void deleteSecurityFolder(String path) throws IOException {
        String secPath = "";
        for (int count = 1; count <= 3; count++) {
            secPath = getSecPath(path, count);
            Files.walkFileTree(Paths.get(secPath), new DSFolderDeleteUtility());
        }
    }

    private String getSecPath(String secPath, int count) {
        String lSecPath = null;
            switch (count) {
                case 1: {
                    lSecPath = String.format(Locale.ENGLISH, "%s%s", secPath, TEMP_WORKSPACE);
                    break;
                }
                case 2: {
                    lSecPath = String.format(Locale.ENGLISH, "%s%s", secPath, DROPIN_USER);
                    break;
                }
                case 3: {
                    lSecPath = String.format(Locale.ENGLISH, "%s%s", secPath, PROFILE_DUMMY);
                    break;
                }
                default: {
                    break;
                }
            }
        return lSecPath;
    }

    /**
     * Write value in file.
     *
     * @param fileDirectory the file directory
     * @param strFileName the str file name
     * @param strMaskData the str mask data
     * @return true, if successful
     * @throws DatabaseOperationException the database operation exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public final boolean writeValueInFile(String fileDirectory, String strFileName, String strMaskData)
            throws DatabaseOperationException, IOException {

        File createRandomFile = new File(fileDirectory, strFileName);
        BufferedWriter bw = null;
        FileOutputStream fos = null;
        OutputStreamWriter filewriter = null;
        boolean isFileCreated = false;
        try {
            if (Files.notExists(createRandomFile.toPath())) {
                Path securePath = withPermission.createFileWithPermission(createRandomFile.getCanonicalPath(), false,
                        null, true);

                fos = new FileOutputStream(securePath.toFile(), true);
                filewriter = new OutputStreamWriter(fos, CHARSET_NAME);
                bw = new BufferedWriter(filewriter);
                bw.write(strMaskData);
                isFileCreated = true;
            }
            return isFileCreated;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                    bw = null;
                }
            } catch (IOException ioException) {
                bw = null;
            }
            try {
                if (filewriter != null) {
                    filewriter.close();
                    filewriter = null;
                }
            } catch (IOException ioExceptione) {
                filewriter = null;
            }
            try {
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException ioException) {
                fos = null;
            }
        }
    }

    /**
     * Gets the random.
     *
     * @return the random
     */
    public BigInteger getRandom() {
        byte[] byteRandomArray = SecureRandomGenerator.getRandomNumber();
        BigInteger generatedRandom = new BigInteger(byteRandomArray);
        return generatedRandom.abs();
    }

    /**
     * Generate XOR result.
     *
     * @param strDMHome the str DM home
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeySpecException the invalid key spec exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public final String generateXORResult(String strDMHome)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, DataStudioSecurityException {
        File createRandomFile = null;
        BigInteger xorResult = null;
        ArrayList<String> arrList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        String strFileName = null;
        String strSecHome = null;
        try {
            for (int count = 1; count <= 2; count++) {
                strSecHome = getSecPath(strDMHome, count);
                strFileName = getSecFileName(count);
                createRandomFile = new File(strSecHome, strFileName);
                long sizeOfFile = Files.size(createRandomFile.toPath());
                if (sizeOfFile > MPPDBIDEConstants.ONE_KB) {
                    throw new IOException("Failed to load key. File size exceeded");
                }
                String fileContent = new String(Files.readAllBytes(createRandomFile.toPath()), CHARSET_NAME);
                String key = "";
                if (fileContent.length() >= 64) {
                    key = fileContent.substring(0, 64);
                } else {
                    throw new IOException("Failed to load key. Illegal key size");
                }
                setKey(this.generatePBKDF(keyPart3, false));
                String decryptedBigRandomNumberStr = secureUtil.decryptString(key, CHARSET_NAME);
                arrList.add(decryptedBigRandomNumberStr);
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException | IOException ex) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), ex);
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, ex);
        }
        // All 3 file Content is ready for Use to Calculate the XOR
        try {
            xorResult = new BigInteger(String.valueOf(keyPart3)).xor(
                    new BigInteger(String.valueOf(arrList.get(0))).xor(new BigInteger(String.valueOf(arrList.get(1)))));
        } catch (NumberFormatException numberFormatException) {
            throw new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR, numberFormatException);
        }
        MPPDBIDELoggerUtility.securityInfo("Root key generated");
        return generatePBKDF(xorResult.toString(), false);
    }

    /**
     * Close file.
     *
     * @param brParam the br param
     * @param frParam the fr param
     * @param fiParam the fi param
     * @param fiParam the bi param
     */
    private void closeFile(BufferedReader brParam, InputStreamReader frParam, FileInputStream fiParam,
            BoundedInputStream biParam) {
        BufferedReader br = brParam;
        InputStreamReader fr = frParam;
        FileInputStream fi = fiParam;
        BoundedInputStream bi = biParam;
        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException ex) {
            br = null;
        }
        try {
            if (fr != null) {
                fr.close();
            }
        } catch (IOException ex) {
            fr = null;
        }
        try {
            if (fi != null) {
                fi.close();
            }
        } catch (IOException ex) {
            fi = null;
        }
        try {
            if (null != bi) {
                bi.close();
            }
        } catch (IOException ioException) {
            bi = null;
        }
    }

    /**
     * Read WK result.
     *
     * @param fileDirectory the file directory
     * @param strFileName the str file name
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public final String readWKResult(File fileDirectory, String strFileName) throws IOException {

        BufferedReader br = null;
        InputStreamReader fr = null;
        BoundedInputStream bi = null;
        FileInputStream fi = null;
        File createRandomFile = null;
        String wkSalt = "";
        try {
            String currentLine = null;
            createRandomFile = new File(fileDirectory, strFileName);
            long fileSize = Files.size(createRandomFile.toPath());
            if (fileSize > MPPDBIDEConstants.ONE_KB) {
                throw new IOException("Failed to load key. File size exceeded");
            }
            fi = new FileInputStream(createRandomFile.getCanonicalFile());
            bi = new BoundedInputStream(fi, 1024);
            fr = new InputStreamReader(bi, CHARSET_NAME);
            br = new BufferedReader(fr, 1024);
            currentLine = br.readLine();
            while (currentLine != null) {
                wkSalt = wkSalt.concat(currentLine);
                currentLine = br.readLine();
            }

        } finally {
            closeFile(br, fr, fi, bi);
        }
        return wkSalt;
    }

    /**
     * Sets the key.
     *
     * @param rootKey the new key
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public void setKey(String rootKey) throws UnsupportedEncodingException {

        secureUtil.setKey(rootKey.getBytes(CHARSET_NAME));

        secureUtil.setKey(Arrays.copyOf(secureUtil.getKey(), 16));

        secureUtil.setRandomKey(new SecretKeySpec(secureUtil.getKey(), "AES"));

    }

    /**
     * Generate PBKDF.
     *
     * @param password the password
     * @return the string
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeySpecException the invalid key spec exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public final String generatePBKDF(String password, boolean isGenWorkingKeyFlow)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        int iterations = MPPDBIDEConstants.PBKDF_ITERATIONS;
        char[] chars = password.toCharArray();
        byte[] salt = isGenWorkingKeyFlow ? SecureRandomGenerator.getRandomNumber() : getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return toHex(hash);
    }

    /**
     * Gets the salt.
     *
     * @return the salt
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private byte[] getSalt() throws IOException {
        String securityFolderPath = secureUtil.getSecurityFolder();
        String saltPath = securityFolderPath + TEMP_WORKSPACE;

        File createRandomsalt = null;
        String defaultSalt = "";
        try {
            String fileContent = null;
            createRandomsalt = new File(saltPath, ENVIRON_FILE);
            long sizeOfFile = Files.size(createRandomsalt.toPath());
            if (sizeOfFile > MPPDBIDEConstants.ONE_KB) {
                throw new IOException("Failed to load Salt. File size exceeded");
            }
            fileContent = new String(Files.readAllBytes(createRandomsalt.toPath()), CHARSET_NAME);
            if (fileContent.length() > 64) {
                String salt = fileContent.substring(64, fileContent.length());
                defaultSalt = defaultSalt.concat(salt);
            } else {
                throw new IOException("Failed to load Salt. Illegal salt size");
            }

            pbkdf2Salt = defaultSalt;
        } catch (FileNotFoundException | NoSuchFileException excep) {
            if (pbkdf2Salt != null) {
                return pbkdf2Salt.getBytes(CHARSET_NAME);
            } else {
                return new byte[0];
            }
        }

        return pbkdf2Salt.getBytes(CHARSET_NAME);
    }

    /**
     * To hex.
     * 
     * @param array the array
     * @return the string
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    private String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format(Locale.ENGLISH, "%0" + paddingLength + 'd', 0) + hex;
        } else {
            return hex;
        }
    }

    /**
     * Gets the IV salt.
     *
     * @return the IV salt
     */
    public final byte[] getIVSalt() {
        try {
            return toHex(this.getSalt()).substring(0, 16).getBytes(CHARSET_NAME);
        } catch (UnsupportedEncodingException exception) {
            return null;
        } catch (NoSuchAlgorithmException exception) {
            return null;
        } catch (IOException exception) {
            return null;
        }
    }

}
