/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface Preferencekeys.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
