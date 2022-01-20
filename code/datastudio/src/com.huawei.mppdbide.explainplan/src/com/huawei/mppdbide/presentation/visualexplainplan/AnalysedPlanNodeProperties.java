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

package com.huawei.mppdbide.presentation.visualexplainplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;
import com.huawei.mppdbide.presentation.objectproperties.ConvertToObjectPropertyData;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.presentation.objectproperties.IObjectPropertyData;
import com.huawei.mppdbide.presentation.objectproperties.IServerObjectProperties;
import com.huawei.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MathUtils;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class AnalysedPlanNodeProperties.
 *
 * @since 3.0.0
 */
public class AnalysedPlanNodeProperties implements IServerObjectProperties, IPropertyDetail {

    private AnalysedPlanNode planNode;
    private PropertyHandlerCore core;

    /**
     * The tab name list.
     */
    protected List<String> tabNameList;

    /**
     * The node properties.
     */
    protected List<List<Object[]>> nodeProperties;

    /**
     * Instantiates a new analysed plan node properties.
     *
     * @param planNode the plan node
     * @param explainPlanNodePropertiesCore the explain plan node properties core
     */
    public AnalysedPlanNodeProperties(AnalysedPlanNode planNode, PropertyHandlerCore explainPlanNodePropertiesCore) {
        this.planNode = planNode;
        this.core = explainPlanNodePropertiesCore;
    }

    /**
     * Gets the object name.
     *
     * @return the object name
     */
    @Override
    public String getObjectName() {
        return this.planNode.getNodeType();
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    @Override
    public String getHeader() {
        return getUniqueID() + ". " + this.planNode.getNodeType();
    }

    /**
     * Gets the unique ID.
     *
     * @return the unique ID
     */
    @Override
    public String getUniqueID() {
        return "" + planNode.getNodeSequenceNum();
    }

    /**
     * Gets the all properties.
     *
     * @param conn the conn
     * @return the all properties
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public List<IObjectPropertyData> getAllProperties(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException {
        tabNameList = new ArrayList<String>(5);
        nodeProperties = new ArrayList<List<Object[]>>(1);
        tabNameList.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PER_NODE_DETAILS_GENERAL_TAB));

        nodeProperties.add(getGeneralProperty());

        List<IObjectPropertyData> l1 = ConvertToObjectPropertyData.getObjectPropertyDataGeneric(tabNameList,
                nodeProperties);
        List<IObjectPropertyData> l2 = getDNSpecificDetails();
        if (null != l2) {
            l1.addAll(l2);
        }

        return l1;
    }

    private List<IObjectPropertyData> getDNSpecificDetails()
            throws DatabaseOperationException, DatabaseCriticalException {
        Map<String, List<Object>> data = planNode.getNodeSpecificDNProperties();
        if (null != data) {
            List<DNIntraNodeDetailsColumn> colgrp = planNode.getPerDNSpecificColumnGroupingInfo();
            ArrayList<String> title = new ArrayList<String>(1);
            title.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PER_NODE_DETAILS_PERDN_TAB));

            DNIntraNodeDetailsColumn firstColDetails = new DNIntraNodeDetailsColumn();
            firstColDetails.setColCount(1);
            firstColDetails.setGroupColumnName(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_DN_COLUMNGRP));
            firstColDetails.setColnames(new ArrayList<String>(1));
            firstColDetails.getColnames()
                    .add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_DN_COLUMN));
            colgrp.add(0, firstColDetails);

            List<IObjectPropertyData> l1 = ConvertToObjectPropertyData.getObjectPropertyDataGenericGroupedColumn(title,
                    data, colgrp);

            return l1;
        }

        return null;
    }

    private List<Object[]> getGeneralProperty() {
        List<Object[]> props = new ArrayList<Object[]>(5);
        String[] genPropHeader = {MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PER_NODE_COLUMN_PROP),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PER_NODE_COLUMN_VALUE)};
        props.add(genPropHeader);

        props.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_ANALYSISNODE_OUTPUT),
                planNode.getFormatedOutput(",")).getProp());

        props.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_ANALYSISNODE_ANALYSIS),
                planNode.getAnalysis()).getProp());

        double deviation = planNode.getPlanDeviationByRecordCount(planNode.getPlanRecordCount(),
                planNode.getRecordCount());

        props.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_ANALYSISNODE_ROWSOUTPUTDEVIATION),
                MathUtils.roundDoubleValues(deviation, 2)).getProp());

        props = addNodeSpecificDetails(planNode, props);
        return props;
    }

    private List<Object[]> addNodeSpecificDetails(AnalysedPlanNode planNode2, List<Object[]> props) {
        return planNode2.getNodeSpecificProperties(props);

    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {

        return null;
    }

    /**
     * Gets the property core.
     *
     * @return the property core
     */
    @Override
    public PropertyHandlerCore getPropertyCore() {
        return this.core;
    }

    /**
     * Objectproperties.
     *
     * @return the list
     */
    @Override
    public List<IObjectPropertyData> objectproperties() {

        return new ArrayList<IObjectPropertyData>();
    }
}
