/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.mock.presentation;

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
