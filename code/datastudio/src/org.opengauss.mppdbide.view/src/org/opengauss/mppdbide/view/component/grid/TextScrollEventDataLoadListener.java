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
import java.time.temporal.ChronoUnit;

import org.eclipse.swt.custom.StyledText;

import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.observer.DSEvent;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.view.component.DSGridStateMachine;
import org.opengauss.mppdbide.view.component.grid.core.DataText;

/**
 * Title: TextScrollEventDataLoadListener
 * 
 * Description:The listener interface for receiving textScrollEventDataLoad
 * events. The class that is interested in processing a textScrollEventDataLoad
 * event implements this interface, and the object created with that class is
 * registered with a component using the component's
 * <code>addTextScrollEventDataLoadListener<code> method. When the
 * textScrollEventDataLoad event occurs, that object's appropriate method is
 * invoked.
 * 
 * @since 3.0.0
 */
public class TextScrollEventDataLoadListener extends GridAndTextScrollEventDataLoadListener {
    private DataText datatext;
    private StyledText text;

    /**
     * Instantiates a new text scroll event data load listener.
     *
     * @param text the text
     * @param dataProvider the data provider
     * @param eventTable the event table
     * @param stateMachine the state machine
     * @param datatext the datatext
     */
    public TextScrollEventDataLoadListener(StyledText text, IDSGridDataProvider dataProvider, DSEventTable eventTable,
            DSGridStateMachine stateMachine, DataText datatext) {
        super(dataProvider, eventTable, stateMachine);
        this.datatext = datatext;
        this.text = text;
    }

    /**
     * Checks if is last row selected.
     *
     * @return true, if is last row selected
     */
    @Override
    public boolean isLastRowSelected() {
        if (this.getDataProvider() instanceof IDSEditGridDataProvider) {
            return this.datatext.getSelectedRowPosition() == (this.getDataProvider().getRecordCount()
                    + ((IDSEditGridDataProvider) this.getDataProvider()).getInsertedRowCount()) - 1;
        }
        return this.datatext.getSelectedRowPosition() == this.getDataProvider().getRecordCount() - 1;
    }

    /**
     * Trigger load more records.
     *
     * @param isKeyStrokeTriggeredScrollEvent the is key stroke triggered scroll
     * event
     */
    @Override
    public void triggerLoadMoreRecords(boolean isKeyStrokeTriggeredScrollEvent) {
        if ((isKeyStrokeTriggeredScrollEvent ? isLastRowSelected()
                : text.getVerticalBar().getSelection() + text.getVerticalBar().getPageIncrement() >= text
                        .getVerticalBar().getMaximum())
                && !this.getDataProvider().isEndOfRecords() && !isSearchInProgress()
                && !isCurrentInitDataTextSatatu()) {
            if (ChronoUnit.MILLIS.between(this.getLastLoadTime(), LocalDateTime.now()) >= 2000) {
                this.setLastLoadTime(LocalDateTime.now());
                if (this.getStateMachine().set(DSGridStateMachine.State.LOADING)) {
                    this.getEventTable().sendEvent(
                            new DSEvent(IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY, getDataProvider()));
                }
            }
        }

    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        super.onPreDestroy();
        this.datatext = null;
    }

    @Override
    public void updateScrolledPosition(int position, int pageIncrement, int increment) {
        datatext.updateScrolledInfo();
    }
}
