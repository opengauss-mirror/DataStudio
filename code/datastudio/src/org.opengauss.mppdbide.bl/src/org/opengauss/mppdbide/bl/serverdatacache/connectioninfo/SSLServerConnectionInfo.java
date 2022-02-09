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

package org.opengauss.mppdbide.bl.serverdatacache.connectioninfo;

import com.google.gson.annotations.Expose;
import org.opengauss.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class SSLServerConnectionInfo.
 * 
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
