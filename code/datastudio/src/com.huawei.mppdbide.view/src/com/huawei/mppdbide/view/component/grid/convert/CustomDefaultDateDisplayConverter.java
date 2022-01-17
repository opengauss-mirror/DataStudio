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

package com.huawei.mppdbide.view.component.grid.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDateDisplayConverter;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomDefaultDateDisplayConverter.
 *
 * @since 3.0.0
 */
public class CustomDefaultDateDisplayConverter extends DefaultDateDisplayConverter {
    private final SimpleDateFormat sdf;
    private final Object LOCK = new Object();

    /**
     * Instantiates a new custom default date display converter.
     *
     * @param format the format
     */
    public CustomDefaultDateDisplayConverter(String format) {
        super(format);
        this.sdf = new SimpleDateFormat(format);
    }

    /**
     * Canonical to display value.
     *
     * @param canonicalValue the canonical value
     * @return the object
     */
    @Override
    public Object canonicalToDisplayValue(Object canonicalValue) {
        if (canonicalValue == null) {
            return canonicalValue;
        }

        if (canonicalValue.toString().isEmpty()) {
            return canonicalValue;
        }

        if (canonicalValue instanceof String) {
            Date date = null;
            try {
                synchronized (LOCK) {
                    date = sdf.parse(canonicalValue.toString());
                }
            } catch (ParseException exception) {
                MPPDBIDELoggerUtility.error("parse cell value fail", exception);
            }
            return super.canonicalToDisplayValue(date == null ? canonicalValue : date);
        } else {
            return super.canonicalToDisplayValue(canonicalValue);
        }
    }

}
