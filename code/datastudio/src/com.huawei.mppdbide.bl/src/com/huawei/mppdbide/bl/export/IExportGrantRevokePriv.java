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

package com.huawei.mppdbide.bl.export;

/** 
 * Title: IExportGrantRevokePriv
 * 
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