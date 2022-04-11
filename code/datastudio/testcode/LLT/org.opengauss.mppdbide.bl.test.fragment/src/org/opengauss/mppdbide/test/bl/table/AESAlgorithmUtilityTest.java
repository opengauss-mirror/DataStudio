package org.opengauss.mppdbide.test.bl.table;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.AESAlgorithmUtility;
import org.opengauss.mppdbide.utils.security.AESAlgorithmUtility.KeyPartFileCreateOption;
import org.opengauss.mppdbide.utils.security.SecureUtil;

public class AESAlgorithmUtilityTest
{
    private static SecureUtil          secureUtil       = null;
    private static AESAlgorithmUtility algorithmUtility = null;
    private static Set<AclEntryPermission>    attributes       = null;
    private static ISetFilePermission withPermission = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        CommonLLTUtils.runLinuxFilePermissionInstance();
        withPermission=FilePermissionFactory.getFilePermissionInstance();
        Files.walkFileTree(Paths.get("aestest"), new ProfileFileVisitor());
        Path path=withPermission.createFileWithPermission(Paths.get("aestest").toString(), true, attributes, true);
        secureUtil = new SecureUtil();
        secureUtil.setPackagePath(path.toString());
        secureUtil.runPreEncryptionTask();
        algorithmUtility = secureUtil.getAesAlgorithmUtil();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        CommonLLTUtils.runLinuxFilePermissionInstance();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    private static final class ProfileFileVisitor implements FileVisitor<Path>
    {
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException
        {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
                throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException
        {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

    @Test
    public void test_CreateSecurityFolder_001()
    {
        String path = "aestest"+ File.separator +"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());
            KeyPartFileCreateOption file_CREATE_OPTION = algorithmUtility.createSecurityFolder(path);
            assertTrue(file_CREATE_OPTION.equals(KeyPartFileCreateOption.CREATED));
        }
        catch (DatabaseOperationException e)
        {
        	if (!e.getMessage()
                    .equalsIgnoreCase(
                            MessageConfigLoader
                                    .getProperty(IMessagesConstants.FILE_PERMISSION_ERROR)))
            {
                fail("Not expected to come here");
            }
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_CreateSecurityFolder_002()
    {
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get("aestest"), new ProfileFileVisitor());
            algorithmUtility.createSecurityFolder(path);
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            if (!e.getMessage()
                    .equalsIgnoreCase(
                            MessageConfigLoader
                                    .getProperty(IMessagesConstants.FILE_PERMISSION_ERROR)))
            {
                fail("Not expected to come here");
            }
        }
        catch (IOException e)
        {
            System.setProperty("file.encoding", "utf-8");
        }
    }

    @Test
    public void test_CreateSecurityFolder_003()
    {
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get("aestest"), new ProfileFileVisitor());

            withPermission.createFileWithPermission(Paths.get("aestest").toString(), true, attributes, true);

            algorithmUtility.createSecurityFolder(path);
        }
        catch (DatabaseOperationException e)
        {
        	if (!e.getMessage()
                    .equalsIgnoreCase(
                            MessageConfigLoader
                                    .getProperty(IMessagesConstants.FILE_PERMISSION_ERROR)))
            {
                fail("Not expected to come here");
            }
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_writeValueInFile_001()
    {
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get("aestest"), new ProfileFileVisitor());
            withPermission.createFileWithPermission(Paths.get("aestest").toString(), true, attributes, true);
            algorithmUtility.createSecurityFolder(path);
            BigInteger strMaskData = algorithmUtility.getRandom();
            algorithmUtility.writeValueInFile(path, "KEYPART_1.txt",
                    strMaskData.toString());
            assertTrue(Files.exists(Paths.get(path + File.separator+"KEYPART_1.txt"),
                    LinkOption.NOFOLLOW_LINKS));
        }
        catch (DatabaseOperationException e)
        {
            fail("Not expected to come here");
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_writeValueInFile_002()
    {
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());
            withPermission.createFileWithPermission(Paths.get("aestest").toString(), true, attributes, true);
            algorithmUtility.createSecurityFolder(path);
            BigInteger strMaskData = algorithmUtility.getRandom();
            algorithmUtility.writeValueInFile(path, "KEYPART_1.txt",
                    strMaskData.toString());
            algorithmUtility.writeValueInFile(path, "KEYPART_1.txt",
                    strMaskData.toString());
            assertTrue(Files.exists(Paths.get(path + File.separator+"KEYPART_1.txt"),
                    LinkOption.NOFOLLOW_LINKS));
        }
        catch (DatabaseOperationException e)
        {
            fail("Not expected to come here");
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_writeValueInFile_003()
    {
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get("aestest"), new ProfileFileVisitor());
            withPermission.createFileWithPermission(Paths.get("aestest").toString(), true, attributes, true);
            algorithmUtility.createSecurityFolder(path);
            BigInteger strMaskData = algorithmUtility.getRandom();

            algorithmUtility.writeValueInFile(path, "KEYPART_1.txt",
                    strMaskData.toString());
        }
        catch (DatabaseOperationException e)
        {
            fail("Not expected to come here");
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_writeValueInFile_004()
    {
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());
            // Files.createDirectories(Paths.get(path), attributes);
            BigInteger strMaskData = algorithmUtility.getRandom();

            algorithmUtility.writeValueInFile(path, "KEYPART_1.txt",
                    strMaskData.toString());
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            if (!(e.getCause() instanceof NoSuchFileException))
            {
                fail("Not expected to come here");
            }
        }
        catch (IOException e)
        {
            System.setProperty("file.encoding", "utf-8");
        }
    }

