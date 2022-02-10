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

package org.opengauss.mppdbide.eclipse.dependent;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * 
 * Title: class
 * 
 * Description: The Class EclipseInjections.
 *
 * @since 3.0.0
 */
public class EclipseInjections {

    /**
     * The eclipse context.
     */
    IEclipseContext eclipseContext;

    /**
     * The workbench.
     */
    IWorkbench workbench;
    private ECommandService commandService;
    private EHandlerService handlerService;
    private EBindingService bindingService;
    private EPartService partService;
    private EModelService modelService;
    private MApplication application;

    /**
     * The my self.
     */
    static EclipseInjections mySelf;

    private EclipseInjections() {

    }

    static {
        mySelf = new EclipseInjections();
    }

    /**
     * Sets the eclipse context.
     *
     * @param eclipseContext the new eclipse context
     */
    public void setEclipseContext(IEclipseContext eclipseContext) {
        this.eclipseContext = eclipseContext;
    }

    /**
     * Sets the command service.
     *
     * @param commService the new command service
     */
    public void setCommandService(ECommandService commService) {
        this.commandService = commService;
    }

    /**
     * Sets the handler service.
     *
     * @param handService the new handler service
     */
    public void setHandlerService(EHandlerService handService) {
        this.handlerService = handService;
    }

    /**
     * Sets the work bench.
     *
     * @param wb the new work bench
     */
    public void setWorkBench(IWorkbench wb) {
        this.workbench = wb;
    }

    /**
     * Sets the binding service.
     *
     * @param bs the new binding service
     */
    public void setBindingService(EBindingService bs) {
        this.bindingService = bs;
    }

    /**
     * Sets the part service.
     *
     * @param ps the new part service
     */
    public void setPartService(EPartService ps) {
        this.partService = ps;

    }

    /**
     * Sets the model service.
     *
     * @param ms the new model service
     */
    public void setModelService(EModelService ms) {
        this.modelService = ms;

    }

    /**
     * Sets the application.
     *
     * @param app the new application
     */
    public void setApplication(MApplication app) {
        this.application = app;

    }

    /**
     * Gets the single instance of EclipseInjections.
     *
     * @return single instance of EclipseInjections
     */
    public static EclipseInjections getInstance() {
        return mySelf;
    }

    /**
     * Gets the command service.
     *
     * @return the command service
     */
    public ECommandService getCommandService() {
        return commandService;
    }

    /**
     * Gets the handler service.
     *
     * @return the handler service
     */
    public EHandlerService getHandlerService() {
        return handlerService;
    }

    /**
     * Gets the binding service.
     *
     * @return the binding service
     */
    public EBindingService getBindingService() {
        return this.bindingService;
    }

    /**
     * Gets the work bench.
     *
     * @return the work bench
     */
    public IWorkbench getWorkBench() {
        return workbench;
    }

    /**
     * Gets the eclipse context.
     *
     * @return the eclipse context
     */
    public IEclipseContext getEclipseContext() {
        return this.eclipseContext;
    }

    /**
     * Gets the ps.
     *
     * @return the ps
     */
    public EPartService getPS() {
        return partService;
    }

    /**
     * Gets the ms.
     *
     * @return the ms
     */
    public EModelService getMS() {
        return modelService;
    }

    /**
     * Gets the app.
     *
     * @return the app
     */
    public MApplication getApp() {
        return application;
    }

}
