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

package org.opengauss.mppdbide.view.sequence.factory;

import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import org.opengauss.mppdbide.view.sequence.ISequenceInitializer;
import org.opengauss.mppdbide.view.sequence.olap.SequenceObjectInitializer;

/**
 * Title: SequenceFactory
 * 
 * Description:A factory for creating Sequence objects.
 * 
 * @since 3.0.0
 */
public class SequenceFactory {

    /**
     * Gets the sequence type object.
     *
     * @param obj the obj
     * @param shell the shell
     * @return the sequence type object
     */
    public ISequenceInitializer getSequenceTypeObject(Object obj, Shell shell) {
        if (obj instanceof SequenceObjectGroup) {
            SequenceObjectGroup sequenceObjectGroup = (SequenceObjectGroup) obj;
            SequenceObjectInitializer initObj = new SequenceObjectInitializer(sequenceObjectGroup, shell);
            return initObj;

        }
        return null;

    }

}
