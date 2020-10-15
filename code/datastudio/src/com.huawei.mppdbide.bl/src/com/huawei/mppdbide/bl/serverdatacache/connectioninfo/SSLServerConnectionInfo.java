/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.connectioninfo;

import com.google.gson.annotations.Expose;
import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class SSLServerConnectionInfo.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public final class SSLServerConnectionInfo {

    @Expose
    private String clSSLCertificatePath;
    @Expose
    private String clSSLKeyPath;
    @Expose
    private char[] sslPrd;
    @Expose
    private String rootCertFilePathText;
    @Expose
    private String sslMode;

    @Expose(deserialize = false, serialize = false)
    private String clSSLPrivateKeyFile = "";

    /**
     * Gets the cl SSL certificate path.
     *
     * @return the cl SSL certificate path
     */
    public String getClSSLCertificatePath() {
        return clSSLCertificatePath;
    }

    /**
     * Sets the cl SSL certificate path.
     *
     * @param clSSLCertificatePath the new cl SSL certificate path
     */
    public void setClSSLCertificatePath(String clSSLCertificatePath) {
        this.clSSLCertificatePath = clSSLCertificatePath;
    }

    /**
     * Gets the cl SSL key path.
     *
     * @return the cl SSL key path
     */
    public String getClSSLKeyPath() {
        return clSSLKeyPath;
    }

    /**
     * Sets the cl SSL key path.
     *
     * @param clSSLKeyPath the new cl SSL key path
     */
    public void setClSSLKeyPath(String clSSLKeyPath) {
        this.clSSLKeyPath = clSSLKeyPath;
    }

    /**
     * Gets the ssl prd.
     *
     * @return the ssl prd
     */
    public char[] getSSLPsrd() {
        if (sslPrd == null) {
            return new char[0];
        }
        return sslPrd.clone();
    }

    /**
     * Sets the ssl prd.
     *
     * @param sslPrd the new ssl prd
     */
    public void setSSLPsrd(char[] sslPrd) {
        this.sslPrd = sslPrd.clone();
    }

    /**
     * Gets the root cert file path text.
     *
     * @return the root cert file path text
     */
    public String getRootCertFilePathText() {
        return rootCertFilePathText;
    }

    /**
     * Sets the root cert file path text.
     *
     * @param rootCertFilePathText the new root cert file path text
     */
    public void setRootCertFilePathText(String rootCertFilePathText) {
        this.rootCertFilePathText = rootCertFilePathText;
    }

    /**
     * Gets the ssl mode.
     *
     * @return the ssl mode
     */
    public String getSSLMode() {
        return sslMode;
    }

    /**
     * Sets the ssl mode.
     *
     * @param sslMode the new ssl mode
     */
    public void setSSLMode(String sslMode) {
        this.sslMode = sslMode;
    }

    /**
     * Gets the cl SSL private key file.
     *
     * @return the cl SSL private key file
     */
    public String getClSSLPrivateKeyFile() {
        return clSSLPrivateKeyFile;
    }

    /**
     * Sets the cl SSL private key file.
     *
     * @param clSSLPrivateKeyFile the new cl SSL private key file
     */
    public void setClSSLPrivateKeyFile(String clSSLPrivateKeyFile) {
        this.clSSLPrivateKeyFile = clSSLPrivateKeyFile;
    }

    /**
     * Clear SSL pssrd. Clear SSL pssrd.
     */

    public void clearSSLPssrd() {
        SecureUtil.clearPassword(this.sslPrd);
    }
}
