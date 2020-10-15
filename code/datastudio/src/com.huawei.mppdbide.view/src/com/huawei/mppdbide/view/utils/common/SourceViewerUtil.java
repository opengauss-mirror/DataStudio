/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils.common;

import java.util.Iterator;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.view.data.DSViewApplicationObjectManager;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.ui.uiif.PLSourceEditorIf;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SourceViewerUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SourceViewerUtil {

    /**
     * Close source viewer by id.
     *
     * @param id the id
     */
    public static void closeSourceViewerById(String id) {
        List<MPartStack> stacksList = DSViewApplicationObjectManager.getInstance().getModelService().findElements(
                DSViewApplicationObjectManager.getInstance().getApplication(), UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (null != stacksList) {
            List<MStackElement> children = stacksList.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> sourceViewersIt = children.iterator();
                MStackElement stackElement = null;
                MPart mPart = null;
                boolean hasNextRc = sourceViewersIt.hasNext();
                while (hasNextRc) {
                    stackElement = sourceViewersIt.next();
                    mPart = (MPart) stackElement;
                    String elementId = mPart.getElementId();
                    if ((mPart.getObject() instanceof PLSourceEditorIf) && null != elementId && elementId.equals(id)) {
                        PLSourceEditorIf sourceEditor = (PLSourceEditorIf) mPart.getObject();
                        removeSourceViewerId(id, sourceEditor.getDebugObjectType());
                        sourceEditor.destroy();
                        break;
                    }
                    hasNextRc = sourceViewersIt.hasNext();
                }
            }
        }
    }

    /**
     * Adds the source viewer id.
     *
     * @param id the id
     */
    public static void addSourceViewerId(String id) {
        DSViewDataManager.getInstance().addSourceViewerId(id);
    }

    /**
     * Removes the source viewer id only.
     *
     * @param id the id
     */
    public static void removeSourceViewerIdOnly(String id) {
        DSViewDataManager.getInstance().removeSourceViewerIdOnly(id);

    }

    /**
     * Removes the source viewer id.
     *
     * @param id the id
     */
    public static void removeSourceViewerId(String id) {
        removeSourceViewerIdOnly(id);
        // DTS2013112604688 start
        UICommonUtil.removePartFromStack(id);
        // DTS2013112604688 End
    }

    /**
     * Removes the source viewer id.
     *
     * @param id the id
     * @param type the type
     */
    public static void removeSourceViewerId(String id, OBJECTTYPE type) {
        removeSourceViewerIdOnly(id);
        // DTS2013112604688 start
        UICommonUtil.removePartFromStack(id, type);
        // DTS2013112604688 End

    }

}
