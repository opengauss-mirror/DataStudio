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

package com.huawei.mppdbide.explainplan.nodetypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class BasicNode.
 *
 * @since 3.0.0
 */
public abstract class BasicNode implements IStatisticalData {

    /**
     * The node category.
     */
    protected NodeCategoryEnum nodeCategory;
    
    /**
     * The sub plan
     */
    @SerializedName("Subplan Name")
    protected String subPlan = "";
    
    /**
     * The node type
     */
    @SerializedName("Node Type")
    protected String nodeType = "";
    
    /**
     * The parent relationship
     */
    @SerializedName("Parent Relationship")
    protected String parentRelationship = "";
    
    /**
     * The startup cost
     */
    @SerializedName("Startup Cost")
    protected double startupCost;
    
    /**
     * The total cost
     */
    @SerializedName("Total Cost")
    protected double totalCost;
    
    /**
     * The plan rows
     */
    @SerializedName("Plan Rows")
    protected long planRows;
    
    /**
     * The plan width
     */
    @SerializedName("Plan Width")
    protected long planWidth;
    
    /**
     * The actual in detail
     */
    @SerializedName("Actual In Detail")
    protected List<ActualInDetail> actualsInDetail;
    
    /**
     * The output
     */
    @SerializedName("Output")
    protected String[] output;
    
    /**
     * The cpu in detail
     */
    @SerializedName("Cpus In Detail")
    protected List<CpuInDetail> cpuInDetail;
    
    /**
     * The exclusive cycles per rowno DN
     */
    @SerializedName("Exclusive Cycles/Row")
    private String exclusiveCyclesPerRowNoDN;

    /**
     * The exclusive cycles per row.
     */
    protected long exclusiveCyclesPerRow;
    
    /**
     * The exclusive cycles
     */
    @SerializedName("Exclusive Cycles")
    protected long exclusiveCycles;
    
    /**
     * The inclusive cycles
     */
    @SerializedName("Inclusive Cycles")
    protected long inclusiveCycles;
    
    /**
     * The buffers in detail
     */
    @SerializedName("Buffers In Detail")
    protected List<BuffersInDetail> buffersInDetail;
    
    @SerializedName("Shared Hit Blocks")
    private String sharedHitBlocksNoDN;

    private long sharedHitBlocks = 0;

    @SerializedName("Shared Read Blocks")
    private long sharedReadBlocks = 0;

    @SerializedName("Shared Dirtied Blocks")
    private long sharedDirtiedBlocks = 0;

    @SerializedName("Shared Written Blocks")
    private long sharedWrittenBlocks = 0;

    @SerializedName("Local Hit Blocks")
    private long localHitBlocks = 0;

    @SerializedName("Local Read Blocks")
    private long localReadBlocks = 0;

    @SerializedName("Local Dirtied Blocks")
    private long localDirtiedBlocks = 0;

    @SerializedName("Local Written Blocks")
    private long localWrittenBlocks = 0;

    @SerializedName("Temp Read Blocks")
    private long tempReadBlocks = 0;

    @SerializedName("Temp Written Blocks")
    private long tempWrittenBlocks = 0;

    @SerializedName("I/O Read Time")
    private double ioReadTime = 0d;

    @SerializedName("I/O Write Time")
    private double ioWriteTime = 0d;
    
    /**
     * The actal min startup time
     */
    @SerializedName("Actual Min Startup Time")
    protected double actualMinStartupTime;
    
    /**
     * The actual max startup time
     */
    @SerializedName("Actual Max Startup Time")
    protected double actualMaxStartupTime;
    
    /**
     * The actual min total time
     */
    @SerializedName("Actual Min Total Time")
    protected double actualMinTotalTime;
    
    /**
     * The actual max total time
     */
    @SerializedName("Actual Max Total Time")
    protected double actualMaxTotalTime;
    
    /**
     * The actual rows
     */
    @SerializedName("Actual Rows")
    protected long actualRows;
    
    /**
     * The actual total rows
     */
    @SerializedName("Actual Total Rows")
    protected long actualTotalRows;
    
    /**
     * The actual loops
     */
    @SerializedName("Actual Loops")
    protected long actualLoops;
    
    /**
     * The actual startup time
     */
    @SerializedName("Actual Startup Time")
    protected double actualStartupTime = 0d;
    
    /**
     * The actual total time
     */
    @SerializedName("Actual Total Time")
    protected double actualTotalTime = 0d;
    
