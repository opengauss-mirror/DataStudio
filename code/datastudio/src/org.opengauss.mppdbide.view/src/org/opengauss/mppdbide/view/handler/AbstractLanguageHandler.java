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

package org.opengauss.mppdbide.view.handler;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.preference.PreferenceStore;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.autosave.AutoSaveManager;
import org.opengauss.mppdbide.view.prefernces.PreferenceWrapper;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractLanguageHandler.
 *
 * @since 3.0.0
 */
public abstract class AbstractLanguageHandler {

    /**
     * The workbench.
     */
    @Optional
    @Inject
    protected IWorkbench workbench;

    /**
     * The language index.
     */
    protected int languageIndex;

    /**
     * Instantiates a new abstract language handler.
     *
     * @param index the index
     */
    public AbstractLanguageHandler(int index) {
        this.languageIndex = index;
    }

    /**
     * Execute.
     */
    protected void execute() {
        final IJobManager jm = Job.getJobManager();
        Job[] allJobs = jm.find(MPPDBIDEConstants.CANCELABLEJOB);
        String title = "";
        StringBuilder builder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        title = getDisplayMessage(allJobs, builder);
        int result = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()), title, builder.toString(),
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO));

        if (result == 0) {
            boolean isRestart = IHandlerUtilities.setLocale(this.languageIndex);
            if (isRestart) {
                PreferenceStore preferenceStore = PreferenceWrapper.getInstance().getPreferenceStore();
                if (preferenceStore != null) {
                    try {
                        if (this.languageIndex == 1) {
                            MPPDBIDELoggerUtility.operationInfo("Language setting is changed to Chinese from English");
                        }
                        if (this.languageIndex == 0) {
                            MPPDBIDELoggerUtility.operationInfo("Language setting is changed to English from Chinese");
                        }
                        preferenceStore.setValue("IsRestarted", Boolean.TRUE);
                        preferenceStore.save();
                    } catch (IOException exception) {
                        MPPDBIDELoggerUtility.error("Prefence.save returned exception while saving to disk :",
                                exception);
                    }
                }
                UIDisplayFactoryProvider.getUIDisplayStateIf().cleanUponWindowClose();
                AutoSaveManager.getInstance().gracefulExit();
                workbench.restart();
            }
        }
    }

    private String getDisplayMessage(Job[] allJobs, StringBuilder builder) {
        String title = "";
        String msg = "";
        if (allJobs.length != 0) {
            title = MessageConfigLoader.getProperty(IMessagesConstants.DS_RESTART_CONFIRMATION_TITLE);
            msg = MessageConfigLoader.getProperty(IMessagesConstants.DS_RESTART_MSG_FOR_JOBS);
        } else {
            title = MessageConfigLoader.getProperty(IMessagesConstants.DS_RESTART_CONFIRMATION_TITLE);
            msg = MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_APP_RESTART_MSG);
        }
        builder.append(msg);
        builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        builder.append(MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_APP_RESTART_NOTE));

        builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        builder.append("    " + MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_APP_RESTART_NOTE_NO));
        return title;
    }
}
