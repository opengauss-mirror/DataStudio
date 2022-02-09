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

package org.opengauss.mppdbide.view.data;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: class Description: The Class DSViewDataManager.
 *
 * @since 3.0.0
 */
public final class DSViewDataManager {

    private static volatile DSViewDataManager instance = null;

    private static final Object LOCK = new Object();

    private boolean showExplainPlanWarningsMultipleQuery = false;

    private boolean showExplainPlanWarningsAnalyzeQuery = false;

    private boolean isDebugBkptNotSupportedPopup = false;

    private boolean showCommitConfirmation = false;

    private boolean showRollbackConfirmation = false;

    private String currentOsUserPath;

    private List<String> sourceViewerIds = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

    private boolean wbGoingToClose = false;

    private String treeRenderPolicy = null;
    
    /**
     * Gets the single instance of DSViewDataManager.
     *
     * @return single instance of DSViewDataManager
     */
    public static DSViewDataManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DSViewDataManager();
                }
            }
        }
        return instance;
    }

    private DSViewDataManager() {
    }

    /**
     * Checks if is debug bkpt not supported popup.
     *
     * @return true, if is debug bkpt not supported popup
     */
    public boolean isDebugBkptNotSupportedPopup() {
        return isDebugBkptNotSupportedPopup;
    }

    /**
     * Sets the debug bkpt not supported popup.
     *
     * @param lDebugBreakpointSupported the new debug bkpt not supported popup
     */
    public void setDebugBkptNotSupportedPopup(boolean lDebugBreakpointSupported) {
        isDebugBkptNotSupportedPopup = lDebugBreakpointSupported;
    }

    /**
     * Checks if is show explain plan warnings analyze query.
     *
     * @return true, if is show explain plan warnings analyze query
     */
    public boolean isShowExplainPlanWarningsAnalyzeQuery() {
        return showExplainPlanWarningsAnalyzeQuery;
    }

    /**
     * Sets the show explain plan warnings analyze query.
     *
     * @param lShowExplainPlanWarningsAnalyzeQuery the new show explain plan
     * warnings analyze query
     */
    public void setShowExplainPlanWarningsAnalyzeQuery(boolean lShowExplainPlanWarningsAnalyzeQuery) {
        showExplainPlanWarningsAnalyzeQuery = lShowExplainPlanWarningsAnalyzeQuery;
    }

    /**
     * Checks if is show explain plan warnings multiple query.
     *
     * @return true, if is show explain plan warnings multiple query
     */
    public boolean isShowExplainPlanWarningsMultipleQuery() {
        return showExplainPlanWarningsMultipleQuery;
    }

    /**
     * Sets the show explain plan warnings multiple query.
     *
     * @param lShowExplainPlanWarningsMultipleQuery the new show explain plan
     * warnings multiple query
     */
    public void setShowExplainPlanWarningsMultipleQuery(boolean lShowExplainPlanWarningsMultipleQuery) {
        showExplainPlanWarningsMultipleQuery = lShowExplainPlanWarningsMultipleQuery;
    }

    /**
     * Checks if is show commit confirmation.
     *
     * @return true, if is show commit confirmation
     */
    public boolean isShowCommitConfirmation() {
        return showCommitConfirmation;
    }

    /**
     * Sets the show commit confirmation.
     *
     * @param lShowCommitConfirmation the new show commit confirmation
     */
    public void setShowCommitConfirmation(boolean lShowCommitConfirmation) {
        showCommitConfirmation = lShowCommitConfirmation;
    }

    /**
     * Checks if is show rollback confirmation.
     *
     * @return true, if is show rollback confirmation
     */
    public boolean isShowRollbackConfirmation() {
        return showRollbackConfirmation;
    }

    /**
     * Sets the show rollback confirmation.
     *
     * @param lShowRollbackConfirmation the new show rollback confirmation
     */
    public void setShowRollbackConfirmation(boolean lShowRollbackConfirmation) {
        showRollbackConfirmation = lShowRollbackConfirmation;
    }

    /**
     * Gets the current os user path.
     *
     * @return the current os user path
     */
    public String getCurrentOsUserPath() {
        return currentOsUserPath;
    }

    /**
     * Sets the current os user path.
     *
     * @param currentOsUserPath the new current os user path
     */
    public void setCurrentOsUserPath(String currentOsUserPath) {
        this.currentOsUserPath = currentOsUserPath;
    }

    /**
     * Gets the source viewer id.
     *
     * @return the source viewer id
     */
    public List<String> getSourceViewerId() {
        return sourceViewerIds;
    }

    /**
     * Sets the source viewer id.
     *
     * @param sourceViewerId the new source viewer id
     */
    public void setSourceViewerId(List<String> sourceViewerId) {
        this.sourceViewerIds = sourceViewerId;
    }

    /**
     * Adds the source viewer id.
     *
     * @param id the id
     */
    public void addSourceViewerId(String id) {
        sourceViewerIds.add(id);
    }

    /**
     * Removes the source viewer id only.
     *
     * @param id the id
     */
    public void removeSourceViewerIdOnly(String id) {
        sourceViewerIds.remove(id);
    }

    /**
     * Checks if is wb going to close.
     *
     * @return true, if is wb going to close
     */
    public boolean isWbGoingToClose() {
        return wbGoingToClose;
    }

    /**
     * Sets the wb going to close.
     *
     * @param wbGoingToClose the new wb going to close
     */
    public void setWbGoingToClose(boolean wbGoingToClose) {
        this.wbGoingToClose = wbGoingToClose;
    }

    /**
     * get the tree render policy
     * 
     * @return returns policy type
     */
    public String getTreeRenderPolicy() {
        return treeRenderPolicy;
    }

    /**
     * set the tree render policy
     * 
     * @param treeRenderPolicy set the tree render policy
     */
    public void setTreeRenderPolicy(String treeRenderPolicy) {
        this.treeRenderPolicy = treeRenderPolicy;
    }

}
