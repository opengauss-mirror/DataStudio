/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.io.File;
import java.nio.file.Files;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.view.handler.connection.ClientSSLKeyDialog;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class GsDumpViewLayerHelper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ShowDDLViewLayerHelper {

    private volatile static ClientSSLKeyDialog askUser = null;
    private volatile static int ret = UIConstants.CANCEL_ID;

    /**
     * Gets the client SSL key file for GS dump.
     *
     * @param db the db
     * @return the client SSL key file for GS dump
     */

    public static boolean getClientSSLKeyFile(Database db) {
        boolean proceed = true;
        if (!db.getServer().getServerConnectionInfo().isSSLEnabled()) {
            return proceed;
        }

        String clientKeyFile = db.getServer().getServerConnectionInfo().getClientSSLPrivateKey();
        File file = new File(clientKeyFile);
        if ("".equals(clientKeyFile) || !Files.isReadable(file.toPath())) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    askUser = new ClientSSLKeyDialog(Display.getDefault().getActiveShell(), db);
                    ret = askUser.open();
                }
            });

            if (ret == UIConstants.OK_ID) {
                db.getServer().getServerConnectionInfo().setClientSSLPrivateKey(askUser.getKeyFileName());
            } else {
                proceed = false;
            }
        }

        return proceed;
    }
}
