/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.IViewTableDataCore;
import com.huawei.mppdbide.presentation.ViewSequenceCore;
import com.huawei.mppdbide.presentation.ViewTableDataCore;
import com.huawei.mppdbide.presentation.ViewTableSequenceDataCore;
import com.huawei.mppdbide.presentation.view.ViewViewDataCore;

/**
 * Title: ViewTableDataFactory
 * 
 * Description:A factory for creating ViewTableData objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-May-2019]
 * @since 21-May-2019
 */

public class ViewTableDataFactory {

    /**
     * Gets the view table data core.
     *
     * @param serverObject the server object
     * @return the view table data core
     */
    public static IViewTableDataCore getViewTableDataCore(ServerObject serverObject, String parameter) {
        switch (serverObject.getType()) {
            case VIEW_META_DATA: {
                return new ViewViewDataCore();
            }
            case TABLEMETADATA:
            default: {
                ViewTableDataCore viewTableDataCore = new ViewTableDataCore();
                viewTableDataCore.setHandlerParameter(parameter);
                return viewTableDataCore;
            }
        }
    }
    
    /**
     * Gets the view table data core.
     *
     * @param serverObject the server object
     * @return the view table data core
     */
    public static IViewTableDataCore getTableSequenceDataCore(ServerObject serverObject, String parameter) {
        switch (serverObject.getType()) {
            case SEQUENCE_METADATA_GROUP: {
                return new ViewSequenceCore();
            }
            case TABLEMETADATA:
            default: {
                ViewTableSequenceDataCore viewTableDataCore = new ViewTableSequenceDataCore();
                viewTableDataCore.setHandlerParameter(parameter);
                return viewTableDataCore;
            }
        }

    }
}
