/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
