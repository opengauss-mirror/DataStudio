/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence.handler;

import com.huawei.mppdbide.bl.serverdatacache.ISequenceMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.sequence.IDropSequenceObjInit;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSequenceObjectManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DropSequenceObjectManager {

    /**
     * Perform drop operation.
     *
     * @param obj the obj
     * @param isCascade the is cascade
     * @return the i drop sequence obj init
     */
    public IDropSequenceObjInit performDropOperation(ISequenceMetaData obj, String isCascade) {
        boolean isAppendCascade = "true".equals(isCascade);
        String name = obj.getSeqNameSpace().getQualifiedObjectName() + '.' + obj.getQualifiedObjectName();
        String title = MessageConfigLoader.getProperty(IMessagesConstants.DROP_SEQUENCE_CONFIRM_TITLE);
        String msg = "";

        if (isAppendCascade) {
            msg = MessageConfigLoader.getProperty(IMessagesConstants.DROP_SEQUENCE_CASCADE_CONFIRM_MSG, name);
        } else {
            msg = MessageConfigLoader.getProperty(IMessagesConstants.DROP_SEQUENCE_CONFIRM_MSG, name);
        }
        int userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                IconUtility.getIconImage(IiconPath.SEQUENCE_OBJECT, this.getClass()), title, msg,
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION),
                    MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION)},
                0);

        if (UIConstants.OK_ID == userChoice) {
            DropSequenceWorkerJob dropSequenceWorkerJob = new DropSequenceWorkerJob(
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_SEQUENCE_CONFIRM_TITLE), (ServerObject) obj,
                    isAppendCascade);
            dropSequenceWorkerJob.schedule();
        }

        return null;
    }

}
