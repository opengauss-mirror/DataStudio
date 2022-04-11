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

package org.opengauss.mppdbide.view.objectpropertywiew.handler;

import org.opengauss.mppdbide.presentation.IExecutionContext;
import org.opengauss.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.view.objectpropertywiew.ViewObjectPropertiesContext;
import org.opengauss.mppdbide.view.terminal.TerminalQueryExecutionWorker;

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
