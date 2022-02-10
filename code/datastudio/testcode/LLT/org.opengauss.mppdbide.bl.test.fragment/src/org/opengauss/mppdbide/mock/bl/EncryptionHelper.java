package org.opengauss.mppdbide.mock.bl;

import org.opengauss.mppdbide.utils.security.EncryptionUtil;
import org.opengauss.mppdbide.utils.security.SecureUtil;

public class EncryptionHelper extends EncryptionUtil
{

    private boolean throwNoSuchAlogoException;
    private boolean throwInvalidKeyException;
    private boolean throwNoSuchPaddingException;
    private boolean throwInvalidAlgorithmParameterException;
    private boolean throwIllegalBlockSizeException;
    private boolean throwBadPaddingException;
    
    public EncryptionHelper(SecureUtil encryptionDecryption)
    {
        super(encryptionDecryption);
    }
    
    public void setThrowNoSuchAlogoException(boolean throwNoSuchAlogoException)
    {
        this.throwNoSuchAlogoException = throwNoSuchAlogoException;
    }
    
    public void setThrowInvalidKeyException(boolean throwInvalidKeyException)
    {
        this.throwInvalidKeyException = throwInvalidKeyException;
    }
    
    public void setThrowNoSuchPaddingException(
            boolean throwNoSuchPaddingException)
    {
        this.throwNoSuchPaddingException = throwNoSuchPaddingException;
    }
    
    public void setThrowInvalidAlgorithmParameterException(
            boolean throwInvalidAlgorithmParameterException)
    {
        this.throwInvalidAlgorithmParameterException = throwInvalidAlgorithmParameterException;
    }
    
    public void setThrowIllegalBlockSizeException(
            boolean throwIllegalBlockSizeException)
    {
        this.throwIllegalBlockSizeException = throwIllegalBlockSizeException;
    }
    
    public void setThrowBadPaddingException(boolean throwBadPaddingException)
    {
        this.throwBadPaddingException = throwBadPaddingException;
    }
}
