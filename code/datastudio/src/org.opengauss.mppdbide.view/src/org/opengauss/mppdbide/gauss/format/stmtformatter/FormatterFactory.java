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

package org.opengauss.mppdbide.gauss.format.stmtformatter;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractProcessor;
import org.opengauss.mppdbide.gauss.format.processor.ProcessorFactory;
import org.opengauss.mppdbide.gauss.format.utils.FormatterUtils;
import org.opengauss.mppdbide.gauss.format.utils.wrapper.FormatStringWrapper;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: FormatterFactory
 *
 * @since 3.0.0
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
