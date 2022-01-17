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

package com.huawei.mppdbide.view.utils;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface Preferencekeys.
 *
 * @since 3.0.0
 */
public interface Preferencekeys {

    /**
     * The autosave enable preference key.
     */
    String AUTOSAVE_ENABLE_PREFERENCE_KEY = "com.huawei.mppdbide.editor.autosave.preferences.enabled";

    /**
     * The autosave encryption preference flag.
     */
    String AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG = "com.huawei.mppdbide.editor.autosave.preferences.encryption";

    /**
     * The autosave interval preference key.
     */
    String AUTOSAVE_INTERVAL_PREFERENCE_KEY = "com.huawei.mppdbide.editor.autosave.preferences.interval";

    /**
     * The file limit for table data.
     */
    String FILE_LIMIT_FOR_TABLE_DATA = "com.huawei.mppdbide.importtabledata.filesize.preferences.limit";
    
    /**
     * The bytea limit for import
     */
    String FILE_LIMIT_FOR_BYTEA = "com.huawei.mppdbide.bytea.filesize.preferences.limit";

    /**
     * The file limit for sql.
     */
    String FILE_LIMIT_FOR_SQL = "com.huawei.mppdbide.sqlfile.filesize.preferences.limit";

    /**
     * Object count for lazy rendering in Object Browser.
     */
    String OBECT_COUNT_FOR_LAZY_RENDERING = "com.huawei.mppdbide.environment.sessionsetting.lazyrendering";
}
