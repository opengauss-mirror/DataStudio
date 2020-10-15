/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.eclipse.swt.custom.StyledText;

import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.view.component.DSGridStateMachine;
import com.huawei.mppdbide.view.component.grid.core.DataText;

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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 23-May-2019]
 * @since 23-May-2019
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
}