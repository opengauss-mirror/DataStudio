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

package org.opengauss.mppdbide.view.ui.visualexplainplan.parts;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.VEPNodeAttributeId;
import org.opengauss.mppdbide.view.ui.visualexplainplan.ExplainPlanOuterEventer;

import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;

/**
 * Title: OpenNodePropertyOnClickHandler
 * 
 * @since 3.0.0
 */
public class OpenNodePropertyOnClickHandler extends AbstractHandler implements IOnClickHandler {

    /**
     * The event broker.
     */
    IEventBroker eventBroker;
    private String tabId;

    /**
     * Instantiates a new open node property on click handler.
     *
     * @param eventBroker the event broker
     * @param tabId tab id
     */
    public OpenNodePropertyOnClickHandler(IEventBroker eventBroker, String tabId) {
        super();
        this.eventBroker = eventBroker;
        this.tabId = tabId;
    }

    /**
     * Click.
     *
     * @param event the event
     */
    @Override
    public void click(MouseEvent event) {
        /* Check for double click */
        if (event.getClickCount() == 2) {
            openNodeProperty(event);
        }
    }

    /**
     * Open node property.
     *
     * @param event the event
     */
    public void openNodeProperty(MouseEvent event) {
        Node content = null;

        IVisualPart<? extends javafx.scene.Node> host = getHost();
        IRootPart<? extends javafx.scene.Node> rootPart = host.getRoot();
        IViewer viewer = null;
        SelectionModel selectionModel = null;
        ObservableList<IContentPart<? extends javafx.scene.Node>> selection = null;
        viewer = validateAndGetViewer(rootPart);
        selectionModel = validateAndGetSelectionModel(viewer);
        selection = validateAndGetSelection(selectionModel);

        if (isOneGraphSelected(selection)) {
            /* Only 1 graph element selected */
            IContentPart<? extends javafx.scene.Node> primarySelection = selection.get(0);
            content = getContent(primarySelection);
            if (null == content) {
                return;
            }

            Object object = content.getAttributes().get(VEPNodeAttributeId.NODE_PROPERTY);
            validateAdnPostNodePropertyEvent(object);
            planNodeProperty(object);
        } else if (isMoreThanOneGraphSelected(selection)) {
            /* More than 1 graph elements are selected */
            for (IContentPart<? extends javafx.scene.Node> primarySelection : selection) {
                if (primarySelection instanceof CustomNodePart) {
                    CustomNodePart customNode = (CustomNodePart) primarySelection;
                    Rectangle bounds = new Rectangle(new Point(customNode.getContentTransform().getTx(),
                            customNode.getContentTransform().getTy()), customNode.getContentSize());
                    if (isPointContainedInRectangle(new Point(event.getX(), event.getY()), bounds)) {
                        Object object = customNode.getContent().getAttributes().get(VEPNodeAttributeId.NODE_PROPERTY);
                        planNodeProperty(object);
                        return;
                    }
                }
            }
        }
    }

    private boolean isMoreThanOneGraphSelected(ObservableList<IContentPart<? extends javafx.scene.Node>> selection) {
        return selection != null && selection.size() > 1;
    }

    private boolean isOneGraphSelected(ObservableList<IContentPart<? extends javafx.scene.Node>> selection) {
        return selection != null && selection.size() == 1;
    }

    private void validateAdnPostNodePropertyEvent(Object object) {
        if (null != object && object instanceof UIModelAnalysedPlanNode) {
            UIModelAnalysedPlanNode planNode = (UIModelAnalysedPlanNode) object;
            postNodePropertyEvent(planNode);
        }
    }

    private Node getContent(IContentPart<? extends javafx.scene.Node> primarySelection) {
        Node content = null;
        if (primarySelection instanceof CustomNodePart) {
            CustomNodePart customNodePart = (CustomNodePart) primarySelection;
            content = customNodePart.getContent();
        }
        return content;
    }

    private ObservableList<IContentPart<? extends javafx.scene.Node>>
            validateAndGetSelection(SelectionModel selectionModel) {
        ObservableList<IContentPart<? extends javafx.scene.Node>> selection = null;
        if (selectionModel != null) {
            selection = selectionModel.getSelectionUnmodifiable();
        }
        return selection;
    }

    private SelectionModel validateAndGetSelectionModel(IViewer viewer) {
        SelectionModel selectionModel = null;
        if (viewer != null) {
            selectionModel = viewer.getAdapter(SelectionModel.class);
        }
        return selectionModel;
    }

    private IViewer validateAndGetViewer(IRootPart<? extends javafx.scene.Node> rootPart) {
        IViewer viewer = null;
        if (rootPart != null) {
            viewer = rootPart.getViewer();
        }
        return viewer;
    }

    private void planNodeProperty(Object object) {
        if (null != object && object instanceof UIModelAnalysedPlanNode) {
            UIModelAnalysedPlanNode planNode = (UIModelAnalysedPlanNode) object;
            postNodePropertyEvent(planNode);
        }
    }

    private void postNodePropertyEvent(UIModelAnalysedPlanNode planNode) {
        if (null != eventBroker) {
            PropertyHandlerCoreWrapper wrapper = new PropertyHandlerCoreWrapper(
                    planNode.getAdapter(PropertyHandlerCore.class), this.tabId);
            eventBroker.post(ExplainPlanOuterEventer.EVENT_PERNODE_DETAILS, wrapper);
        }
    }

    private boolean isPointContainedInRectangle(Point point, Rectangle rectangle) {
        return rectangle.contains(point);
    }
}
