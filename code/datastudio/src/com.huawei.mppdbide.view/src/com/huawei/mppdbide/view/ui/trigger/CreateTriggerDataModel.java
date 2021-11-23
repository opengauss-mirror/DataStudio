/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Title: CreateTriggerDataModel for use
 * Description: the class CreateTriggerDataModel
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2021-06-01]
 * @since 2021-06-01
 */
public class CreateTriggerDataModel {
    private String triggerName;
    private boolean triggerNameCase;
    private String triggerNamespaceName;
    private String triggerTableName;

    // value 0:before 1:after 2:instead of
    private int triggerStage;

    // bit 0/1/2/3 insert/delete/truncate/update
    private int selectOptration;
    private List<String> updateColumn = new ArrayList<String>(4);
    private boolean isStatementLevel;
    private String whenCodition;
    private String triggerFunc;

    /**
     * Gets trigger namespace name
     *
     * @return String the trigger namespace name
     */
    public String getTriggerNamespaceName() {
        return triggerNamespaceName;
    }

    /**
     * Gets unique id
     *
     * @return String the unique id
     */
    public String getUniqueId() {
        return getTriggerNamespaceName() + "_" +
            (triggerNameCase ? getTriggerName() : getTriggerName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Sets trigger namespace name
     *
     * @param String the trigger namespace name to set
     */
    public void setTriggerNamespaceName(String triggerNamespaceName) {
        this.triggerNamespaceName = triggerNamespaceName;
    }

    /**
     * Gets trigger function
     *
     * @return String the trigger function
     */
    public String getTriggerFunc() {
        return triggerFunc;
    }

    /**
     * Sets the trigger function
     *
     * @param String the trigger function to set
     */
    public void setTriggerFunc(String triggerFunc) {
        this.triggerFunc = triggerFunc;
    }

    /**
     * Gets trigger name
     *
     * @return String the trigger name
     */
    public String getTriggerName() {
        return triggerName;
    }

    /**
     * Sets the trigger name
     *
     * @param String the trigger name to set
     */
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    /**
     * Gets trigger name case
     *
     * @return boolean trigger name case
     */
    public boolean getTriggerNameCase() {
        return triggerNameCase;
    }

    /**
     * Sets trigger name case
     *
     * @param boolean trigger name case
     */
    public void setTriggerNameCase(boolean triggerNameCase) {
        this.triggerNameCase = triggerNameCase;
    }

    /**
     * Gets trigger table name
     *
     * @return String the trigger table name
     */
    public String getTriggerTableName() {
        return triggerTableName;
    }

    /**
     * Sets trigger table name
     *
     * @param String the trigger table name to set
     */
    public void setTriggerTableName(String triggerTableName) {
        this.triggerTableName = triggerTableName;
    }

    /**
     * Gets the trigger stage
     *
     * @return int the trigger stage
     */
    public int getTriggerStage() {
        return triggerStage;
    }

    /**
     * Sets the trigger stage
     *
     * @param String the trigger stage to set
     */
    public void setTriggerStage(int triggerStage) {
        this.triggerStage = triggerStage;
    }

    /**
     * Gets update column
     *
     * @return List<String> the update column list
     */
    public List<String> getUpdateColumn() {
        return updateColumn;
    }

    /**
     * Gets select operation
     *
     * @return int the selectOptration
     */
    public int getSelectOptration() {
        return selectOptration;
    }

    /**
     * Sets the selection operation
     *
     * @param int the selectOptration to set
     */
    public void setSelectOptration(int selectOptration) {
        this.selectOptration = selectOptration;
    }

    /**
     * Sets the update column
     *
     * @param List<String> the updateColumn to set
     */
    public void setUpdateColumn(List<String> updateColumn) {
        this.updateColumn = updateColumn;
    }

    /**
     * If is statement level
     *
     * @return boolean true if is statement level
     */
    public boolean isStatementLevel() {
        return isStatementLevel;
    }

    /**
     * Sets if is statement level
     *
     * @param boolean true if is statement level
     */
    public void setStatementLevel(boolean isStatementLevel) {
        this.isStatementLevel = isStatementLevel;
    }

    /**
     * Gets when condition
     *
     * @return String the when condition
     */
    public String getWhenCodition() {
        return whenCodition;
    }

    /**
     * Sets the when condition
     *
     * @param String the whenCodition to set
     */
    public void setWhenCodition(String whenCodition) {
        this.whenCodition = whenCodition;
    }
}
