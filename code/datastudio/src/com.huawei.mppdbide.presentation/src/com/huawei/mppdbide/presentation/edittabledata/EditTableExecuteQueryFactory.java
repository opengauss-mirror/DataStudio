/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;

/**
 * Title: EditTableExecuteQueryFactory
 * 
 * Description:A factory for Edit Table Execute Query.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author gWX773294
 * @version
 * @since 30 August, 2019
 */

public final class EditTableExecuteQueryFactory {

    private EditTableExecuteQueryFactory() {

    }

    /**
     * Gets the edits the table data core.
     *
     * @param serverObject the server object
     * @return the edits the table data core
     */
    public static EditTableExecuteQueryUtility getEditTableExecuteQuery(DBTYPE dbType) {
        switch (dbType) {
            case OPENGAUSS:
            default: {
                return new EditTableExecuteQuery();
            }

        }

    }

}
