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

package com.huawei.mppdbide.view.objectpropertywiew.handler;

import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.objectpropertywiew.ViewObjectPropertiesContext;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionPropertyWorker.
 *
 * @since 3.0.0
 */
public class ConnectionPropertyWorker extends TerminalQueryExecutionWorker {

    private PropertyHandlerCore propertycore;
    private IPropertyDetail propertiesObject;

    /**
     * Instantiates a new connection property worker.
     *
     * @param context the context
     */
    public ConnectionPropertyWorker(IExecutionContext context) {
        super(context);
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        propertycore = ((ViewObjectPropertiesContext) context).getPropertyHandlerCore();

        propertiesObject = propertycore.getproperty();

        displayUIResult();

        return null;
    }

    private void displayUIResult() {
        context.getResultDisplayUIManager().handleResultDisplay(propertiesObject);
    }

}
