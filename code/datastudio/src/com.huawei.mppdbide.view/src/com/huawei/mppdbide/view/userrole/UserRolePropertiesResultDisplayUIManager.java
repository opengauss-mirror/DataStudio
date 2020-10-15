/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.userrole;

import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesUserRoleImpl;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.view.component.grid.CommitRecordEventData;
import com.huawei.mppdbide.view.objectpropertywiew.ViewObjectPropertiesResultDisplayUIManager;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserRolePropertiesResultDisplayUIManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UserRolePropertiesResultDisplayUIManager extends ViewObjectPropertiesResultDisplayUIManager {

    /**
     * Instantiates a new user role properties result display UI manager.
     *
     * @param core the core
     */
    public UserRolePropertiesResultDisplayUIManager(PropertyHandlerCore core) {
        super(core);
    }

    /**
     * Handle commit data.
     *
     * @param event the event
     */
    protected void handleCommitData(DSEvent event) {
        CommitRecordEventData eventDate = (CommitRecordEventData) event.getObject();

        DSObjectPropertiesGridDataProvider userRolePropertiesGridDataProvider = (DSObjectPropertiesGridDataProvider) eventDate
                .getDataProvider();
        PropertiesUserRoleImpl propertiesUserRoleImpl = (PropertiesUserRoleImpl) userRolePropertiesGridDataProvider
                .getObjectPropertyObject();
        String userRoleName = propertiesUserRoleImpl.getObjectName();
        String serverName = propertiesUserRoleImpl.getUserRole().getServer().getName();
        String jobName = ProgressBarLabelFormatter.getProgressLabelForUserRole(userRoleName, serverName);

        UserRolePropertiesCommitUIWorkerJob userRolePropertiesCommitWorker = new UserRolePropertiesCommitUIWorkerJob(
                jobName, null, eventDate, jobName);
        userRolePropertiesCommitWorker.schedule();

    }

}
