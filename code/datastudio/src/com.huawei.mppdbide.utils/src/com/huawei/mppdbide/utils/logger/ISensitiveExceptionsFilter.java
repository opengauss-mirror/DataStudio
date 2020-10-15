/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.logger;

import java.io.FileNotFoundException;
import java.net.BindException;
import java.security.acl.NotOwnerException;
import java.util.ConcurrentModificationException;
import java.util.MissingResourceException;
import java.util.jar.JarException;

import javax.naming.InsufficientResourcesException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISensitiveExceptionsFilter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author g00408002
 * @version [DataStudio 8.0.1, 17 Dec, 2019]
 * @since 17 Dec, 2019
 */
public interface ISensitiveExceptionsFilter {
    /*
     * SQLException is not considered as sensitive exception because DS is a
     * database client tool and, masking SQL exception would make the logs
     * useless for debugging
     */
    /**
     * Checks if exception/error is sensitive type, according to Huawei Java
     * security Guidelines V 3.0
     *
     * @param throwable the throwable
     * @return is sensitive exception or not
     */
    static boolean isSensitiveException(Throwable throwable) {
        if (throwable instanceof FileNotFoundException || throwable instanceof JarException
                || throwable instanceof MissingResourceException || throwable instanceof NotOwnerException
                || throwable instanceof ConcurrentModificationException
                || throwable instanceof InsufficientResourcesException || throwable instanceof BindException
                || throwable instanceof OutOfMemoryError || throwable instanceof StackOverflowError) {
            return true;
        }
        return false;
    }
}