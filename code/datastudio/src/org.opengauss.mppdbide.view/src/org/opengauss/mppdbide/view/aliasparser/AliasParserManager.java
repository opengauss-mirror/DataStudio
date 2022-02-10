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
import java.util.Stack;

import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class AliasParserManager.
 *
 * @since 3.0.0
 */
public class AliasParserManager extends UIWorkerJob implements IAliasParserJobManager {

    /**
     * The Constant ALIAS_PARSER_MAX_WORKER_NUMBER.
     */
    public static final int ALIAS_PARSER_MAX_WORKER_NUMBER = 1;

    private static volatile AliasParserManager aliasparsermanager = null;
    private ArrayList<AliasParserWorker> aliasParserWorkerPool;
    private Stack<AliasRequestResponsePacket> aliasParserJobStack;
    private boolean isExitFlagSet;
    private final Object instanceLock = new Object();

    /**
     * Instantiates a new alias parser manager.
     *
     * @param name the name
     * @param family the family
     */
    public AliasParserManager(String name, Object family) {
        super(name, family);
        aliasParserWorkerPool = new ArrayList<AliasParserWorker>(ALIAS_PARSER_MAX_WORKER_NUMBER);
        initializeAliasParserJobStack();
        isExitFlagSet = false;
    }

    private void initializeAliasParserJobStack() {
        synchronized (instanceLock) {
            aliasParserJobStack = new Stack<AliasRequestResponsePacket>();
        }
    }

    /**
     * Launch alias parser worker threads.
     */
    public void launchAliasParserWorkerThreads() {
        for (int workerCounter = 0; workerCounter < ALIAS_PARSER_MAX_WORKER_NUMBER; workerCounter++) {
            AliasParserWorker worker = new AliasParserWorker("Alias Parser Worker:" + workerCounter, null,
                    workerCounter);
            aliasParserWorkerPool.add(worker);
            worker.schedule();
        }
    }

    /**
     * Sets the alias parser manager exit flag.
     */
    public void setAliasParserManagerExitFlag() {
        this.isExitFlagSet = true;
    }

    /**
     * Stop all alias parser worker threads.
     */
    public void stopAllAliasParserWorkerThreads() {
        for (AliasParserWorker worker : aliasParserWorkerPool) {
            worker.stopAliasParserWorker();
            worker.getThread().interrupt();
        }
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
        while (!this.isExitFlagSet) {
            try {
                if (!isAliasParserJobStackEmpty()) {
                    /* Pick an idle worker and assign the parsing job to it */
                    for (AliasParserWorker worker : aliasParserWorkerPool) {
                        if (AliasParserWorkerState.IDLE == worker.getWorkerState()) {
                            AliasRequestResponsePacket packet = popFromAliasParserStack();
                            worker.setWorkingContext(packet);
                            worker.schedule();
                            break;
                        } else {
                            cancelAliasParserWorker(worker);
                        }
                    }
                }

                Thread.sleep(10);
            } catch (Exception exception) {
                MPPDBIDELoggerUtility.error("Alias parser manager got exception. Ignoring...", exception);
            }
        }
        return null;
    }

    private boolean isAliasParserJobStackEmpty() {
        synchronized (instanceLock) {
            return aliasParserJobStack.isEmpty();
        }
    }

    private AliasRequestResponsePacket popFromAliasParserStack() {
        synchronized (instanceLock) {
            AliasRequestResponsePacket mostRecentJob = aliasParserJobStack.pop();
            aliasParserJobStack.clear();
            return mostRecentJob;
        }
    }

    /**
     * Gets the single instance of AliasParserManager.
     *
     * @return single instance of AliasParserManager
     */
    public static AliasParserManager getInstance() {
        return aliasparsermanager;
    }

    /**
     * Creates the alias parser manager instance.
     */
    public static void createAliasParserManagerInstance() {
        if (aliasparsermanager == null) {
            aliasparsermanager = new AliasParserManager("Alias Parser", null);
        }
    }

    private void pushToAliasParserJobStack(AliasRequestResponsePacket job) {
        synchronized (instanceLock) {
            aliasParserJobStack.add(job);
        }
    }

    /**
     * Submit alias parser job.
     *
     * @param job the job
     */
    @Override
    public void submitAliasParserJob(AliasRequestResponsePacket job) {
        if (job.changeState(AliasRequestResponsePacketState.REQUEST)) {
            pushToAliasParserJobStack(job);
        }
    }

    /**
     * Cancel alias parser job.
     *
     * @param packetId the packet id
     */
    @Override
    public void cancelAliasParserJob(int packetId) {
        for (AliasParserWorker worker : aliasParserWorkerPool) {
            if (worker.getWorkingContext() != null && worker.getWorkingContext().getPacketId() == packetId) {
                if (worker.getWorkerState() == AliasParserWorkerState.BUSY) {
                    cancelAliasParserWorker(worker);
                }
            }
        }
    }

    private void cancelAliasParserWorker(AliasParserWorker worker) {
        worker.setCancelled(true);
        worker.getThread().interrupt();
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        this.aliasParserWorkerPool = null;
        cleanupAliasParserJobStack();
    }

    private void cleanupAliasParserJobStack() {
        synchronized (instanceLock) {
            this.aliasParserJobStack = null;
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        return;

    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        return;

    }

}
