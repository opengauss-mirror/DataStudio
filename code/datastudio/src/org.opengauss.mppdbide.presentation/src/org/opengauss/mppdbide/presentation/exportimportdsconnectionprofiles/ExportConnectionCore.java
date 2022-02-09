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

package org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportConnectionCore.
 *
 * @since 3.0.0
 */
public class ExportConnectionCore {

    private String filePath;
    private List<IServerConnectionInfo> loadedProfileList;
    private List<Integer> selectedProfilesIndexes;

    /**
     * Sets the file output path.
     *
     * @param path the new file output path
     */
    public void setFileOutputPath(String path) {
        this.filePath = path;

    }

    /**
     * Gets the file output path.
     *
     * @return the file output path
     */
    public String getFileOutputPath() {
        return this.filePath;
    }

    /**
     * Export files.
     *
     * @param serverConnInfoList the server conn info list
     */
    public void setExportProfilesIndexList(List<Integer> indexList) {
        this.selectedProfilesIndexes = indexList;
    }

    /**
     * Gets the export file list.
     *
     * @return the export file list
     */
    public List<IServerConnectionInfo> getExportFileList() {
        List<IServerConnectionInfo> serverConnInfoList = new ArrayList<IServerConnectionInfo>(
                MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        if (selectedProfilesIndexes != null && loadedProfileList != null) {
            selectedProfilesIndexes.stream().forEach(item -> {
                serverConnInfoList.add(loadedProfileList.get(item));
            });
        }
        return serverConnInfoList;
    }

    /**
     * Gets the loaded profile list.
     *
     * @return the loaded profile list
     */
    public List<IServerConnectionInfo> getLoadedProfileList() {
        if (loadedProfileList != null) {
            return loadedProfileList;
        }
        return new ArrayList<IServerConnectionInfo>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    }

    /**
     * Sets the loaded profile list.
     *
     * @param loadedProfileList the new loaded profile list
     */
    public void setLoadedProfileList(List<IServerConnectionInfo> loadedProfileList) {
        this.loadedProfileList = loadedProfileList;
    }

}
