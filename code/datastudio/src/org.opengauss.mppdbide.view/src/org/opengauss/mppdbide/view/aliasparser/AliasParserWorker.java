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

package org.opengauss.mppdbide.view.aliasparser;

import java.util.ArrayList;

import org.opengauss.mppdbide.parser.alias.AliasParser;
import org.opengauss.mppdbide.utils.SQLTerminalQuerySplit;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class AliasParserWorker.Worker responsible for invoking
 * parsing job and getting the parsed data
 *
 * @since 3.0.0
 */
public class AliasParserWorker extends UIWorkerJob {

    private final int workerId;
    private AliasParserWorkerState workerState;
    private AliasRequestResponsePacket packet;
    private AliasParser aliasParser;
    private boolean canReschedule;
    private SQLTerminalQuerySplit querySplitter;
    private ArrayList<String> queryArray;
    private final Object instanceLock = new Object();

    /**
     * Instantiates a new alias parser worker.
     *
     * @param name the name
     * @param family the family
     * @param workerId the worker id
     */
    public AliasParserWorker(String name, Object family, int workerId) {
        super(name, family);
        this.workerId = workerId;
        this.aliasParser = new AliasParser();
        this.canReschedule = true;
        this.queryArray = new ArrayList<String>(1);
        this.querySplitter = new SQLTerminalQuerySplit();
        this.setWorkerState(AliasParserWorkerState.IDLE);
        this.packet = null;
    }

    private void resetWorkerData() {
        this.setWorkerState(AliasParserWorkerState.IDLE);
        this.packet = null;
        this.queryArray.clear();
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        while (true) {
            if (null != packet) {
                setWorkerState(AliasParserWorkerState.BUSY);

                try {
                    /*
                     * Split queries in SQL terminal till current cursor
                     * location
                     */
                    querySplitter.splitQuerries(queryArray, packet.getQueries(), true);
                } catch (DatabaseOperationException exception) {
                    MPPDBIDELoggerUtility.error("AliasParserWorker: splitting queries failed.", exception);
                    resetWorkerData();
                    continue;
                }

                packet.calculateQueryExtents(queryArray);
                String currentQuery = packet.extractFormattedCurrentQuery(queryArray);

                if (null != currentQuery) {
                    /* invoke parsing of query in the request */
                    aliasParser.parseQuery(currentQuery);
                    /*
                     * set parsed data in the packet and change packet state to
                     * indicate parsing done
                     */
                    packet.setPContext(aliasParser.getParseContext());
                    packet.changeState(AliasRequestResponsePacketState.RESPONSE);
                }
                resetWorkerData();
            }
            Thread.sleep(10);
        }
    }

    /**
     * Stop alias parser worker.
     */
    public void stopAliasParserWorker() {
        this.canReschedule = false;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        // This is a daemon thread. Control never comes here, hence nothing todo
        stopAliasParserWorker();
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
        // Nothing todo
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException e) {
        // Nothing todo
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        if (canReschedule) {
            resetWorkerData();
            this.schedule();
        } else {
            this.aliasParser = null;
            this.packet = null;
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        // Nothing todo
    }

    /**
     * Sets the working context.
     *
     * @param inPacket the new working context
     */
    public void setWorkingContext(AliasRequestResponsePacket inPacket) {
        this.packet = inPacket;
    }

    /**
     * Gets the working context.
     *
     * @return the working context
     */
    public AliasRequestResponsePacket getWorkingContext() {
        return this.packet;
    }

    /**
     * Gets the worker state.
     *
     * @return the worker state
     */
    public AliasParserWorkerState getWorkerState() {
        synchronized (instanceLock) {
            return this.workerState;
        }
    }

    private void setWorkerState(AliasParserWorkerState state) {
        synchronized (instanceLock) {
            this.workerState = state;
        }
    }

    /**
     * Gets the worker id.
     *
     * @return the worker id
     */
    public int getWorkerId() {
        return this.workerId;
    }

}
