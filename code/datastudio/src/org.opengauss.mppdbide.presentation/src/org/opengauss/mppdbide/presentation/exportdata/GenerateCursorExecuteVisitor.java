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

package org.opengauss.mppdbide.presentation.exportdata;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.opengauss.mppdbide.bl.export.GenerateCursorExecuteUtil;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class GenerateCursorExecuteVisitor.
 *
 * @since 3.0.0
 */
public class GenerateCursorExecuteVisitor implements ICursorExecuteRecordVisitor {

    private Path path;
    private String encoding;
    private long totalRows;
    private FileOutputStream fileOutPutStream = null;
    private OutputStreamWriter filewriter;
    private GenerateCursorExecuteUtil cursorExecuteUtil;

    /**
     * Instantiates a new generate cursor execute visitor.
     *
     * @param path the path
     * @param encoding the encoding
     * @param isOLAP the is OLAP
     * @param tableNames the table names
     */
    public GenerateCursorExecuteVisitor(Path path, String encoding, boolean isOLAP, String tableNames) {
        this.path = path;
        this.encoding = encoding;
        cursorExecuteUtil = new GenerateCursorExecuteUtil(tableNames, encoding, isOLAP);
    }

    @Override
    public long visitRecord(ResultSet rs, boolean isFirstBatch, boolean isFirstBatchFirstRecord,
            boolean isFuncProcExport) throws ParseException, MPPDBIDEException {
        try {
            // Only for the firstbatch and firstrecord, exportexcel object will
            // be created and it will be reused further.
            long rowsCount = cursorExecuteUtil.getAllRowsCount(rs, isFirstBatch);
            totalRows += rowsCount;
        } catch (SQLException ex) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET), ex);
            throw new DatabaseOperationException(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET);
        }
        return totalRows;

    }

    /**
     * Gets the column datatype.
     *
     * @param colCount the col count
     * @param rs the rs
     * @return the column datatype
     * @throws SQLException the SQL exception
     */
    public List<Integer> getColumnDatatype(int colCount, ResultSet rs) throws SQLException {
        List<Integer> columnList = new ArrayList<Integer>();
        if (rs.getMetaData() != null) {
            for (int i = 1; i <= colCount; i++) {
                columnList.add(rs.getMetaData().getColumnType(i));

            }
        }
        return columnList;
    }

    /**
     * Write to sql file.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void writeToSqlFile() throws MPPDBIDEException {
        boolean isExceptionThrown = false;
        try {
            fileOutPutStream = new FileOutputStream(path.toFile());
            filewriter = new OutputStreamWriter(fileOutPutStream, encoding);

            filewriter.write(cursorExecuteUtil.getOutPutInsertSql().toString());

        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_GENERATE_INSERT_DIALOG_SAVE_FILE_ERROR),
                    exception);
            isExceptionThrown = true;
            throw new MPPDBIDEException(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_GENERATE_INSERT_DIALOG_SAVE_FILE_ERROR),
                    exception);

        } finally {
            closeStream(isExceptionThrown);
        }
    }

    private void closeStream(Boolean isExceptionThrown) {
        try {
            if (filewriter != null) {
                filewriter.close();
            }
            if (fileOutPutStream != null) {
                fileOutPutStream.close();
            }

        } catch (IOException error) {
            MPPDBIDELoggerUtility.error("Error while generating file in exception.", error);

        }
        if (isExceptionThrown) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("Error while deleting file in exception.", exception);
            }
        }

    }

    /**
     * Clean up file content.
     */
    public void cleanUpFileContent() {
        cursorExecuteUtil.cleanOutputInsertSql();
    }

    /**
     * visit records
     */
    @Override
    public long visitRecord(ResultSet rs, boolean isFirstBatch, boolean isFirstBatchFirstRecord,
            ArrayList<DefaultParameter> inputDailogValueList, ArrayList<Object> outResultList,
            boolean isCursorResultSet, boolean isFuncProcExport) throws ParseException, MPPDBIDEException {
        return 0;
    }

    /**
     * getPath path
     * 
     * @return path sql file
     */
    public Path getPath() {
        return path;
    }
}