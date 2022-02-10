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

package org.opengauss.mppdbide.view.prefernces;

import java.net.URL;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.preference.PreferenceStore;

import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.eclipse.dependent.EclipseInjections;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.autosave.AutoSaveManager;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLSyntaxColorProvider;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class PreferencesLoadHandler.
 *
 * @since 3.0.0
 */
public class PreferencesLoadHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        PreferenceStore ps = null;
        KeyBindingWrapper bindingWrapper = KeyBindingWrapper.getInstance(
                EclipseInjections.getInstance().getBindingService(),
                EclipseInjections.getInstance().getCommandService());
        URL filePath = getFilePath();
        if (filePath != null) {
            bindingWrapper.loadXMLFile(filePath);
            try {
                ps = PreferenceWrapper.getInstance().loadPreferenceStore();

                if (ps != null) {
                    SQLSyntaxColorProvider.setPreferenceColor(ps);
                    DSTemplatePreferencePage.setPreferences(ps);
                    bindingWrapper.reconfigureKeyBinding(ps);
                    SecurityOptionProviderForPreferences.updateSecurityPreferenceOption(ps);
                    DBAssistantOption.updateDBAssistantEnableOnce(ps);
                    AutoSaveManager.updateAutosavePreferences(ps);

                    ResultManagementViewDataPreferencePage.setPreferenceResultWindow(ps);
                    ExportDDLPreferencePage.setDefaultPreferences(ps);
                    EditTableOptionProviderForPreferences.setDefaultEditTablePreferences(ps);
                    DSFormatterPreferencePage.setPreferenceStore(ps);
                    IBLPreference sysPref = BLPreferenceImpl.getBLPreference();
                    BLPreferenceManager.getInstance().setBLPreference(sysPref);
                    UserEncodingOption.updatePreference(ps);
                    DateTimePreferencePage.setDefaultPreferences(ps);
                    return;
                }

                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_LOAD_FAIL_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_LOAD_FAIL_MSG));
                IWorkbench workbench = EclipseInjections.getInstance().getWorkBench();
                workbench.close();
                return;
            } catch (MPPDBIDEException exception) {
                MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_HEADER),
                        MessageConfigLoader.getProperty(IMessagesConstants.MAXIMUM_PREF_FILE_SIZE));
                IWorkbench workbench = EclipseInjections.getInstance().getWorkBench();
                workbench.close();
                return;
            }
        }

    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    private URL getFilePath() {

        URL propertiesURL = null;
        ClassLoader classLoader = PreferencesLoadHandler.class.getClassLoader();
        if (null != classLoader) {
            propertiesURL = classLoader.getResource("keybinding.xml");
            return propertiesURL;
        }
        return propertiesURL;

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return true;
    }

}
