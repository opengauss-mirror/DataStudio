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

package org.opengauss.mppdbide.presentation.visualexplainplan;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.explainplan.service.AnalysedPlanNode;
import org.opengauss.mppdbide.presentation.objectproperties.ConvertToObjectPropertyData;
import org.opengauss.mppdbide.presentation.objectproperties.IObjectPropertyData;
import org.opengauss.mppdbide.presentation.objectproperties.IServerObjectProperties;
import org.opengauss.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MathUtils;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanOverAllProperties.
 *
 * @since 3.0.0
 */
public class ExplainPlanOverAllProperties implements IServerObjectProperties, IPropertyDetail {

    private ExplainPlanPresentation presentation;
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
     * Instantiates a new explain plan over all properties.
     *
     * @param presentation the presentation
     * @param explainPlanOverAllPlanPropertiesCore the explain plan over all plan properties core
     */
    public ExplainPlanOverAllProperties(ExplainPlanPresentation presentation,
            PropertyHandlerCore explainPlanOverAllPlanPropertiesCore) {
        this.presentation = presentation;
        this.core = explainPlanOverAllPlanPropertiesCore;
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

    /**
     * Gets the object name.
     *
     * @return the object name
     */
    @Override
    public String getObjectName() {
        return "ExplainPlanOverAllProperties.getObjectName";
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    @Override
    public String getHeader() {
        return "ExplainPlanOverAllProperties.getHeader";
    }

    /**
     * Gets the unique ID.
     *
     * @return the unique ID
     */
    @Override
    public String getUniqueID() {
        return "This is unique for Now";
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
        tabNameList.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_GENERAL_TAB));

        nodeProperties.add(getGeneralProperty());

        tabNameList.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_ALLNODES_TAB));

        nodeProperties.add(getAllNodeDetails());
        tabNameList.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROPERTIES_EXEC_PLAN_TITLE));
        nodeProperties.add(getExecPlanDetails());
        return ConvertToObjectPropertyData.getObjectPropertyDataGeneric(tabNameList, nodeProperties);
    }

    private List<Object[]> getAllNodeDetails() {
        List<Object[]> props = new ArrayList<Object[]>(5);
        String[] genPropHeader = {
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_NODENAME),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_ANALYSIS),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_ROWSOUTPUT),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_ROWSOUTPUTDEVIATION),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_EXECUTIONTIME),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_CONTRIBUTION),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_SELFCOST),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_TOTALCOST)};
        props.add(genPropHeader);

        AnalysedPlanNode rootNode = this.presentation.getAnalysedPlanOutput().getAnalysedPlanNode();

        getAllNodesInfo(props, rootNode, 1);

        return props;
    }

    private List<Object[]> getExecPlanDetails() {
        List<Object[]> props = new ArrayList<Object[]>(5);
        String[] genPropHeader = {
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_COLUMN_NODENAME),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_EXEC_PLAN_COLUMN_ENTITYNAME),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_EXEC_PLAN_COLUMN_COST),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_EXEC_PLAN_COLUMN_ROWS),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_EXEC_PLAN_COLUMN_LOOPS),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_EXEC_PLAN_COLUMN_WIDTH),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_EXEC_PLAN_COLUMN_ACTUAL_ROWS),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_EXEC_PLAN_COLUMN_ACTUAL_TIME)};
        props.add(genPropHeader);

        AnalysedPlanNode rootNode = this.presentation.getAnalysedPlanOutput().getAnalysedPlanNode();

        getExecPlanInfo(props, rootNode, 1);

        return props;
    }

    private int getAllNodesInfo(List<Object[]> props, AnalysedPlanNode rootNode, int sNoParam) {
        int sno = sNoParam;
        props.add(detailsInfo(rootNode, sno++));

        for (AnalysedPlanNode node : rootNode.getChildNodeStats()) {
            sno = getAllNodesInfo(props, node, sno);
        }

        return sno;
    }

    private int getExecPlanInfo(List<Object[]> props, AnalysedPlanNode rootNode, int sNoParam) {
        int sno = sNoParam;
        props.add(execPlanDetailsInfo(rootNode, sno++, props));

        for (AnalysedPlanNode node : rootNode.getChildNodeStats()) {
            sno = getExecPlanInfo(props, node, sno);
        }

        return sno;
    }

    private Object[] execPlanDetailsInfo(AnalysedPlanNode rootNode, int sno, List<Object[]> props) {

        Object[] colInfo = new Object[8];
        int jindex = 0;
        colInfo[jindex++] = rootNode.getNodeUniqueNameWithType(); // node name
        colInfo[jindex++] = rootNode.getChild().getEntityName();
        colInfo[jindex++] = rootNode.getTotalCost();
        colInfo[jindex++] = rootNode.getChild().getPlanRows();
        colInfo[jindex++] = rootNode.getChild().getActualLoopCount();
        colInfo[jindex++] = rootNode.getChild().getPlanWidth();
        colInfo[jindex++] = rootNode.getChild().getActualRows();
        colInfo[jindex++] = rootNode.getChild().getActualTotalTime();
        return colInfo;

    }

    private Object[] detailsInfo(AnalysedPlanNode rootNode, int sno) {
        double deviation = rootNode.getPlanDeviationByRecordCount(rootNode.getPlanRecordCount(),
                rootNode.getRecordCount());

        Object[] colInfo = new Object[8];
        int index = 0;
        // node name
        colInfo[index++] = rootNode.getNodeUniqueNameWithType();
        // Analysis
        colInfo[index++] = rootNode.getAnalysis();
        // RowsOutput
        colInfo[index++] = rootNode.getRecordCount();
        // RowsOutput Deviation
        colInfo[index++] = MathUtils.roundDoubleValues(deviation, 2);
        // timeTaken
        colInfo[index++] = MathUtils.roundDoubleValues(rootNode.getSelfTotalTime(), 2);
        // Contribution
        colInfo[index++] = MathUtils.roundDoubleValues(rootNode.getSelfTimeContributionInOverAllPlan(), 2);
        // Self Cost
        colInfo[index++] = MathUtils.roundDoubleValues(rootNode.getSelfCost(), 2);
        // Total Cost
        colInfo[index++] = MathUtils.roundDoubleValues(rootNode.getTotalCost(), 2);

        return colInfo;

    }

    private List<Object[]> getGeneralProperty() {
        List<Object[]> props = new ArrayList<Object[]>(5);
        String[] genPropHeader = {
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_GENERAL_COLUMN_PROP),
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_GENERAL_COLUMN_VALUE)};
        props.add(genPropHeader);
        props.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_OVERALL_DETAILS_ROOTNODE_TOTALRUNTIME),
                this.presentation.getRootNode().getTotalRuntime()).getProp());
        return props;
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
}
