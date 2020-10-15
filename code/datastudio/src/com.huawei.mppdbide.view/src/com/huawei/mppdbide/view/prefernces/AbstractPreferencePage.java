/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractPreferencePage.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractPreferencePage extends PreferencePage {

    /**
     * The preference store.
     */
    protected IPreferenceStore preferenceStore;

    /**
     * The yes button.
     */
    protected Button yesButton;

    /**
     * The no button.
     */
    protected Button noButton;

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected abstract Control createContents(Composite parent);

    /**
     * Instantiates a new abstract preference page.
     *
     * @param title the title
     */
    public AbstractPreferencePage(String title) {
        super(title);
    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getDefaultsButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_DEFAULT));
        getApplyButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));
    }

    /**
     * Perform cancel.
     *
     * @return true, if successful
     */
    @Override
    public boolean performCancel() {

        PreferenceWrapper.getInstance().setNeedRestart(false);
        PreferenceWrapper.getInstance().setPreferenceApply(false);
        PreferenceWrapper.getInstance().setDefaultStore(false);
        return true;
    }

    /**
     * Enable disable apply btn.
     */
    protected void enableDisableApplyBtn() {
        if (!getApplyButton().isEnabled()) {
            getApplyButton().setEnabled(true);
        }
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        PreferenceWrapper.getInstance().setPreferenceApply(true);
        performOk();
        getApplyButton().setEnabled(false);
        if (!getDefaultsButton().isEnabled()) {
            getDefaultsButton().setEnabled(true);
        }
    }

    /**
     * The listener interface for receiving buttonSelection events. The class
     * that is interested in processing a buttonSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addButtonSelectionListener<code>
     * method. When the buttonSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * ButtonSelectionEvent
     */
    protected class ButtonSelectionListener implements SelectionListener {

        /**
         * The preference item.
         */
        String preferenceItem;

        /**
         * The option selected.
         */
        boolean optionSelected;

        /**
         * Instantiates a new button selection listener.
         *
         * @param prefItem the pref item
         * @param option the option
         */
        public ButtonSelectionListener(String prefItem, boolean option) {
            preferenceItem = prefItem;
            optionSelected = option;
        }

        /**
         * Widget selected.
         *
         * @param e the e
         */
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (preferenceStore.getBoolean(preferenceItem) != optionSelected) {
                PreferenceWrapper.getInstance().setChangeDone(true);
            }

            if (getDefaultsButton() != null && !getDefaultsButton().getEnabled()
                    && PreferenceWrapper.getInstance().isChangeDone()) {
                getDefaultsButton().setEnabled(true);
            }
        }

        /**
         * Widget default selected.
         *
         * @param e the e
         */
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            // Auto-generated method stub

        }

    }

}
