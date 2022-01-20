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

package com.huawei.mppdbide.view.aliasparser;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IAliasParserJobManager.
 *
 * @since 3.0.0
 */
public interface IAliasParserJobManager {

    /**
     * Submit alias parser job.
     *
     * @param job the job
     */
    public void submitAliasParserJob(AliasRequestResponsePacket job);

    /**
     * Cancel alias parser job.
     *
     * @param packetId the packet id
     */
    public void cancelAliasParserJob(int packetId);
}