    /**
     * The Llvm detail
     */
    @SerializedName("Llvm Detail")
    protected List<LLVMDNDetails> llvmDetail;
    
    /**
     * The nodes
     */
    @SerializedName("Node/s")
    protected String nodes;

    /**
     * The cost info for text display.
     */
    protected String costInfoForTextDisplay = null;

    /**
     * Instantiates a new basic node.
     *
     * @param cat the cat
     */
    public BasicNode(NodeCategoryEnum cat) {
        this.nodeCategory = cat;
        actualsInDetail = new ArrayList<ActualInDetail>(5);
        cpuInDetail = new ArrayList<CpuInDetail>(5);
    }

    /**
     * Creates the cost info for text display.
     *
     * @return the string
     */
    private String createCostInfoForTextDisplay() {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("(cost=");
        sb.append(startupCost);
        sb.append("..");
        sb.append(totalCost);
        sb.append(" rows=");
        sb.append(planRows);
        sb.append(" width=");
        sb.append(planWidth);
        sb.append(")");
        return sb.toString();
    }

    /**
     * Creates the analyze info for text display.
     *
     * @return the string
     */
    private String createAnalyzeInfoForTextDisplay() {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("(actual time=");
        if (getActualStartupTime() != 0) {
            sb.append(getActualStartupTime());
        } else {
            sb.append("[");
            sb.append(actualMinStartupTime);
            sb.append(",");
            sb.append(actualMaxStartupTime);
            sb.append("]");
        }
        sb.append("..");
        if (getActualTotalTime() != 0) {
            sb.append(getActualTotalTime());
        } else {
            sb.append("[");
            sb.append(actualMinTotalTime);
            sb.append(",");
            sb.append(actualMaxTotalTime);
            sb.append("]");
        }
        sb.append(" rows=");
        sb.append(getActualRows());
        if (getActualLoopCount() != 0) {
            sb.append(" loops=");
            sb.append(getActualLoopCount());
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Gets the cost info for text display.
     *
     * @param isAnalyze the is analyze
     * @return the cost info for text display
     */
    protected String getCostInfoForTextDisplay(boolean isAnalyze) {
        if (this.costInfoForTextDisplay == null) {
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append(createCostInfoForTextDisplay());
            if (isAnalyze) {
                sb.append(" ");
                sb.append(createAnalyzeInfoForTextDisplay());
            }
            this.costInfoForTextDisplay = sb.toString();
        }
        return this.costInfoForTextDisplay;
    }

    /**
     * Gets the actual max total time.
     *
     * @return the actual max total time
     */
    public double getActualMaxTotalTime() {
        return Math.abs(actualStartupTime) > 0d ? Math.abs(actualStartupTime) : Math.abs(actualMaxTotalTime);
    }

    /**
     * Gets the actual max startup time.
     *
     * @return the actual max startup time
     */
    public double getActualMaxStartupTime() {
        return actualMaxStartupTime;
    }

    /**
     * Gets the node type.
     *
     * @return the node type
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * Gets the plan rows.
     *
     * @return the plan rows
     */
    public long getPlanRows() {
        return planRows;
    }

    /**
     * Gets the plan width.
     *
     * @return the plan width
     */
    public long getPlanWidth() {
        return planWidth;
    }

    /**
     * To string.
     *
     * @return toString
     */
    public String toString() {
        return getNodeType() + EnvirnmentVariableValidator.validateAndGetLineSeperator();
    }

    /**
     * Gets the total cost.
     *
     * @return the total cost
     */
    @Override
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Gets the actual max time taken.
     *
     * @return getActualMaxTimeTaken
     */
    public double getActualMaxTimeTaken() {
        return this.getActualMaxTotalTime();
    }

    /**
     * Gets the actual total time.
     *
     * @return the actual total time
     */
    @Override
    public double getActualTotalTime() {
        if (0 == actualsInDetail.size()) {
            return this.actualTotalTime;
        } else {
            double count = 0;

            for (ActualInDetail a : actualsInDetail) {
                if (Math.abs(count) < Math.abs(a.getActualTotalTime())) {
                    count = a.getActualTotalTime();
                }
            }

            return count;
        }
    }

    /**
     * Gets the actual rows.
     *
     * @return the actual rows
     */
    @Override
    public long getActualRows() {
        if (0 == actualsInDetail.size()) {
            return (0 == actualRows) ? this.actualTotalRows : this.actualRows;
        } else {
            long count = 0;

            for (ActualInDetail a : actualsInDetail) {
                count += a.getActualRows();
            }

            return count;
        }
    }

    /**
     * Gets the actual loop count.
     *
     * @return the actual loop count
     */
    public long getActualLoopCount() {
        if (0 == actualsInDetail.size()) {
            return this.actualLoops;
        } else {
            long count = 0;

            for (ActualInDetail a : actualsInDetail) {
                count += a.getActualLoops();
            }

            return count;
        }
    }

    /**
     * Gets the actual startup time.
     *
     * @return the actual startup time
     */
    public double getActualStartupTime() {
        if (0 == actualsInDetail.size()) {
            return this.actualStartupTime;
        } else {
            double count = 0;

            for (ActualInDetail a : actualsInDetail) {
                count += a.getActualStartupTime();
            }

            return count;
        }
    }

    /**
     * Checks for DN data.
     *
     * @return true, if successful
     */
    public boolean hasDNData() {

        return actualsInDetail.size() != 0;

    }

    /**
     * Gets the parent relationship.
     *
     * @return the parent relationship
     */
    public String getParentRelationship() {
        return "Parent Relationship : " + parentRelationship;
    }

    /**
     * Gets the node specific properties.
     *
     * @return getNodeSpecificProperties
     */
    public List<String[]> getNodeSpecificProperties() {
        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_PLANWIDTH), planWidth)
                        .getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_PLANROWS), getPlanRows())
                        .getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_ACTUALOUTROWS),
                getActualRows()).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_ACTUALSTARTUPTIME),
                getActualStartupTime()).getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_ACTUALTOTALTIME),
                getActualTotalTime()).getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_ACTUALLOOPS),
                getActualLoopCount()).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_STARTUPCOST), startupCost)
                        .getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_TOTALCOST), totalCost)
                        .getProp());

        String nodeNameFirstPart = nodeType.split(" ")[0];
        if ("Vector".equalsIgnoreCase(nodeNameFirstPart)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_ISVECTORIZED), "Yes")
                            .getProp());
        } else {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_ISVECTORIZED), "No")
                            .getProp());
        }

        if (!this.hasDNData()) {
            fillnoDNBufferDetail(arList);
            fillnoDNCPUDetail(arList);
        }

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_NODETYPE), nodeType)
                        .getProp());
        if (!"".equals(parentRelationship)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_PARENTRELATIONSHIP),
                    parentRelationship).getProp());
        }

        return arList;
    }

    private void fillnoDNCPUDetail(ArrayList<String[]> arList) {
        if (null == exclusiveCyclesPerRowNoDN) {
            return;
        }

        try {
            exclusiveCyclesPerRow = Long.parseLong(exclusiveCyclesPerRowNoDN);

            arList.add(new ServerProperty(
                    MessageConfigLoader
                            .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_CPU_EXCLUSIVECYCLESROW),
                    exclusiveCyclesPerRow).getProp());

            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_CPU_EXCLUSIVECYCLES),
                    exclusiveCycles).getProp());

            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_CPU_INCLUSIVECYCLES),
                    inclusiveCycles).getProp());
        } catch (NumberFormatException numberFormatException) {
            MPPDBIDELoggerUtility.error("Error while parsing the string argument as a signed decimal ", numberFormatException);
        }
    }

    private void fillnoDNBufferDetail(ArrayList<String[]> arList) {
        // shall fill this info only if Info is present.
        if (null == sharedHitBlocksNoDN) {
            return;
        }

        this.sharedHitBlocks = Long.parseLong(sharedHitBlocksNoDN);

        fillnoDNBufferDetail1(arList);

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_TEMPREADBLOCKS),
                tempReadBlocks).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_TEMPWRITTENBLOCKS),
                tempWrittenBlocks).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_IOREADTIME),
                ioReadTime).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_IOWRITETIME),
                ioWriteTime).getProp());
    }

    private void fillnoDNBufferDetail1(ArrayList<String[]> arList) {
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_SHAREDHITBLOCKS),
                sharedHitBlocks).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_SHAREDREADBLOCKS),
                sharedReadBlocks).getProp());

        arList.add(
                new ServerProperty(
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_SHAREDDIRTIEDBLOCKS),
                        sharedDirtiedBlocks).getProp());

        arList.add(
                new ServerProperty(
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_SHAREDWRITTENBLOCKS),
                        sharedWrittenBlocks).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_LOCALHITBLOCKS),
                localHitBlocks).getProp());

        arList.add(
                new ServerProperty(
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_LOCALREADHITBLOCKS),
                        localReadBlocks).getProp());

        arList.add(
                new ServerProperty(
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_LOCALDIRTIEDBLOCKS),
                        localDirtiedBlocks).getProp());

        arList.add(
                new ServerProperty(
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_BUFFER_LOCALWRITTENBLOCKS),
                        localWrittenBlocks).getProp());
    }

    /**
     * Gets the per DN specific details.
     *
     * @param inputMap the input map
     * @return the per DN specific details
     */
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        getActualsDetails(inputMap);
        getCpuDetails(inputMap);

        if (null != llvmDetail) {
            getLlvmDetails(inputMap);
        }

        if (null != buffersInDetail) {
            getBufferInDetails(inputMap);
        }

        return inputMap;
    }

    /**
     * Gets the per DN specific column grouping info.
     *
     * @param colGroup the col group
     * @return the per DN specific column grouping info
     */
    public List<DNIntraNodeDetailsColumn> getPerDNSpecificColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup) {
        colGroup.add(ActualInDetailUtility.fillColumnPropertyHeader());
        colGroup.add(CpuInDetail.fillColumnPropertyHeader());

        if (null != llvmDetail) {
            colGroup.add(LLVMDNDetails.fillColumnPropertyHeader());
        }

        if (null != buffersInDetail) {
            colGroup.add(BuffersInDetail.fillColumnPropertyHeader());
        }

        return colGroup;
    }

    /**
     * Gets the per DN specific details.
     *
     * @param inputMap the input map
     * @param detailsNeeded the details needed
     * @return the per DN specific details
     */
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap,
            ArrayList<INDETAILSCATEGORY> detailsNeeded) {
        if (detailsNeeded.contains(INDETAILSCATEGORY.ACTUALS_IN_DETAIL)) {
            getActualsDetails(inputMap);
        }

        if (detailsNeeded.contains(INDETAILSCATEGORY.CPU_IN_DETAIL)) {
            getCpuDetails(inputMap);
        }

        if (detailsNeeded.contains(INDETAILSCATEGORY.LLVM_DETAIL) && null != llvmDetail) {
            getLlvmDetails(inputMap);
        }

        if (detailsNeeded.contains(INDETAILSCATEGORY.BUFFERS_IN_DETAIL) && null != buffersInDetail) {
            getBufferInDetails(inputMap);
        }

        return inputMap;
    }

    private void getCpuDetails(Map<String, List<Object>> inputMap) {
        for (CpuInDetail cnt : this.cpuInDetail) {
            if (!inputMap.containsKey(cnt.getDnName())) {
                List<Object> propsValues = cnt.propertyDetails();
                inputMap.put(cnt.getDnName(), propsValues);
            } else {
                inputMap.get(cnt.getDnName()).addAll(cnt.propertyDetails());
            }
        }

    }

    private void getActualsDetails(Map<String, List<Object>> inputMap) {
        for (ActualInDetail entry : actualsInDetail) {
            if (!inputMap.containsKey(entry.getDnName())) {
                List<Object> propsValues = propertyDetails(entry);
                inputMap.put(entry.getDnName(), propsValues);
            } else {
                inputMap.get(entry.getDnName()).addAll(propertyDetails(entry));
            }
        }

    }

    private List<Object> propertyDetails(ActualInDetail arList) {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);

        colInfo.add(arList.getActualStartupTime());
        colInfo.add(arList.getActualTotalTime());
        colInfo.add(arList.getActualRows());
        colInfo.add(arList.getActualLoops());

        return colInfo;
    }

    private void getBufferInDetails(Map<String, List<Object>> inputMap) {
        for (BuffersInDetail entry : buffersInDetail) {
            if (!inputMap.containsKey(entry.getDnName())) {
                List<Object> propsValues = entry.propertyDetails();
                inputMap.put(entry.getDnName(), propsValues);
            } else {
                inputMap.get(entry.getDnName()).addAll(entry.propertyDetails());
            }
        }

    }

    private void getLlvmDetails(Map<String, List<Object>> inputMap) {
        for (LLVMDNDetails entry : llvmDetail) {
            if (!inputMap.containsKey(entry.getDnName())) {
                List<Object> propsValues = entry.propertyDetails();
                inputMap.put(entry.getDnName(), propsValues);
            } else {
                inputMap.get(entry.getDnName()).addAll(entry.propertyDetails());
            }
        }

    }

    /**
     * Gets the per DN specific column grouping info.
     *
     * @param colGroup the col group
     * @param detailsNeeded the details needed
     * @return the per DN specific column grouping info
     */
    public List<DNIntraNodeDetailsColumn> getPerDNSpecificColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup,
            ArrayList<INDETAILSCATEGORY> detailsNeeded) {
        if (detailsNeeded.contains(INDETAILSCATEGORY.ACTUALS_IN_DETAIL)) {
            colGroup.add(ActualInDetailUtility.fillColumnPropertyHeader());
        }

        if (detailsNeeded.contains(INDETAILSCATEGORY.CPU_IN_DETAIL)) {
            colGroup.add(CpuInDetail.fillColumnPropertyHeader());
        }

        if (detailsNeeded.contains(INDETAILSCATEGORY.LLVM_DETAIL) && null != llvmDetail) {
            colGroup.add(LLVMDNDetails.fillColumnPropertyHeader());
        }

        if (detailsNeeded.contains(INDETAILSCATEGORY.BUFFERS_IN_DETAIL) && null != buffersInDetail) {
            colGroup.add(BuffersInDetail.fillColumnPropertyHeader());
        }

        return colGroup;
    }

    /**
     * Gets the DN involved.
     *
     * @param dnsInvolved the dns involved
     * @return the DN involved
     */
    public ArrayList<String> getDNInvolved(ArrayList<String> dnsInvolved) {

        for (ActualInDetail elmnt : actualsInDetail) {
            dnsInvolved.add(elmnt.getDnName());
        }

        return dnsInvolved;
    }

    /**
     * Adds the node DN plan view.
     *
     * @param dnViewofExplainPlan the dn viewof explain plan
     * @param idx the idx
     */
    public void addNodeDNPlanView(Map<String, Object[]> dnViewofExplainPlan, int idx) {
        String dnName = null;
        Object[] nodesArr = null;
        for (Entry<String, Object[]> entry : dnViewofExplainPlan.entrySet()) {
            dnName = entry.getKey();
            nodesArr = entry.getValue();
            nodesArr[idx] = null;

            for (ActualInDetail elemt : actualsInDetail) {
                if (dnName.equals(elemt.getDnName())) {
                    nodesArr[idx] = this;
                    break;
                }
            }
        }

    }

    /**
     * Gets the node name.
     *
     * @return the node name
     */
    public String getNodeName() {
        return getNodeType();
    }

    /**
     * Gets the node category name.
     *
     * @return the node category name
     */
    public String getNodeCategoryName() {
        return this.nodeCategory.getCategoryName();
    }

    /**
     * Gets the actuals by DN name.
     *
     * @param dnName the dn name
     * @return the actuals by DN name
     */
    public ActualInDetail getActualsByDNName(String dnName) {
        for (ActualInDetail elmnt : actualsInDetail) {
            if (dnName.equals(elmnt.getDnName())) {
                return elmnt;
            }
        }

        return null;
    }

    /**
     * Gets the additional info.
     *
     * @param isAnalyze the is analyze
     * @return the additional info
     */
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        List<String> arList = new ArrayList<String>(4);
        if (nodes != null) {
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Node/s: ");
            sb.append(nodes);
            arList.add(sb.toString());
        }
        if (isAnalyze) {
            if (buffersInDetail != null) {
                StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                sb.append("Buffers: ");
                if (sharedHitBlocks + sharedReadBlocks != 0) {
                    sb.append("shared hit=");
                    sb.append(sharedHitBlocks);
                    sb.append(" read=");
                    sb.append(sharedReadBlocks);
                    arList.add(sb.toString());
                }
                if (tempReadBlocks + tempWrittenBlocks != 0) {
                    sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                    sb.append("temp read=");
                    sb.append(tempReadBlocks);
                    sb.append(" written=");
                    sb.append(tempWrittenBlocks);
                    arList.add(sb.toString());
                }
            }
        }
        return arList;
    }

    /**
     * Gets the startup cost.
     *
     * @return the startup cost
     */
    public double getStartupCost() {
        return startupCost;
    }

    /**
     * Gets the subplan name.
     *
     * @return the subplan name
     */
    public String getSubplanName() {
        return this.subPlan;
    }

}
