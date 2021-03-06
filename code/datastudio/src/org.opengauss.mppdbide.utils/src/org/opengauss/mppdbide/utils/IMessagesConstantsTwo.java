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

package org.opengauss.mppdbide.utils;

/**
 * Title: IMessagesConstantsTwo
 * 
 * @since 3.0.0
 */
public interface IMessagesConstantsTwo {
    String EXECUTION_DYANMIC_TIME_LABEL = "EXECUTION_DYANMIC_TIME_LABEL";

    String EXECUTION_ELAPSED_TIME_LABEL = "EXECUTION_ELAPSED_TIME_LABEL";

    String PREF_TRANSACTION_SETTING = "PREF_TRANSACTION_SETTING";

    String PREF_TRANSACTION_AUTOCOMMIT = "PREF_TRANSACTION_AUTOCOMMIT";

    String AUTOCOMMIT_ENABLE = "AUTOCOMMIT_ENABLE";

    String AUTOCOMMIT_DISABLE = "AUTOCOMMIT_DISABLE";

    String TRANSACTION_DIALOG_TITLE = "TRANSACTION_DIALOG_TITLE";

    String TRANSACTION_DIALOG_BODY = "TRANSACTION_DIALOG_BODY";

    String TRANSACTION_COMMIT_FEEDBACK = "TRANSACTION_COMMIT_FEEDBACK";

    String TRANSACTION_ROLLBACK_FEEDBACK = "TRANSACTION_ROLLBACK_FEEDBACK";

    String TRANSACTION_OPERATION_EXCEPTION_TITLE = "TRANSACTION_OPERATION_EXCEPTION_TITLE";

    String TRANSACTION_COMMIT_EXCEPTION_BODY = "TRANSACTION_COMMIT_EXCEPTION_BODY";

    String TRANSACTION_ROLLBACK_EXCEPTION_BODY = "TRANSACTION_ROLLBACK_EXCEPTION_BODY";

    String TRANSACTION_RESET_BUTTIONS_EXCEPTION = "TRANSACTION_RESET_BUTTIONS_EXCEPTION";

    String TRANSACTION_OPERATION_TOGGLE_TITLE = "TRANSACTION_OPERATION_TOGGLE_TITLE";

    String AUTOCOMMIT_ENABLE_DESC = "AUTOCOMMIT_ENABLE_DESC";

    String AUTOCOMMIT_DISABLE_DESC = "AUTOCOMMIT_DISABLE_DESC";

    String TRANSACTION_DIALOG_OK_BUTTION = "TRANSACTION_DIALOG_OK_BUTTION";

    String TRANSACTION_COMMIT_JOB = "TRANSACTION_COMMIT_JOB";

    String TRANSACTION_ROLLBACK_JOB = "TRANSACTION_ROLLBACK_JOB";

    String TRANSACTION_COMMIT_TOOL_TIP = "TRANSACTION_COMMIT_TOOL_TIP";

    String TRANSACTION_ROLLBACK_TOOL_TIP = "TRANSACTION_ROLLBACK_TOOL_TIP";

    String TRANSACTION_COMMIT_CONFIRMATION = "TRANSACTION_COMMIT_CONFIRMATION";

    String TRANSACTION_ROLLBACK_CONFIRMATION = "TRANSACTION_ROLLBACK_CONFIRMATION";

    String NO_TRANSACTION_COMMIT_ROLLBACK_TOGGLE = "NO_TRANSACTION_COMMIT_ROLLBACK_TOGGLE";

    String ERR_START_TRANSACTION_FAILED = "ERR_START_TRANSACTION_FAILED";

    String ERR_ROLL_BACK_TRANSACTION_FAILED = "ERR_ROLL_BACK_TRANSACTION_FAILED";

    String SHOW_SEQUENCE_DDL_CANCELING = "SHOW_SEQUENCE_DDL_CANCELING";

    String SHOW_SEQUENCE_DDL_PROGRESS_NAME = "SHOW_SEQUENCE_DDL_PROGRESS_NAME";

    String SHOW_SEQUENCE_DDL_FAILED = "SHOW_SEQUENCE_DDL_FAILED";

