/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportimportdsconnectionprofiles;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportConnectionCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
