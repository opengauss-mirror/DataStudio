/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.HandlerUtilities;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLEditorHyperLink.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
                String command = "com.huawei.mppdbide.command.id.properties";

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