    String BATCH_EXPORT_NO_RELATION_PREVILAGE = "BATCH_EXPORT_NO_RELATION_PREVILAGE";

    String EXECUTE_DEBUGE = "EXECUTE_DEBUGE";

    String WRITE_HERE = "WRITE_HERE";

    String PARAMETER_INPUT_TABLE_PARAM_NAME_COLUMN = "PARAMETER_INPUT_TABLE_PARAM_NAME_COLUMN";

    String PARAMETER_INPUT_TABLE_PARAM_TYPE_COLUMN = "PARAMETER_INPUT_TABLE_PARAM_TYPE_COLUMN";

    String PARAMETER_INPUT_TABLE_PARAM_VALUE_COLUMN = "PARAMETER_INPUT_TABLE_PARAM_VALUE_COLUMN";

    String OPERATION_CANNOT_BE_PERFOREMD_TITLE = "OPERATION_CANNOT_BE_PERFOREMD_TITLE";

    String ERROR_IMPORTING_CONNECTION_PROFILES = "ERROR_IMPORTING_CONNECTION_PROFILES";

    String EXPORTING_CONNECTION_PROFILES = "EXPORTING_CONNECTION_PROFILES";

    String IMPORTING_CONNECTION_PROFILES = "IMPORTING_CONNECTION_PROFILES";

    String EXPORT_CONNECTION_PROFILE_DIALOG_HEADER = "EXPORT_CONNECTION_PROFILE_DIALOG_HEADER";

    String EXPORT_CONNECTION_PROFILE_DIALOG_GROUP = "EXPORT_CONNECTION_PROFILE_DIALOG_GROUP";

    String EXPORT_CONNECTION_PROFILE_SUCCESS_MESSAGE = "EXPORT_CONNECTION_PROFILE_SUCCESS_MESSAGE";

    String EXPORT_CONNECTION_PROFILE_LOAD_ERR_MSG = "EXPORT_CONNECTION_PROFILE_LOAD_ERR_MSG";

    String EXPORT_CONNECTION_PROF_LOADING_STATUS_MESSAGE = "EXPORT_CONNECTION_PROF_LOADING_STATUS_MESSAGE";

    String IMPORT_CONNECTIONS_LOADING_STATUS_MSG = "IMPORT_CONNECTIONS_LOADING_STATUS_MSG";

    String IMPORT_CONNECTIONS_PROFILE_DIALOG_HEADER = "IMPORT_CONNECTIONS_PROFILE_DIALOG_HEADER";

    String IMPORT_CONNECTIONS_PROFILE_ERR_MSG = "IMPORT_CONNECTIONS_PROFILE_ERR_MSG";

    String IMPORT_CONN_PROFILE_REPLACE_LABEL = "IMPORT_CONN_PROFILE_REPLACE_LABEL";

    String IMPORT_CONN_PROFILE_KEEP_BOTH_FILES_LBL = "IMPORT_CONN_PROFILE_KEEP_BOTH_FILES_LBL";

    String IMPORT_CONN_PROFILE_DONT_CPY_LBL = "IMPORT_CONN_PROFILE_DONT_CPY_LBL";

    String IMPORT_CONN_PROFILE_CONFLICTS_LBL = "IMPORT_CONN_PROFILE_CONFLICTS_LBL";

    String IMPORT_PROFILE_INCORRECT_FILE = "IMPORT_PROFILE_INCORRECT_FILE";

    String SHORTCUT_KEY_ALREADY_EXIST = "SHORTCUT_KEY_ALREADY_EXIST";

    String SELECT_SHORTCUT_KEY_AGAIN = "SELECT_SHORTCUT_KEY_AGAIN";

    String SHORTCUT_KEY_DUPLICATE = "SHORTCUT_KEY_DUPLICATE";

    String PROCESS_TIMEOUT_ERROR = "PROCESS_TIMEOUT_ERROR";

    String EXPORT_TIMEOUT = "EXPORT_TIMEOUT";

    String DEFAULT_TIMEOUT = "DEFAULT_TIMEOUT";

    String CUSTOM_TIMEOUT = "CUSTOM_TIMEOUT";

    String SECONDS = "SECONDS";

    String ROLLBACK_COMPLETED = "ROLLBACK_COMPLETED";

