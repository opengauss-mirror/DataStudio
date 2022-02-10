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

package org.opengauss.mppdbide.view.core.sourceeditor.templates;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.sourceeditor.templates.persistence.TemplateFactory;
import org.opengauss.mppdbide.view.core.sourceeditor.templates.persistence.TemplateIf;
import org.opengauss.mppdbide.view.core.sourceeditor.templates.persistence.TemplatePersistenceDataIf;

/**
 * 
 * Title: class
 * 
 * Description: The Class TemplateStore.
 *
 * @since 3.0.0
 */
public class TemplateStore {
    private final List<TemplatePersistenceDataIf> templates = new ArrayList<TemplatePersistenceDataIf>(7);

    private IPreferenceStore preferenceStore;
    private String key;

    /**
     * The property listener, if any is registered, <code>null</code> otherwise.
     *
     * @since 3.2
     */
    private IPropertyChangeListener fPropertyChangeListener;

    private boolean ignorePreferenceStoreChanges;

    /**
     * Instantiates a new template store.
     *
     * @param ps the ps
     * @param key the key
     */
    public TemplateStore(IPreferenceStore ps, String key) {
        this.preferenceStore = ps;
        this.key = key;
    }

    /**
     * Load.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void load() throws IOException {
        templates.clear();
        loadContributedTemplates();
        loadCustomTemplates();
    }

    /**
     * Start listening for preference changes.
     */
    public final void startListeningForPreferenceChanges() {
        if (fPropertyChangeListener == null) {
            fPropertyChangeListener = new IPropertyChangeListener() {

                /**
                 * Property change.
                 *
                 * @param event the event
                 */
                public void propertyChange(PropertyChangeEvent event) {
                    /*
                     * Don't load if we are in the process of saving ourselves.
                     * We are in sync anyway after the save operation, and
                     * clients may trigger reloading by listening to preference
                     * store updates.
                     */
                    if (!ignorePreferenceStoreChanges && key.equals(event.getProperty())) {
                        try {
                            load();
                        } catch (IOException exception) {
                            handleException(exception);
                        }
                    }
                }
            };

            preferenceStore.addPropertyChangeListener(fPropertyChangeListener);
        }
    }

    /**
     * Handle exception.
     *
     * @param ioException the x
     */
    private void handleException(IOException ioException) {
        MPPDBIDELoggerUtility.error("TemplateStore: IOException occurred.", ioException);
    }

    /**
     * Hook method to load contributed templates. Contributed templates are
     * superseded by customized versions of user added templates stored in the
     * preferences.
     * <p>
     * The default implementation does nothing.
     * </p>
     *
     * @throws IOException if loading fails
     */
    private void loadContributedTemplates() throws IOException {
        String pref = preferenceStore.getDefaultString(key);
        if (pref != null && pref.trim().length() > 0) {
            Reader readerInput = new StringReader(pref);
            TemplateReaderWriter templateReader = new TemplateReaderWriter();
            TemplatePersistenceDataIf[] templatedatas = templateReader.read(readerInput);
            for (int cnt = 0; cnt < templatedatas.length; cnt++) {
                TemplatePersistenceDataIf data = templatedatas[cnt];
                if (data.isCustom()) {
                    MPPDBIDELoggerUtility.info("Selected template is custom.");
                } else {
                    templates.add(data);
                }
            }
        }
    }

    /**
     * Save.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void save() throws IOException {
        ArrayList<TemplatePersistenceDataIf> custom = new ArrayList<TemplatePersistenceDataIf>(1);
        for (Iterator<TemplatePersistenceDataIf> it = templates.iterator(); it.hasNext();) {
            TemplatePersistenceDataIf templatedata = it.next();
            // don't save deleted user-added templates
            if (templatedata.isCustom() && !(templatedata.isUserAdded() && templatedata.isDeleted())) {
                custom.add(templatedata);
            }
        }

        StringWriter output = new StringWriter();
        TemplateReaderWriter templateWriter = new TemplateReaderWriter();
        templateWriter.save((TemplatePersistenceDataIf[]) custom.toArray(new TemplatePersistenceDataIf[custom.size()]),
                output);

        ignorePreferenceStoreChanges = true;
        try {
            preferenceStore.setValue(key, output.toString());
            if (preferenceStore instanceof IPersistentPreferenceStore) {
                ((IPersistentPreferenceStore) preferenceStore).save();
            }
        } finally {
            ignorePreferenceStoreChanges = false;
        }
    }

    /**
     * Adds the.
     *
     * @param data the data
     */
    public void add(TemplatePersistenceDataIf data) {
        if (data.isUserAdded()) {
            templates.add(data);
        } else {
            for (Iterator<TemplatePersistenceDataIf> it = templates.iterator(); it.hasNext();) {
                TemplatePersistenceDataIf data2 = it.next();
                if (data2.getId() != null && data2.getId().equals(data.getId())) {
                    data2.setTemplate(data.getTemplate());
                    data2.setDeleted(data.isDeleted());
                    data2.setEnabled(data.isEnabled());
                    return;
                }
            }

            // here add an id which is not contributed as add-on
            if (data.getTemplate() != null) {
                TemplatePersistenceDataIf newData = TemplateFactory.getTemplatePersistenceData(data.getTemplate(),
                        data.isEnabled());
                templates.add(newData);
            }
        }
    }

