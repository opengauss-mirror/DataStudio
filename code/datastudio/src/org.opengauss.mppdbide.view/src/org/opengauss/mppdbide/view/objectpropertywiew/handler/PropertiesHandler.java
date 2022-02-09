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

package org.opengauss.mppdbide.view.objectpropertywiew.handler;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import org.opengauss.mppdbide.eclipse.dependent.EclipseInjections;
import org.opengauss.mppdbide.presentation.IExecutionContext;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.AbstractExplainPlanPropertyCore;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.objectpropertywiew.ObjectPropertyViewWorker;
import org.opengauss.mppdbide.view.objectpropertywiew.ViewObjectPropertiesContext;
import org.opengauss.mppdbide.view.objectpropertywiew.ViewObjectPropertiesResultDisplayUIManager;
import org.opengauss.mppdbide.view.objectpropertywiew.factory.ObjectPropertiesResultDisplayUIManagerFactory;
import org.opengauss.mppdbide.view.ui.visualexplainplan.VisualExplainPlanUIPresentation;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.visualexplainplanpropertyview.VisualExplainPlanNodePropertiesUIManager;
import org.opengauss.mppdbide.view.visualexplainplanpropertyview.VisualExplainPlanPropertiesContext;
import org.opengauss.mppdbide.view.visualexplainplanpropertyview.VisualExplainPlanPropertyViewWorker;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesHandler.
 *
 * @since 3.0.0
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
