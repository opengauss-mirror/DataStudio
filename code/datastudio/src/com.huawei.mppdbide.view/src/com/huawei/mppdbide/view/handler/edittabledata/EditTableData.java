/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since 03 September, 2019
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