    String RENAME_TABLESPACE_FAILED_TITLE = "RENAME_TABLESPACE_FAILED";

    String ERR_FETCH_RESULT_SET_COLUMN_COMMENT = "ERR_FETCH_RESULT_SET_COLUMN_COMMENT";

    // olap table excell format import
    String IMPORT_RESULT_INVALID_CONNECTION = "IMPORT_RESULT_INVALID_CONNECTION";

    String IMPORT_EXCEL_DATE = "IMPORT_EXCEL_DATE";

    String FILE_LIMIT_WARNING_MESSAGE = "FILE_LIMIT_WARNING_MESSAGE";

    String FILE_LIMIT = "FILE_LIMIT";

    String FILE_LIMIT_HEADER = "FILE_LIMIT_HEADER";

    String IMPORT_TABLE_DATA_LIMIT = "IMPORT_TABLE_DATA_LIMIT";

    String IMPORT_FILE_DATA_LIMIT = "IMPORT_FILE_DATA_LIMIT";
    
    String IMPORT_BYTEA_DATA_LIMIT = "IMPORT_BYTEA_DATA_LIMIT";
    
    String SUFFIX_DATA_LIMIT = "SUFFIX_DATA_LIMIT";
    
    String IMPORT_UNLIMITED_NOTE = "IMPORT_UNLIMITED_NOTE";

    // Object Browser Lazy Rendering
    String LAZY_RENDER_PREFERENCE_GROUP_NAME = "LAZY_RENDER_PREFERENCE_GROUP_NAME";

    String LAZY_RENDER_OBJECT_COUNT_LABEL = "LAZY_RENDER_OBJECT_COUNT_LABEL";

    String LAZY_RENDER_SUFFIX_LABEL = "LAZY_RENDER_SUFFIX_LABEL";

    String LAZY_RENDERING_ERROR_MSG = "LAZY_RENDERING_ERROR_MSG";

    String INVALID_EXTENSION_HEADER = "INVALID_EXTENSION_HEADER";

    String INVALID_EXTENSION_MESSAGE = "INVALID_EXTENSION_MESSAGE";

    String VALIDATE_CIPHER_FAIL = "VALIDATE_CIPHER_FAIL";

    // openGauss excel format of table data import

    String ENCODING_NOTE = "ENCODING_NOTE";

    String OPTION_ZIP = "OPTION_ZIP";

    String COMPRESS_FAIL_DAILOG_TITLE = "COMPRESS_FAIL_DAILOG_TITLE";

    String SQL_DDL_EXPORT_WINDOW_TITLE = "SQL_DDL_EXPORT_WINDOW_TITLE";

    String SQL_DDL_DATA_EXPORT_WINDOW_TITLE = "SQL_DDL_DATA_EXPORT_WINDOW_TITLE";

    String GENERATE_SQL_EXPORT_WINDOW_TITLE = "GENERATE_SQL_EXPORT_WINDOW_TITLE";

    String INVALID_TEMP_ENVIRONMENT_VARIABLE = "INVALID_TEMP_ENVIRONMENT_VARIABLE";

    String ERR_ZIP_EXPORT_DAILOG_TITLE = "ERR_ZIP_EXPORT_DAILOG_TITLE";

    String ERR_ZIP_EXPORT_DAILOG_DESC = "ERR_ZIP_EXPORT_DAILOG_DESC";

    String ENTER_COL_DESCRIPTIONS_TEXT = "ENTER_COL_DESCRIPTIONS_TEXT";

    String ERR_REMOVE_TARGET_SESSION_FAILED = "ERR_REMOVE_TARGET_SESSION_FAILED";

    String ERR_ATTACH_TARGET_SESSION_FAILED = "ERR_ATTACH_TARGET_SESSION_FAILED";

    String ERR_BL_QUERY_EXEC_STATUS_FAILED = "ERR_BL_QUERY_EXEC_STATUS_FAILED";

    String LOAD_MORE_RECORD_TOOL_ITEM = "LOAD_MORE_RECORD_TOOL_ITEM";

    String RESULT_WINDOW = "RESULT_WINDOW";

    String RESULT_TAB_GENERATE_NEW = "RESULT_TAB_GENERATE_NEW";

