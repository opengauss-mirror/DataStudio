/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.editor.extension;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IEditorExtension.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IEditorExtension {

    /**
     * Format queries.
     *
     * @param input the input
     * @param startOffset the start offset
     * @param lineDelimiter the line delimiter
     * @return the string
     */
    String formatQueries(String input, int startOffset, String lineDelimiter);
}
