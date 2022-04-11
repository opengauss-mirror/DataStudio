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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.opengauss.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import org.opengauss.mppdbide.eclipse.dependent.EclipseInjections;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.MemoryCleaner;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.visualexplainplan.parts.PropertyHandlerCoreWrapper;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanPart.
 *
 * @since 3.0.0
 */
public class VisualExplainPlanPart extends AbstractVisualExplainCore implements EventHandler {

    private VisualExplainPlanUIPresentation uiPresenatationWrapper;
    private Composite toolbarComposite;
    private Button zoomResetButton;
    @Inject
    private IEventBroker eventBroker;

    /**
     * The Constant EVENT_PERNODE_DETAILS.
     */
    public static final String EVENT_PERNODE_DETAILS = "PerNodeDetailEvent";

    /**
     * Post construct.
     *
     * @param availableComp the available comp
     * @param part the part
     * @return the composite
     */
    @PostConstruct
    public Composite postConstruct(Composite availableComp, MPart part) {
        this.uiPresenatationWrapper = (VisualExplainPlanUIPresentation) (part.getObject());
        createComponent(availableComp);
        eventBroker.subscribe(EVENT_PERNODE_DETAILS, this);
        return availableComp;
    }

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    public IEventBroker getEventBroker() {
        return eventBroker;
    }

    /**
     * Handle event.
     */
    public static void handleEvent() {

    }

    private void createComponent(Composite parent) {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        Composite currComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        currComposite.setLayout(layout);
        currComposite.setData(gridData);

        createToolBar2(currComposite);

        SashForm sashForm = new SashForm(currComposite, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm.setOrientation(SWT.VERTICAL);
        super.createPartControl(sashForm, this.uiPresenatationWrapper);
        createOverallPropertiesView(this.uiPresenatationWrapper);

    }

    private void createToolBar2(Composite parent) {
        Composite composite = new Composite(parent, SWT.None);
        GridData gdToolbarComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);

        this.toolbarComposite = composite;
        toolbarComposite.setLayoutData(gdToolbarComposite);
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        this.toolbarComposite.setLayout(layout);
        this.toolbarComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));

        createZoomResetBtnArea();
    }

    private void createZoomResetBtnArea() {
        Composite executeComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdExecuteComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        executeComposite.setLayoutData(gdExecuteComposite);
        GridLayout executelayout = new GridLayout(1, false);
        executelayout.marginHeight = 0;
        executeComposite.setLayout(executelayout);
        executeComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        zoomResetButton = new Button(executeComposite, SWT.NONE);
        zoomResetButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_EXPLAIN_PLAN_ZOOM_IN");
        GridData gdExecuteButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdExecuteButton.widthHint = 27;
        zoomResetButton.setLayoutData(gdExecuteButton);
        zoomResetButton.setImage(IconUtility.getIconImage(IiconPath.VIS_EXPLAIN_REDRAW, getClass()));

        zoomResetButton.setToolTipText(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ICON_TOOLTIP_ZOOM_RESET));
        zoomResetButton.getParent().setToolTipText(zoomResetButton.getToolTipText());
        zoomResetButton.addSelectionListener(zoomInBtnSelectionListener());
    }

    private SelectionListener zoomInBtnSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                handleResetViewPort();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        };
    }

    private void createOverallPropertiesView(VisualExplainPlanUIPresentation uiPresenatationWrapper2) {
        IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
        eclipseContext.set(EclipseContextDSKeys.SERVER_OBJECT, uiPresenatationWrapper2);
        uiPresenatationWrapper2.setOperationType(VisualExplainPlanConstants.VISUAL_EXPLAIN_OPTYPE_ALLPROPERTY);
        String command = "org.opengauss.mppdbide.command.id.properties";

        UIElement.getInstance().execCommand(command);

    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        UIElement.getInstance().removePartFromStack(UIConstants.VIS_EXPLAIN_PART_ID);
        MemoryCleaner.cleanUpMemory();
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(Event event) {
        if (EVENT_PERNODE_DETAILS.equals(event.getTopic())) {
            Object property = event.getProperty(IEventBroker.DATA);

            if (property instanceof PropertyHandlerCoreWrapper) {
                PropertyHandlerCoreWrapper propertyHandlerCoreInstance = (PropertyHandlerCoreWrapper) property;
                String tabId = propertyHandlerCoreInstance.getTabId();

                if (!tabId.equals(this.uiPresenatationWrapper.getExplainPlanTabId())) {
                    return;
                }
                this.uiPresenatationWrapper
                        .setOperationObject(propertyHandlerCoreInstance.getPropertyHandlerCoreInstance());
                this.uiPresenatationWrapper.setOperationType(VisualExplainPlanConstants.VISUAL_EXPLAIN_PERNODE_DETAILS);

                IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
                eclipseContext.set(EclipseContextDSKeys.SERVER_OBJECT, this.uiPresenatationWrapper);
                String command = "org.opengauss.mppdbide.command.id.properties";

                UIElement.getInstance().execCommand(command);
            }
        }
    }

}
