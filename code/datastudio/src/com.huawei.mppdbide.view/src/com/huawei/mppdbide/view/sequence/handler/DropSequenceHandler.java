/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import com.huawei.mppdbide.bl.serverdatacache.ISequenceMetaData;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSequenceHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
