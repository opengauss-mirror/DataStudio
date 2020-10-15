package com.huawei.mppdbide.mock.bl;

import com.huawei.mppdbide.utils.security.AESAlgorithmUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;

public class AesAlgorithmUtilHelper extends AESAlgorithmUtility
{

    private boolean throwDBOperationException;
    private boolean throwInvalidKeySpecException;
    
    public AesAlgorithmUtilHelper(SecureUtil encryptionDecryption)
    {
        super(encryptionDecryption);
    }
    
    public void setThrowDBOperationException(boolean throwDBOperationException)
    {
        this.throwDBOperationException = throwDBOperationException;
    }
    
    public void setThrowInvalidKeySpecException(
            boolean throwInvalidKeySpecException)
    {
        this.throwInvalidKeySpecException = throwInvalidKeySpecException;
    }
   
}