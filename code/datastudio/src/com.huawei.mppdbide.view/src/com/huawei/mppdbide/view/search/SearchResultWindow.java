/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.search;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchResultWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SearchResultWindow {

    private String executionTime;
    private int rowFetched;

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param partService the part service
     * @param modelService the model service
     * @param application the application
     */
    @Inject
    public void createPartControl(Composite parent, EPartService partService, EModelService modelService,
            MApplication application) {

    }

    /**
     * Gets the execution time.
     *
     * @return the execution time
     */
    public String getExecutionTime() {
        return executionTime;
    }

    /**
     * Sets the execution time.
     *
     * @param executionTime the new execution time
     */
    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * Gets the row fetched.
     *
     * @return the row fetched
     */
    public int getRowFetched() {
        return rowFetched;
    }

    /**
     * Sets the row fetched.
     *
     * @param rowFetched the new row fetched
     */
    public void setRowFetched(int rowFetched) {
        this.rowFetched = rowFetched;
    }

    /**
     * Display status bar.
     */
    public void displayStatusBar() {

    }

    /**
     * Destroy.
     */
    @PreDestroy
    public void destroy() {
    }

}
