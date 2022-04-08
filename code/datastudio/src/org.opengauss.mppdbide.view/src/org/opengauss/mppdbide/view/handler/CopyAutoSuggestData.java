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

package org.opengauss.mppdbide.view.handler;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.SortedMap;

import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLContentAssistProcessor;

/**
 * 
 * Title: class
 * 
 * Description: The Class CopyAutoSuggestData.
 *
 * @since 3.0.0
 */
public class CopyAutoSuggestData {
    private Clipboard system;
    private StringSelection stsel;

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        LinkedHashMap<String, ServerObject> fetchMap = SQLContentAssistProcessor.getMap();
        StringBuffer sbf = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (null != fetchMap) {
            Set<String> set = fetchMap.keySet();
            system = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (system == null) {
                return;
            }
            for (String s : set) {
                sbf.append(s);
                sbf.append(MPPDBIDEConstants.LINE_SEPARATOR);
            }
            stsel = new StringSelection(sbf.toString());
            system.setContents(stsel, stsel);

        }
    }
}