    String RESULT_TAB_RETAIN_CURRENT = "RESULT_TAB_RETAIN_CURRENT";

    String RESULT_TAB_GENERATE_NEW_HINT = "RESULT_TAB_GENERATE_NEW_HINT";

    String RESULT_TAB_RETAIN_CURRENT_HINT = "RESULT_TAB_RETAIN_CURRENT_HINT";

    String BUTTON_OVERWRITE_RESULTSET_TOOLTIP = "BUTTON_OVERWRITE_RESULTSET_TOOLTIP";

    String PREPARED_QUERY_RELATED_TABLE_FAILED = "PREPARED_QUERY_RELATED_TABLE_FAILED";

    String PREPARED_QUERY_RELATED_SEQUENCE_FAILED = "PREPARED_QUERY_RELATED_SEQUENCE_FAILED";

    String ERR_WRITE_FILE = "ERR_WRITE_FILE";

    String ERR_IMPORT_TABLE_TO_EXCEL = "ERR_IMPORT_TABLE_TO_EXCEL";

    String AUTO_COMPLETE_SETTING = "AUTO_COMPLETE_SETTING";

    String AUTO_COMPLETE_MIN_SIZE = "AUTO_COMPLETE_MIN_SIZE";

    String AUTO_COMPLETE_MIN_SIZE_DESC = "AUTO_COMPLETE_MIN_SIZE_DESC";

    String AUTO_SUGGEST_ERROR_MSG = "AUTO_SUGGEST_ERROR_MSG";

    String MPPDBIDE_DIA_BTN_DISCARD_All = "MPPDBIDE_DIA_BTN_DISCARD_ALL";

    String PREF_FOLDING_SETTING = "PREF_FOLDING_SETTING";

    String PREF_FOLDING_ENABLE_DESC = "PREF_FOLDING_ENABLE_DESC";

    String PREF_FOLDING_DISABLE_DESC = "PREF_FOLDING_DISABLE_DESC";

    String PREF_FOLDING_DESC = "PREF_FOLDING_DESC";

    String PREF_FONT_SETTING = "PREF_FONT_SETTING";

    String PREF_FONT_STYLE = "PREF_FONT_STYLE";

    String PREF_FONT_STYLE_SIZE = "PREF_FONT_STYLE_SIZE";

    String PREF_FONT_STYLE_SIZE_EXPLANATION = "PREF_FONT_STYLE_SIZE_EXPLANATION";

    String DESCRIBE_IMPACT_OF_COLUMN_SEQUENCE = "DESCRIBE_IMPACT_OF_COLUMN_SEQUENCE";

    String ERR_DELETE_FILE_MSG = "ERR_DELETE_FILE_MSG";

    String ERR_IMPORT_DATE_FORMAT = "ERR_IMPORT_DATE_FORMAT";

    String ERR_IMPORT_DATA_FILE = "ERR_IMPORT_DATA_FILE";

    String ERR_IMPORT_DIFF_FILE_FORMAT = "ERR_IMPORT_DIFF_FILE_FORMAT";

    String LOCALE_CHANGE_APP_EXIT_MSG = "LOCALE_CHANGE_APP_EXIT_MSG";

    String LOCAL_CHANGE_EXIT_NOTE_YES = "LOCAL_CHANGE_EXIT_NOTE_YES";

    String LOCALE_CHANGE_APP_RESTART_NOTE_NO = "LOCALE_CHANGE_APP_RESTART_NOTE_NO";

    String LOADING_DATA = "LOADING_DATA";

    String LOADED_DATA = "LOADED_DATA";

    String FILE_SIZE_EXCEEDED_FOUR = "FILE_SIZE_EXCEEDED_FOUR";

    // for ER --start
    String ER_JOB_DETAILS = "ER_JOB_DETAILS";

    String ER_ERROR_POPUP_HEADER = "ER_ERROR_POPUP_HEADER";

    String ER_DESC_PRIMARYKEY = "ER_DESC_PRIMARYKEY";

    String ER_DESC_FOREIGNKEY = "ER_DESC_FOREIGNKEY";

    String ER_DESC_CURRENT_COLOR = "ER_DESC_CURRENT_COLOR";

