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

package com.huawei.mppdbide.view.handler.edittabledata;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.IEditTableDataCore;
import com.huawei.mppdbide.view.core.edittabledata.EditTableDataResultDisplayUIManager;
import com.huawei.mppdbide.view.terminal.EditTableDataWorker;
import com.huawei.mppdbide.view.terminal.executioncontext.EditTableDataExecutionContext;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableData.
 *
 * @since 3.0.0
 */
public class EditTableData {

    /**
     * Instantiates a new edits the table data.
     */
    public EditTableData() {

    }

    /**
     * Excute edit.
     *
     * @param table the table
     * @param core the core
     */
    public void excuteEdit(ServerObject table, IEditTableDataCore core) {
        EditTableDataResultDisplayUIManager uiManager = new EditTableDataResultDisplayUIManager(core);
        EditTableDataExecutionContext context = new EditTableDataExecutionContext(core, uiManager, table);
        EditTableDataWorker worker = new EditTableDataWorker(context);
        worker.setTaskDB(context.getTermConnection().getDatabase());
        worker.schedule();

    }
}