    @Test
    public void test_generateXORResult_001()
    {
        String path = "aestest"+File.separator+"config";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());

            secureUtil.runPreEncryptionTask();
            String wkKey = algorithmUtility.generateXORResult(path);
            assertTrue(wkKey != null && !wkKey.isEmpty());
        }
        catch (NoSuchAlgorithmException e)
        {
            fail("Not expected to come here");
        }
        catch (InvalidKeySpecException e)
        {
            fail("Not expected to come here");
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
        catch (DataStudioSecurityException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_generateXORResult_003()
    {
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());

            algorithmUtility.createSecurityFolder(path);
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());
            algorithmUtility.generateXORResult(path);
            fail("Not expected to come here");
        }
        catch (NoSuchAlgorithmException e)
        {
            fail("Not expected to come here");
        }
        catch (InvalidKeySpecException e)
        {
            fail("Not expected to come here");
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
        	if (!(e.getCause() instanceof NoSuchFileException)) {
                fail("Not expected to come here");
            }
        }
        catch (DataStudioSecurityException e)
        {
            if (!(e.getCause() instanceof IOException))
            {
                fail("Not expected to come here");
            }
        }
    }

    @Test
    public void test_readWKResult_001()
    {
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());
            algorithmUtility.createSecurityFolder(path);
            String wkKey = algorithmUtility.readWKResult(new File(path),
                    "SEC_PART1.txt");
            assertTrue(wkKey != null && !wkKey.isEmpty());
        }
        catch (IOException e)
        {
            if (!(e instanceof NoSuchFileException)) {
                fail("Not expected to come here");
            }
        }
        catch (DatabaseOperationException e)
        {
        	if (!(e.getCause() instanceof NoSuchFileException)) {
                fail("Not expected to come here");
            }
        }
    }

    @Test
    public void test_readWKResult_002()
    {
        String path = "aestest"+File.separator+"security";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());
            algorithmUtility.createSecurityFolder(path);
            algorithmUtility.readWKResult(new File(path), "KEY_PART_1.txt");
            fail("Not expected to come here");
        }
        catch (IOException e)
        {
            if (!(e instanceof NoSuchFileException))
            {
                fail("Not expected to come here");
            }

        }
        catch (DatabaseOperationException e)
        {
        	if (!(e.getCause() instanceof NoSuchFileException))
            {
                fail("Not expected to come here");
            }
        }
    }

    @Test
    public void test_setKey_001()
    {
        String path = "aestest"+File.separator+"config";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());
            secureUtil.runPreEncryptionTask();
            algorithmUtility.setKey(algorithmUtility.generateXORResult(path));
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
        catch (NoSuchAlgorithmException e)
        {
            fail("Not expected to come here");
        }
        catch (InvalidKeySpecException e)
        {
            fail("Not expected to come here");
        }
        catch (DataStudioSecurityException e)
        {
            fail("Not expected to come here");
        }
    }

    @Test
    public void test_setKey_002()
    {
        String path = "aestest"+File.separator+"config";
        secureUtil.setPackagePath("aestest");
        try
        {
            Files.walkFileTree(Paths.get(path), new ProfileFileVisitor());
            secureUtil.runPreEncryptionTask();
            String str = algorithmUtility.generateXORResult(path);
            System.setProperty("file.encoding", "");
            algorithmUtility.setKey(str);
        }
        catch (IOException e)
        {
            if (!(e instanceof UnsupportedEncodingException))
            {
                fail("Not expected to come here");
            }
            System.setProperty("file.encoding", "utf-8");
        }
        catch (NoSuchAlgorithmException e)
        {
            fail("Not expected to come here");
        }
        catch (InvalidKeySpecException e)
        {
            fail("Not expected to come here");
        }
        catch (DataStudioSecurityException e)
        {
            fail("Not expected to come here");
        }
    }
    
    @Test
    public void test_generatePBKDF_001()
    {
        try
        {
            algorithmUtility.generatePBKDF("password", false);
        }
        catch (IOException e)
        {
            fail("Not expected to come here");
        }
        catch (NoSuchAlgorithmException e)
        {
            fail("Not expected to come here");
        }
        catch (InvalidKeySpecException e)
        {
            fail("Not expected to come here");
        }
    }
       
    @Test
    public void test_bytes_encrypt_decrypt_001()
    {
        byte[] text = {'A','H','A','N','A'};
        try
        {
            secureUtil.runPreEncryptionTask();
            byte[] enStr = secureUtil.encryptByteArray(text);
            byte[] deStr = secureUtil.decryptByteArray(enStr);
            
            boolean compare = Arrays.equals(text, deStr);
            if (!compare)
            {
                fail("Not expected to come here");
            }
        }
        catch (Exception e)
        {
        	 MPPDBIDELoggerUtility.securityError("Error occured during encrypting or decrypting");
            fail("Not expected to come here");

        }
    }
}
