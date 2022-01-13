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

package com.huawei.mppdbide.view.component.grid;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.huawei.mppdbide.presentation.objectproperties.PropertiesConstants;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridUIUtils.
 *
 * @since 3.0.0
 */
public class GridUIUtils {

    /**
     * Hide or show UI component
     * 
     * @param composite
     * @param isHide
     * @param focusElem
     */
    private static final String GEN_TAB = "General";
    private static final String COLUMNS_TAB = "Columns";
    private static final String CONSTRAINT_TAB = "Constraints";
    private static final String INDEX_TAB = "Index";
    private static final String GEN_TAB_VALUE = MessageConfigLoader
            .getProperty(IMessagesConstants.PROPERTIES_WID_VALUE);

    /**
     * Toggle composite section visibility.
     *
     * @param composite the composite
     * @param isHide the is hide
     * @param focusElem the focus elem
     * @param parentResizeSpecial the parent resize special
     */
    public static void toggleCompositeSectionVisibility(Composite composite, boolean isHide, Control focusElem,
            boolean parentResizeSpecial) {
        composite.setVisible(!isHide);
        Object layoutData = composite.getLayoutData();
        if (null != layoutData && layoutData instanceof GridData) {
            ((GridData) layoutData).exclude = isHide;
        }

        if (parentResizeSpecial) {
            /*
             * this is only to be used for explain plan tabs where composite
             * structure is more layered.
             */
            final GridData data = (GridData) composite.getParent().getParent().getLayoutData();
            data.heightHint -= 20;
            composite.getParent().getParent().layout();
        } else {
            /*
             * resulttab case
             */
            composite.getParent().layout(true, true);
        }

        if (!isHide && null != focusElem) {
            focusElem.setFocus();
        }
    }

    /**
     * Checks if is datatype edit supported.
     *
     * @param sqlType the sql type
     * @param precision the precision
     * @return true, if is datatype edit supported
     */
    public static boolean isDatatypeEditSupported(String sqlType, int precision) {
        switch (sqlType.toLowerCase(Locale.ENGLISH)) {
            case "bpchar":
            case "char":
            case "varchar":
            case "text":
            case "int4":
            case "int2":
            case "int8":
            case "date":
            case "numeric":
            case "decimal":
            case "float8":
            case "float4":
            case "time":
            case "timestamp":
            case "bool":
            case "serial":
            case "integer":
            case "bigint":
            case "real":
            case "number":
            case "boolean":
            case MPPDBIDEConstants.BYTEA: {
                return true;
            }
            case "bit": {
                if (precision > 1) {
                    return false;
                }
                return true;
            }
            default: {
                return false;
            }
        }

    }

    /**
     * Checks if is editable properties attributes.
     *
     * @param propertyName the property name
     * @param columnName the column name
     * @param rowData the row data
     * @return true, if is editable properties attributes
     */
    public static boolean isEditablePropertiesAttributes(String propertyName, String columnName, String rowData) {
        switch (propertyName) {
            case GEN_TAB: {
                return isGeneralEditablePropAttributes(columnName, rowData);
            }

            case COLUMNS_TAB: {
                return true;
            }
            case CONSTRAINT_TAB: {
                return isConstraintEditablePropAttributes(columnName);
            }
            case INDEX_TAB: {
                return isIndexTabEditablePropAttributes(columnName);
            }
            case PropertiesConstants.USER_ROLE_PROPERTY_TAB_GENERAL: {
                return isUserRolePropTabGeneralEditableAttributes(columnName, rowData);
            }
            case PropertiesConstants.USER_ROLE_PROPERTY_TAB_PRIVILEGE: {
                return isUserRolePropTabPrivilegeEditableAttributes(columnName);
            }
            case PropertiesConstants.USER_ROLE_PROPERTY_TAB_MEMBERSHIP: {
                return isUserRolePropTabMembershipEditableAttributes(columnName);
            }
            default: {
                break;
            }

        }

        return false;
    }

    private static boolean isUserRolePropTabMembershipEditableAttributes(String columnName) {
        if (columnName
                .equals(MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_USER_ROLE))) {
            return false;
        }
        return true;
    }

    private static boolean isUserRolePropTabPrivilegeEditableAttributes(String columnName) {
        if (columnName
                .equals(MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_PROPERTY))) {
            return false;
        }
        return true;
    }

    private static boolean isIndexTabEditablePropAttributes(String columnName) {
        if (columnName.equals(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_INDEX_INDEXNAME))) {
            return true;
        }
        return false;
    }

    private static boolean isConstraintEditablePropAttributes(String columnName) {
        if (columnName
                .equals(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_CONSTRAINT_CONSTRAINTNAME))) {
            return true;
        }
        return false;
    }

    private static boolean isUserRolePropTabGeneralEditableAttributes(String columnName, String rowData) {
        if (columnName.equals(MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_VALUE))) {
            if (rowData != null && (rowData.equals(MessageConfigLoader.getProperty(IMessagesConstants.OID_MSG)))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isGeneralEditablePropAttributes(String columnName, String rowData) {
        if (rowData != null && rowData.equals(MessageConfigLoader.getProperty(IMessagesConstants.DESC_MSG))
                && columnName.equals(GEN_TAB_VALUE)) {
            return true;
        }
        return false;
    }

    /**
     * Truncate string.
     *
     * @param str the str
     * @param maxLength the max length
     * @return the string
     */
    public static String truncateString(String str, int maxLength) {
        if (str != null && str.length() > maxLength) {
            return str.substring(0, maxLength);
        }
        return str;
    }

    /**
     * Creates the horizontal line.
     *
     * @param parent the parent
     * @return the label
     */
    public static Label createHorizontalLine(Composite parent) {
        return createHorizontalLine(parent, 1, 0);
    }

    /**
     * Creates the horizontal line.
     *
     * @param parent the parent
     * @param hSpan the h span
     * @param vIndent the v indent
     * @return the label
     */
    public static Label createHorizontalLine(Composite parent, int hSpan, int vIndent) {
        Label horizontalLine = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
        gd.horizontalSpan = hSpan;
        gd.verticalIndent = vIndent;
        horizontalLine.setLayoutData(gd);
        return horizontalLine;
    }

}
