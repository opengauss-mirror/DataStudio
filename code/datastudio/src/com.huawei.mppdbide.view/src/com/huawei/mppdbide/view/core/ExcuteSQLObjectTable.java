/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExcuteSQLObjectTable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExcuteSQLObjectTable extends ParameterInputDialog {

    /**
     * Instantiates a new excute SQL object table.
     *
     * @param parentShell the parent shell
     */
    public ExcuteSQLObjectTable(Shell parentShell) {
        super(parentShell);
        setDefaultImage(IconUtility.getIconImage(IiconPath.ICO_TOOL_128X128, this.getClass()));
    }

}
