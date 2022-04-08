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

package org.opengauss.mppdbide.view.sequence.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import org.opengauss.mppdbide.bl.serverdatacache.ISequenceMetaData;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSequenceHandler.
 *
 * @since 3.0.0
 */
public class DropSequenceHandler {

    /**
     * Execute.
     *
     * @param isCascade the is cascade
     */
    @Execute
    public void execute(@Optional @Named("iscascade") String isCascade) {

        ISequenceMetaData obj = (ISequenceMetaData) IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj == null) {
            return;
        }
        DropSequenceObjectManager dropSequenceFactory = new DropSequenceObjectManager();
        dropSequenceFactory.performDropOperation(obj, isCascade);

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();

        if (null != obj && obj instanceof ISequenceMetaData) {

            return true;
        }

        return false;
    }

}
