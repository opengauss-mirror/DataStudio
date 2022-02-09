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

package org.opengauss.mppdbide.view.ui.visualexplainplan;

import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanViewer.
 *
 * @since 3.0.0
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