    /**
     * Delete.
     *
     * @param templatedata the templatedata
     */
    public void delete(TemplatePersistenceDataIf templatedata) {
        if (templatedata.isUserAdded()) {
            templates.remove(templatedata);
        } else {
            templatedata.setDeleted(true);
        }
    }

    /**
     * Restore deleted.
     */
    public void restoreDeleted() {
        for (Iterator<TemplatePersistenceDataIf> it = templates.iterator(); it.hasNext();) {
            TemplatePersistenceDataIf templateData = it.next();
            if (templateData.isDeleted()) {
                templateData.setDeleted(false);
            }
        }
    }

    /**
     * Restore defaults.
     *
     * @param doSave the do save
     */
    public void restoreDefaults(boolean doSave) {
        String oldValue = null;
        if (!doSave) {
            oldValue = preferenceStore.getString(key);
        }

        try {
            ignorePreferenceStoreChanges = true;
            preferenceStore.setToDefault(key);
        } finally {
            ignorePreferenceStoreChanges = false;
        }

        try {
            load();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("TemplateStore: IOException occurred.", exception);
        }

        if (oldValue != null) {
            try {
                ignorePreferenceStoreChanges = true;
                preferenceStore.putValue(key, oldValue);
            } finally {
                ignorePreferenceStoreChanges = false;
            }
        }
    }

    /**
     * Gets the template data.
     *
     * @param includeDeleted the include deleted
     * @return the template data
     */
    public TemplatePersistenceDataIf[] getTemplateData(boolean includeDeleted) {
        List<TemplatePersistenceDataIf> datas = new ArrayList<TemplatePersistenceDataIf>(1);
        for (Iterator<TemplatePersistenceDataIf> it = templates.iterator(); it.hasNext();) {
            TemplatePersistenceDataIf templateData = it.next();
            if (includeDeleted || !templateData.isDeleted()) {
                datas.add(templateData);
            }
        }

        return (TemplatePersistenceDataIf[]) datas.toArray(new TemplatePersistenceDataIf[datas.size()]);
    }

    /**
     * Load custom templates.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void loadCustomTemplates() throws IOException {

        if (null == preferenceStore) {
            return;
        }

        String pref = preferenceStore.getString(key);
        if (pref != null && pref.trim().length() > 0) {
            Reader readerInput = new StringReader(pref);
            TemplateReaderWriter reader = new TemplateReaderWriter();
            TemplatePersistenceDataIf[] templatedatas = reader.read(readerInput);
            for (int index = 0; index < templatedatas.length; index++) {
                TemplatePersistenceDataIf data = templatedatas[index];
                add(data);
            }
        }
    }

    /**
     * Gets the matched templates.
     *
     * @param prefixParam the prefix param
     * @param isMatchCase the is match case
     * @return the matched templates
     */
    public TemplateIf[] getMatchedTemplates(String prefixParam, boolean isMatchCase) {
        List<TemplateIf> result = new ArrayList<TemplateIf>(5);
        String prefix = prefixParam;
        if ("".equals(prefix)) {
            for (TemplatePersistenceDataIf obj : templates) {
                if (obj.isEnabled() && !obj.isDeleted()) {
                    result.add(obj.getTemplate());
                }
            }

            return (TemplateIf[]) result.toArray(new TemplateIf[result.size()]);
        }

        if (isMatchCase) {
            for (TemplatePersistenceDataIf obj : templates) {
                if (obj.isEnabled() && !obj.isDeleted() && obj.getTemplate().getName().startsWith(prefix)) {
                    result.add(obj.getTemplate());
                }
            }
        } else {
            prefix = prefix.toLowerCase(Locale.ENGLISH);
            for (TemplatePersistenceDataIf obj : templates) {
                if (obj.isEnabled() && !obj.isDeleted()
                        && obj.getTemplate().getName().toLowerCase(Locale.ENGLISH).startsWith(prefix)) {
                    result.add(obj.getTemplate());
                }
            }
        }

        return (TemplateIf[]) result.toArray(new TemplateIf[result.size()]);
    }
}
