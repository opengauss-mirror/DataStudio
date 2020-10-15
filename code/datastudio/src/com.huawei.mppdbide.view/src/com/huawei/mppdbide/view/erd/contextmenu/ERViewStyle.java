/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.erd.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;

/**
 * Title: ERViewStyle
 * 
 * Description: The enum category of view styles.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00518937
 * @version [DataStudio 6.5.1, 2019 M10 21]
 * @since 2019 M10 21
 */
public enum ERViewStyle {

    /** 
     * The icons. 
     */
    ICONS(1, MessageConfigLoader.getProperty(IMessagesConstants.SHOW_ICONS)),
    
    /** 
     * The types. 
     */
    TYPES(2, MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DATA_TYPES)),
    
    /** 
     * The nullability. 
     */
    NULLABILITY(4, MessageConfigLoader.getProperty(IMessagesConstants.SHOW_NULLABILITY)),
    
    /** 
     * The comments. 
     */
    COMMENTS(8, MessageConfigLoader.getProperty(IMessagesConstants.SHOW_COMMENTS)),
    
    /** 
     * The entity fqn. 
     */
    ENTITY_FQN(16, MessageConfigLoader.getProperty(IMessagesConstants.SHOW_FULLY_QUALIFIED_NAMES));

    private static final IPreferenceStore PREFERENCE_STORE = PreferenceWrapper.getInstance().getPreferenceStore();
    private static List<ERViewStyle> attributeStyles = getDefaultStyles();

    private final int value;
    private final String title;

    /**
     * Instantiates a new ER view style.
     *
     * @param value the value
     * @param title the title
     */
    ERViewStyle(int value, String title) {
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
     * Gets the default styles.
     *
     * @return the default styles
     */
    public static List<ERViewStyle> getDefaultStyles() {
        String attrString = PREFERENCE_STORE.getString(ERPreferencekeys.PREF_ATTR_STYLES);
        List<ERViewStyle> vsList = new ArrayList<>();
        if (!(attrString == null || attrString.length() == 0)) {
            String[] psList = attrString.split(",");
            for (int i = 0; i < psList.length; i++) {
                vsList.add(ERViewStyle.valueOf(psList[i]));
            }
        } else {
            vsList.add(ICONS);
        }

        return vsList;
    }

    /**
     * Sets the default styles.
     *
     * @param styles the new default styles
     */
    private static void setDefaultStyles(List<ERViewStyle> styles) {
        StringBuffer stylesString = new StringBuffer();
        for (ERViewStyle style : styles) {
            if (!(stylesString.length() == 0)) {
                stylesString.append(",");
            }
            stylesString.append(style.name());
        }
        PREFERENCE_STORE.setValue(ERPreferencekeys.PREF_ATTR_STYLES, stylesString.toString());
    }

    /**
     * Sets the attribute style.
     *
     * @param style the style
     * @param enable the enable
     */
    public static void setAttributeStyle(ERViewStyle style, boolean enable) {
        if (enable) {
            attributeStyles.add(style);
        } else {
            attributeStyles.remove(style);
        }
        setDefaultStyles(attributeStyles);
    }

    /**
     * Gets the attribute styles.
     *
     * @return the attribute styles
     */
    public static List<ERViewStyle> getAttributeStyles() {
        return attributeStyles;
    }

    /**
     * Checks if is contain view style.
     *
     * @param vsList the vs list
     * @param style the style
     * @return true, if is contain view style
     */
    public static boolean isContainViewStyle(List<ERViewStyle> vsList, ERViewStyle style) {
        if (null == vsList) {
            return false;
        }

        return vsList.contains(style);
    }

    /**
     * Checks if is show icons.
     *
     * @return true, if is show icons
     */
    public static boolean isShowIcons() {
        return getDefaultStyles().contains(ICONS);
    }

    /**
     * Checks if is show data types.
     *
     * @return true, if is show data types
     */
    public static boolean isShowDataTypes() {
        return getDefaultStyles().contains(TYPES);
    }

    /**
     * Checks if is show nullability.
     *
     * @return true, if is show nullability
     */
    public static boolean isShowNullability() {
        return getDefaultStyles().contains(NULLABILITY);
    }

    /**
     * Checks if is show comments.
     *
     * @return true, if is show comments
     */
    public static boolean isShowComments() {
        return getDefaultStyles().contains(COMMENTS);
    }

    /**
     * Checks if is show fully qualified names.
     *
     * @return true, if is show fully qualified names
     */
    public static boolean isShowFullyQualifiedNames() {
        return getDefaultStyles().contains(ENTITY_FQN);
    }
}
