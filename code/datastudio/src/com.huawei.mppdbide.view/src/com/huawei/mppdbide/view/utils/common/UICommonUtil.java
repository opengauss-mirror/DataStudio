/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.data.DSViewApplicationObjectManager;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class UICommonUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class UICommonUtil {

    /**
     * Removes the part from stack.
     *
     * @param id the id
     */
    public static void removePartFromStack(String id) {
        MPart part = DSViewApplicationObjectManager.getInstance().getPartService().findPart(id);
        if (null != part) {
            List<MPartStack> stacks = DSViewApplicationObjectManager.getInstance().getModelService().findElements(
                    DSViewApplicationObjectManager.getInstance().getApplication(), UIConstants.PARTSTACK_ID_EDITOR,
                    MPartStack.class, null);
            if (null != stacks) {
                List<MStackElement> children = stacks.get(0).getChildren();
                if (null != children) {
                    children.remove(part);
                }
            }
        }
    }

    /**
     * Removes the part from stack.
     *
     * @param id the id
     * @param type the type
     */
    public static void removePartFromStack(String id, OBJECTTYPE type) {

        Collection<MPart> parts = DSViewApplicationObjectManager.getInstance().getPartService().getParts();
        MPart prt = null;
        for (Iterator<MPart> itrtor = parts.iterator(); itrtor.hasNext();) {
            MPart mPart = (MPart) itrtor.next();

            String mprtId = mPart.getElementId();
            if (mprtId != null && mprtId.equals(id)) {
                String objectType = mPart.getProperties().get(OBJECTTYPE.class.getSimpleName());

                if (objectType != null && objectType.equals(type.name())) {
                    prt = mPart;
                    break;
                }
            }

        }

        if (null != prt) {
            List<MPartStack> stacks = DSViewApplicationObjectManager.getInstance().getModelService().findElements(
                    DSViewApplicationObjectManager.getInstance().getApplication(), UIConstants.PARTSTACK_ID_EDITOR,
                    MPartStack.class, null);
            if (null != stacks) {
                List<MStackElement> children = stacks.get(0).getChildren();
                if (null != children) {
                    children.remove(prt);
                }
            }
        }
    }

    /**
     * Gets the un quoted identifier.
     *
     * @param queryString the query string
     * @return the un quoted identifier
     */
    public static String getUnQuotedIdentifierOLAP(String queryString) {
        String str = queryString;
        if (str.startsWith(MPPDBIDEConstants.DOUBLE_QUOTE) && str.endsWith(MPPDBIDEConstants.DOUBLE_QUOTE)) {
            return str.substring(MPPDBIDEConstants.DOUBLE_QUOTE.length(),
                    str.length() - MPPDBIDEConstants.DOUBLE_QUOTE.length());
        } else {
            str = str.toLowerCase(Locale.ENGLISH);
        }
        return str;
    }
}
