/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.stmtformatter.type;

import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.format.processor.ProcessorFactory;
import com.huawei.mppdbide.gauss.format.processor.utils.StmtKeywordAlignUtil;
import com.huawei.mppdbide.gauss.format.utils.FormatterUtils;
import com.huawei.mppdbide.gauss.format.utils.wrapper.FormatStringWrapper;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TSelectSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: SelectStmtFormatter
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
 */

public class SelectStmtFormatter extends AbstractStmtFormatter<TSelectSqlStatement> {

    /**
     * Do format.
     *
     * @param sql the sql
     * @return the string
     */
    protected String doFormat(TSelectSqlStatement sql) {

        int maxKeywordWidth = StmtKeywordAlignUtil.getMaxKeywordWidth(sql, getOptions());

        OptionsProcessData lOptionsProcessData = new OptionsProcessData();

        lOptionsProcessData.setMaxKeywordLength(maxKeywordWidth);
        lOptionsProcessData.setOffSet(0);
        lOptionsProcessData.setParentOffSet(0);

        AbstractProcessor<TParseTreeNode> proc = ProcessorFactory.getProcessor(sql);
        if (null != proc) {
            proc.setOptions(getOptions());
            proc.process(sql, getOptions(), lOptionsProcessData);
        }

        FormatStringWrapper formatWrapper = new FormatStringWrapper();

        FormatterUtils.prepareFormatString(sql, formatWrapper, getOptions());

        return formatWrapper.toString();

    }

}
