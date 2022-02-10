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

package org.opengauss.mppdbide.view.handler;

import java.io.File;
import java.nio.file.Files;

import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.view.handler.connection.ClientSSLKeyDialog;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class GsDumpViewLayerHelper.
 *
 * @since 3.0.0
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
