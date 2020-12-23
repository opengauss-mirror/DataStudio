/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan;

import org.eclipse.e4.ui.model.application.ui.basic.impl.TrimmedWindowImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPartsManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class VisualExplainPartsManager {
    /**
     * The Constant WINDOWID_PRE_TEXT.
     */
    public static final String WINDOWID_PRE_TEXT = "com.huawei.mppdbide.view.visualexplainplanwindow.0";

    /**
     * The Constant MAIN_PART_STACKID.
     */
    public static final String MAIN_PART_STACKID = "com.huawei.mppdbide.view.visualExplain.partStack.0";

    /**
     * The Constant OVERALL_PROPERTIES_STACKID.
     */
    public static final String OVERALL_PROPERTIES_STACKID = "com.huawei.mppdbide.view.visualexplainplanwindow."
            + "stack.OverAllProperties.0";

    /**
     * The Constant DETAILS_STACKID.
     */
    public static final String DETAILS_STACKID = "com.huawei.mppdbide.view.visualexplainplanwindow.stack.details.0";
    private static final String DIAGRAM_PART_CLASS = "bundleclass://com.huawei.mppdbide.view/com.huawei."
            + "mppdbide.view.ui.visualexplainplan.VisualExplainPlanPart";

    private static final String OVERALL_INFO_PART_CLASS = "bundleclass://com.huawei.mppdbide.view/"
            + "com.huawei.mppdbide.view.objectpropertywiew.PropertiesWindow";
    private static final String PERNODE_INFO_PART_CLASS = "bundleclass://com.huawei.mppdbide.view/"
            + "com.huawei.mppdbide.view.objectpropertywiew.PropertiesWindow";
    private static final String DIAGRAM_PART_ID = "com.huawei.mppdbide.view.part.donotdeleteme.1";
    private static final String GENERIC_PROPS_PART_ID = "com.huawei.mppdbide.view.part."
            + "donotdeleteme.genericproperties.0";
    private static final String SPECIFIC_PROPS_PART_ID = "com.huawei.mppdbide.view."
            + "visualexplainplanwindow.stack.details.0";

    private static volatile VisualExplainPartsManager selfObject;
    private static final Object LOCK = new Object();
    private final Object instanceLock = new Object();
    private int windowId = 0;

    private VisualExplainPartsManager() {
    }

    /**
     * getDiagramPartId
     * 
     * @return diagram part id
     */
    public static String getDiagramPartId() {
        return DIAGRAM_PART_ID;
    }

    /**
     * getGenericPropsPartId
     * 
     * @return generic property part id
     */
    public static String getGenericPropsPartId() {
        return GENERIC_PROPS_PART_ID;
    }

    /**
     * getSpecificPropsPartId
     * 
     * @return specific property part id
     */
    public static String getSpecificPropsPartId() {
        return SPECIFIC_PROPS_PART_ID;
    }

    /**
     * Gets the single instance of VisualExplainPartsManager.
     *
     * @return single instance of VisualExplainPartsManager
     */
    public static VisualExplainPartsManager getInstance() {
        if (null == selfObject) {
            synchronized (LOCK) {
                if (null == selfObject) {
                    selfObject = new VisualExplainPartsManager();
                }
            }
        }
        return selfObject;
    }

    private String getExisitingWindowPartId() {
        StringBuilder sb = new StringBuilder(getVisualPlanWindowId());
        return sb.toString();
    }

    private String getNextWindowPartId() {
        String windowPartId = getExisitingWindowPartId();
        return windowPartId;
    }

    /**
     * Creates the visual explain plan parts.
     *
     * @param windowHandler the window handler
     * @param presentation the presentation
     */
    public void createVisualExplainPlanParts(String windowHandler, Object visualExplainPlanUIPresentations) {
        String explainWindowId = getVisualPlanWindowId();
        String compositeID = (String) windowHandler;
        IWindowDetail diagramWindowDetail = new VisualExplainWindowDetailImpl(
                VisualExplainPlanWindowDetailsEnum.VISUAL_EXPLAIN_DIAGRAM_WINDOW);
        createVisualExplainMainPart(diagramWindowDetail, explainWindowId, compositeID,
                visualExplainPlanUIPresentations);
    }

    /**
     * Creates the per node info part.
     *
     * @param windowHandler the window handler
     * @param windowDetails the window details
     * @param presentation the presentation
     */
    public void createPerNodeInfoPart(Object windowHandler, IWindowDetail windowDetails, Object presentation) {
        if (windowHandler instanceof String) {
            String explainWindowId = getVisualPlanWindowId();
            IWindowDetail nodeWindow = new VisualExplainWindowDetailImpl(
                    VisualExplainPlanWindowDetailsEnum.VISUAL_EXPLAIN_NODE_PROPERTY_WINDOW, windowDetails);
            String compositeID = (String) windowHandler;
            createNodePropertiesPart(nodeWindow, explainWindowId, compositeID, presentation);
        }
    }

    /**
     * Creates the over all plan info part.
     *
     * @param windowHandler the window handler
     * @param presentation the presentation
     */
    public void createOverAllPlanInfoPart(Object windowHandler, Object presentation) {
        if (windowHandler instanceof String) {
            String explainWindowId = getVisualPlanWindowId();
            IWindowDetail windowDetail = new VisualExplainWindowDetailImpl(
                    VisualExplainPlanWindowDetailsEnum.VISUAL_EXPLAIN_OVERALL_PROPERTY_WINDOW);
            String compositeID = (String) windowHandler;
            createVisualExplainOverAllInfoPart(windowDetail, explainWindowId, compositeID, presentation);
        }
    }

    private void createVisualExplainOverAllInfoPart(IWindowDetail details, String explainWindowId, String compositeID,
            Object presentation) {
        UIElement.getInstance().newRenderInWindow(explainWindowId, VisualExplainPartsManager.getMainPartStackid(),
                compositeID, VisualExplainPartsManager.OVERALL_INFO_PART_CLASS, details, presentation,
                GENERIC_PROPS_PART_ID);

    }

    /**
     * Creates the visual explain main part.
     *
     * @param details the details
     * @param explainWindowId the explain window id
     * @param compositeID the composite ID
     * @param presentation the presentation
     */
    public void createVisualExplainMainPart(IWindowDetail details, String explainWindowId, String compositeID,
            Object presentation) {
        UIElement.getInstance().newRenderInWindow(explainWindowId, VisualExplainPartsManager.getMainPartStackid(),
                compositeID, VisualExplainPartsManager.getDiagramPartClass(), details, presentation, DIAGRAM_PART_ID);
        getWindowsDetailsForShutDown(explainWindowId);
    }

    private void getWindowsDetailsForShutDown(String windowId) {
        TrimmedWindowImpl window1 = UIElement.getInstance().getVisualPlanWindow(windowId);
        final Shell shell = (Shell) window1.getWidget();
        final Display display = shell.getDisplay();
        display.addFilter(SWT.Close, new VisualPlanWindowShutdownListener());
    }
   
    private void createNodePropertiesPart(IWindowDetail details, String explainWindowId, String compositeID,
            Object presentation) {
        UIElement.getInstance().newRenderInWindow(explainWindowId, VisualExplainPartsManager.getMainPartStackid(),
                compositeID, PERNODE_INFO_PART_CLASS, details, presentation, SPECIFIC_PROPS_PART_ID);
    }

    /**
     * Gets the new window handler.
     *
     * @return the new window handler
     */
    public Object getNewWindowHandler() {
        synchronized (instanceLock) {
            return this.getNextWindowPartId();
        }
    }

    /**
     * Gets the main part stackid.
     *
     * @return the main part stackid
     */
    public static String getMainPartStackid() {
        return MAIN_PART_STACKID;
    }

    /**
     * Gets the visual plan window id.
     *
     * @return the visual plan window id
     */
    public static String getVisualPlanWindowId() {
        return WINDOWID_PRE_TEXT;
    }

    /**
     * Gets the diagram part class.
     *
     * @return the diagram part class
     */
    public static String getDiagramPartClass() {
        return DIAGRAM_PART_CLASS;
    }
}
