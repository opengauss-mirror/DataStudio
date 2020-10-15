/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence.olap;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.presentation.SequenceDataCore;
import com.huawei.mppdbide.view.sequence.ISequenceInitializer;

/**
 * 
 * Title: class
 * 
 * Description: The Class SequenceObjectInitializer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
