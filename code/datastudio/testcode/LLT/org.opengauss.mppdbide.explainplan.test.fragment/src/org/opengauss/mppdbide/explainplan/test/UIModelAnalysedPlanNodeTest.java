package com.hauwei.mppdbide.explainplan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hauwei.mppdbide.explainplan.mock.CommonLLTUtils;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.explainplan.service.ExplainPlanAnlysisService;
import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExplainPlanNodePropertiesCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.IUIModelAnalysedPlanNodeToGraphModelConvertor;
import org.opengauss.mppdbide.presentation.visualexplainplan.Relationship;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelConverter;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

public class UIModelAnalysedPlanNodeTest
{
    @Test
    public void test_stream_gather_node()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils
                .json_to_node_test(CommonLLTUtils.STREAMING_GATHER_NODE_SAMPLE_JSON);

        OperationalNode child = node.getAnalysedPlanNode().getChild();

        assertTrue(node.isSlowest());
        assertTrue(node.isHeaviest());
        assertTrue(node.isCostliest());

        assertEquals(node.getSelfCost(), 123.37, 0);
        assertEquals(node.getSelfTime(), 34.134, 0);

        List<UIModelAnalysedPlanNode> flattenList = new ArrayList<UIModelAnalysedPlanNode>(1);
        List<Relationship> flattenedExplainPlanEdges = new ArrayList<Relationship>(1);
        node.flatten(flattenList, flattenedExplainPlanEdges);

        assertEquals(flattenList.size(), 1);
        assertEquals(flattenedExplainPlanEdges.size(), 0);

        String tooltip = node.getToopTipText();
        assertTrue(tooltip.equals("Node: Streaming (type: GATHER)"));

        ExplainPlanNodePropertiesCore core = (ExplainPlanNodePropertiesCore) node.getAdapter(PropertyHandlerCore.class);
        IWindowDetail windowDetails = core.getWindowDetails();
        assertEquals("1. Streaming (type: GATHER)", windowDetails.getTitle());
        assertEquals("1", windowDetails.getUniqueID());
        assertNull(windowDetails.getIcon());
        assertEquals("1. Streaming (type: GATHER) - DN Details", windowDetails.getShortTitle());
        assertTrue(windowDetails.isCloseable());
    }
    
    @Test
    public void test_AnalysedPlanedNode_methods_01()
    {
        String jsonOutput="[{\"Plan\": {\"Node Type\": \"Seq Scan\",\"Relation Name\": \"pg_authid\",\"Schema\": \"pg_catalog\",\"Alias\": \"ad\",\"Startup Cost\": 0.00,\"Total Cost\": 3.03,\"Plan Rows\": 3,\"Plan Width\": 68,\"Actual Startup Time\": 0.014,\"Actual Total Time\": 0.031,\"Actual Rows\": 7,\"Actual Loops\": 1,\"Output\": [\"ad.rolname\", \"ad.oid\"],\"Shared Hit Blocks\": 3,\"Shared Read Blocks\": 0,\"Shared Dirtied Blocks\": 0,\"Shared Written Blocks\": 0,\"Local Hit Blocks\": 0,\"Local Read Blocks\": 0,\"Local Dirtied Blocks\": 0,\"Local Written Blocks\": 0,\"Temp Read Blocks\": 0,\"Temp Written Blocks\": 0,\"IO Read Time\": 0.000,\"IO Write Time\": 0.000,\"Exclusive Cycles Per Row\": 12215,\"Exclusive Cycles\": 85508,\"Inclusive Cycles\": 85508},\"Triggers\": [],\"Total Runtime\": 4.825}]";
        ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonOutput);
        UIModelAnalysedPlanNode node = null;
        String jsonClone=jsonOutput;
        ExplainPlanAnlysisService planAnalysis2 = new ExplainPlanAnlysisService(jsonClone);
        UIModelAnalysedPlanNode node2 = null;
        try
        {
            node = UIModelConverter.covertToUIModel(planAnalysis.doAnalysis());
            node2 = UIModelConverter.covertToUIModel(planAnalysis2.doAnalysis());
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
        }
        String opNode = node.getAnalysedPlanNode().toString();  
        assertEquals("Seq Scan { heaviest = true;  slowest = true;  costliest = true;  cost_contribution: 0.29%}" + MPPDBIDEConstants.LINE_SEPARATOR, opNode);
        
        assertTrue(node.getAnalysedPlanNode().equals(node2.getAnalysedPlanNode()));
        assertFalse(node.getAnalysedPlanNode().equals(null));
        assertFalse(node.getAnalysedPlanNode().equals(opNode));
    }
    
    @Test
    public void test_AnalysedPlanedNode_methods_02()
    {
        UIModelAnalysedPlanNode node = CommonLLTUtils.json_to_node_test(CommonLLTUtils.STREAMING_GATHER_NODE_SAMPLE_JSON);
        node.getAnalysedPlanNode().setHeaviest(true);
        node.getAnalysedPlanNode().setSelfCost(0);
        assertTrue(node.getAnalysedPlanNode().isHeaviest());
        node.getAnalysedPlanNode().setTotalTimeContributionPercentage(50);
        assertEquals(50,node.getAnalysedPlanNode().getTotalTimeContributionPercentage(),0);
        assertNull(node.getAnalysedPlanNode().getParent());
        assertNotNull(node.getAnalysedPlanNode().getOutput());
        assertNotNull(node.getAnalysedPlanNode().getNodeType());
        assertNotNull(node.getAnalysedPlanNode().getParentRelationship());
        assertNotNull(node.getAnalysedPlanNode().getSourceRelationship());
        assertNotNull(node.getAnalysedPlanNode().getTargetRelationship());
        assertNull(node.getAnalysedPlanNode().getNodeSpecific());
        assertNull(node.getAnalysedPlanNode().getNodeSpecificDNProperties());
        assertEquals(132277796,node.getAnalysedPlanNode().hashCode());
        assertEquals("HEAVIEST, COSTLIEST, SLOWEST",node.getAnalysedPlanNode().getAnalysis());
    }
 
    @Test
    public void test_AnalysedPlanedNode_methods_test()
    {
        try {
            String jsonString = "[{\"Plan\": {\"Node Type\": \"Streaming (type: GATHER)\",\"Startup Cost\": 74.28,"
                    + "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
                    + "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,\"Output\": [\"comp.name\", "
                    + "\"cindex.idx_col\", \"ng.dob\", \"part.part_range\", \"cpart.part_range\"],\"Nodes\": "
                    + "\"All datanodes\",\"Shared Hit Blocks\": 1,\"Shared Read Blocks\": 2,\"Shared Dirtied Blocks\": 3,"
                    + "\"Shared Written Blocks\": 4,\"Local Hit Blocks\": 5,\"Local Read Blocks\": 6,\"Local Dirtied Blocks\": 7,"
                    + "\"Local Written Blocks\": 8,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
                    + "\"IO Write Time\": 0.000}}]";
            ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonString);
            UIModelAnalysedPlanNode node = null;

            node = UIModelConverter.covertToUIModel(planAnalysis.doAnalysis());
            Relationship relationShip = new Relationship(node, node);
            relationShip.getParentNode();
            relationShip.setParentNode(node);
            relationShip.setChildNode(node);
            relationShip.getChildNode();
            relationShip.getRecordCount();
            List<UIModelAnalysedPlanNode> flattenedExplainPlan = new ArrayList<UIModelAnalysedPlanNode>(5);
            flattenedExplainPlan.add(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
