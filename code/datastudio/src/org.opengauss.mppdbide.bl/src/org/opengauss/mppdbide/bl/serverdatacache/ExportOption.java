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

package org.opengauss.mppdbide.bl.serverdatacache;

import org.opengauss.mppdbide.utils.EnvirnmentVariableValidator;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: ExportOption
 * 
 * Description:
 * 
 */

public class ExportOption {
    private String format;
    private boolean zip;
    private String folderName;
    private String fileName;

    /**
     * Instantiates a new export option.
     *
     * @param format the format
     * @param zip the zip
     * @param foldeName the folde name
     * @param fileName the file name
     */
    public ExportOption(String format, boolean zip, String foldeName, String fileName) {
        this.format = format;
        this.zip = zip;
        this.folderName = foldeName;
        this.fileName = fileName;
    }

    /**
     * Gets the format.
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the format.
     *
     * @param format the new format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Checks if is zip.
     *
     * @return true, if is zip
     */
    public boolean isZip() {
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

    /**
     * Gets the folder name.
     *
     * @return the folder name
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Sets the folder name.
     *
     * @param folderName the new folder name
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the file path.
     *
     * @return file path
     * @Title: getFilePath
     * @Description: Assembly file path
     */
    public String getFilePath() {
        return this.folderName + EnvirnmentVariableValidator.validateAndGetFileSeperator() + this.fileName;
    }

    /**
     * Gets the file path with suffix format.
     *
     * @return the file path with suffix format
     * @Title: getFilePathWithSuffixFormat
     * @Description: Assembly file path with format suffix
     */
    public String getFilePathWithSuffixFormat() {
        String filePath = this.folderName + EnvirnmentVariableValidator.validateAndGetFileSeperator() + this.fileName;
        if (zip) {
            if (filePath.endsWith(".zip")) {
                return filePath;
            }
            return filePath + ".zip";
        } else {
            if ((getFileFormatSuffix().equals(".xls") && filePath.endsWith(".xls"))
                    || (getFileFormatSuffix().equals(".xlsx") && filePath.endsWith(".xlsx"))
                    || (getFileFormatSuffix().equals(".txt") && filePath.endsWith(".txt"))) {
                return filePath;
            }
            return filePath + getFileFormatSuffix();
        }
    }

    /**
     * Gets the file format suffix.
     *
     * @return the file format suffix
     * @Title: getFileFormatSuffix
     * @Description: get file format suffix
     */
    public String getFileFormatSuffix() {
        String suffix = "";
        switch (format) {
            case "Excel(xlsx)": {
                suffix = ".xlsx";
                break;
            }
            case "Excel(xls)": {
                suffix = ".xls";
                break;
            }
            case ".sql": {
                suffix = ".sql";
                break;
            }
            case "Text": {
                suffix = ".txt";
                break;
            }
            default: {
                break;
            }
        }
        return suffix;
    }
}
