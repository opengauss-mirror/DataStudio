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
 * @since 3.0.0
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
