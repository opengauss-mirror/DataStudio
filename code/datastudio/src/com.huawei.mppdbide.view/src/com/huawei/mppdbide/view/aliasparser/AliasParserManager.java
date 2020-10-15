/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.aliasparser;

import java.util.ArrayList;
import java.util.Stack;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class AliasParserManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
