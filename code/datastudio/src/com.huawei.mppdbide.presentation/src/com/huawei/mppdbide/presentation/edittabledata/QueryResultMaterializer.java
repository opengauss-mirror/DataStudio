/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

import java.sql.SQLException;
import java.util.ArrayList;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IQueryMaterializer;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.QueryResultType;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class QueryResultMaterializer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class QueryResultMaterializer implements IQueryMaterializer {
    private IResultConfig iResultConfig;
    private IQueryExecutionSummary qes;
    private IConsoleResult consoleWrapper;
    private IExecutionContext context;
    private boolean isEditSupported;
    private boolean isQueryResultEditSupported;
    private Object materializedQueryResult = null;

    private static ArrayList<DefaultParameter> inputDailogValueList;

    /**
     * Instantiates a new query result materializer.
     *
     * @param iResultConfig the i result config
     * @param qes the qes
     * @param consoleWrapper the console wrapper
     * @param context the context
     * @param isEditSupported the is edit supported
     * @param isQueryResultEditSupported the is query result edit supported
     */
    public QueryResultMaterializer(IResultConfig iResultConfig, IQueryExecutionSummary qes,
            IConsoleResult consoleWrapper, IExecutionContext context, boolean isEditSupported,
            boolean isQueryResultEditSupported) {
        this.iResultConfig = iResultConfig;
        this.qes = qes;
        this.consoleWrapper = consoleWrapper;
        this.context = context;
        this.isEditSupported = isEditSupported;
        this.isQueryResultEditSupported = isQueryResultEditSupported;
    }

    /**
     * sets the input dailog value list
     * 
     * @param inDailogValueList the inDailogValueList
     */
    public static void setInputDailogValueList(ArrayList<DefaultParameter> inDailogValueList) {
        inputDailogValueList = inDailogValueList;
    }

    /**
     * gets the input dailog value list
     * 
     * @return inputDailogValueList the inputDailogValueList
     */
    public static ArrayList<DefaultParameter> getInputDailogValueList() {
        return inputDailogValueList;
    }

    /**
     * Materialize query result.
     *
     * @param irq the irq
     * @param iResultConfig the i result config
     * @param qes the qes
     * @param consoleWrapper the console wrapper
     * @param context the context
     * @param isEditSupported the is edit supported
     * @param isQueryResultEditSupported the is query result edit supported
     * @return the object
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException
     */
    public static Object materializeQueryResult(IQueryResult irq, IResultConfig iResultConfig,
            IQueryExecutionSummary qes, IConsoleResult consoleWrapper, IExecutionContext context,
            boolean isEditSupported, boolean isQueryResultEditSupported,
            ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        setInputDailogValueList(debugInputValueList);
        if (!isEditSupported && !isQueryResultEditSupported
                && QueryResultType.RESULTTYPE_RESULTSET == irq.getReturnType()) {
            DSResultSetGridDataProvider rsdw = new DSResultSetGridDataProvider(irq, iResultConfig, qes);
            rsdw.setIncludeEncoding(BLPreferenceManager.getInstance().getBLPreference().isIncludeEncoding());
            rsdw.init(irq, debugInputValueList, isCallableStmt);

            return rsdw;
        } else if (isEditSupported || isQueryResultEditSupported) {
            DSEditTableDataGridDataProvider editTableDataProvider;
            editTableDataProvider = new DSEditTableDataGridDataProvider(irq, iResultConfig, qes, context,
                    isQueryResultEditSupported);
            editTableDataProvider
                    .setIncludeEncoding(BLPreferenceManager.getInstance().getBLPreference().isIncludeEncoding());
            editTableDataProvider.init();
            editTableDataProvider.setDatabase(context.getTermConnection().getDatabase());
            return editTableDataProvider;
        }

        else {
            qes.setNumRecordsFetched(irq.getRowsAffected());
            return consoleWrapper;
        }

    }

    /**
     * Materialize query result.
     *
     * @param irq the irq
     * @return the object
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException
     */
    @Override
    public Object materializeQueryResult(IQueryResult irq, boolean isCallableStmt)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {

        ArrayList<DefaultParameter> inputDailogValueListLocal = this.context.getInputValues();
        materializedQueryResult = materializeQueryResult(irq, iResultConfig, qes, consoleWrapper, context,
                isEditSupported, isQueryResultEditSupported, inputDailogValueListLocal, isCallableStmt);
        return materializedQueryResult;
    }

    /**
     * Gets the materialized query result.
     *
     * @return the materialized query result
     */
    public Object getMaterializedQueryResult() {
        return materializedQueryResult;
    }

    /**
     * Materialize query result.
     *
     * @param irq the irq
     * @return the object
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    @Override
    public Object materializeQueryResult(IQueryResult irq)
            throws DatabaseCriticalException, DatabaseOperationException {
        return null;
    }
}
