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
 * @since 3.0.0
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
