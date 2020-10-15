/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFolderDeleteUtility;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportExcelApachePOI.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ExportExcelApachePOI {
    private static final String POIFILES_NAME = "\\poifiles";
    private static final int MAX_COL_COUNT_XLS = 256;
    private static final int MAX_COL_COUNT_XLSX = 16384;
    private static final int MAX_ROW_COUNT_XLS = 64000;
    private static final int MAX_ROW_COUNT_XLSX = 1000000;
    private static final String EXCEL_XLSX = "Excel(xlsx)";
    private static final String EXCEL_XLS = "Excel(xls)";
    private String fileFormat;
    private SXSSFWorkbook xssfWorkBook;
    private HSSFWorkbook hssfWorkBook;
    private SXSSFSheet xssfsheet;
    private HSSFSheet hssfSheet;
    private FileOutputStream fileOutStream;
    private HSSFCellStyle hssfDateCellStyl;
    private XSSFCellStyle xssfDateCellStyl;

    /**
     * Instantiates a new export excel apache POI.
     *
     * @param fileFormat the file format
     * @param columnDatatype the column datatype
     */
    public ExportExcelApachePOI(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * Sets the cell value.
     *
     * @param oneRow the one row
     * @param rowNo the row no
     * @throws ParseException the parse exception
     */
    public void setCellValue(List<String> oneRow, int rowNo) throws ParseException {

        if (EXCEL_XLSX.equals(fileFormat)) {
            SXSSFRow row = xssfsheet.createRow(rowNo);
            for (int i = 0; i < oneRow.size(); i++) {
                String cellValue = oneRow.get(i);
                SXSSFCell cell = (SXSSFCell) row.createCell(i);
                if (cellValue == null || "".equals(cellValue)) {
                    cell.setCellValue("");
                } else {
                    String truncatedCellValue = truncateCellSize(cellValue);
                    /*
                     * Set all the values of the cell as String to avoid
                     * executing
                     */

                    DataFormat fmt = xssfWorkBook.createDataFormat();
                    CellStyle cellStyle = xssfWorkBook.createCellStyle();
                    cellStyle.setDataFormat(fmt.getFormat("@"));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(truncatedCellValue);
                }
            }
        }
        if (EXCEL_XLS.equals(fileFormat)) {
            HSSFRow row = hssfSheet.createRow(rowNo);
            for (int i = 0; i < oneRow.size(); i++) {
                String cellValue = oneRow.get(i);
                HSSFCell cell = row.createCell(i);
                if (cellValue == null || "".equals(cellValue)) {
                    cell.setCellValue("");
                } else {
                    cellValue = truncateCellSize(cellValue);
                    /*
                     * Set all the values of the cell as String to avoid
                     * executing
                     */

                    DataFormat fmt = hssfWorkBook.createDataFormat();
                    CellStyle cellStyle = hssfWorkBook.createCellStyle();
                    cellStyle.setDataFormat(fmt.getFormat("@"));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(cellValue);
                }
            }
        }
    }

    /**
     * Truncate cell size.
     *
     * @param cellValue - CellValue to set in the workbook. If number of
     * characters in the cell is greater than 32767 then it would truncate the
     * cell characters to 32767
     * @return the string
     */
    private String truncateCellSize(String cellValue) {
        String truncatedCellValue = cellValue;
        if (cellValue.length() > 32767) {
            truncatedCellValue = cellValue.substring(0, 32767 - 3) + "...";
        }
        return truncatedCellValue;
    }

    /**
     * Creates the sheet.
     *
     * @param sheetName the sheet name
     * @throws DatabaseOperationException the database operation exception
     */
    public void createSheet(String sheetName) throws DatabaseOperationException {
        try {
            if ("Excel(xlsx)".equals(fileFormat)) {
                xssfWorkBook = new SXSSFWorkbook();
                POIXMLProperties properties = xssfWorkBook.getXSSFWorkbook().getProperties();
                properties.getCoreProperties().setCreator("Data Studio");
                xssfsheet = xssfWorkBook.createSheet(sheetName);
                createXSSFCellStyleFormat();
            } else if ("Excel(xls)".equals(fileFormat)) {
                hssfWorkBook = new HSSFWorkbook();
                hssfWorkBook.createInformationProperties();
                SummaryInformation summaryInfo = hssfWorkBook.getSummaryInformation();
                if (null != summaryInfo) {
                    summaryInfo.setAuthor("Data Studio");
                }
                hssfSheet = (HSSFSheet) hssfWorkBook.createSheet(sheetName);
                createHSSFCellStyleFormat();
            }
        } catch (Exception exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE), exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE);
        }
    }

    /**
     * Cell Style and Format will be created globally once for date format so
     * that it can be used for the cells with date format
     */
    private void createHSSFCellStyleFormat() {
        hssfDateCellStyl = (HSSFCellStyle) hssfWorkBook.createCellStyle();
        HSSFDataFormat format = (HSSFDataFormat) hssfWorkBook.createDataFormat();
        short dateFormatCode = format.getFormat("yyyy-MM-dd hh:mm:ss");
        hssfDateCellStyl.setDataFormat(dateFormatCode);
    }

    private void createXSSFCellStyleFormat() {
        xssfDateCellStyl = (XSSFCellStyle) xssfWorkBook.createCellStyle();
        XSSFDataFormat xssfDataFormat = (XSSFDataFormat) xssfWorkBook.createDataFormat();
        int dateFormatCode = xssfDataFormat.getFormat("yyyy-MM-dd hh:mm:ss");
        xssfDateCellStyl.setDataFormat(dateFormatCode);
    }

    /**
     * Write to workbook.
     *
     * @param fileName the file name
     * @param encoding the encoding
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void writeToWorkbook(String fileName, String encoding) throws MPPDBIDEException {
        try {
            ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();
            Path path = withPermission.createFileWithPermission(fileName, false, null, false);
            fileOutStream = new FileOutputStream(path.toString(), true);
            if ("Excel(xlsx)".equals(fileFormat)) {
                xssfWorkBook.write(fileOutStream);
            } else if ("Excel(xls)".equals(fileFormat)) {
                hssfWorkBook.write(fileOutStream);
            }
            fileOutStream.flush();

        } catch (FileNotFoundException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE), exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE, exe);
        } catch (IOException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT),
                    exe);
            String msg = (exe.getMessage().contains(MPPDBIDEConstants.DISK_FULL_ERR_MSG))
                    ? IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE
                    : IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT;
            throw new DatabaseOperationException(msg, exe);
        } catch (Exception exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.FAILED_TO_WRITE_TO_EXCEL);
        } finally {
            if (fileOutStream != null) {
                try {
                    fileOutStream.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("Error while closing file output stream.", exception);
                }
            }
        }
    }

    /**
     * Creates the header row.
     *
     * @param headerList the header list
     */
    public void createHeaderRow(List<String> headerList) {
        if ("Excel(xlsx)".equals(fileFormat)) {
            SXSSFRow row = xssfsheet.createRow(0);

            for (int i = 0; i < headerList.size(); i++) {
                SXSSFCell cell = row.createCell(i);
                cell.setCellValue(headerList.get(i));
            }

        } else if ("Excel(xls)".equals(fileFormat)) {
            HSSFRow row = hssfSheet.createRow(0);

            for (int i = 0; i < headerList.size(); i++) {
                HSSFCell cell = (HSSFCell) row.createCell(i);
                cell.setCellValue(headerList.get(i));
            }

        }
    }

    /**
     * Check row length.
     *
     * @param rowCount the row count
     * @return true, if successful
     */

    public boolean checkRowLength(int rowCount) {
        if ("Excel(xlsx)".equals(fileFormat)) {
            if (rowCount > MAX_ROW_COUNT_XLSX) {
                return false;
            }
        } else if ("Excel(xls)".equals(fileFormat)) {
            if (rowCount > MAX_ROW_COUNT_XLS) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check col length.
     *
     * @param colCount the col count
     * @return true, if successful
     */
    public boolean checkColLength(int colCount) {
        if ("Excel(xlsx)".equals(fileFormat)) {
            if (colCount > MAX_COL_COUNT_XLSX) {
                return false;
            }
        } else if ("Excel(xls)".equals(fileFormat)) {
            if (colCount > MAX_COL_COUNT_XLS) {
                return false;
            }
        }
        return true;
    }

    private void handleWorkbookCleanupActivity(String fileName) throws DatabaseOperationException {
        if ("Excel(xlsx)".equals(fileFormat)) {
            if (xssfWorkBook != null) {
                try {
                    xssfWorkBook.close();
                } catch (IOException exp) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT), exp);
                    throw new DatabaseOperationException(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT, exp);
                }
                xssfWorkBook.dispose();
                Path path = Paths.get(fileName);
                Path parent = path.getParent();
                Path newpath;
                if (parent != null) {
                    newpath = Paths.get(parent.toString() + POIFILES_NAME);
                } else {
                    newpath = Paths.get(path.toString() + POIFILES_NAME);
                }
                try {
                    Files.walkFileTree(newpath, new DSFolderDeleteUtility());
                } catch (IOException exception) {

                    if (exception.getMessage().contains(
                            "The process cannot access the file because it is being used by another process.")) {
                        MPPDBIDELoggerUtility.error(
                                "The process cannot access the file because it is being used by another process",
                                exception);
                        return;
                    }
                    throw new DatabaseOperationException(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT, exception);
                }
            }
        }
    }

    /**
     * Clean up workbook POI files.
     *
     * @param fileName the file name
     * @throws DatabaseOperationException the database operation exception
     */
    public void cleanUpWorkbookPOIFiles(String fileName) throws DatabaseOperationException {
        handleWorkbookCleanupActivity(fileName);
    }

}
