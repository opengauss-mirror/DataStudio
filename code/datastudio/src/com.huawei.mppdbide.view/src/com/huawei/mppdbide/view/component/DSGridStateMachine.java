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

package com.huawei.mppdbide.view.component;

import java.util.Observable;

import javax.annotation.PreDestroy;

import org.eclipse.swt.SWT;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGridStateMachine.
 *
 * @since 3.0.0
 */
public class DSGridStateMachine extends Observable {

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum State.
     */
    public enum State {

        /**
         * The loading.
         */
        LOADING(MessageConfigLoader.getProperty(IMessagesConstants.LOADING_DATA_STATUS_MSG), SWT.COLOR_BLUE),

        /**
         * The exporting.
         */
        EXPORTING(MessageConfigLoader.getProperty(IMessagesConstants.EXPORTING_DATA_STATUS_MSG), SWT.COLOR_BLUE),

        /**
         * The refreshing.
         */
        REFRESHING(MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_REFRESH_STATUSBAR), SWT.COLOR_BLUE),

        /**
         * The idle.
         */
        IDLE("", SWT.COLOR_BLACK),

        /**
         * The error.
         */
        ERROR(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_LOADING_DATA_STATUS_MSG), SWT.COLOR_RED);

        private final String displayMsg;
        private final int color;

        private State(String msg, int color) {
            this.displayMsg = msg;
            this.color = color;
        }

        /**
         * Gets the display msg.
         *
         * @return the display msg
         */
        public String getDisplayMsg() {
            return this.displayMsg;
        }

        /**
         * Gets the color.
         *
         * @return the color
         */
        public int getColor() {
            return this.color;
        }
    };

    private State currentState;

    /**
     * Instantiates a new DS grid state machine.
     */
    public DSGridStateMachine() {
        this.currentState = State.IDLE;
    }

    /**
     * Can.
     *
     * @param newState the new state
     * @return true, if successful
     */
    public boolean can(State newState) {
        /*
         * Allowed state changes: IDLE -> Loading IDLE -> EXPORT EXPORT -> IDLE
         * LOADING -> IDLE, LOADING -> ERROR
         */
        return (!isIdle(currentState) && isIdle(newState)) || (isIdle(currentState) && !isIdle(newState))
                || (isLoading() && newState == State.ERROR);
    }

    /**
     * Sets the.
     *
     * @param newState the new state
     * @return true, if successful
     */
    public boolean set(State newState) {
        if (can(newState)) {
            this.currentState = newState;
            setChanged();
            notifyObservers(currentState);
            return true;
        }

        return false;
    }

    private boolean isIdle(State givenState) {
        return givenState == State.IDLE;
    }

    /**
     * Checks if is loading.
     *
     * @return true, if is loading
     */
    public boolean isLoading() {
        return currentState == State.LOADING;
    }

    /**
     * Checks if is exporting.
     *
     * @return true, if is exporting
     */
    public boolean isExporting() {
        return currentState == State.EXPORTING;
    }

    /**
     * Checks if is refreshing.
     *
     * @return true, if is refreshing
     */
    public boolean isRefreshing() {
        return currentState == State.REFRESHING;
    }

    /**
     * Checks if is error.
     *
     * @return true, if is error
     */
    public boolean isError() {
        return currentState == State.ERROR;
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        this.currentState = null;
    }
}
