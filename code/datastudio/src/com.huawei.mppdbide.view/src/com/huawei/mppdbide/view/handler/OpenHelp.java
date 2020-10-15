/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.program.Program;

import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class OpenHelp.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class OpenHelp {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        final String helpDocName = MessageConfigLoader.getProperty(IMessagesConstants.USER_GUIDE_NAME);

        String os = System.getProperty("os.name");

        if (null != os) {
            if (os.contains("Windows") || os.contains("windows") || os.contains("Linux")) {
                // Already opened UM file can be get from windows task by
                // its name and closed so that no duplicate files are opened.

                URL url = OpenHelp.class.getProtectionDomain().getCodeSource().getLocation();
                String filePath = null;
                // Code used to get the file from the release package

                StringBuilder builder = new StringBuilder(url.getFile());
                builder.deleteCharAt(0);
                // we are using forward slash since the URL for document will
                // always contain forward slash("/") irrespective of the
                // platform
                builder.delete(builder.lastIndexOf("/"), builder.length());
                builder.delete(builder.lastIndexOf("/"), builder.length());
                builder.append(EnvirnmentVariableValidator.validateAndGetFileSeperator()).append(helpDocName);
                filePath = builder.toString();
                File file = null;
                try {
                    // CanonicalFile will change the seperator as per the OS
                    file = new File(filePath).getCanonicalFile();
                } catch (IOException e) {
                    docError();
                }
                if (file != null && file.exists()) {
                    Program.launch(filePath);
                } else {
                    docError();
                }
            }
        }
    }

    /**
     * Doc error.
     *
     * @return the int
     */
    private int docError() {
        return MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.HLP_DOC_NOT_EXIST),
                MessageConfigLoader.getProperty(IMessagesConstants.UNABLE_TO_FIND_HLP_DOC));
    }
}
