/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.view.ui.DBAssistantWindow;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBAssistantOption.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DBAssistantOption {

    /**
     * The Constant DB_ASSISTANT_ENABLE.
     */
    public static final String DB_ASSISTANT_ENABLE = "environment.sessionsetting.datastudiodbassistantenable";

    /**
     * Sets the default DB assistant preferences.
     *
     * @param ps the new default DB assistant preferences
     */
    public static void setDefaultDBAssistantPreferences(PreferenceStore ps) {
        ps.setDefault(DB_ASSISTANT_ENABLE, true);
    }

    /**
     * Update DB assistant enable.
     *
     * @param ps the ps
     */
    public static void updateDBAssistantEnable(IPreferenceStore ps) {
        // update SQL assistant based on preference store when at least one
        // terminal is open
        if (!DBAssistantWindow.isAllTerminalsClosed()) {
            DBAssistantWindow.setEnableB(ps.getBoolean(DB_ASSISTANT_ENABLE));
        }
    }

    /**
     * Update DB assistant enable once.
     *
     * @param ps the ps
     */
    public static void updateDBAssistantEnableOnce(IPreferenceStore ps) {
        DBAssistantWindow.setEnableA(ps.getBoolean(DB_ASSISTANT_ENABLE));
        if (!ps.getBoolean(DB_ASSISTANT_ENABLE)) {
            MPart part = (MPart) EclipseInjections.getInstance().getMS().find(UIConstants.UI_PART_ID_SQL_ASSISTANT,
                    EclipseInjections.getInstance().getApp());
            if (part != null) {
                part.setToBeRendered(false);
            }
        }

    }
}
