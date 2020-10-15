/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: ExportOption
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author xWX634836
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since Jun 14, 2019
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
     * @Author: xWX634836
     * @Date: Jul 4, 2019
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
     * @Author: lijialiang(l00448174)
     * @Date: Jul 31, 2019
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
     * @Author: lijialiang(l00448174)
     * @Date: Jul 31, 2019
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
