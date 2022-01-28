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

package com.huawei.mppdbide.bl.export;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IExportManager.
 * 
 */

public interface IExportManager {

    /**
     * Export sql to file.
     *
     * @param path the path
     * @param type the type
     * @param object the object
     * @param isTablespaceOption the is tablespace option
     * @param workingDir the working dir
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    void exportSqlToFile(String path, EXPORTTYPE type, ServerObject object, boolean isTablespaceOption, File workingDir)
            throws DatabaseOperationException, DatabaseCriticalException, DataStudioSecurityException;

    /**
     * Export sql to files.
     *
     * @param path the path
     * @param type the type
     * @param object the object
     * @param isTablespaceOption the is tablespace option
     * @param workingDir the working dir
     * @param encryptedPwd encrypted psw
     * @return the list
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    List<String> exportSqlToFiles(String path, EXPORTTYPE type, ArrayList<ServerObject> object,
            boolean isTablespaceOption, File workingDir, String encryptedPwd)
            throws DatabaseOperationException, DatabaseCriticalException, DataStudioSecurityException;

    /**
     * Export sql to file.
     *
     * @param expParameter the exp parameter
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    void exportSqlToFile(ExportParameters expParameter)
            throws DatabaseOperationException, DatabaseCriticalException, DataStudioSecurityException;

    /**
     * Cancel.
     */
    void cancel();
}
