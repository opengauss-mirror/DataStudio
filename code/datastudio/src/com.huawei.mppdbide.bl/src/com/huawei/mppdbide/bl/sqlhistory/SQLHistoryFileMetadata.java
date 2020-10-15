/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.sqlhistory;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * Title: SQLHistoryFileMetadata
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 21-Apr-2020]
 * @since 21-Apr-2020
 */
public class SQLHistoryFileMetadata implements Serializable {
    private static final long serialVersionUID = 3505657588142448599L;
    private ArrayList<String> hisFileMetadata = null;

    public ArrayList<String> getHisFileMetadata() {
        return hisFileMetadata;
    }

    public void setHisFileMetadata(ArrayList<String> hisFileMetadata) {
        this.hisFileMetadata = hisFileMetadata;
    }

}
