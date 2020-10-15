/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.SortedMap;

import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.core.sourceeditor.SQLContentAssistProcessor;

/**
 * 
 * Title: class
 * 
 * Description: The Class CopyAutoSuggestData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
