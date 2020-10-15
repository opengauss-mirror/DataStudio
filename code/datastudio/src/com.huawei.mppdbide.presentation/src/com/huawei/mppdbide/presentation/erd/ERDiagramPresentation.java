/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * @Description: The Class ERDiagramPresentation. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 * @author: f00512995
 * @version:
 * @since: Oct 15, 2019
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
