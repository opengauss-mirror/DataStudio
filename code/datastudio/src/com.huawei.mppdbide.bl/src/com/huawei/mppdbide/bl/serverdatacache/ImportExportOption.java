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

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.ArrayList;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportOption.
 * 
 */

public class ImportExportOption {

    private String fileFormat;
    private boolean header;
    private String quotes;
    private String escape;
    private String replaceNull;
    private String encoding;
    private String delimiter;
    private boolean zip;
    private boolean allColunms;
    private ArrayList<String> tablecolumns;
    private String cononicalPath;
    private boolean isExport;
    private String dateSelector;

    /**
     * Gets the file format.
     *
     * @return the file format
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the file format.
     *
     * @param fileFormat the new file format
     */
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * Checks if is header.
     *
     * @return true, if is header
     */
    public boolean isHeader() {
        return header;
    }

    /**
     * Sets the header.
     *
     * @param header the new header
     */
    public void setHeader(boolean header) {
        this.header = header;
    }

    /**
     * Gets the quotes.
     *
     * @return the quotes
     */
    public String getQuotes() {
        return quotes;
    }

    /**
     * Sets the quotes.
     *
     * @param quotes the new quotes
     */
    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }

    /**
     * Gets the escape.
     *
     * @return the escape
     */
    public String getEscape() {
        return escape;
    }

    /**
     * Sets the escape.
     *
     * @param escape the new escape
     */
    public void setEscape(String escape) {
        this.escape = escape;
    }

    /**
     * Gets the replace null.
     *
     * @return the replace null
     */
    public String getReplaceNull() {
        return replaceNull;
    }

    /**
     * Sets the replace null.
     *
     * @param replaceNull the new replace null
     */
    public void setReplaceNull(String replaceNull) {
        this.replaceNull = replaceNull;
    }

    /**
     * Gets the encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding.
     *
     * @param encoding the new encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Gets the delimiter.
     *
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the delimiter.
     *
     * @param delimiter the new delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Checks if is all colunms.
     *
     * @return true, if is all colunms
     */
    public boolean isAllColunms() {
        return allColunms;
    }

    /**
     * Sets the all colunms.
     *
     * @param allColunms the new all colunms
     */
    public void setAllColunms(boolean allColunms) {
        this.allColunms = allColunms;
    }

    /**
     * Gets the tablecolumns.
     *
     * @return the tablecolumns
     */
    public ArrayList<String> getTablecolumns() {
        return tablecolumns;
    }

    /**
     * Sets the tablecolumns.
     *
     * @param selectedColsList the new tablecolumns
     */
    public void setTablecolumns(ArrayList<String> selectedColsList) {
        this.tablecolumns = selectedColsList;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return cononicalPath;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.cononicalPath = fileName;
    }

    /**
     * Checks if is export.
     *
     * @return true, if is export
     */
    public boolean isExport() {
        return isExport;
    }

    /**
     * Sets the export.
     *
     * @param isExprt the new export
     */
    public void setExport(boolean isExprt) {
        this.isExport = isExprt;
    }

    /**
     * Gets the date selector.
     *
     * @return the date selector
     * @Title: getDateSelector
     * @Description: Get table import excel date format
     */
    public String getDateSelector() {
        return dateSelector;
    }

    /**
     * Sets the date selector.
     *
     * @param dateSelector the new date selector
     * @Title: setDateSelector
     * @Description: Set table import excel date format
     */
    public void setDateSelector(String dateSelector) {
        this.dateSelector = dateSelector;
    }

    /**
     * Export if compress.
     *
     * @return true ,if is compress
     */
    public boolean getZip() {
        return zip;
    }

    /**
     * Sets the zip.
     *
     * @param zip the new zip
     */
    public void setZip(boolean zip) {
        this.zip = zip;
    }

}
