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
 * @since 3.0.0
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
