/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.huawei.mppdbide.bl.serverdatacache.ImportExportOption;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ImportExcelApachePOI
 * 
 * Description: The Class ImportExcelApachePOI.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 23-Oct-2019]
 * @since 23-Oct-2019
 */

public class ImportExcelApachePOI {
    private ImportExportOption importExportoptions;
    private HSSFSheet hssfSheet;
    private XSSFSheet xssfSheet;
    private String dateFormat;
    private static final String EXCEL_XLSX = "EXCEL(xlsx)";

    /**
     * Instantiates a new import excel apache POI.
     *
     * @param importExportoptions the import exportoptions
     */
    public ImportExcelApachePOI(ImportExportOption importExportoptions) {
        this.importExportoptions = importExportoptions;
    }

    /**
     * Gets the cell values.
     *
     * @return the cell values
     * @throws DatabaseOperationException the database operation exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @Author: wWX633190
     * @Date: July 1,2019
     * @Title: getCellValues
     * @Description: Get excel cell values
     */
    public List<ArrayList<String>> getCellValues() throws DatabaseOperationException, IOException {
        List<ArrayList<String>> countList;
        countList = new ArrayList<ArrayList<String>>();
        dateFormat = importExportoptions.getDateSelector();
        int firstRow = 0;
        if (importExportoptions.isHeader()) {
            firstRow = 1;
        }

        boolean isXLSX = false;
        if (EXCEL_XLSX.equals(importExportoptions.getFileFormat())) {
            isXLSX = true;
        }
        getExcelCellValues(firstRow, countList, isXLSX);
        return countList;
    }

    private void getExcelCellValues(int firstRow, List<ArrayList<String>> countList, boolean isXLSX)
            throws IOException, DatabaseOperationException {
        Workbook workbook = null;
        FileInputStream excelFile = null;
        try {
            excelFile = new FileInputStream(importExportoptions.getFileName());
            if (isXLSX) {
                workbook = new XSSFWorkbook(excelFile);
                xssfSheet = (XSSFSheet) workbook.getSheetAt(0);
                if (null != xssfSheet && xssfSheet.getLastRowNum() > 0) {
                    for (int i = firstRow; i <= xssfSheet.getLastRowNum(); i++) {
                        XSSFRow xssfRow = xssfSheet.getRow(i);
                        if (xssfRow == null) {
                            continue;
                        }
                        countList.add(getCellList(xssfRow));
                    }
                }
            } else {
                workbook = new HSSFWorkbook(excelFile);
                hssfSheet = (HSSFSheet) workbook.getSheetAt(0);
                if (null != hssfSheet && hssfSheet.getLastRowNum() > 0) {
                    for (int i = firstRow; i <= hssfSheet.getLastRowNum(); i++) {
                        HSSFRow hssfRow = hssfSheet.getRow(i);
                        if (hssfRow == null) {
                            continue;
                        }
                        countList.add(getCellList(hssfRow));
                    }
                }
            }
        } catch (FileNotFoundException exception) {
            MPPDBIDELoggerUtility.error("Import File not found", exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_IMPORT_TABLE_TO_EXCEL);
        } finally {
            try {
                if (excelFile != null) {
                    excelFile.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException ioException) {
                MPPDBIDELoggerUtility.error("Import while get cell values", ioException);
                excelFile = null;
            }
        }
    }

    private ArrayList<String> getCellList(HSSFRow hssfRow) {
        ArrayList<String> cellList = new ArrayList<String>();
        for (int i = 0; i < importExportoptions.getTablecolumns().size(); i++) {
            Cell cellObj = hssfRow.getCell(i);
            cellList.add(getCellValue(cellObj));
        }
        return cellList;
    }

    private ArrayList<String> getCellList(XSSFRow xssfRow) {
        ArrayList<String> cellList = new ArrayList<String>();
        for (int j = 0; j < importExportoptions.getTablecolumns().size(); j++) {
            Cell cellObj = xssfRow.getCell(j);
            cellList.add(getCellValue(cellObj));
        }
        return cellList;
    }

    private String getCellValue(Cell cellObj) {
        String cellValue = null;
        if (cellObj == null) {
            cellValue = "";
        } else if (cellObj.getCellType() == CellType.BOOLEAN) {
            cellValue = String.valueOf(cellObj.getBooleanCellValue());
        } else if (cellObj.getCellType() == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cellObj)) {
                SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                if (cellObj.getDateCellValue() != null) {
                    cellValue = formatter.format(cellObj.getDateCellValue()); 
                }  
            } else {
                cellValue = String.valueOf(cellObj.getNumericCellValue());
            }
        } else {
            cellValue = cellObj.getStringCellValue();
        }
        return cellValue;
    }
}
