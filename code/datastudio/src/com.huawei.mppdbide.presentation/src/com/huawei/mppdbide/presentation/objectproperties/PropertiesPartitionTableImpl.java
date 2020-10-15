/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesPartitionTableImpl.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class PropertiesPartitionTableImpl extends PropertiesTableImpl {

    private String partitionType;
    private PartitionTable partitionTable;
    private OlapConvertToObjectPropertyData convertToObjectPropertyData;
    private static final String BY_VALUE_PART_TYPE = "By Value";
    private static final String BY_RANGE_PART_TYPE = "By Range";

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
            this.partitionType = BY_RANGE_PART_TYPE;

        } else {
            this.partitionType = BY_VALUE_PART_TYPE;
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
