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

package org.opengauss.mppdbide.explainplan.nodetypes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import org.opengauss.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class BuffersInDetail.
 *
 * @since 3.0.0
 */
public class BuffersInDetail {
    @SerializedName("DN Name")
    private String dnName;

    @SerializedName("Shared Hit Blocks")
    private long sharedHitBlocks;

    @SerializedName("Shared Read Blocks")
    private long sharedReadBlocks;

    @SerializedName("Shared Dirtied Blocks")
    private long sharedDirtiedBlocks;

    @SerializedName("Shared Written Blocks")
    private long sharedWrittenBlocks;

    @SerializedName("Local Hit Blocks")
    private long localHitBlocks;

    @SerializedName("Local Read Blocks")
    private long localReadBlocks;

    @SerializedName("Local Dirtied Blocks")
    private long localDirtiedBlocks;

    @SerializedName("Local Written Blocks")
    private long localWrittenBlocks;

    @SerializedName("Temp Read Blocks")
    private long tempReadBlocks;

    @SerializedName("Temp Written Blocks")
    private long tempWrittenBlocks;

    @SerializedName("I/O Read Time")
    private double ioReadTime;

    @SerializedName("I/O Write Time")
    private double ioWriteTime;

    /**
     * Property details.
     *
     * @return the list
     */
    public List<Object> propertyDetails() {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);

        colInfo.add(getSharedHitBlocks());
        colInfo.add(getSharedReadBlocks());
        colInfo.add(getSharedDirtiedBlocks());
        colInfo.add(getSharedWrittenBlocks());

        colInfo.add(getLocalHitBlocks());
        colInfo.add(getLocalReadBlocks());
        colInfo.add(getLocalDirtiedBlocks());
        colInfo.add(getLocalWrittenBlocks());

        colInfo.add(getTempReadBlocks());
        colInfo.add(getTempWrittenBlocks());
        colInfo.add(getIoReadTime());
        colInfo.add(getIoWriteTime());

        return colInfo;
    }

    /**
     * Fill column property header.
     *
     * @return the DN intra node details column
     */
    public static DNIntraNodeDetailsColumn fillColumnPropertyHeader() {
        DNIntraNodeDetailsColumn dnBufferInDetail = new DNIntraNodeDetailsColumn();

        dnBufferInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_BUFFERSINDETAIL));

        ArrayList<String> colnames = new ArrayList<String>(12);
        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_SHAREDHITBLOCKS));
        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_SHAREDREADBLOCKS));
        colnames.add(MessageConfigLoader
                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_SHAREDDIRTIEDBLOCKS));
        colnames.add(MessageConfigLoader
                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_SHAREDWRITTENBLOCKS));

        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_LOCALHITBLOCKS));
        colnames.add(MessageConfigLoader
                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_LOCALREADHITBLOCKS));
        colnames.add(MessageConfigLoader
                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_LOCALDIRTIEDBLOCKS));
        colnames.add(MessageConfigLoader
                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_LOCALWRITTENBLOCKS));

        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_TEMPREADBLOCKS));
        colnames.add(MessageConfigLoader
                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_TEMPWRITTENBLOCKS));
        colnames.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_IOREADTIME));
        colnames.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_IOWRITETIME));

        dnBufferInDetail.setColCount(12);
        dnBufferInDetail.setColnames(colnames);

        return dnBufferInDetail;
    }

    /**
     * Gets the dn name.
     *
     * @return the dn name
     */
    public String getDnName() {
        return dnName;
    }

    private double getLocalReadBlocks() {
        return localReadBlocks;
    }

    private double getSharedHitBlocks() {
        return sharedHitBlocks;
    }

    private long getSharedReadBlocks() {
        return sharedReadBlocks;
    }

    private long getSharedDirtiedBlocks() {
        return sharedDirtiedBlocks;
    }

    private long getSharedWrittenBlocks() {
        return sharedWrittenBlocks;
    }

    private long getLocalHitBlocks() {
        return localHitBlocks;
    }

    private long getLocalDirtiedBlocks() {
        return localDirtiedBlocks;
    }

    private long getLocalWrittenBlocks() {
        return localWrittenBlocks;
    }

    private long getTempReadBlocks() {
        return tempReadBlocks;
    }

    private long getTempWrittenBlocks() {
        return tempWrittenBlocks;
    }

    private double getIoReadTime() {
        return ioReadTime;
    }

    private double getIoWriteTime() {
        return ioWriteTime;
    }
}
