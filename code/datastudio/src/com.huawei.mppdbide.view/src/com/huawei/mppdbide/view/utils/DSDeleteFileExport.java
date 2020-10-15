/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
