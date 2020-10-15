/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.stmtformatter;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.format.processor.ProcessorFactory;
import com.huawei.mppdbide.gauss.format.utils.FormatterUtils;
import com.huawei.mppdbide.gauss.format.utils.wrapper.FormatStringWrapper;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: FormatterFactory Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class FormatterFactory {

    /**
     * Format.
     *
     * @param sqlStmt the sql stmt
     * @param options the options
     * @param formatterOffset the formatter offset
     * @return the string
     */
    public static String format(TCustomSqlStatement sqlStmt, FmtOptionsIf options, int formatterOffset) {

        OptionsProcessData lOptionsProcessData = new OptionsProcessData();

        lOptionsProcessData.setMaxKeywordLength(0);
        lOptionsProcessData.setOffSet(formatterOffset);
        lOptionsProcessData.setParentOffSet(0);

        AbstractProcessor<TParseTreeNode> proc = ProcessorFactory.getProcessor(sqlStmt);
        if (null != proc) {
            proc.setOptions(options);
            proc.process(sqlStmt, options, lOptionsProcessData);
        }

        FormatStringWrapper formatWrapper = new FormatStringWrapper();

        FormatterUtils.prepareFormatString(sqlStmt, formatWrapper, options);

        return formatWrapper.toString();
    }

}
