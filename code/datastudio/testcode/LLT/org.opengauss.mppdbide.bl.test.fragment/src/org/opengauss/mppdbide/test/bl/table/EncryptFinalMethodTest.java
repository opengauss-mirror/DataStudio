package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.security.EncryptionUtil;
import org.opengauss.mppdbide.utils.security.SecureUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EncryptionUtil.class)
public class EncryptFinalMethodTest {
    
    @Test
    public void test_TTA_ENCRYPTION_001_FUNC_001_03()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        char[] pswd = "password".toCharArray();
        try
        {
            EncryptionUtil mockEncrypt = PowerMock.createMock(EncryptionUtil.class);
            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("encryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, mockEncrypt);
            enc.setAccessible(false);
            EasyMock.expect(mockEncrypt.encrypt("password")).andThrow(new DataStudioSecurityException(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            EasyMock.replay(mockEncrypt);
            encryptionDecryption.encryptPrd(pswd);
            fail("Not expected to come here.");
        }
        catch (DataStudioSecurityException e)
        {
        }
        catch (NoClassDefFoundError e)
        {
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_TTA_ENCRYPTION_001_FUNC_001_04()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        char[] pswd = "password".toCharArray();
        try
        {
            EncryptionUtil mockEncrypt = PowerMock.createMock(EncryptionUtil.class);

            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("encryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, mockEncrypt);
            enc.setAccessible(false);
            EasyMock.expect(mockEncrypt.encrypt("password")).andThrow(new UnsupportedEncodingException(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            EasyMock.replay(mockEncrypt);
            encryptionDecryption.encryptPrd(pswd);
            fail("Not expected to come here.");
        }
        catch (DataStudioSecurityException e)
        {
        }
        catch (NoClassDefFoundError e)
        {
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
            e.printStackTrace();
        }

    }

    @Test
    public void test_TTA_ENCRYPTION_001_FUNC_001_05()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        char[] pswd = "password".toCharArray();
        try
        {
            EncryptionUtil mockEncrypt = PowerMock.createMock(EncryptionUtil.class);

            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("encryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, mockEncrypt);
            enc.setAccessible(false);
            EasyMock.expect(mockEncrypt.encrypt("password")).andThrow(new BadPaddingException(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            EasyMock.replay(mockEncrypt);
            
            encryptionDecryption.encryptPrd(pswd);
            fail("Not expected to come here.");
        }
        catch (DataStudioSecurityException e)
        {
        }
        catch (NoClassDefFoundError e)
        {
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
            e.printStackTrace();
        }

    }

    @Test
    public void test_TTA_ENCRYPTION_001_FUNC_001_06()
    {
        SecureUtil encryptionDecryption = new SecureUtil();
        char[] pswd = "password".toCharArray();
        try
        {
            EncryptionUtil mockEncrypt = PowerMock.createMock(EncryptionUtil.class);

            Field enc = encryptionDecryption.getClass()
                    .getDeclaredField("encryption");
            enc.setAccessible(true);
            enc.set(encryptionDecryption, mockEncrypt);
            enc.setAccessible(false);
            EasyMock.expect(mockEncrypt.encrypt("password")).andThrow(new IllegalBlockSizeException(IMessagesConstants.ERR_DS_SECURITY_ERROR));
            EasyMock.replay(mockEncrypt);

            encryptionDecryption.encryptPrd(pswd);
            fail("Not expected to come here.");
        }
        catch (DataStudioSecurityException e)
        {
        }
        catch (NoClassDefFoundError e)
        {
        }
        catch (Exception e)
        {
            fail("Not expected to come here.");
            e.printStackTrace();
        }

    }
}
