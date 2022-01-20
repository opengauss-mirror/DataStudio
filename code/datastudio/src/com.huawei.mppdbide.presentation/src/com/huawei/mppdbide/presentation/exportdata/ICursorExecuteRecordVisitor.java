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
 * @since 3.0.0
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
