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

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.search.SearchWindow;
import org.opengauss.mppdbide.view.sequence.ISequenceInitializer;
import org.opengauss.mppdbide.view.sequence.factory.SequenceFactory;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateSequenceHandler.
 *
 * @since 3.0.0
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
