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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTypeEnum;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesPartitionTableImpl.
 * 
 * @since 3.0.0
 */
public class PropertiesPartitionTableImpl extends PropertiesTableImpl {

    private String partitionType;
    private PartitionTable partitionTable;
    private OlapConvertToObjectPropertyData convertToObjectPropertyData;

    /**
     * Instantiates a new properties partition table impl.
     *
     * @param obj the obj
     */
    public PropertiesPartitionTableImpl(Object obj) {
        super(obj);
        this.partitionTable = (PartitionTable) obj;
        convertToObjectPropertyData = new OlapConvertToObjectPropertyData();
    }

    @Override
    public List<IObjectPropertyData> getAllProperties(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {

        super.getAllProperties(conn);
        if (!(this.partitionTable.getPartitions().getSize() == 0)) {
            this.partitionType = partitionTable.getPartitions().getItem(0).getPartitionType();
        } else {
            this.partitionType = PartitionTypeEnum.BY_RANGE.getTypeName();
        }
        tabNameList.add(PropertiesConstants.PARTITION);
        tableProperties.add(getPartitionInfo(conn));
        tableProperties.get(0)
                .add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_TYPE),
                        partitionType).getProp());
        tableProperties.get(0)
                .add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_COLUMN_ID),
                        partitionTable.getPartKey()).getProp());
        return convertToObjectPropertyData.getObjectPropertyData(tabNameList, tableProperties, this.partitionTable,
                this);

    }

    /**
     * Gets the partition info.
     *
     * @param conn the conn
     * @return the partition info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private List<String[]> getPartitionInfo(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        ArrayList<PartitionMetaData> partitionMetadataList = PartitionTable.class.cast(table).getPartitions().getList();
        Map<String, String[]> partInfo = getPartitionData(conn);
        List<String[]> partitionList = new ArrayList<String[]>(5);
        String partName = null;
        String[] partitionHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROP_PARTITION_PARTITIONNAME),
            MessageConfigLoader.getProperty(IMessagesConstants.PROP_PARTITION_PARTITIONBOUNDARIES),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_TABLESPACE)};

        partitionList.add(partitionHeader);
        for (PartitionMetaData part : partitionMetadataList) {
            int jindex = 0;
            partName = part.getName();
            String[] partitionsInfo = new String[partitionHeader.length];
            partitionsInfo[jindex] = partName;

            partitionsInfo[++jindex] = partInfo.get(partName)[0];
            partitionsInfo[++jindex] = partInfo.get(partName)[1];

            partitionList.add(partitionsInfo);
        }
        return partitionList;
    }

    /**
     * Gets the partition data.
     *
     * @param conn the conn
     * @return the partition data
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private Map<String, String[]> getPartitionData(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet rs = null;
        Map<String, String[]> partMap = new HashMap<>();
        String query = "select pr.relname ,array_to_string(pr.boundaries, ','::text, 'MAXVALUE'::text) as boundaries ,"
                + "t.spcname from pg_partition pr "
                + "left join pg_tablespace t on (pr.reltablespace=t.oid) where parttype in('p,v') and pr.parentid ="
                + partitionTable.getOid();
        rs = conn.execSelectAndReturnRs(query);
        boolean hasNext;
        try {
            hasNext = rs.next();
            while (hasNext) {
                String[] partInfo = new String[2];
                partInfo[0] = rs.getString("boundaries");
                partInfo[1] = rs.getString("spcname");
                partMap.put(rs.getString("relname"), partInfo);
                hasNext = rs.next();
            }
        } catch (SQLException ex) {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, ex);
        } finally {
            conn.closeResultSet(rs);
        }
        return partMap;

    }
}
