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

import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLSyntaxColorProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class SyntaxColorPreferncePage.
 *
 * @since 3.0.0
 */
public class SyntaxColorPreferncePage extends PreferencePage {
    @Optional
    @Inject
    private ColorSelector singleLineCommentColor;
    private ColorSelector defaultColor;
    private ColorSelector unreservedKeywordColor;
    private ColorSelector reservedKeywordColor;
    private ColorSelector typeColor;
    private ColorSelector predicateColor;
    private ColorSelector constantsColor;
    private ColorSelector stringsColor;

    /**
     * Instantiates a new syntax color prefernce page.
     */
    public SyntaxColorPreferncePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_TITLE));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Label label = new Label(parent, SWT.BOLD);
        label.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_MSG));

        Composite composite = getMainComposite(parent);
        IPreferenceStore preferenceStore = getPreferenceStore();

        addSingleLineCommentUI(composite, preferenceStore);

        addDefaultColorUi(composite, preferenceStore);

        addUnReservedKeywordUi(composite, preferenceStore);

        addReservedKeywordUi(composite, preferenceStore);

        addTypeKeywordUi(composite, preferenceStore);

        addPredicateColorUi(composite, preferenceStore);

        addConstantColorUi(composite, preferenceStore);

        addStringColorUi(composite, preferenceStore);

        PreferenceWrapper.getInstance().setNeedRestart(false);
        return composite;
    }

    /**
     * Adds the string color ui.
     *
     * @param composite the composite
     * @param preferenceStore the preference store
     */
    private void addStringColorUi(Composite composite, IPreferenceStore preferenceStore) {
        Label stringssLabel = new Label(composite, SWT.LEFT);
        stringssLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_STRINGS));
        stringsColor = new ColorSelector(composite);
        if (SQLSyntaxColorProvider.validateColor(preferenceStore.getString(SQLSyntaxColorProvider.PREF_STRING))) {
            stringsColor
                    .setColorValue(PreferenceConverter.getColor(preferenceStore, SQLSyntaxColorProvider.PREF_STRING));
        } else {
            stringsColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_STRING));
        }
        Label stringExample = new Label(composite, SWT.RIGHT);
        stringExample
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_EXAMPLE) + "  'DATASTUDIO'");
        stringsColor.addListener(new ColorPropertyChangeListener());
    }

    /**
     * Adds the constant color ui.
     *
     * @param composite the composite
     * @param preferenceStore the preference store
     */
    private void addConstantColorUi(Composite composite, IPreferenceStore preferenceStore) {
        Label constantsLabel = new Label(composite, SWT.LEFT);
        constantsLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_CONSTANTS));
        constantsColor = new ColorSelector(composite);
        if (SQLSyntaxColorProvider.validateColor(preferenceStore.getString(SQLSyntaxColorProvider.PREF_CONSTANTS))) {
            constantsColor.setColorValue(
                    PreferenceConverter.getColor(preferenceStore, SQLSyntaxColorProvider.PREF_CONSTANTS));
        } else {
            constantsColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_CONSTANTS));
        }
        Label constantExample = new Label(composite, SWT.RIGHT);
        constantExample.setText(
                MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_EXAMPLE) + "  BINARY,FREEZE,FULL,CROSS");
        constantsColor.addListener(new ColorPropertyChangeListener());
    }

    /**
     * Adds the predicate color ui.
     *
     * @param composite the composite
     * @param preferenceStore the preference store
     */
    private void addPredicateColorUi(Composite composite, IPreferenceStore preferenceStore) {
        Label predicateLabel = new Label(composite, SWT.LEFT);
        predicateLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_PREDICATE));
        predicateColor = new ColorSelector(composite);
        if (SQLSyntaxColorProvider.validateColor(preferenceStore.getString(SQLSyntaxColorProvider.PREF_PREDICATES))) {
            predicateColor.setColorValue(
                    PreferenceConverter.getColor(preferenceStore, SQLSyntaxColorProvider.PREF_PREDICATES));
        } else {
            predicateColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_PREDICATES));
        }
        Label predicateExample = new Label(composite, SWT.RIGHT);
        predicateExample
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_EXAMPLE) + "  >,@@,=,#,~");
        predicateColor.addListener(new ColorPropertyChangeListener());
    }

    /**
     * Adds the type keyword ui.
     *
     * @param composite the composite
     * @param preferenceStore the preference store
     */
    private void addTypeKeywordUi(Composite composite, IPreferenceStore preferenceStore) {
        Label typeLabel = new Label(composite, SWT.LEFT);
        typeLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_TYPE));
        typeColor = new ColorSelector(composite);
        if (SQLSyntaxColorProvider.validateColor(preferenceStore.getString(SQLSyntaxColorProvider.PREF_TYPE))) {
            typeColor.setColorValue(PreferenceConverter.getColor(preferenceStore, SQLSyntaxColorProvider.PREF_TYPE));
        } else {
            typeColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_TYPE));
        }
        Label typeExample = new Label(composite, SWT.RIGHT);
        typeExample.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_EXAMPLE)
                + "  FLOAT,INTEGER,VARCHAR,CHAR");
        typeColor.addListener(new ColorPropertyChangeListener());
    }

    /**
     * Adds the reserved keyword ui.
     *
     * @param composite the composite
     * @param preferenceStore the preference store
     */
    private void addReservedKeywordUi(Composite composite, IPreferenceStore preferenceStore) {
        Label reservedKeywordLabel = new Label(composite, SWT.LEFT);
        reservedKeywordLabel
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_RESERVED_KEY));
        reservedKeywordColor = new ColorSelector(composite);
        if (SQLSyntaxColorProvider
                .validateColor(preferenceStore.getString(SQLSyntaxColorProvider.PREF_RESERVED_KEYWORD))) {
            reservedKeywordColor.setColorValue(
                    PreferenceConverter.getColor(preferenceStore, SQLSyntaxColorProvider.PREF_RESERVED_KEYWORD));
        } else {
            reservedKeywordColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_RESERVED_KEYWORD));
        }
        Label resrevedKeywordExample = new Label(composite, SWT.RIGHT);
        resrevedKeywordExample.setText(
                MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_EXAMPLE) + "  DEFAULT,CREATE,DISTINCT");
        reservedKeywordColor.addListener(new ColorPropertyChangeListener());
    }

    /**
     * Adds the un reserved keyword ui.
     *
     * @param composite the composite
     * @param preferenceStore the preference store
     */
    private void addUnReservedKeywordUi(Composite composite, IPreferenceStore preferenceStore) {
        Label unreservedKeywordLabel = new Label(composite, SWT.LEFT);
        unreservedKeywordLabel
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_UNRESERVED_KEY));
        unreservedKeywordColor = new ColorSelector(composite);
        if (SQLSyntaxColorProvider
                .validateColor(preferenceStore.getString(SQLSyntaxColorProvider.PREF_UNRESERVED_KEYWORD))) {
            unreservedKeywordColor.setColorValue(
                    PreferenceConverter.getColor(preferenceStore, SQLSyntaxColorProvider.PREF_UNRESERVED_KEYWORD));
        } else {
            unreservedKeywordColor.setColorValue(PreferenceConverter.getDefaultColor(preferenceStore,
                    SQLSyntaxColorProvider.PREF_UNRESERVED_KEYWORD));
        }
        Label unresrevedKeywordExample = new Label(composite, SWT.RIGHT);
        unresrevedKeywordExample.setText(
                MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_EXAMPLE) + "  ACTION,BEGIN,DELETE,ADD");
        unreservedKeywordColor.addListener(new ColorPropertyChangeListener());
    }

    /**
     * Adds the default color ui.
     *
     * @param composite the composite
     * @param preferenceStore the preference store
     */
    private void addDefaultColorUi(Composite composite, IPreferenceStore preferenceStore) {
        Label defaultLabel = new Label(composite, SWT.LEFT);
        defaultLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_DEFAULT));
        defaultColor = new ColorSelector(composite);
        if (SQLSyntaxColorProvider.validateColor(preferenceStore.getString(SQLSyntaxColorProvider.PREF_DEFAULT))) {
            defaultColor
                    .setColorValue(PreferenceConverter.getColor(preferenceStore, SQLSyntaxColorProvider.PREF_DEFAULT));
        } else {
            defaultColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_DEFAULT));
        }
        Label defaultExample = new Label(composite, SWT.RIGHT);
        defaultExample
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_EXAMPLE) + "   greekromanm");
        defaultColor.addListener(new ColorPropertyChangeListener());
    }

    /**
     * Adds the single line comment UI.
     *
     * @param composite the composite
     * @param preferenceStore the preference store
     */
    private void addSingleLineCommentUI(Composite composite, IPreferenceStore preferenceStore) {
        Label singleLineCommentLabel = new Label(composite, SWT.LEFT);
        singleLineCommentLabel
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_SINGLE_LINE_COMM));
        singleLineCommentColor = new ColorSelector(composite);
        if (SQLSyntaxColorProvider
                .validateColor(preferenceStore.getString(SQLSyntaxColorProvider.PREF_SQL_SINGLE_LINE_COMMENT))) {
            singleLineCommentColor.setColorValue(
                    PreferenceConverter.getColor(preferenceStore, SQLSyntaxColorProvider.PREF_SQL_SINGLE_LINE_COMMENT));
        } else {
            singleLineCommentColor.setColorValue(PreferenceConverter.getDefaultColor(preferenceStore,
                    SQLSyntaxColorProvider.PREF_SQL_SINGLE_LINE_COMMENT));
        }
        Label singleLineCommentExample = new Label(composite, SWT.RIGHT);
        singleLineCommentExample.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_EXAMPLE)
                + "  --Select * from pg_am, /*Select * from pg_am*/");
        singleLineCommentColor.addListener(new ColorPropertyChangeListener());
    }

    /**
     * Gets the main composite.
     *
     * @param parent the parent
     * @return the main composite
     */
    private Composite getMainComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gdToolbarComposite = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 2);
        gdToolbarComposite.widthHint = 2000;
        gdToolbarComposite.minimumWidth = 500;

        composite.setLayoutData(gdToolbarComposite);

        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setData(new GridData(SWT.FILL, SWT.TOP, false, false));
        return composite;
    }

    /**
     * The listener interface for receiving colorPropertyChange events. The
     * class that is interested in processing a colorPropertyChange event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addColorPropertyChangeListener<code> method. When the
     * colorPropertyChange event occurs, that object's appropriate method is
     * invoked.
     *
     * ColorPropertyChangeEvent
     */
    private class ColorPropertyChangeListener implements IPropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            validate(event);

        }

    }

    /**
     * Creates the control.
     *
     * @param parentObj the parent obj
     */
    @Override
    public void createControl(Composite parentObj) {
        super.createControl(parentObj);
        // rewrite default & apply
        getDefaultsButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_DEFAULT));
        getApplyButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));
    }

    /**
     * Validate.
     *
     * @param event the event
     */
    public void validate(PropertyChangeEvent event) {
        if (!event.getNewValue().equals(event.getOldValue())) {
            getApplyButton().setEnabled(true);
            getDefaultsButton().setEnabled(true);
            PreferenceWrapper.getInstance().setChangeDone(true);
        }

    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        // Get the preference store
        IPreferenceStore preferenceStore = getPreferenceStore();
        if (null != preferenceStore) {

            singleLineCommentColor.setColorValue(PreferenceConverter.getDefaultColor(preferenceStore,
                    SQLSyntaxColorProvider.PREF_SQL_SINGLE_LINE_COMMENT));
            defaultColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_DEFAULT));

            unreservedKeywordColor.setColorValue(PreferenceConverter.getDefaultColor(preferenceStore,
                    SQLSyntaxColorProvider.PREF_UNRESERVED_KEYWORD));

            reservedKeywordColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_RESERVED_KEYWORD));

            typeColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_TYPE));

            predicateColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_PREDICATES));

            constantsColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_CONSTANTS));

            stringsColor.setColorValue(
                    PreferenceConverter.getDefaultColor(preferenceStore, SQLSyntaxColorProvider.PREF_STRING));
        }

        PreferenceWrapper.getInstance().setDefaultStore(true);
        getDefaultsButton().setEnabled(false);
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Syntax coloring in preferences is set to default colors");
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        // Get the preference store
        IPreferenceStore preferenceStore = getPreferenceStore();

        // Set the values from the fields
        if (null != preferenceStore) {
            setValuesForSyntaxColorKeywords(preferenceStore);
            setValuesForSyntaxColorType(preferenceStore);

        }
        if (null != getApplyButton() && !getApplyButton().isEnabled()) {
            PreferenceWrapper.getInstance().setPreferenceApply(false);
        }
        PreferenceWrapper.getInstance().setNeedRestart(true);
        return true;
    }

    private void setValuesForSyntaxColorType(IPreferenceStore preferenceStore) {
        if (!StringConverter.asString(typeColor.getColorValue())
                .equals(preferenceStore.getString(SQLSyntaxColorProvider.PREF_TYPE))) {
            MPPDBIDELoggerUtility.operationInfo(
                    String.format(Locale.ENGLISH, "Syntax color of Type in preferences changed from %s to %s",
                            preferenceStore.getString(SQLSyntaxColorProvider.PREF_TYPE),
                            StringConverter.asString(typeColor.getColorValue())));
        }
        preferenceStore.setValue(SQLSyntaxColorProvider.PREF_TYPE,
                StringConverter.asString(typeColor.getColorValue()));
        if (!StringConverter.asString(predicateColor.getColorValue()).equals(preferenceStore
                .getString(SQLSyntaxColorProvider.PREF_PREDICATES))) {
            MPPDBIDELoggerUtility.operationInfo(
                    String.format(Locale.ENGLISH, "Syntax color of Predicate in preferences changed from %s to %s",
                            preferenceStore.getString(SQLSyntaxColorProvider.PREF_PREDICATES),
                            StringConverter.asString(predicateColor.getColorValue())));
        }
        preferenceStore.setValue(SQLSyntaxColorProvider.PREF_PREDICATES,
                StringConverter.asString(predicateColor.getColorValue()));
        if (!StringConverter.asString(constantsColor.getColorValue())
                .equals(preferenceStore.getString(SQLSyntaxColorProvider.PREF_CONSTANTS))) {
            MPPDBIDELoggerUtility.operationInfo(
                    String.format(Locale.ENGLISH, "Syntax color of Constant in preferences changed from %s to %s",
                            preferenceStore.getString(SQLSyntaxColorProvider.PREF_CONSTANTS),
                            StringConverter.asString(constantsColor.getColorValue())));
        }
        preferenceStore.setValue(SQLSyntaxColorProvider.PREF_CONSTANTS,
                StringConverter.asString(constantsColor.getColorValue()));
        if (!StringConverter.asString(stringsColor.getColorValue())
                .equals(preferenceStore.getString(SQLSyntaxColorProvider.PREF_STRING))) {
            MPPDBIDELoggerUtility.operationInfo(
                    String.format(Locale.ENGLISH, "Syntax color of String in preferences changed from %s to %s",
                            preferenceStore.getString(SQLSyntaxColorProvider.PREF_STRING),
                            StringConverter.asString(stringsColor.getColorValue())));
        }
        preferenceStore.setValue(SQLSyntaxColorProvider.PREF_STRING,
                StringConverter.asString(stringsColor.getColorValue()));
    }

    private void setValuesForSyntaxColorKeywords(IPreferenceStore preferenceStore) {
        if (!StringConverter.asString(singleLineCommentColor.getColorValue())
                .equals(preferenceStore.getString(SQLSyntaxColorProvider.PREF_SQL_SINGLE_LINE_COMMENT))) {
            MPPDBIDELoggerUtility.operationInfo(
                    String.format(Locale.ENGLISH, "Syntax color of Comment lines in preferences changed from %s to %s",
                            preferenceStore.getString(SQLSyntaxColorProvider.PREF_SQL_SINGLE_LINE_COMMENT),
                            StringConverter.asString(singleLineCommentColor.getColorValue())));
        }
        preferenceStore.setValue(SQLSyntaxColorProvider.PREF_SQL_SINGLE_LINE_COMMENT,
                StringConverter.asString(singleLineCommentColor.getColorValue()));
        if (!StringConverter.asString(defaultColor.getColorValue())
                .equals(preferenceStore.getString(SQLSyntaxColorProvider.PREF_DEFAULT))) {
            MPPDBIDELoggerUtility.operationInfo(
                    String.format(Locale.ENGLISH, "Syntax color of Default in preferences changed from %s to %s",
                            preferenceStore.getString(SQLSyntaxColorProvider.PREF_DEFAULT),
                            StringConverter.asString(defaultColor.getColorValue())));
        }
        preferenceStore.setValue(SQLSyntaxColorProvider.PREF_DEFAULT,
                StringConverter.asString(defaultColor.getColorValue()));
        if (!StringConverter.asString(unreservedKeywordColor.getColorValue())
                .equals(preferenceStore.getString(SQLSyntaxColorProvider.PREF_UNRESERVED_KEYWORD))) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Syntax color of Unreserved Keyword in preferences changed from %s to %s",
                    preferenceStore.getString(SQLSyntaxColorProvider.PREF_UNRESERVED_KEYWORD),
                    StringConverter.asString(unreservedKeywordColor.getColorValue())));
        }
        preferenceStore.setValue(SQLSyntaxColorProvider.PREF_UNRESERVED_KEYWORD,
                StringConverter.asString(unreservedKeywordColor.getColorValue()));
        if (!StringConverter.asString(reservedKeywordColor.getColorValue()).equals(preferenceStore
                .getString(SQLSyntaxColorProvider.PREF_RESERVED_KEYWORD))) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Syntax color of Reserved Keyword in preferences changed from %s to %s",
                    preferenceStore.getString(SQLSyntaxColorProvider.PREF_RESERVED_KEYWORD),
                    StringConverter.asString(reservedKeywordColor.getColorValue())));
        }
        preferenceStore.setValue(SQLSyntaxColorProvider.PREF_RESERVED_KEYWORD,
                StringConverter.asString(reservedKeywordColor.getColorValue()));
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        PreferenceWrapper.getInstance().setPreferenceApply(true);
        performOk();
        getApplyButton().setEnabled(false);
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

}
