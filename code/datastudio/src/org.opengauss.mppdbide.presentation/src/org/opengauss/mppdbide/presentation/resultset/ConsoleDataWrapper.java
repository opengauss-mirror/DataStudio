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

package org.opengauss.mppdbide.presentation.resultset;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.presentation.resultsetif.IConsoleResult;

/**
 * Title: class Description: The Class ConsoleDataWrapper.
 *
 * @since 3.0.0
 */
public class ConsoleDataWrapper extends ArrayList<String> implements IConsoleResult {

    private static final long serialVersionUID = 1L;

    private List<String> hintMessage = new ArrayList<>(1);

    /**
     * Instantiates a new console data wrapper.
     */
    public ConsoleDataWrapper() {
        super();
    }

    /**
     * get the list of hint messages
     * 
     * @return return the hint message list
     */
    @Override
    public List<String> getHintMessages() {
        return hintMessage;
    }

}
