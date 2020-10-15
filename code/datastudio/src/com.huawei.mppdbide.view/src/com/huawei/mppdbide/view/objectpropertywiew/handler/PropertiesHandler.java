/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.objectpropertywiew.handler;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.presentation.visualexplainplan.AbstractExplainPlanPropertyCore;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.objectpropertywiew.ObjectPropertyViewWorker;
import com.huawei.mppdbide.view.objectpropertywiew.ViewObjectPropertiesContext;
import com.huawei.mppdbide.view.objectpropertywiew.ViewObjectPropertiesResultDisplayUIManager;
import com.huawei.mppdbide.view.objectpropertywiew.factory.ObjectPropertiesResultDisplayUIManagerFactory;
import com.huawei.mppdbide.view.ui.visualexplainplan.VisualExplainPlanUIPresentation;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.visualexplainplanpropertyview.VisualExplainPlanNodePropertiesUIManager;
import com.huawei.mppdbide.view.visualexplainplanpropertyview.VisualExplainPlanPropertiesContext;
import com.huawei.mppdbide.view.visualexplainplanpropertyview.VisualExplainPlanPropertyViewWorker;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PropertiesHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();

        Object obj1 = eclipseContext.get(EclipseContextDSKeys.SERVER_OBJECT);
        if (null == obj1) {
            return;
        }

        if (obj1 instanceof ServerObject) {
            PropertyHandlerCore core = new PropertyHandlerCore(obj1);
            Object propertiesWindow = UIElement.getInstance().findWindowAndActivate(core.getWindowDetails());
            if (propertiesWindow != null) {
                // The window is already open so do nothing.
                return;
            }
            IExecutionContext context = initializeContext(core, obj1);
            if (context == null) {
                return;
            }
            ObjectPropertyViewWorker worker = new ObjectPropertyViewWorker(context);
            worker.setTaskDB(core.getTermConnection().getDatabase());
            worker.schedule();
        }
        if (obj1 instanceof Server) {
            PropertyHandlerCore core = new PropertyHandlerCore(obj1);
            IExecutionContext context = initializeContext(core, obj1);
            if (context == null) {
                return;
            }
            ConnectionPropertyWorker worker = new ConnectionPropertyWorker(context);
            worker.schedule();

        }
        if (obj1 instanceof VisualExplainPlanUIPresentation) {
            VisualExplainPlanUIPresentation explainUIPresenter = (VisualExplainPlanUIPresentation) obj1;

            AbstractExplainPlanPropertyCore core = explainUIPresenter.getSuitablePropertyPresenter();
            if (null == core) {
                return;
            }

            if (!core.isExecutable()) {
                return;
            }

            VisualExplainPlanNodePropertiesUIManager uiManager = new VisualExplainPlanNodePropertiesUIManager(core,
                    explainUIPresenter);

            IExecutionContext context = new VisualExplainPlanPropertiesContext(core, uiManager);

            VisualExplainPlanPropertyViewWorker worker = new VisualExplainPlanPropertyViewWorker(context);
            worker.schedule();
        }

    }

    private IExecutionContext initializeContext(PropertyHandlerCore core, Object obj1) {

        if (!core.isExecutable()) {
            return null;
        }

        if (UIElement.getInstance().isWindowLimitReached()) {
            UIElement.getInstance().openMaxSourceViewerDialog();
            return null;
        }
        ViewObjectPropertiesResultDisplayUIManager uiManager = ObjectPropertiesResultDisplayUIManagerFactory
                .getUImanagerObject(core, obj1);

        IExecutionContext context = new ViewObjectPropertiesContext(core, uiManager);
        return context;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        if (null != IHandlerUtilities.getSelectedDatabase()) {
            return IHandlerUtilities.getSelectedDatabase().isConnected();
        }

        IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();

        Object obj = eclipseContext.get(EclipseContextDSKeys.SERVER_OBJECT);
        Server server = null;
        if (obj == null) {
            return false;
        }
        if (obj instanceof ServerObject) {
            ServerObject servObj = (ServerObject) obj;
            Database db = servObj.getDatabase();
            if (db != null) {
                server = db.getServer();
            }
        }
        if (obj instanceof Server) {
            return true;
        }
        if (obj instanceof VisualExplainPlanUIPresentation) {
            return true;
        }
        if (server != null && IHandlerUtilities.getActiveDB(server)) {
            return !IHandlerUtilities.isSelectedTableForignPartition();
        }

        return false;
    }
}
