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

package com.huawei.mppdbide.bl.sqlhistory;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLHistoryItemDetail.
 * 
 */

public class SQLHistoryItemDetail extends SQLHistoryItem {

    private String fileName;

    /**
     * Instantiates a new SQL history item detail.
     *
     * @param summary the summary
     * @param querySize the query size
     */
    public SQLHistoryItemDetail(QueryExecutionSummary summary, int querySize) {
        super(summary, querySize);
        setDestinationFileName();
    }

    private void setDestinationFileName() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
        this.fileName = dateFormat.format(date) + ".hist";
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public final String getFileName() {
        return fileName;
    }

    /**
     * Gets the serialized content.
     *
     * @return the serialized content
     */
    public String getSerializedContent() {
        Gson gson = new Gson();
        Type type = new SQLHistoryItemTypeToken().getType();
        return gson.toJson(this, type);
    }

    /**
     * Gets the deserialized content.
     *
     * @param string the string
     * @return the deserialized content
     * @throws JsonSyntaxException the json syntax exception
     */
    public static SQLHistoryItemDetail getDeserializedContent(String string) throws JsonSyntaxException {
        if (null == string || string.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<SQLHistoryItemDetail>() {
        }.getType();
        SQLHistoryItemDetail historyItem = gson.fromJson(string, type);
        return historyItem;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SQLHistoryItemTypeToken.
     * 
     */
    private static final class SQLHistoryItemTypeToken extends TypeToken<SQLHistoryItemDetail> {
    }

    @Override
    public int hashCode() {
        return super.hashCode() + fileName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (!(obj instanceof SQLHistoryItemDetail)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        SQLHistoryItemDetail other = (SQLHistoryItemDetail) obj;
        if (fileName == null) {
            if (other.fileName != null) {
                return false;
            }
        } else if (!fileName.equals(other.fileName)) {
            return false;
        }

        return false;
    }

}
