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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.SynonymConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: Class
 * 
 * Description: The Class ZPropertiesSynonymImpl
 *
 * @since 3.0.0
 */
public class PropertiesSynonymImpl implements IServerObjectProperties {
    private SynonymMetaData synonymMetaData;
    private OlapConvertToObjectPropertyData convertToObjectPropertyData;

    /**
     * Instantiates a new properties synonym impl.
     * 
     * @param obj the object
     */
    public PropertiesSynonymImpl(Object obj) {
        this.synonymMetaData = (SynonymMetaData) obj;
        convertToObjectPropertyData = new OlapConvertToObjectPropertyData();
    }

    @Override
    public String getObjectName() {
        return this.synonymMetaData.getName();
    }

    @Override
    public String getHeader() {
        return this.synonymMetaData.getWindowTitleName();
    }

    @Override
    public String getUniqueID() {
        return this.synonymMetaData.getOid() + '@' + synonymMetaData.getParent().getDisplayName() + ".properties";
    }

    @Override
    public List<IObjectPropertyData> getAllProperties(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException {
        List<String> tabName = new ArrayList<String>(5);
        List<List<String[]>> propertyList = new ArrayList<List<String[]>>(5);
        tabName.add(PropertiesConstants.GENERAL);
        propertyList.add(getSynonymProperties(conn));

        return convertToObjectPropertyData.getObjectPropertyData(tabName, propertyList, null, this);
    }

    @Override
    public Database getDatabase() {
        return this.synonymMetaData.getDatabase();
    }

    /**
     * @Title: getSynonymProperties
     * @Description: get the synonym properties
     * @param conn the db connection
     * @return List the synonym properties
     * @throws MPPDBIDEException the MPPDBIDE exception
     *
     */
    public List<String[]> getSynonymProperties(DBConnection conn) throws MPPDBIDEException {
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.getPrepareStmt(SynonymMetaData.REFRESH_SYNONYM_STATEMENT);
            preparedStatement.setString(1, synonymMetaData.getParent().getQualifiedObjectName());
            preparedStatement.setString(2, synonymMetaData.getName());
            rs = preparedStatement.executeQuery();
            boolean hasNext = rs.next();
            while (hasNext) {
                synonymMetaData.setName(rs.getString(SynonymConstants.SYN_NAME));
                synonymMetaData.setOwner(rs.getString(SynonymConstants.OWNER.toLowerCase(Locale.ENGLISH)));
                synonymMetaData.setObjectOwner(rs.getString(SynonymConstants.SCHEMA_NAME));
                synonymMetaData.setObjectName(rs.getString(SynonymConstants.TAB_NAME));
                hasNext = rs.next();
            }
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            conn.closeResultSet(rs);
            conn.closeStatement(preparedStatement);
        }

        List<String[]> str = new ArrayList<String[]>(5);
        String[] viewPropHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_PROPERTY),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_VALUE)};
        str.add(viewPropHeader);
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_SYNONYM_NAME),
                synonymMetaData.getName()).getProp());
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_SYNONYM_OWNER),
                synonymMetaData.getObjectOwner()).getProp());
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_OBJECT_OWNER),
                synonymMetaData.getOwner()).getProp());
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_OBJECT_NAME),
                synonymMetaData.getObjectName()).getProp());
        return str;
    }
}
