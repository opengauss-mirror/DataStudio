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

package org.opengauss.mppdbide.view.core.sourceeditor;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import org.opengauss.mppdbide.eclipse.dependent.EclipseInjections;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.handler.HandlerUtilities;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLEditorHyperLink.
 *
 * @since 3.0.0
 */
public class SQLEditorHyperLink implements IHyperlink {
    private IRegion region;
    private ServerObject serverObj;

    /**
     * Instantiates a new SQL editor hyper link.
     *
     * @param region the region
     * @param serverObj the server obj
     */
    public SQLEditorHyperLink(IRegion region, ServerObject serverObj) {
        super();
        this.region = region;
        this.serverObj = serverObj;
    }

    /**
     * Gets the hyperlink region.
     *
     * @return the hyperlink region
     */
    @Override
    public IRegion getHyperlinkRegion() {
        return this.region;
    }

    /**
     * Gets the type label.
     *
     * @return the type label
     */
    @Override
    public String getTypeLabel() {
        return this.serverObj.getTypeLabel();
    }

    /**
     * Gets the hyperlink text.
     *
     * @return the hyperlink text
     */
    @Override
    public String getHyperlinkText() {
        return this.serverObj.getSearchName().trim();
    }

    /**
     * Open.
     */
    @Override
    public void open() {
        switch (serverObj.getType()) {
            case SQLFUNCTION:
            case PLSQLFUNCTION:
            case CFUNCTION: {
                if (serverObj instanceof IDebugObject) {
                    IDebugObject func = (IDebugObject) serverObj;
                    try {
                        HandlerUtilities.displaySourceCodeInEditor(func, false);
                    } catch (DatabaseOperationException exception) {

                        if (!exception.getMessage().contentEquals(
                                MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_INVALID_STATE))) {
                            IHandlerUtilities.handleGetSrcCodeException(func);
                        }

                        return;

                    } catch (DatabaseCriticalException exception) {
                        UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception,
                                func.getDatabase());
                        return;
                    }
                }
                break;
            }
            case TABLEMETADATA:
            case VIEW_META_DATA:
            case PARTITION_TABLE: {
                IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
                eclipseContext.set(EclipseContextDSKeys.SERVER_OBJECT, serverObj);
                String command = "org.opengauss.mppdbide.command.id.properties";

                Command cmd = EclipseInjections.getInstance().getCommandService().getCommand(command);
                ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
                if (EclipseInjections.getInstance().getHandlerService().canExecute(parameterizedCmd)) {
                    // execute the command
                    EclipseInjections.getInstance().getHandlerService().executeHandler(parameterizedCmd);

                }
                break;
            }

            default: {
                break;
            }
        }

    }
}
