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

package org.opengauss.mppdbide.utils.messaging;

import java.text.MessageFormat;

import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ProgressBarLabelFormatter.
 *
 * @since 3.0.0
 */
public class ProgressBarLabelFormatter {

    /**
     * Gets the progress label for table with msg.
     *
     * @param tableName the table name
     * @param schemaName the schema name
     * @param databaseName the database name
     * @param serverName the server name
     * @param message the message
     * @return the progress label for table with msg
     */
    public static String getProgressLabelForTableWithMsg(String tableName, String schemaName, String databaseName,
            String serverName, String message) {
        String msgParam = tableName + '.' + schemaName + '.' + databaseName + '@' + serverName;
        String progressLabel = MessageConfigLoader.getProperty(message, msgParam);
        return progressLabel;

    }

    /**
     * Gets the progress label for table without msg.
     *
     * @param tableName the table name
     * @param schemaName the schema name
     * @param databaseName the database name
     * @param serverName the server name
     * @return the progress label for table without msg
     */
    public static String getProgressLabelForTableWithoutMsg(String tableName, String schemaName, String databaseName,
            String serverName) {
        String msgParam = tableName + '.' + schemaName + '.' + databaseName + '@' + serverName;
        return msgParam;

    }

    /**
     * Gets the progress label for database.
     *
     * @param databaseName the database name
     * @param serverName the server name
     * @param message the message
     * @return the progress label for database
     */
    public static String getProgressLabelForDatabase(String databaseName, String serverName, String message) {
        String msgParam = databaseName + '@' + serverName;
        String progressLabel = MessageConfigLoader.getProperty(message, msgParam);
        return progressLabel;

    }

    /**
     * Gets the progress label for schema.
     *
     * @param schemaName the schema name
     * @param databaseName the database name
     * @param serverName the server name
     * @param message the message
     * @return the progress label for schema
     */
    public static String getProgressLabelForSchema(String schemaName, String databaseName, String serverName,
            String message) {
        String msgParam = schemaName + '.' + databaseName + '@' + serverName;
        String progressLabel = MessageConfigLoader.getProperty(message, msgParam);
        return progressLabel;

    }

    /**
     * Gets the progress label for column.
     *
     * @param columnName the column name
     * @param tableName the table name
     * @param schemaName the schema name
     * @param databaseName the database name
     * @param serverName the server name
     * @param message the message
     * @return the progress label for column
     */
    public static String getProgressLabelForColumn(String columnName, String tableName, String schemaName,
            String databaseName, String serverName, String message) {
        String msgParam = columnName + '.' + tableName + '.' + schemaName + '.' + databaseName + '@' + serverName;
        String progressLabel = MessageConfigLoader.getProperty(message, msgParam);
        return progressLabel;

    }

    /**
     * Gets the progress label for view.
     *
     * @param viewName the view name
     * @param schemaName the schema name
     * @param databaseName the database name
     * @param serverName the server name
     * @param message the message
     * @return the progress label for view
     */
    public static String getProgressLabelForView(String viewName, String schemaName, String databaseName,
            String serverName, String message) {
        String msgParam = viewName + '.' + schemaName + '.' + databaseName + '@' + serverName;
        String progressLabel = MessageConfigLoader.getProperty(message, msgParam);
        return progressLabel;

    }

    /**
     * Gets the progress label for batch export.
     *
     * @param exportProp the export prop
     * @param currentExportCount the current export count
     * @param totalExportCount the total export count
     * @param objName the obj name
     * @param dbName the db name
     * @param serverName the server name
     * @return the progress label for batch export
     */
    public static String getProgressLabelForBatchExport(String exportProp, int currentExportCount, int totalExportCount,
            String objName, String dbName, String serverName) {
        StringBuilder builder = new StringBuilder(" (" + currentExportCount + "/" + totalExportCount + "): ");
        builder.append(objName + "." + dbName + "@" + serverName);
        String details = builder.toString();

        String label = MessageConfigLoader.getProperty(exportProp, details);
        return label;
    }

    /**
     * Gets the progress label for user role.
     *
     * @param userRoleName the user role name
     * @param serverName the server name
     * @return the progress label for user role
     */
    public static String getProgressLabelForUserRole(String userRoleName, String serverName) {
        return MessageFormat.format("{0}@{1}", userRoleName, serverName);
    }

    /**
     * Gets the progress label for Synonym Objects with msg.
     *
     * @param childObjectName the child object name
     * @param namespaceName the namespace name
     * @param serverName the server name
     * @param message the message
     * @return the progress label for Synonym Objects with msg
     */
    public static String getProgressLabelForSynonymObjectsWithMsg(String childObjectName, String namespaceName,
            String serverName, String message) {
        String msgParam = childObjectName + '.' + namespaceName + '@' + serverName;
        String progressLabel = MessageConfigLoader.getProperty(message, msgParam);
        return progressLabel;
    }

    /**
     * Gets the progress label for text mode loading.
     *
     * @param databaseName the database name
     * @param serverName the server name
     * @param sqlTerminalPartLabel the sql terminal part label
     * @param message the message
     * @return the progress label for text mode loading
     */
    public static String getProgressLabelForTextModeLoading(String databaseName, String serverName,
            String sqlTerminalPartLabel, String message) {
        String msgParam = databaseName + '@' + serverName;
        if (!sqlTerminalPartLabel.trim().isEmpty()) {
            msgParam = sqlTerminalPartLabel + "." + msgParam;
        }
        return MessageConfigLoader.getProperty(message, msgParam);

    }

    /**
     * Gets the progress label for sequence with msg.
     *
     * @param sequenceName the sequence name
     * @param schemaName the schema name
     * @param databaseName the database name
     * @param serverName the server name
     * @param message the message
     * @return the progress label for sequence with msg
     */
    public static String getProgressLabelForSequenceWithMsg(String sequenceName, String schemaName, String databaseName,
            String serverName, String message) {
        String msgParam = sequenceName + '.' + schemaName + '.' + databaseName + '@' + serverName;
        String progressLabel = MessageConfigLoader.getProperty(message, msgParam);
        return progressLabel;

    }
}
