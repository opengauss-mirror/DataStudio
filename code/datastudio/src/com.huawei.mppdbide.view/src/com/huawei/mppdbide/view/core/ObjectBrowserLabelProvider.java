/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.huawei.mppdbide.bl.serverdatacache.GaussOLAPDBMSObject;
import com.huawei.mppdbide.bl.serverdatacache.ShowMoreObject;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserLabelProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