    String ER_DESC_RELATED_COLOR = "ER_DESC_RELATED_COLOR";

    String ER_VIEW_FAILED = "ER_VIEW_FAILED";

    String ER_VIEW_FAILED_MSG = "ER_VIEW_FAILED_MSG";

    String ER_VIEW_INSUFFICIENT_PRIVILEGES_MSG = "ER_VIEW_INSUFFICIENT_PRIVILEGES_MSG";

    String VIEW_STYLES = "VIEW_STYLES";

    String SHOW_ICONS = "SHOW_ICONS";

    String SHOW_DATA_TYPES = "SHOW_DATA_TYPES";

    String SHOW_NULLABILITY = "SHOW_NULLABILITY";

    String SHOW_COMMENTS = "SHOW_COMMENTS";

    String SHOW_FULLY_QUALIFIED_NAMES = "SHOW_FULLY_QUALIFIED_NAMES";

    String SHOW_ATTRIBUTES = "SHOW_ATTRIBUTES";

    String ATTRIBUTE_VIS_ALL = "ATTRIBUTE_VIS_ALL";

    String ATTRIBUTE_VIS_ANY_KEY = "ATTRIBUTE_VIS_ANY_KEY";

    String ATTRIBUTE_VIS_PRIMARY_KEY = "ATTRIBUTE_VIS_PRIMARY_KEY";

    String ATTRIBUTE_VIS_NONE = "ATTRIBUTE_VIS_NONE";
    
    // for ER --end

    // SYNONYMS Start
    String SYNONYM_GROUP_NAME = "SYNONYM_GROUP_NAME";

    String TRIGGER_GROUP_NAME = "TRIGGER_GROUP_NAME";

    String CREATE_NEW_SYNONYM = "CREATE_NEW_SYNONYM";

    String SYNONYM_NAME = "SYNONYM_NAME";

    String OBJECT_OWNER = "OBJECT_OWNER";

    String OBJECT_TYPE = "OBJECT_TYPE";

    String OBJECT_NAME = "OBJECT_NAME";

    String REPLACE_IF_EXIST = "REPLACE_IF_EXIST";

    String SYNONYM_NAME_ENTER_NM = "SYNONYM_NAME_ENTER_NM";

    String SYNONYM_NAME_EXCEED_MAX = "SYNONYM_NAME_EXCEED_MAX";

    String OBJECT_OWNER_SELECT_NM = "OBJECT_OWNER_SELECT_NM";

    String OBJECT_TYPE_SELECT_NM = "OBJECT_TYPE_SELECT_NM";

    String OBJECT_NAME_SELECT_NM = "OBJECT_NAME_SELECT_NM";

    String CREATED_SYNONYM_SUCESS = "CREATED_SYNONYM_SUCESS";

    String CREATE_SYNONYM_NO_DATABASE = "CREATE_SYNONYM_NO_DATABASE";

    String CREATE_SYNONYM_ERROR = "CREATE_SYNONYM_ERROR";

    String DROP_SYNONYM_CONFIRM_MSG = "DROP_SYNONYM_CONFIRM_MSG";

    String DROP_SYNONYM_TITLE = "DROP_SYNONYM_TITLE";

    String STATUS_MSG_DROP_SYNONYM = "STATUS_MSG_DROP_SYNONYM";

    String DROP_SYNONYM_PROGRESS_NAME = "DROP_SYNONYM_PROGRESS_NAME";

    String DROP_SYNONYM_SUCCESS = "DROP_SYNONYM_SUCCESS";

    String DROP_SYNONYM_ERROR = "DROP_SYNONYM_ERROR";

    String DROP_SYNONYM_UNABLE = "DROP_SYNONYM_UNABLE";

    String PROPERTIES_SYNONYM_NAME = "PROPERTIES_SYNONYM_NAME";

    String PROPERTIES_SYNONYM_OWNER = "PROPERTIES_SYNONYM_OWNER";

    String PROPERTIES_OBJECT_OWNER = "PROPERTIES_OBJECT_OWNER";

    String PROPERTIES_OBJECT_NAME = "PROPERTIES_OBJECT_NAME";
    
    // SYNONYMS End

    // DS Commandline parameter support start
    String DS_COMMANDLINE_ERROR = "DS_COMMANDLINE_ERROR";

