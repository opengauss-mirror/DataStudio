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

package org.opengauss.mppdbide.view.sequence.olap;

import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import org.opengauss.mppdbide.presentation.SequenceDataCore;
import org.opengauss.mppdbide.view.sequence.ISequenceInitializer;

/**
 * 
 * Title: class
 * 
 * Description: The Class SequenceObjectInitializer.
 *
 * @since 3.0.0
 */
public class SequenceObjectInitializer implements ISequenceInitializer {

    private SequenceObjectGroup sequenceObjectGroup;
    private Namespace ns;
    private Shell shell;

    /**
     * Instantiates a new sequence object initializer.
     *
     * @param sequenceObjectGroup the sequence object group
     * @param shell the shell
     */
    public SequenceObjectInitializer(SequenceObjectGroup sequenceObjectGroup, Shell shell) {
        this.sequenceObjectGroup = sequenceObjectGroup;
        this.shell = shell;
    }

    /**
     * Inits the configuration.
     */
    @Override
    public void initConfiguration() {
        ns = (Namespace) sequenceObjectGroup.getParent();

    }

    /**
     * Open dilog.
     */
    @Override
    public void openDilog() {
        SequenceDataCore sequenceDataCore = new SequenceDataCore(ns);
        CreateSequenceDialoge dlog = new CreateSequenceDialoge(shell, sequenceDataCore, ns);
        dlog.open();

    }

}
