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

package org.opengauss.mppdbide.view.save;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler;

import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.ui.saveif.ISaveablePart;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSSaveHandler.
 *
 * @since 3.0.0
 */
public class DSSaveHandler implements ISaveHandler {

    /**
     * Save.
     *
     * @param dirtyPart the dirty part
     * @param confirm the confirm
     * @return true, if successful
     */
    @Override
    public boolean save(MPart dirtyPart, boolean confirm) {
        if (confirm) {
            Save result = promptToSave(dirtyPart);
            if (Save.NO == result) {
                return true;
            } else if (Save.CANCEL == result) {
                return false;
            } else if (Save.YES == result) {
                Object obj = dirtyPart.getObject();
                if (obj instanceof ISaveablePart) {
                    ISaveablePart dirtyWindow = (ISaveablePart) obj;
                    dirtyWindow.savePart();
                    return true;
                }
            }
        }

        Object client = dirtyPart.getObject();
        try {
            ContextInjectionFactory.invoke(client, Persist.class, dirtyPart.getContext());
        } catch (InjectionException exception) {
            MPPDBIDELoggerUtility.error("prompt to save dirty part failed", exception);
            return false;
        }

        return true;
    }

    /**
     * Save parts.
     *
     * @param dirtyParts the dirty parts
     * @param confirm the confirm
     * @return true, if successful
     */
    @Override
    public boolean saveParts(Collection<MPart> dirtyParts, boolean confirm) {
        Save[] decisions = null;
        if (confirm) {
            List<MPart> dirtyPartsList = Collections.unmodifiableList(new ArrayList<MPart>(dirtyParts));
            decisions = promptToSave(dirtyPartsList);
            for (Save decision : decisions) {
                if (decision == Save.CANCEL) {
                    return false;
                }
            }

            int index = 0;
            while (index < decisions.length) {
                if (decisions[index] == Save.YES) {
                    if (!save(dirtyPartsList.get(index), true)) {
                        return false;
                    }
                }

                index++;
            }

            return true;
        }

        for (MPart dirtyPart : dirtyParts) {
            if (!save(dirtyPart, false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prompt to save.
     *
     * @param dirtyPart the dirty part
     * @return the save
     */
    @Override
    public Save promptToSave(MPart dirtyPart) {
        Object obj = dirtyPart.getObject();
        if (obj instanceof ISaveablePart) {
            ISaveablePart dirtyWindow = (ISaveablePart) obj;
            return dirtyWindow.promptUserToSave();
        }

        return Save.YES;
    }

    /**
     * Prompt to save.
     *
     * @param dirtyParts the dirty parts
     * @return the save[]
     */
    @Override
    public Save[] promptToSave(Collection<MPart> dirtyParts) {
        Save[] rc = new Save[dirtyParts.size()];
        for (int i = 0; i < rc.length; i++) {
            rc[i] = Save.YES;
        }
        return rc;
    }
}
