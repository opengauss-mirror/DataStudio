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

package org.opengauss.mppdbide.view.handler.connection;

import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.ImportExportFileCounter;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: ImportExportPreUIWorker
 *
 * @since 3.0.0
 */
public abstract class ImportExportPreUIWorker extends PromptPasswordUIWorkerJob {
    private Object familyObj;
    private boolean isCancelled = false;

    /**
     * ImportExportPreUIWorker constructor
     */
    public ImportExportPreUIWorker(String name, Object family, String errorWindowTitle) {
        super(name, family, errorWindowTitle);
        this.familyObj = family;
    }

    @Override
    public boolean preUISetup(Object preHandlerObject) {
        if (!ImportExportFileCounter.getInstance().canExportOrImport()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_LIMIT_MSG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_LIMIT_MSG_INFO));
            isCancelled = true;
            return false;
        }
        ImportExportFileCounter.getInstance().registerCounter();
        return callPresetUp();
    }

    /**
     * callPresetUp
     * 
     * @param preHandlerObject handler object
     * @return boolean callPresetUp
     */
    public boolean callPresetUp() {
        return super.preUISetup(familyObj);
    }

    /**
     * finalCleanup
     */
    public void finalCleanup() {
        if (!isCancelled) {
            ImportExportFileCounter.getInstance().deRegisterCounter();
        }
        if (null != getDatabase() && getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
            super.finalCleanup();
        }
    }

}
