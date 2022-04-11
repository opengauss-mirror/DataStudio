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

package org.opengauss.mppdbide.view.component.grid;

import java.time.LocalDateTime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.view.component.DSGridStateMachine;

/**
 * Title: GridAndTextScrollEventDataLoadListener
 * 
 * Description:The listener interface for receiving
 * gridAndTextScrollEventDataLoad events. The class that is interested in
 * processing a gridAndTextScrollEventDataLoad event implements this interface,
 * and the object created with that class is registered with a component using
 * the component's <code>addGridAndTextScrollEventDataLoadListener<code> method.
 * When the gridAndTextScrollEventDataLoad event occurs, that object's
 * appropriate method is invoked.
 * 
 * @since 3.0.0
 */
public abstract class GridAndTextScrollEventDataLoadListener implements Listener, KeyListener {
    private IDSGridDataProvider dataProvider;
    private DSEventTable eventTable;
    private DSGridStateMachine stateMachine;
    private String currentSearchString;
    private LocalDateTime lastLoadTime;
    private boolean currentInitDataTextSatatu;

    /**
     * Instantiates a new grid and text scroll event data load listener.
     *
     * @param dataProvider the data provider
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public GridAndTextScrollEventDataLoadListener(IDSGridDataProvider dataProvider, DSEventTable eventTable,
            DSGridStateMachine stateMachine) {
        this.dataProvider = dataProvider;
        this.eventTable = eventTable;
        this.stateMachine = stateMachine;
        this.lastLoadTime = LocalDateTime.now();
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(Event event) {
        ScrollBar bar = (ScrollBar) event.widget;
        updateScrolledPosition(bar.getSelection(), bar.getPageIncrement(), bar.getIncrement());
        if (bar.getSelection() + bar.getThumb() == bar.getMaximum()) {
            triggerLoadMoreRecords(false);
        }
    }

    /**
     * Key pressed.
     *
     * @param event the event
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.PAGE_DOWN) {
            triggerLoadMoreRecords(true);
        }
    }

    /**
     * update scrolled position
     *
     * @param position the ui scrolled position
     * @param pageIncrement the page increment
     * @param increment the one row increment
     */
    public abstract void updateScrolledPosition(int position, int pageIncrement, int increment);

    /**
     * Checks if is last row selected.
     *
     * @return true, if is last row selected
     */
    public abstract boolean isLastRowSelected();

    /**
     * Trigger load more records.
     *
     * @param isKeyStrokeTriggeredScrollEvent the is key stroke triggered scroll
     * event
     */
    public abstract void triggerLoadMoreRecords(boolean isKeyStrokeTriggeredScrollEvent);

    /**
     * Key released.
     *
     * @param e the e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // Ignore. No need to handle release event.
    }

    /**
     * Reset loading status.
     */
    public void resetLoadingStatus() {
        this.stateMachine.set(DSGridStateMachine.State.IDLE);
    }

    /**
     * Gets the data provider.
     *
     * @return the data provider
     */
    public IDSGridDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * Sets the search string.
     *
     * @param searchText the new search string
     */
    public void setSearchString(String searchText) {
        this.currentSearchString = searchText;
    }

    /**
     * Checks if is search in progress.
     *
     * @return true, if is search in progress
     */
    public boolean isSearchInProgress() {
        return null != this.currentSearchString && !this.currentSearchString.isEmpty();
    }

    /**
     * Gets the event table.
     *
     * @return the event table
     */
    public DSEventTable getEventTable() {
        return eventTable;
    }

    /**
     * Gets the last load time.
     *
     * @return the last load time
     */
    public LocalDateTime getLastLoadTime() {
        return lastLoadTime;
    }

    /**
     * Sets the last load time.
     *
     * @param lastLoadTime the new last load time
     */
    public void setLastLoadTime(LocalDateTime lastLoadTime) {
        this.lastLoadTime = lastLoadTime;
    }

    /**
     * Gets the state machine.
     *
     * @return the state machine
     */
    public DSGridStateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * Checks if is current init data text satatu.
     *
     * @return true, if is current init data text satatu
     */
    public boolean isCurrentInitDataTextSatatu() {
        return currentInitDataTextSatatu;
    }

    /**
     * Sets the current init data text satatu.
     *
     * @param currentInitDataTextSatatu the new current init data text satatu
     */
    public void setCurrentInitDataTextSatatu(boolean currentInitDataTextSatatu) {
        this.currentInitDataTextSatatu = currentInitDataTextSatatu;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        this.dataProvider = null;
        this.eventTable = null;
        this.lastLoadTime = null;
        this.currentSearchString = null;
        if (this.stateMachine != null && this.stateMachine.countObservers() > 0) {
            this.stateMachine.deleteObservers();
        }
    }
}
