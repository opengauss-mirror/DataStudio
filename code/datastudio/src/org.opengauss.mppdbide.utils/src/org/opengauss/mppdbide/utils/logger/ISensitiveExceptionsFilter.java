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

package org.opengauss.mppdbide.utils.logger;

import java.io.FileNotFoundException;
import java.net.BindException;
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
 * @since 3.0.0
 */
public interface ISensitiveExceptionsFilter {
    /*
     * SQLException is not considered as sensitive exception because DS is a
     * database client tool and, masking SQL exception would make the logs
     * useless for debugging
     */
    /**
     * isSensitiveException
     *
     * @param throwable the throwable
     * @return is sensitive exception or not
     */
    static boolean isSensitiveException(Throwable throwable) {
        if (throwable instanceof FileNotFoundException || throwable instanceof JarException
                || throwable instanceof MissingResourceException
                || throwable instanceof ConcurrentModificationException
                || throwable instanceof InsufficientResourcesException || throwable instanceof BindException
                || throwable instanceof OutOfMemoryError || throwable instanceof StackOverflowError) {
            return true;
        }
        return false;
    }
}