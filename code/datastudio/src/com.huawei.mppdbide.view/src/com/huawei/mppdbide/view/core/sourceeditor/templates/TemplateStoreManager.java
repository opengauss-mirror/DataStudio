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

package com.huawei.mppdbide.view.core.sourceeditor.templates;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.templates.persistence.TemplatePersistenceDataIf;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class TemplateStoreManager.
 *
 * @since 3.0.0
 */
public class TemplateStoreManager {
    private TemplateStore templateStore = null;

    private boolean matchCase = true;

    private static TemplateStoreManager instance = new TemplateStoreManager();

    private final Object LOCK = new Object();

    /**
     * Instantiates a new template store manager.
     */
    private TemplateStoreManager() {

    }

    /**
     * Gets the single instance of TemplateStoreManager.
     *
     * @return single instance of TemplateStoreManager
     */
    public static TemplateStoreManager getInstance() {
        return instance;
    }

    /**
     * Creates the template store.
     *
     * @param ps the ps
     */
    public void createTemplateStore(PreferenceStore ps) {
        synchronized (LOCK) {
            if (templateStore == null && null != ps) {
                templateStore = new TemplateStore(ps, UIConstants.TEMPLATESTORE_PREFERENCE_KEY);
                try {
                    templateStore.load();
                } catch (IOException e) {
                    MPPDBIDELoggerUtility.error("TemplateStoreManager: Ioexception occurred.");
                } catch (Exception e1) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_TEMPLATE_LOAD_FAILURE_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_PREFERENCE_LOAD_FAILURE_DETAIL));
                }
                templateStore.startListeningForPreferenceChanges();
            }

        }
    }

    /**
     * Load.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void load() throws IOException {

        templateStore.load();
    }

    /**
     * Gets the template store.
     *
     * @return the template store
     */
    public TemplateStore getTemplateStore() {
        return templateStore;
    }

    /**
     * Gets the template data.
     *
     * @param includeDeleted the include deleted
     * @return the template data
     */
    public TemplatePersistenceDataIf[] getTemplateData(boolean includeDeleted) {
        return templateStore.getTemplateData(includeDeleted);
    }

    /**
     * Adds the.
     *
     * @param data the data
     */
    public void add(TemplatePersistenceDataIf data) {
        templateStore.add(data);
    }

    /**
     * Delete.
     *
     * @param data the data
     */
    public void delete(TemplatePersistenceDataIf data) {
        templateStore.delete(data);
    }

    /**
     * Restore deleted.
     */
    public void restoreDeleted() {
        templateStore.restoreDeleted();
    }

    /**
     * Restore defaults.
     *
     * @param doSave the do save
     */
    public void restoreDefaults(boolean doSave) {
        templateStore.restoreDefaults(doSave);
    }

    /**
     * Save.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void save() throws IOException {
        templateStore.save();
    }

    /**
     * Checks if is match case.
     *
     * @return true, if is match case
     */
    public boolean isMatchCase() {
        return matchCase;
    }

    /**
     * Sets the match case.
     *
     * @param isMatchCase the new match case
     */
    public void setMatchCase(boolean isMatchCase) {
        this.matchCase = isMatchCase;
    }

}
