/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.sequence.ISequenceInitializer;
import com.huawei.mppdbide.view.sequence.factory.SequenceFactory;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateSequenceHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateSequenceHandler {
    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        SequenceFactory sequenceFactory = new SequenceFactory();
        ISequenceInitializer initializerObj = null;
        if (obj != null) {
            initializerObj = sequenceFactory.getSequenceTypeObject(obj, shell);
        }
        if (initializerObj == null) {
            return;
        }
        initializerObj.initConfiguration();
        initializerObj.openDilog();

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object partObject = UIElement.getInstance().getActivePartObject();
        if (null != partObject && partObject instanceof SearchWindow) {
            return false;
        }
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null != obj) {
            if (obj instanceof SequenceObjectGroup) {
                return true;
            }
        }

        return false;
    }
}
