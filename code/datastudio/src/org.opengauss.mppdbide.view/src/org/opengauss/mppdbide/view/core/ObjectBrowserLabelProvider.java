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

package org.opengauss.mppdbide.view.core;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import org.opengauss.mppdbide.bl.serverdatacache.GaussOLAPDBMSObject;
import org.opengauss.mppdbide.bl.serverdatacache.ShowMoreObject;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserLabelProvider.
 *
 * @since 3.0.0
 */
public class ObjectBrowserLabelProvider extends ColumnLabelProvider {
    private ObjectBrowserLabelProviderForGaussOLAP gaussOlapLp = null;

    /**
     * Instantiates a new object browser label provider.
     */
    public ObjectBrowserLabelProvider() {
        gaussOlapLp = new ObjectBrowserLabelProviderForGaussOLAP();
    }

    /**
     * Gets the text.
     *
     * @param element the element
     * @return the text
     */
    @Override
    public String getText(Object element) {
        if (element instanceof GaussOLAPDBMSObject) {
            if (gaussOlapLp != null) {
                return gaussOlapLp.getText(element);
            }
        }
        if (element instanceof LoadingUIElement) {
            return ((LoadingUIElement) element).getDisplayName();
        } else if (element instanceof ShowMoreObject && gaussOlapLp != null) {
            return gaussOlapLp.getText(element);
        }

        // Will be hit only when new datatypes are displayed in further
        // iterations
        return MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_LABEL_MSG);
    }

    /**
     * Gets the tool tip text.
     *
     * @param element the element
     * @return the tool tip text
     */
    @Override
    public String getToolTipText(Object element) {
        if (element instanceof GaussOLAPDBMSObject) {
            if (gaussOlapLp != null) {
                return gaussOlapLp.getToolTipText(element);
            }
        } else if (element instanceof ShowMoreObject) {
            return gaussOlapLp.getToolTipText(element);
        }
        return null;
    }

    /**
     * Gets the image.
     *
     * @param element the element
     * @return the image
     */
    @Override
    public Image getImage(Object element) {
        if (element instanceof GaussOLAPDBMSObject) {
            if (gaussOlapLp != null) {
                return gaussOlapLp.getImage(element);
            }
        } else if (element instanceof LoadingUIElement) {
            return ((LoadingUIElement) element).getDisplayimage();
        } else if (element instanceof ShowMoreObject) {
            return gaussOlapLp.getImage(element);
        }
        return null;
    }
}
