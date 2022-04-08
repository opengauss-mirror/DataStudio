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

package org.opengauss.mppdbide.view.handler.edittabledata;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIEditHandler.
 *
 * @since 3.0.0
 */
public class UIEditHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        ServerObject selTable = (ServerObject) IHandlerUtilities.getObjectBrowserSelectedObject();

        EditTableDataFactory.getEditTableDataUIInitializer(selTable);
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return !IHandlerUtilities.isSelectedTableForignPartition();
    }

}
