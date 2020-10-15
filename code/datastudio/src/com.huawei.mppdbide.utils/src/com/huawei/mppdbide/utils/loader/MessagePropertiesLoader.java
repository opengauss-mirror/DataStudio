/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
