/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.export;

/** 
 * Title: IExportGrantRevokePriv
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 10-Sep-2020]
 * @since 10-Sep-2020
 */

public interface IExportGrantRevokePriv {
    String PRIV_EXECUTE = "EXECUTE"; // 'X'
    String PRIV_USAGE = "USAGE"; // 'U'
    String PRIV_CREATE = "CREATE"; // 'C'
    String PRIV_UPDATE = "UPDATE"; // 'w'
    String PRIV_INSERT = "INSERT"; // 'a'
    String PRIV_REFERENCES = "REFERENCES"; // 'x'
    String PRIV_DELETE = "DELETE"; // 'd'
    String PRIV_TRIGGER = "TRIGGER"; // 't'
    String PRIV_TRUNCATE = "TRUNCATE"; // 'D'

}