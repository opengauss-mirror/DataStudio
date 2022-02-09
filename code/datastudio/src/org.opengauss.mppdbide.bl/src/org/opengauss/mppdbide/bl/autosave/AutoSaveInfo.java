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

package org.opengauss.mppdbide.bl.autosave;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: class
 * 
 * Description: The Class AutoSaveInfo.
 * 
 */

public class AutoSaveInfo {

    @SerializedName("version")
    private String version;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("activeterminal")
    private String activeTerminalName;

    @SerializedName("tabinfo")
    private List<AutoSaveMetadata> autosaveMD = new ArrayList<>();

    private static final String VERSIONNO = "1.0.0";

    /**
     * Instantiates a new auto save info.
     */
    public AutoSaveInfo() {
        this.version = VERSIONNO;
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
     * Gets the autosave MD.
     *
     * @return the autosave MD
     */
    public List<AutoSaveMetadata> getAutosaveMD() {
        // sending the cloned copy of metadata
        return new ArrayList<>(autosaveMD);
    }

    /**
     * Sets the autosave MD.
     *
     * @param autosaveMD the new autosave MD
     */
    public void setAutosaveMD(List<AutoSaveMetadata> autosaveMD) {
        this.autosaveMD = autosaveMD;
    }

    /**
     * Invalidate.
     */
    public void invalidate() {
        autosaveMD.clear();
        setVersion("");
        timestamp = "";
        activeTerminalName = "";
    }

    /**
     * Adds the auto save metadata.
     *
     * @param metaData the meta data
     */
    public void addAutoSaveMetadata(AutoSaveMetadata metaData) {
        autosaveMD.add(metaData);
    }

    /**
     * Removes the auto save metadata.
     *
     * @param metaData the meta data
     * @return true, if successful
     */
    public boolean removeAutoSaveMetadata(AutoSaveMetadata metaData) {
        return autosaveMD.remove(metaData);
    }

    /**
     * Gets the active terminal name.
     *
     * @return the active terminal name
     */
    public String getActiveTerminalName() {
        return activeTerminalName;
    }

    /**
     * Sets the active terminal name.
     *
     * @param activeTerminalName the new active terminal name
     */
    public void setActiveTerminalName(String activeTerminalName) {
        this.activeTerminalName = activeTerminalName;
    }

    /**
     * Gets the meta data.
     *
     * @param id the id
     * @return the meta data
     */
    public AutoSaveMetadata getMetaData(String id) {
        for (AutoSaveMetadata meta : autosaveMD) {
            if (id.equals(meta.getTabID())) {
                return meta;
            }
        }
        return null;

    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
