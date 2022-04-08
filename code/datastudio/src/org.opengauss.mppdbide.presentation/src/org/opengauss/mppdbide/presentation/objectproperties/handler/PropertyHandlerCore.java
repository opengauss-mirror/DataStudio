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

package org.opengauss.mppdbide.presentation.objectproperties.handler;

import java.sql.SQLException;
import java.util.List;

import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.PropertyOperationType;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.objectproperties.IObjectPropertyData;
import org.opengauss.mppdbide.presentation.objectproperties.IServerObjectProperties;
import org.opengauss.mppdbide.presentation.objectproperties.factory.ServerFactory;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertyHandlerCore.
 * 
 * @since 3.0.0
 */
public class PropertyHandlerCore {
    private IServerObjectProperties iServerObject;
    private String objectName;

    /**
     * The conn infra.
     */
    protected TerminalExecutionConnectionInfra connInfra;

    /**
     * The details.
     */
    protected IWindowDetail details;
    /**
     * Instantiates a new property handler core.
     */
    public PropertyHandlerCore() {

    }

    /**
     * Instantiates a new property handler core.
     *
     * @param obj the obj
     */
    public PropertyHandlerCore(Object obj) {
        this(obj, PropertyOperationType.PROPERTY_OPERATION_VIEW);
    }

    /**
     * Instantiates a new property handler core.
     *
     * @param obj the obj
     * @param propertyOperationEdit the property operation edit
     */
    public PropertyHandlerCore(Object obj, PropertyOperationType propertyOperationEdit) {
        // initialization of the object
        ServerFactory factory = new ServerFactory();
        this.iServerObject = factory.getObject(obj, propertyOperationEdit);
        if (null != iServerObject) {
            this.setObjectName(iServerObject.getObjectName());
        }

        details = new ObjectPropertiesWindowDetails();
    }

    /**
     * Sets the properties object.
     *
     * @param obj the new properties object
     */
    protected void setPropertiesObject(IServerObjectProperties obj) {
        this.iServerObject = obj;
    }

    /**
     * Checks if is executable.
     *
     * @return true, if is executable
     */
    public boolean isExecutable() {
        if (iServerObject != null) {
            return true;
        }
        return false;
    }

    /**
     * Gets the property.
     *
     * @return the property
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws SQLException the SQL exception
     */
    public IPropertyDetail getproperty() throws MPPDBIDEException, SQLException {

        PropertyDetailImpl obj = null;
        List<IObjectPropertyData> propList = null;
        IObjectPropertyData parentProperty = null;
        propList = iServerObject.getAllProperties(connInfra.getConnection());
        parentProperty = iServerObject.getParentProperties(connInfra.getConnection());
        String uId = getUniqueID();
        String windowTitle = getWindowTitle();
        obj = new PropertyDetailImpl(uId, windowTitle, propList, this, parentProperty);
        return obj;
    }

    private String getWindowTitle() {
        return iServerObject.getHeader();
    }

    private String getUniqueID() {
        return iServerObject.getUniqueID();
    }

    private void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PropertyDetailImpl.
     */
    private static class PropertyDetailImpl implements IPropertyDetail {
        private String uid;
        private String title;
        private List<IObjectPropertyData> prop;
        private PropertyHandlerCore core;
        private IObjectPropertyData parentObjectProperty;

        PropertyDetailImpl(String uID, String windowTitle, List<IObjectPropertyData> propList,
                PropertyHandlerCore propertyHandlerCore, IObjectPropertyData parentObjectProperty) {
            this.uid = uID;
            this.title = windowTitle;
            this.prop = propList;
            this.core = propertyHandlerCore;
            this.parentObjectProperty = parentObjectProperty;

        }

        @Override
        public String getHeader() {
            return title;
        }

        @Override
        public String getUniqueID() {
            return uid;
        }

        @Override
        public List<IObjectPropertyData> objectproperties() {
            return prop;
        }

        @Override
        public PropertyHandlerCore getPropertyCore() {
            return this.core;
        }

        @Override
        public IObjectPropertyData getParentProperty() {
            return this.parentObjectProperty;
        }

    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    public TerminalExecutionConnectionInfra getTermConnection() {
        if (null == connInfra) {
            this.connInfra = new TerminalExecutionConnectionInfra();
            this.connInfra.setDatabase(iServerObject.getDatabase());
        }

        return connInfra;
    }

    /**
     * Gets the window details.
     *
     * @return the window details
     */
    public IWindowDetail getWindowDetails() {
        return details;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ObjectPropertiesWindowDetails.
     */
    private class ObjectPropertiesWindowDetails implements IWindowDetail {

        @Override
        public String getTitle() {
            return iServerObject.getHeader();
        }

        @Override
        public String getUniqueID() {
            return iServerObject.getUniqueID();   
        }

        @Override
        public String getIcon() {
            return null;
        }

        @Override
        public String getShortTitle() {
            return objectName;
        }

        @Override
        public boolean isCloseable() {
            return true;
        }
    }

}
