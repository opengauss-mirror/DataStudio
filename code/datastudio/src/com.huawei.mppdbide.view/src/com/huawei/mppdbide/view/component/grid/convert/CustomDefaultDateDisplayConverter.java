/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