    String DS_COMMANDLINE_MANDATORY_PARAM_MISSING = "DS_COMMANDLINE_MANDATORY_PARAM_MISSING";

    String DS_COMMANDLINE_SSL_MODE_NOT_SUPPORTED = "DS_COMMANDLINE_SSL_MODE_NOT_SUPPORTED";

    String DS_COMMANDLINE_UNIDENTIFIED_PARAM = "DS_COMMANDLINE_UNIDENTIFIED_PARAM";

    String DS_COMMANDLINE_DB_TYPE_PARAM_MISSING = "DS_COMMANDLINE_DB_TYPE_PARAM_MISSING";

    String DS_COMMANDLINE_INVALID_CONNECTION_NAME_VALUE = "DS_COMMANDLINE_INVALID_CONNECTION_NAME_VALUE";

    String DS_COMMANDLINE_INVALID_VALUE_LENGTH = "DS_COMMANDLINE_INVALID_VALUE_LENGTH";

    String DS_COMMANDLINE_NULL_VALUE_FOR_PARAM = "DS_COMMANDLINE_NULL_VALUE_FOR_PARAM";

    String DS_COMMANDLINE_INVALID_FILE_PATH = "DS_COMMANDLINE_INVALID_FILE_PATH";

    String DS_COMMANDLINE_INVALID_HOST_PORT_VALUE = "DS_COMMANDLINE_INVALID_HOST_PORT_VALUE";

    String DS_COMMANDLINE_INVALID_SAVE_CIPHER_VALUE = "DS_COMMANDLINE_INVALID_SAVE_CIPHER_VALUE";

    String DS_COMMANDLINE_INVALID_SSL_ENABLE_VALUE = "DS_COMMANDLINE_INVALID_SSL_ENABLE_VALUE";

    String DS_COMMANDLINE_INVALID_SSL_MODE_VALUE = "DS_COMMANDLINE_INVALID_SSL_MODE_VALUE";

    String DS_COMMANDLINE_ATTEMPTING_CONNECTION_MSG = "DS_COMMANDLINE_ATTEMPTING_CONNECTION_MSG";

    String DS_COMMANDLINE_ENTER_CIPHER = "DS_COMMANDLINE_ENTER_CIPHER";
    
    String DS_COMMANDLINE_PRESS_ENTER_FOLLOWED_BY_CIPHER = "DS_COMMANDLINE_PRESS_ENTER_FOLLOWED_BY_CIPHER";
    
    // DS Commandline parameter support end

    // Formatter preference start
    String FORMATTER_PREVIEW = "FORMATTER_PREVIEW";

    String FORMATTER_RIGHT_MARGIN = "FORMATTER_RIGHT_MARGIN";

    String FORMATTER_INDENT = "FORMATTER_INDENT";

    String FORMATTER_USE_TAB_CHAR = "FORMATTER_USE_TAB_CHAR";
    
    String FORMATTER_TABS = "FORMATTER_TABS";
    
    String FORMATTER_SPACES = "FORMATTER_SPACES";

    String FORMATTER_TAB_CHAR_SIZE = "FORMATTER_TAB_CHAR_SIZE";

    String FORMATTER_ALIGN_DECLARATION = "FORMATTER_ALIGN_DECLARATION";

    String FORMATTER_ALIGN_ASSIGNMENTS = "FORMATTER_ALIGN_ASSIGNMENTS";

    String FORMATTER_THEN_ON_NEW_LINE = "FORMATTER_THEN_ON_NEW_LINE";

    String FORMATTER_LOOP_ON_NEW_LINE = "FORMATTER_LOOP_ON_NEW_LINE";

    String FORMATTER_SPLIT_AND_OR = "FORMATTER_SPLIT_AND_OR";

    String FORMATTER_AND_OR_AFTER_EXP = "FORMATTER_AND_OR_AFTER_EXP";

    String FORMATTER_AND_OR_UNDER = "FORMATTER_AND_OR_UNDER";

    String FORMATTER_DML = "FORMATTER_DML";

    String FORMATTER_GENERAL = "FORMATTER_GENERAL";

    String FORMATTER_SELECT = "FORMATTER_SELECT";

