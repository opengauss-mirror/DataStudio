/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ICursorExecuteRecordVisitor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface ICursorExecuteRecordVisitor {

    /** 
     * visits the Record
     * 
     * @param rs the rs
     * @param isFirstBatch the isFirstBatch
     * @param isFirstBatchFirstRecord the isFirstBatchFirstRecord
     * @param isFuncProcExport the isFuncProcExport
     * @return the effected rows
     * @throws ParseException the ParseException
     * @throws MPPDBIDEException the MPPDBIDEException
     */
    long visitRecord(ResultSet rs, boolean isFirstBatch, boolean isFirstBatchFirstRecord, boolean isFuncProcExport)
            throws ParseException, MPPDBIDEException;

    /** 
     * gets the HeaderOfRecord
     * 
     * @param rs the rs
     * @param isFirstBatch the isFirstBatch
     * @param isFuncProcExport the isFuncProcExport
     * @throws ParseException the ParseException
     * @throws MPPDBIDEException the MPPDBIDEException
     */  
    default void getHeaderOfRecord(ResultSet rs, boolean isFirstBatch, boolean isFuncProcExport)
            throws ParseException, MPPDBIDEException {
        return;
    }

    /** 
     * visits the Record
     * 
     * @param rs the rs
     * @param isFirstBatch the isFirstBatch
     * @param isFirstBatchFirstRecord the isFirstBatchFirstRecord
     * @param inputDailogValueList the inputDailogValueList
     * @param outResultList the outResultList
     * @param isCursorResultSet the isCursorResultSet
     * @param isFuncProcExport the isFuncProcExport
     * @return the number of visited records
     * @throws ParseException the ParseException
     * @throws MPPDBIDEException the MPPDBIDEException
     */
    long visitRecord(ResultSet rs, boolean isFirstBatch, boolean isFirstBatchFirstRecord,
            ArrayList<DefaultParameter> inputDailogValueList, ArrayList<Object> outResultList,
            boolean isCursorResultSet, boolean isFuncProcExport) throws ParseException, MPPDBIDEException;

}
