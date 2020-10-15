/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan;

import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanViewer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class VisualExplainPlanViewer extends InfiniteCanvasViewer implements IPropertyChangeListener {

    /**
     * Instantiates a new visual explain plan viewer.
     *
     * @param editor the editor
     */
    public VisualExplainPlanViewer(AbstractVisualExplainCore editor) {
        super();
    }

    /**
     * Property change.
     *
     * @param event the event
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {

    }
}
