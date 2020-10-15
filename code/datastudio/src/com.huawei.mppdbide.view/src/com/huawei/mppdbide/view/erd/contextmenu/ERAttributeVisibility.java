/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.erd.contextmenu;

import org.eclipse.jface.preference.IPreferenceStore;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;

/**
 * Title: ERAttributeVisibility
 * 
 * Description: The enum category of show attribute.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00518937
 * @version [DataStudio 6.5.1, Nov 5, 2019]
 * @since Nov 5, 2019
 */
public enum ERAttributeVisibility {

    /** 
     * The all. 
     */
    ALL(1, MessageConfigLoader.getProperty(IMessagesConstants.ATTRIBUTE_VIS_ALL)),
    
    /** 
     * The any keys. 
     */
    ANY_KEYS(2, MessageConfigLoader.getProperty(IMessagesConstants.ATTRIBUTE_VIS_ANY_KEY)),
    
    /** 
     * The primary key. 
     */
    PRIMARY_KEY(4, MessageConfigLoader.getProperty(IMessagesConstants.ATTRIBUTE_VIS_PRIMARY_KEY)),
    
    /** 
     * The none. 
     */
    NONE(8, MessageConfigLoader.getProperty(IMessagesConstants.ATTRIBUTE_VIS_NONE));

    private static final IPreferenceStore PREFERENCE_STORE = PreferenceWrapper.getInstance().getPreferenceStore();

    private final int value;
    private final String title;

    /**
     * Instantiates a new ER attribute visibility.
     *
     * @param value the value
     * @param title the title
     */
    ERAttributeVisibility(int value, String title) {
        this.value = value;
        this.title = title;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the default visibility.
     *
     * @return the default visibility
     */
    public static ERAttributeVisibility getDefaultVisibility() {
        String attrVisibilityString = PREFERENCE_STORE.getString(ERPreferencekeys.PREF_ATTR_VISIBILITY);
        if (!(null == attrVisibilityString || attrVisibilityString.length() == 0)) {
            return ERAttributeVisibility.valueOf(attrVisibilityString);
        }

        return ALL;
    }

    /**
     * Sets the default visibility.
     *
     * @param visibility the new default visibility
     */
    static void setDefaultVisiblity(ERAttributeVisibility visibility) {
        PREFERENCE_STORE.setValue(ERPreferencekeys.PREF_ATTR_VISIBILITY, visibility.name());
    }

    /**
     * Checks if is all.
     *
     * @return true, if is all
     */
    public static boolean isAll() {
        return ALL == getDefaultVisibility();
    }

    /**
     * Checks if is any key.
     *
     * @return true, if is any key
     */
    public static boolean isAnyKey() {
        return ANY_KEYS == getDefaultVisibility();
    }

    /**
     * Checks if is primary key.
     *
     * @return true, if is primary key
     */
    public static boolean isPrimaryKey() {
        return PRIMARY_KEY == getDefaultVisibility();
    }

    /**
     * Checks if is none.
     *
     * @return true, if is none
     */
    public static boolean isNone() {
        return NONE == getDefaultVisibility();
    }
}
