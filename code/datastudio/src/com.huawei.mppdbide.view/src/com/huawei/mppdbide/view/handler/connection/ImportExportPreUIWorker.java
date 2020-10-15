/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.connection;

import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.ImportExportFileCounter;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: ImportExportPreUIWorker
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-Apr-2020]
 * @since 21-Apr-2020
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
