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
 * @since 3.0.0
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
