/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.consts.TOOLTIPS;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIMandatoryAttribute.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class UIMandatoryAttribute {
    private static ControlDecoration deco;

    /**
     * Mandatory field.
     *
     * @param textName the text name
     * @param image the image
     * @param toolTripType the tool trip type
     */
    public static void mandatoryField(Text textName, Image image, TOOLTIPS toolTripType) {
        deco = new ControlDecoration(textName, SWT.TOP | SWT.LEFT);
        String tooltripName = null;
        switch (toolTripType) {

            case TABLENAME_TOOLTIPS: {
                tooltripName = MessageConfigLoader.getProperty(IMessagesConstants.ENTER_TABLE_NAME);
                break;
            }
            case COLUMNNAME_TOOLTIPS: {
                tooltripName = MessageConfigLoader.getProperty(IMessagesConstants.ENTER_COLUMN_NAME);
                break;
            }
            case INDEXNAME_TOOLTIPS: {
                tooltripName = MessageConfigLoader.getProperty(IMessagesConstants.ENTER_INDEX_NAME);
                break;
            }
            case ROLENAME_TOOLTIPS: {
                // add for userrole by martin
                tooltripName = MessageConfigLoader.getProperty(IMessagesConstants.ENTER_ROLE_NAME);
                break;
            }
            case PASSWORD_TOOLTIPS: {
                // add for userrole by martin
                tooltripName = MessageConfigLoader.getProperty(IMessagesConstants.ENTER_CIPHER);
                break;
            }
            case PASSWORD_TWICE_TOOLTIPS: {
                // add for userrole by martin
                tooltripName = MessageConfigLoader.getProperty(IMessagesConstants.ENTER_CIPHER_TWICE);
                break;
            }
            default: {
                break;
            }
        }
        deco.setDescriptionText(tooltripName);
        deco.setImage(image);

        deco.setShowOnlyOnFocus(false);
    }

    /**
     * Enable disable index name.
     *
     * @param value the value
     */
    public static void enableDisableIndexName(final boolean value) {
        if (value) {
            deco.show();
        } else {
            deco.hide();
        }
    }

}
