/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.SynonymConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: Class
 * 
 * Description: The Class ZPropertiesSynonymImpl
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since 11-Nov-2019
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
     * @Author: gWX773294
     * @Date: 12-Nov-2019
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
                synonymMetaData.getOwner()).getProp());
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_OBJECT_OWNER),
                synonymMetaData.getObjectOwner()).getProp());
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_OBJECT_NAME),
                synonymMetaData.getObjectName()).getProp());
        return str;
    }
}
