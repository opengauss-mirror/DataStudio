/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import java.net.URL;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.autosave.AutoSaveManager;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSyntaxColorProvider;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class PreferencesLoadHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
