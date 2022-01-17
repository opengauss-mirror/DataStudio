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

package com.huawei.mppdbide.view.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSDeleteFileExport.
 *
 * @since 3.0.0
 */
public class DSDeleteFileExport {

    /**
     * Delete file.
     *
     * @param path the path
     * @param msgKey1 the msg key 1
     * @param msgKey2 the msg key 2
     * @param fileName the file name
     */
    public boolean deleteFile(Path path, String msgKey1, String msgKey2, String fileName) {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            String msg = "Deleting file failed.";
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                    MessageConfigLoader.getProperty(msgKey1, fileName) + msg);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(msgKey2, msg), exception);
            return false;
        }
        return true;
    }
}
