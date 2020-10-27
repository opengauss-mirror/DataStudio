/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.graph.Graph;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.explainplan.nodetypes.RootPlanNode;
import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;
import com.huawei.mppdbide.explainplan.service.ExplainPlanAnlysisService;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.MaxLineBufferedReader;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.messaging.MessageType;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanPresentation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExplainPlanPresentation {
    private static final double EXPALIN_PLAN_DUMMY_MAX_SIZE_IN_MB = 1;
    
    private DBConnection conn = null;
    private String allQuery;
    private String query;
    private String queryWithExplainPretext;
    private MessageQueue msgQ;
    private static final String PRE_TEXT_DETAILED = "explain (analyze, verbose, format json, costs true, cpu true, "
            + "buffers true, timing true) ";
    private UIModelAnalysedPlanNode analysedPlanOutput;
    private List<UIModelAnalysedPlanNode> flattenedExplainPlan;
    private List<Relationship> flattenedExplainPlanEdges;
    private Map<String, Object[]> dnViewofExplainPlan;
    private RootPlanNode planRootNode;
    private Database database;
    

    /**
     * Instantiates a new explain plan presentation.
     *
     * @param query2 the query 2
     * @param queryArray the query array
     * @param messageQueue the message queue
     * @param conn2 the conn 2
     * @param db the db
     */
    public ExplainPlanPresentation(String allQuery, String query, MessageQueue messageQueue, DBConnection conn2,
            Database db) {
        this.allQuery = allQuery;
        this.database = db;
        this.conn = conn2;
        this.msgQ = messageQueue;
        this.query = query;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return this.database;
    }

    /**
     * Do explain plan analysis.
     *
     * @param executionPlanFeatureCrossCheck the execution plan feature cross check
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void doExplainPlanAnalysis(boolean executionPlanFeatureCrossCheck) throws MPPDBIDEException {
        dnLevelExplainPlanQueryArray(executionPlanFeatureCrossCheck);

        doFlattenPlan();
    }

    private void consoleLogExecutionFailure(MPPDBIDEException exception) {
        String message = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED_ITEM,
                MPPDBIDEConstants.LINE_SEPARATOR + " Plan Query:" + this.queryWithExplainPretext
                        + MPPDBIDEConstants.LINE_SEPARATOR,
                exception.getErrorCode(),
                exception.getServerMessage() == null ? exception.getDBErrorMessage() : exception.getServerMessage());
        msgQ.push(new Message(MessageType.ERROR, message));
        MPPDBIDELoggerUtility.error("ExplainPlan: Failed to execute query", exception);
    }

    private void resetAutoCommit(boolean isAutoCommitChanged) {
        if (isAutoCommitChanged) {
            conn.rollback();
            try {
                conn.getConnection().setAutoCommit(true);
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Unable to reset Autocommit state", exception);
            }
        }
    }

    private void dnLevelExplainPlanQueryArray(boolean executionPlanFeatureCrossCheck) throws MPPDBIDEException {
        String jsonOutput = "";
        // testing flow
        if (executionPlanFeatureCrossCheck) {
            jsonOutput = getDummyJsonFromFile();
            if (null == jsonOutput) {
                MPPDBIDELoggerUtility.error("plan read error");
                return;
            }
        }
        // normal flow
        else {
            IQueryResult result = null;
            boolean isAutoCommitChanged = false;

            /*
             * 1. Get a connection from the database. DO NOT FREE CONNECTION IN
             * THIS METHOD. CALLER WILL HANDLE IT!
             */

            try {
                if (conn.getConnection().getAutoCommit()) {
                    conn.getConnection().setAutoCommit(false);
                    isAutoCommitChanged = true;
                }
            } catch (SQLException exception) {
                msgQ.push(new Message(MessageType.ERROR, exception.getMessage()));
                MPPDBIDELoggerUtility.error("ExplainPlanPresentation: Explain query failed.", exception);
                return;
            }
            result = executeExplainQuery(isAutoCommitChanged, query);

            jsonOutput = getJsonPlan(result, isAutoCommitChanged);
        }

        ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonOutput);
        this.setAnalysedPlanOutput(UIModelConverter.covertToUIModel(planAnalysis.doAnalysis()));
        this.planRootNode = planAnalysis.getRootPlan();
    }

    private String getDummyJsonFromFile() {
        MaxLineBufferedReader reader = null;
        InputStreamReader inputStream = null;
        FileInputStream file = null;
        StringBuilder stringBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        try {
            File jsonFile = new File(MPPDBIDEConstants.JSON_PLAN_DUMP_FILE);
            long fileSize = Files.size(jsonFile.toPath()) / 1024 * 1024;
            if (fileSize > EXPALIN_PLAN_DUMMY_MAX_SIZE_IN_MB) {
                MPPDBIDELoggerUtility.error("file read error");
                return null;
            }
            file = new FileInputStream(MPPDBIDEConstants.JSON_PLAN_DUMP_FILE);
            inputStream = new InputStreamReader(file, StandardCharsets.UTF_8); // test-flow
            reader = new MaxLineBufferedReader(inputStream);
            String line = null;
            while ((line = reader.readMaxLenLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(MPPDBIDEConstants.LINE_SEPARATOR);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } catch (FileNotFoundException exception) {
            MPPDBIDELoggerUtility.error("Json File not found", exception);
            return null;
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("file read error", exception);
            return null;
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("file read error", exception);
            return null;
        } finally {
            closeResources(reader, inputStream, file);
        }

        return stringBuilder.toString();
    }

    private void closeResources(MaxLineBufferedReader reader, InputStreamReader inputStream, FileInputStream file) {
        File jsonFile = new File(MPPDBIDEConstants.JSON_PLAN_DUMP_FILE);
        try {
            Files.delete(jsonFile.toPath());
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("error while deleting old file", exception);
        }
        try {
            if (null != reader) {
                reader.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != file) {
                file.close();
            }
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("exception while closing resources", exception);
        }
    }

    private String getJsonPlan(IQueryResult result, boolean isAutoCommitChanged) throws DatabaseOperationException {
        ResultSet rs = null;
        String jsonOutput = "";

        if (result != null) {
            rs = result.getResultsSet();
        }
        try {
            if (rs != null && rs.next()) {
                jsonOutput = rs.getString(1);
            }
        } catch (SQLException exception) {
            msgQ.push(new Message(MessageType.ERROR, exception.getMessage()));
            MPPDBIDELoggerUtility.error("ExplainPlanPresentation: explain plan query failed.", exception);
            throw new DatabaseOperationException(IMessagesConstants.VIS_EXPLAIN_JSON_PARSING_FAILED);
        } catch (Exception exception) {
            msgQ.push(new Message(MessageType.ERROR, exception.getMessage()));
            MPPDBIDELoggerUtility.error("ExplainPlanPresentation: explain plan query failed.", exception);
            throw new DatabaseOperationException(IMessagesConstants.VIS_EXPLAIN_JSON_PARSING_FAILED);
        } finally {
            closeResultSet(rs);
            resetAutoCommit(isAutoCommitChanged);
        }
        return jsonOutput;
    }

    private IQueryResult executeExplainQuery(boolean isAutoCommitChanged, String query)
            throws DatabaseOperationException, DatabaseCriticalException {
        /* 3. Execute the explain query after prepending with explain text */
        IQueryResult result = null;
        String qry = query;
        StringBuilder sb = new StringBuilder(ExplainPlanPresentation.PRE_TEXT_DETAILED);
        sb.append(qry);

        this.queryWithExplainPretext = sb.toString();

        try {
            result = DatabaseUtils.executeOnSqlTerminal(this.queryWithExplainPretext, 10, conn, msgQ);
        } catch (DatabaseOperationException e) {
            consoleLogExecutionFailure(e);
            resetAutoCommit(isAutoCommitChanged);
            throw e;
        } catch (DatabaseCriticalException e) {
            consoleLogExecutionFailure(e);
            resetAutoCommit(isAutoCommitChanged);
            throw e;
        }
        return result;
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("ADAPTER: resultset close returned exception", exception);
        }
    }

    private void doFlattenPlan() {
        flattenedExplainPlan = new ArrayList<UIModelAnalysedPlanNode>(5);
        flattenedExplainPlanEdges = new ArrayList<Relationship>(5);
        analysedPlanOutput.flatten(flattenedExplainPlan, flattenedExplainPlanEdges);
    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getAllQuery() {
        return this.allQuery;
    }

    /**
     * Gets the analysed plan output.
     *
     * @return the analysed plan output
     */
    public UIModelAnalysedPlanNode getAnalysedPlanOutput() {
        return analysedPlanOutput;
    }

    /**
     * Sets the analysed plan output.
     *
     * @param analysedPlanOutput the new analysed plan output
     */
    public void setAnalysedPlanOutput(UIModelAnalysedPlanNode analysedPlanOutput) {
        this.analysedPlanOutput = analysedPlanOutput;
    }

    /**
     * Gets the plan node names.
     *
     * @return the plan node names
     */
    public List<String> getPlanNodeNames() {
        ArrayList<String> names = new ArrayList<String>(flattenedExplainPlan.size());
        AnalysedPlanNode model = null;
        for (UIModelAnalysedPlanNode n : this.flattenedExplainPlan) {
            model = n.getAnalysedPlanNode();
            names.add(model.getNodeUniqueName());
        }

        return names;
    }

    /**
     * Gets the all DN viewof plan.
     *
     * @return the all DN viewof plan
     */
    public Map<String, Object[]> getAllDNViewofPlan() {
        if (null == this.dnViewofExplainPlan) {
            this.dnViewofExplainPlan = new HashMap<String, Object[]>(1);

            this.getAllDNNamesInvolved();

            this.buildPlanViewPerDN();

        }

        return this.dnViewofExplainPlan;
    }

    private void buildPlanViewPerDN() {
        int idx = 0;
        for (UIModelAnalysedPlanNode n : this.flattenedExplainPlan) {
            AnalysedPlanNode model = n.getAnalysedPlanNode();
            addNodeDNPlanView(model, idx);
            idx++;
        }

    }

    private void addNodeDNPlanView(AnalysedPlanNode model, int idx) {
        model.addNodeDNPlanView(this.dnViewofExplainPlan, idx);

    }

    private void getAllDNNamesInvolved() {
        AnalysedPlanNode model = null;
        for (UIModelAnalysedPlanNode n : this.flattenedExplainPlan) {
            model = n.getAnalysedPlanNode();
            addDNDataToMap(model);
        }

    }

    private void addDNDataToMap(AnalysedPlanNode model) {
        ArrayList<String> dnsInvolved = new ArrayList<String>(5);
        dnsInvolved = model.getDNInvolved(dnsInvolved);

        for (String dnName : dnsInvolved) {
            this.dnViewofExplainPlan.put(dnName, new Object[flattenedExplainPlan.size()]);
        }

    }

    /**
     * Gets the root node.
     *
     * @return the root node
     */
    public RootPlanNode getRootNode() {
        return this.planRootNode;
    }

    /**
     * Gets the graph model.
     *
     * @return the graph model
     */
    public Graph getGraphModel() {
        return IUIModelAnalysedPlanNodeToGraphModelConvertor.getGraphModel(this.flattenedExplainPlan,
                this.flattenedExplainPlanEdges);
    }

}
