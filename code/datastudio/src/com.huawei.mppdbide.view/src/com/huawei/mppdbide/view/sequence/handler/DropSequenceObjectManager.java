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
 * @since 3.0.0
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
