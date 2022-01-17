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

package com.huawei.mppdbide.view.prefernces;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: FormatterContent
 * 
 * Description: The Class FormatterContent.
 *
 * @since 3.0.0
 */
public class FormatterContent {
    /**
     * The instance.
     */
    private static volatile FormatterContent instance = null;

    /**
     * The general.
     */
    @Expose
    private String general;

    /**
     * Gets the general.
     *
     * @return the general
     */
    public String getGeneral() {
        return general;
    }

    /**
     * Sets the general.
     *
     * @param general the new general
     */
    public void setGeneral(String general) {
        this.general = general;
    }

    /**
     * Gets the single instance of BeautifierRules.
     *
     * @return single instance of BeautifierRules
     */
    public static FormatterContent getInstance() {
        if (null == instance) {
            instance = new FormatterContent();
        }
        return instance;
    }

    /**
     * Sets the instance.
     *
     * @param instance the new instance
     */
    public static void setInstance(FormatterContent instance) {
        FormatterContent.instance = instance;
    }

    /**
     * Load content.
     *
     * @param filePath the file path
     */
    public void loadContent(URL filePath) {
        FormatterContent obj = null;
        InputStream stream = null;
        try {
            if (filePath != null) {
                stream = filePath.openStream();
            } else {
                generateUnableToLoadContentDialog();
                return;
            }
            if (stream != null) {
                byte[] formatterTemplatesBytes = IOUtils.toByteArray(stream);
                String json = new String(formatterTemplatesBytes, StandardCharsets.UTF_8);
                Gson gson = new Gson();
                try {
                    obj = gson.fromJson(json, this.getClass());
                } catch (JsonSyntaxException excep) {
                    generateUnableToLoadContentDialog();
                    MPPDBIDELoggerUtility.error("[Invalid json] Failed to parse json.");
                }
            }
        } catch (IOException execp) {
            generateUnableToLoadContentDialog();
            MPPDBIDELoggerUtility.error("FormatterContent: Ioexception occurred.");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    MPPDBIDELoggerUtility.error("FormatterContent: Ioexception occurred.");
                }
            }
        }
        if (obj != null) {
            setGeneral(obj.getGeneral());
        }
    }

    /**
     * Generate unable to load content dialog.
     */
    private void generateUnableToLoadContentDialog() {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_TEMPLATE_LOAD_FAILURE_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION));
    }
}
