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
 * @since 3.0.0
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
