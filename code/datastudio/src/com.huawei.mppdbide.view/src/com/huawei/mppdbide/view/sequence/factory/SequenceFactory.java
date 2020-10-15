/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence.factory;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.view.sequence.ISequenceInitializer;
import com.huawei.mppdbide.view.sequence.olap.SequenceObjectInitializer;

/**
 * Title: SequenceFactory
 * 
 * Description:A factory for creating Sequence objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
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