    String FORMATTER_INSERT = "FORMATTER_INSERT";

    String FORMATTER_UPDATE = "FORMATTER_UPDATE";
    
    String FORMATTER_OTHERS = "FORMATTER_OTHERS";

    String FORMATTER_LEFT_ALIGN_KEYWORDS = "FORMATTER_LEFT_ALIGN_KEYWORDS";

    String FORMATTER_LEFT_ALIGN_ITEMS = "FORMATTER_LEFT_ALIGN_ITEMS";

    String FORMATTER_FORMAT = "FORMATTER_FORMAT";

    String FORMATTER_ON_ONE_LINE = "FORMATTER_ON_ONE_LINE";

    String FORMATTER_ONE_PARAM_PER_LINE = "FORMATTER_ONE_PARAM_PER_LINE";

    String FORMATTER_FIT = "FORMATTER_FIT";

    String FORMATTER_ALIGN = "FORMATTER_ALIGN";

    String FORMATTER_COMMA_AFTER_ITEM = "FORMATTER_COMMA_AFTER_ITEM";

    String FORMATTER_PARAMATER = "FORMATTER_PARAMATER";

    String FORMATTER_ALIGN_DATATYPES = "FORMATTER_ALIGN_DATATYPES";

    String FORMATTER_COMMA_AFTER_DATATYPE = "FORMATTER_COMMA_AFTER_DATATYPE";

    String FORMATTER_LIST_AT_LEFT_MARGIN = "FORMATTER_LIST_AT_LEFT_MARGIN";

    String FORMATTER_IMPORT = "FORMATTER_IMPORT";

    String FORMATTER_EXPORT = "FORMATTER_EXPORT";

    String FORMATTER_INVALID_FILE_SELECTED = "FORMATTER_INVALID_FILE_SELECTED";

    String FORMATTER_SELECT_VALID_JSON = "FORMATTER_SELECT_VALID_JSON";

    String FORMATTER_FILE_CREATION_ERROR_HEADER = "FORMATTER_FILE_CREATION_ERROR_HEADER";

    String FORMATTER_FILE_READ_ERROR_HEADER = "FORMATTER_FILE_READ_ERROR_HEADER";

    String FORMATTER_FILE_CREATION_ERROR = "FORMATTER_FILE_CREATION_ERROR";

    String FORMATTER_FILE_READ_ERROR = "FORMATTER_FILE_READ_ERROR";

    String FORMATTER_ERROR_RESIDE_IN_FILE = "FORMATTER_ERROR_RESIDE_IN_FILE";
    
    // Formatter preference end

    String FORMATTER_UNABLE_TO_PARSE_STMT = "FORMATTER_UNABLE_TO_PARSE_STMT";

    String OBJECT_BROWSER = "OBJECT_BROWSER";
    String OBJECT_BROWSER_FILTER_TIMEOUT = "OBJECT_BROWSER_FILTER_TIMEOUT";
    String OBJECT_BROWSER_FILTER_TIMEOUT_INFO = "OBJECT_BROWSER_FILTER_TIMEOUT_INFO";
    String OBJECT_BROWSER_FILTER_TIMEOUT_ERR_MSG = "OBJECT_BROWSER_FILTER_TIMEOUT_ERR_MSG";
    String OBJECT_BROWSER_FILTER_TIMEOUT_ERROR = "OBJECT_BROWSER_FILTER_TIMEOUT_ERROR";
    String OBJECT_BROWSER_FILTER_TIMEOUT_ERROR_TITLE = "OBJECT_BROWSER_FILTER_TIMEOUT_ERROR_TITLE";
    String OBJECT_BROWSER_FILTER_FINISH_MSG = "OBJECT_BROWSER_FILTER_FINISH_MSG";
    String OBJECT_BROWSER_FILTER_TIMEOUT_TOOLTIP_MESSAGE = "OBJECT_BROWSER_FILTER_TIMEOUT_TOOLTIP_MESSAGE";

    String LABEL_LOAD_CHILD_OBJECT = "LABEL_LOAD_CHILD_OBJECT";
    String FILE_SIZE_EXCEED_WARNING_MSG = "FILE_SIZE_EXCEED_WARNING_MSG";
}
