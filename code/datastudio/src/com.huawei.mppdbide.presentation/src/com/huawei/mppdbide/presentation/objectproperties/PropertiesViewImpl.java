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

package com.huawei.mppdbide.presentation.objectproperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.presentation.objectbrowser.ObjectBrowserObjectRefreshPresentation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesViewImpl.
 *
 * @since 3.0.0
 */
public class PropertiesViewImpl implements IServerObjectProperties {

    private ViewMetaData view;
    private OlapConvertToObjectPropertyData convertToObjectPropertyData;
    private static final String FETCH_SOURCE_CODE_QUERY = "SELECT * FROM pg_get_viewdef";

    /**
     * Instantiates a new properties view impl.
     *
     * @param obj the obj
     */
    public PropertiesViewImpl(Object obj) {

        this.view = (ViewMetaData) obj;
        convertToObjectPropertyData = new OlapConvertToObjectPropertyData();
    }

    @Override
    public List<IObjectPropertyData> getAllProperties(DBConnection conn) throws MPPDBIDEException {
        if (!view.isLoaded()) {
            ObjectBrowserObjectRefreshPresentation.refreshSeverObject(view);
        }
        List<String> tabName = new ArrayList<String>(5);
        List<List<String[]>> propertyList = new ArrayList<List<String[]>>(5);
        tabName.add(PropertiesConstants.GENERAL);
        tabName.add(PropertiesConstants.COLUMNS);

        propertyList.add(getViewProperties(conn));
        propertyList.add(getViewColumnInfo(conn));

        return convertToObjectPropertyData.getObjectPropertyData(tabName, propertyList, null, this);

    }

    /**
     * Gets the view column info.
     *
     * @param conn the conn
     * @return the view column info
     */
    private List<String[]> getViewColumnInfo(DBConnection conn) {
        ArrayList<ViewColumnMetaData> viewMetaDataList = this.view.getColumns().getList();
        List<String[]> viewColmnsList = new ArrayList<String[]>(5);
        String[] viewColsHeader = new String[] {
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_COLUMNSNAME),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_DATATYPE),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_ISNULLABLE)};
        viewColmnsList.add(viewColsHeader);
        for (ViewColumnMetaData col : viewMetaDataList) {
            int jcnt = 0;
            String[] viewCol = new String[viewColsHeader.length];
            viewCol[jcnt] = col.getName();
            if (null != col.getDataType()) {
                viewCol[++jcnt] = col.getViewDisplayDatatype();
            }
            // retrieve the boolean value invert it for UI purpose and change to
            // string
            viewCol[++jcnt] = "" + !col.isNotNull();
            viewColmnsList.add(viewCol);
        }

        return viewColmnsList;

    }

    /**
     * Gets the view properties.
     *
     * @param conn the conn
     * @return the view properties
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public List<String[]> getViewProperties(DBConnection conn) throws MPPDBIDEException {
        List<String[]> str = new ArrayList<String[]>(5);
        String viewName = view.getName();
        String ownerName = view.getOwner();
        String viewDefinition = getDDL(conn);

        String[] viewPropHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_PROPERTY),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_VALUE)};
        str.add(viewPropHeader);
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_VIEW_NAME), viewName)
                .getProp());
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_VIEW_OWNER), ownerName)
                .getProp());
        str.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_VIEW_DEF),
                viewDefinition).getProp());
        return str;
    }

    /**
     * Fetch DDL.
     *
     * @param conn the conn
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void fetchDDL(DBConnection conn) throws MPPDBIDEException {

        ResultSet rs = null;
        try {
            rs = conn.execSelectAndReturnRs(FETCH_SOURCE_CODE_QUERY + '(' + view.getOid() + ')');

            boolean hasNext = rs.next();
            if (hasNext) {
                if (!rs.getString(1).contains("Not a view")) {
                    view.setSource(rs.getString(1));
                    view.setViewCodeLoaded(true);
                } else {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.VIEW_DOES_NOT_EXIST));
                    throw new DatabaseOperationException(IMessagesConstants.VIEW_DOES_NOT_EXIST);
                }
            }
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Gets the ddl.
     *
     * @param conn the conn
     * @return the ddl
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private String getDDL(DBConnection conn) throws MPPDBIDEException {
        if (!view.isViewCodeLoaded()) {
            fetchDDL(conn);
        }

        StringBuilder strbldr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (view.isViewCodeLoaded()) {
            strbldr.append(String.format(Locale.ENGLISH, "CREATE %s%sVIEW ",
                    this.view.getOrReplaceString(), this.view.getMaterViewString()));
            strbldr.append(view.getNamespace().getQualifiedObjectName()).append(".")
                    .append(view.getQualifiedObjectName()).append(System.lineSeparator()).append(" AS ")
                    .append(System.lineSeparator()).append(view.getSource());
        }
        return strbldr.toString();
    }

    @Override
    public Database getDatabase() {
        return this.view.getDatabase();
    }

    @Override
    public String getObjectName() {

        return view.getQualifiedObjectName();
    }

    @Override
    public String getHeader() {
        return view.getNameSpaceName() + '.' + view.getName() + '-' + view.getDatabase().getDbName() + '@'
                + view.getDatabase().getServerName();
    }

    @Override
    public String getUniqueID() {
        return view.getOid() + '@' + view.getDatabase().getServerName() + "properties";
    }
}
