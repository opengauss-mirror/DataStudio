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

package com.huawei.mppdbide.presentation.erd;

import java.sql.SQLException;
import java.util.ArrayList;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.erd.model.EREntity;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * The Class ERDiagramPresentation.
 *
 * @ClassName: ERDiagramPresentation
 * @Description: The Class ERDiagramPresentation.
 *
 * @since 3.0.0
 */
public class ERDiagramPresentation extends AbstractERPresentation<TableObjectGroup> {

    public ERDiagramPresentation(TableObjectGroup obj, DBConnection dbcon) {
        super(obj, dbcon);
    }

    @Override
    public void initERPresentation() throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        ArrayList<TableMetaData> tables = serverObject.getSortedServerObjectList();
        for (TableMetaData table : tables) {
            if (!table.isLoaded()) {
                table.setLevel3Loaded(false);
                serverObject.getNamespace().fetchTableColumnMetaData(table.getOid(), serverObject, dbcon);
            }
            EREntity entity = new EREntity(table, false, dbcon);
            entity.initEREntity();
            addEntity(entity);
        }
    }

    @Override
    public String getWindowTitle() {
        Namespace namespace = serverObject.getNamespace();
        return namespace.getName() + "-" + namespace.getDatabaseName() + '@' + namespace.getServerName();
    }
}
