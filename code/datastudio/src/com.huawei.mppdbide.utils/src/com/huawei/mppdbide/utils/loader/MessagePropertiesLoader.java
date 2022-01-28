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

package com.huawei.mppdbide.utils.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loggerutil.LoggerUtils;

/**
 * 
 * Title: class
 * 
 * Description: The Class MessagePropertiesLoader.
 *
 * @since 3.0.0
 */
public class MessagePropertiesLoader {

    private Properties dbProperties;

    /**
     * Instantiates a new message properties loader.
     *
     * @param propertiesURL the properties URL
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MessagePropertiesLoader(URL propertiesURL) throws IOException {

        // get the URL of the property file
        InputStream propertiesFileStream = null;
        try {
            propertiesFileStream = propertiesURL.openStream();
            dbProperties = new Properties();
            dbProperties.load(propertiesFileStream);
        }
        // closing the stream in finally block
        finally {
            if (null != propertiesFileStream) {
                try {
                    propertiesFileStream.close();
                } catch (IOException e) {
                    LoggerUtils.error(MessageConfigLoader.getProperty(IMessagesConstants.EXE_BL_CLS));
                }
            }
        }

    }

    /**
     * Gets the property.
     *
     * @param key the key
     * @return the property
     */
    public String getProperty(String key) {
        return dbProperties.getProperty(key, key);
    }

    /**
     * Gets the property.
     *
     * @param key the key
     * @param params the params
     * @return the property
     */
    public String getProperty(String key, Object... params) {
        return MessageFormat.format(dbProperties.getProperty(key, key), params);
    }
}
