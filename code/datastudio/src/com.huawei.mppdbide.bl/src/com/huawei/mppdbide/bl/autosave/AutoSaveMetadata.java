/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.autosave;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class AutoSaveMetadata.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class AutoSaveMetadata {

    @SerializedName("database")
    private String databaseName;

    @SerializedName("connection")
    private String connectionName;

    @SerializedName("terminaltype")
    private String type;

    @SerializedName("encrypted")
    private boolean isEncrypted;

    @SerializedName("encoding")
    private String encoding;

    @SerializedName("filename")
    private String fileName;

    @SerializedName("id")
    private String tabID;

    @SerializedName("label")
    private String tabLabel;

    @SerializedName("tooltip")
    private String tabToolTip;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("debugobject")
    private AutoSaveDbgObjInfo dbgObjInfo;

    private int versionNumber;

    @SerializedName("Verifier")
    private byte[] shaval;

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets the database name.
     *
     * @param databaseName the new database name
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Gets the connection name.
     *
     * @return the connection name
     */
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * Sets the connection name.
     *
     * @param connectionName the new connection name
     */
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Checks if is encrypted.
     *
     * @return true, if is encrypted
     */
    public boolean isEncrypted() {
        return isEncrypted;
    }

    /**
     * Sets the encrypted.
     *
     * @param isEncrypted1 the new encrypted
     */
    public void setEncrypted(boolean isEncrypted1) {
        this.isEncrypted = isEncrypted1;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getAutoSaveFileName() {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setAutoSaveFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the tab ID.
     *
     * @return the tab ID
     */
    public String getTabID() {
        return tabID;
    }

    /**
     * Sets the tab ID.
     *
     * @param tabID the new tab ID
     */
    public void setTabID(String tabID) {
        this.tabID = tabID;
    }

    /**
     * Gets the dbg obj info.
     *
     * @return the dbg obj info
     */
    public AutoSaveDbgObjInfo getDbgObjInfo() {
        return dbgObjInfo;
    }

    /**
     * Sets the dbg obj info.
     *
     * @param dbgObjInfo the new dbg obj info
     */
    public void setDbgObjInfo(AutoSaveDbgObjInfo dbgObjInfo) {
        this.dbgObjInfo = dbgObjInfo;
    }

    /**
     * Gets the tab tool tip.
     *
     * @return the tab tool tip
     */
    public String getTabToolTip() {
        return tabToolTip;
    }

    /**
     * Sets the tab tool tip.
     *
     * @param tabToolTip the new tab tool tip
     */
    public void setTabToolTip(String tabToolTip) {
        this.tabToolTip = tabToolTip;
    }

    /**
     * Gets the tab label.
     *
     * @return the tab label
     */
    public String getTabLabel() {
        return tabLabel;
    }

    /**
     * Sets the tab label.
     *
     * @param tabLabel the new tab label
     */
    public void setTabLabel(String tabLabel) {
        this.tabLabel = tabLabel;
    }

    /**
     * Gets the version number.
     *
     * @return the version number
     */
    public int getVersionNumber() {
        return versionNumber;
    }

    /**
     * Sets the version number.
     *
     * @param versionNumber the new version number
     */
    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AutoSaveMetadata) {
            AutoSaveMetadata meta = (AutoSaveMetadata) obj;
            return this.getTabID().equals(meta.getTabID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        char[] charArray = tabID.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            hashCode += charArray[i];
        }
        return hashCode;
    }


    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp the new timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    private byte[] getShaval() {
        return shaval;
    }

    private byte[] calcShaVal() {
        String data = getConnectionName() + getDatabaseName() + getAutoSaveFileName() + getTabID() + getTabLabel()
                + getTabID();
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes(StandardCharsets.UTF_8));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException exception) {
            MPPDBIDELoggerUtility.error("Encoding file failed", exception);
        }

        return new byte[0];
    }

    /**
     * Update shaval.
     */
    public void updateShaval() {
        this.shaval = calcShaVal();
    }

    /**
     * Calc and compare.
     *
     * @return true, if successful
     */
    public boolean calcAndCompare() {
        if (getShaval() == null) {
            return false;
        }

        byte[] ary = calcShaVal();
        if (ary == null) {
            return false;
        }

        return Arrays.equals(ary, getShaval());
    }
}
