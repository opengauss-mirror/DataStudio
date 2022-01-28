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

package com.huawei.mppdbide.utils;

import java.text.Normalizer;

import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: interface Description: The Interface MPPDBIDEConstants.
 *
 * @since 3.0.0
 */
public interface MPPDBIDEConstants {
    String JSON_PLAN_DUMP_FILE = "Execution_plan_JSON.txt";

    int VARIABLE_ARRAY_SIZE = 10;

    int OBJECT_ARRAY_SIZE = 10;

    int RECORD_ARRAY_SIZE = 10;

    int SHOWTOOLTIPAFTER = 100;

    int SHOWTOOLTIPFOR = 5000;

    int FUNC_PROC_COLUMN_COUNT = 4;

    int STR_EXPLAIN_PLAN_SIZE = 6;

    int STR_EXPLAIN_PLAN_ANALYZE_SIZE = 10;

    int LINE_NUMBER_OFFSET = 1;

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS z";

    String DATE_COLLAPSE_FORMAT = "yyyyMMddHHmmssSSS";

    String CONNECTION_NAME = "New Database Connection";

    String GMDB = "GMDB";

    String GAUSS = "GAUSS";

    String LINE_SEPARATOR = System.lineSeparator();

    int FIXED_EXECUTOR_THREAD_POOL_COUNT = 1;

    int FIXED_DEBUG_THREAD_POOL_COUNT = 2;

    /**
     * The Constant ZER0.
     */
    int ZER0 = 0;

    String GUI = "GUI";

    String BL = "BL";

    String ADAPTER = "ADAPTER";

    String UNKNOWN_DATATYPE_STR = "<unknown>";

    String DRIVER_NAME = "org.postgresql.Driver";

    String SWTBOT_KEY = "org.eclipse.swtbot.widget.key";

    String EXECUTE_PLAN_COST = "EXPLAIN ";

    // AR.Tools.IDE.030.006
    String EXECUTION_PLAN = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_DISPLAY);

    // AR.Tools.IDE.030.006
    int PRIME_31 = 31;

    String CHINESE_LOCALE = "zh_CN";

    String CANCELABLEJOB = "DataSdutioCancelable Job";

    String PLSOURCEVIEWERJOB = "PLSource viewer Job";

    String ALTER_VIEW_COLUMN_PROPERTIES = "Alter View column property Job";

    String PROFILE_BASE_PATH = "Profile";

    String HISTORY_BASE_PATH = "History";

    String TEMP_FOLDER_PATH = "Temp";

    String CONNECTION_PROFILE_NAME = "connection.properties";

    String CONNECTION_PROFILE_META_FILE = "Profiles.txt";

    String PUBLIC_SCHEMA_NAME = "public";

    String SPACE_CHAR = " ";

    String SINGLE_QUOTE = "'";

    String DOUBLE_QUOTE = "\"";

    String ADD_QUOTE = "''";

    String BINARY = "Binary";

    String USER_DATA_FOLDER = "UserData";

    String SECURITY_FOLDER = "security";

    String SAVE_HISTORY_FILE_NAME = "file_save_history.log";

    String LOCK_FILE = ".lock";

    String PREFERENCES_FOLDER = "Preferences";

    String PREFERENCES_FILE = "Preferences.prefs";

    String PROTOCOL_VERSION_ERROR = "PROTOCOL_VERSION_ERROR";

    String PREF_MAINTAIN_RESULT = "resultwindow.maintainrs";

    String PREF_MAINTAIN_TABLESPACE_OPTIONS = "tablespaceincludeoptions.prefs";

    String PREF_MAINTAIN_EXPORT_DATA_OPTIONS = "includeexportdataoptions.prefs";

    String PREF_RESULT_ROWNUMBER = "resultwindow.copyrownumber";

    String PREF_COLUMN_WIDTH_LENGTH = "resultwindow.columnwidthlength";

    String TAB_WIDTH_OPTION = "editor.formatter.tabwidth";

    String PREF_RESULT_IS_COLUMN_LENGTH_BY_VALUE = "resultwindow.iscolumnlengthbyvalue";

    String PREF_RESULT_IS_RECORD_FETCH_ALL = "resultwindow.isrecordfetchall";

    String PREF_RESULT_RECORD_FETCH_COUNT = "resultwindow.recordfetchcount";

    String PREF_RESULT_IS_COPY_COLUMN_HEADER = "resultwindow.iscopycolumnheader";

    String PREF_RESULT_IS_COPY_ROW_HEADER = "resultwindow.iscopyrowheader";

    String PREF_RESULT_IS_SHOW_ENCODING = "resultwindow.isshowencoding";

    String PREF_RESULT_IS_SHOW_TEXTMODE = "resultwindow.isshowtextmode";

    String PREF_RESULT_WINDOW_GENERATE = "resultwindow.isgeneratenew";
    String PREF_RESULT_WINDOW_COUNT = "resultwindow.resultwindowcount";

    String IS_DEFAULT_TIMEOUT = "exportpref.isRecordDefaultTimeout";

    String TIMEOUT_VALUE = "exportpref.recordTimeOut";

    String STR_TEXT = "txt";

    String STR_BINARY = "bin";

    String STR_EXCEL_XLSX = "EXCEL(xlsx)";

    String STR_EXCEL_XLS = "EXCEL(xls)";

    String REPLICATION = "REPLICATION";

    String HASH = "HASH";

    int FILE_NAME_MAX_LENGTH = 200;

    int RENAME_TERMINAL_MAX_LENGTH = 150;

    String COMMA_SEPARATE = ",";

    String ESCAPE_STRING_UNDERSCORE = "\\_";

    String ESCAPE_STRING_PERCENTILE = "\\%";

    String ESCAPE_FORWARDSLASH = "/";

    String GS_DUMP_ENCODING = "UTF-8";

    String SEPARATOR = "-";

    String UNSUPPORTED_SCHEMA = "public";

    String CANCEL_CONN_ERR_MSG = "Something unusual has occured to cause the driver to fail. Please report this exception.";

    String DISK_FULL_ERR_MSG = "There is not enough space on the disk";

    String CASCADE = " CASCADE";

    String SQL_TERMINAL = "SQL_TERMINAL";

    String PLSQL_EDITOR = "PLSQL_EDITOR";

    int KEY_CODE_FOR_COPY = 99;

    int RECONNECT_POPUP_LIMIT = 3;

    /**
     * The Constant DEFAULT_TREE_NODE_COUNT.
     */
    int DEFAULT_TREE_NODE_COUNT = 10000;

    /**
     * The Constant TESTDRIVER.
     */
    String TESTDRIVER = "FOR_JUNIT";

    /**
     * The Constant OPENGAUSS.
     */
    String OPENGAUSS = "OPENGAUSS";

    /**
     * The Constant GAUSS200V1R5DRIVER.
     */
    String GAUSS200V1R5DRIVER = "Gauss200V1R5Driver";

    /**
     * The Constant GAUSS200V1R6DRIVER.
     */
    String GAUSS200V1R6DRIVER = "Gauss200V1R6Driver";

    /**
     * The Constant GAUSS200V1R7DRIVER.
     */
    String GAUSS200V1R7DRIVER = "Gauss200V1R7Driver";

    String USER_ROLE_DATE_DISPLAY_FORMAT = "yyyy-MM-dd";

    String PRIVILEGE_GRANTEE_PUBLIC = "PUBLIC";

    String PRIVILEGE_SELECT = "select";

    String PRIVILEGE_INSERT = "insert";

    String PRIVILEGE_WITH_GRANT_OPTION = "with grant option";

    String PRIVILEGE_GRANT_OPTION_FOR = "only grant privilege";

    String PRIVILEGE_ALL = "all";

    String PRIVILEGE_UPDATE = "update";

    String PRIVILEGE_DELETE = "delete";

    String PRIVILEGE_TRUNCATE = "truncate";

    String PRIVILEGE_EXECUTE = "execute";

    String PRIVILEGE_USAGE = "usage";

    String PRIVILEGE_REFERENCES = "references";

    String PRIVILEGE_CREATE = "create";

    String NEW_LINE_SIGN = "\r\n";

    String LEFT_PARENTHESIS = "(";

    String RIGHT_PARENTHESIS = ")";

    String SEMICOLON = ";";

    String SERVER_ENCODING_KEY = "characterEncoding";

    String EMPTY_STRING = "";

    String SINGLE_NODE = "SINGLENODE";

    String MULTI_NODE = "MULTINODE";

    String COORDINATOR_NODE = "COORDINATOR";

    String DATA_NODE = "DATANODE";

    String RESULT_WINDOW_GRID = "Grid";

    String RESULT_WINDOW_TEXT = "Text";

    int TEXT_MODE_LOAD_MAXIMUM = 30000000;

    int TEXT_MODE_CELL_DIALOG_MAXINUM = 4679;

    // used for validating query while splitting queries to dodge DOS attacks on
    // Regular Expression
    // 2Crore is the limit to number of characters that SQL terminal can hold
    // for a query

    int SQL_TERMINAL_LOAD_MAXIMUM = 20000000;

    int STRING_BUILDER_CAPACITY = 128;

    String FILE_SEPARATOR = "\\";

    String TEMP_FILE_PATH = Normalizer.normalize(System.getProperty("java.io.tmpdir"), Normalizer.Form.NFD);

    int MAX_FILL_FACTOR = 100;

    int MAX_HOST_PORT = 65535;

    int MAX_LOAD_LIMIT = 30000;
    
    /**
     * the FILE_LIMIT_FOR_SYNTAX_COLOR i.e 40MB, this is the safe value chosen based on multiple trials.
     */
    double FILE_LIMIT_FOR_SYNTAX_COLOR = 40;

    String FILEENCODING_UTF = "UTF-8";

    String FILEENCODING_GBK = "GBK";

    String FILEENCODING_LATIN1 = "LATIN1";

    String TAB = "\t";

    // default process timeout which is the maximum time to wait

    int PROCESS_TIMEOUT = 86400;

    // max timeout value user can enter in preference page

    int MAX_PROCESS_TIMEOUT = 86400;

    // min timeout value user can enter in preference page

    int MIN_PROCESS_TIMEOUT = 1;

    String COLUMN_KEY_SIGN = "schemaName-tblName-colName";

    /**
     * The Constant CSV.
     */
    String CSV = "CSV";

    /**
     * The Constant FETCH_COUNT.
     */
    int FETCH_COUNT = 500;

    // import excel format

    String STR_XLSX = "XLSX";

    String STR_XLS = "XLS";

    String STR_CSV = "CSV";

    String DOT = ".";

    String TEMP_ENVIRONMENT_VARIABLE = "TEMP";

    String SHOW_SEARCHPATH_QUERY = "SHOW search_path";

    String BOTTOM_BAR = "_";

    String PREF_FONT_STYLE_SIZE = "editor.font.size";

    String INVALID_INSERT = "invalid_insert";

    int MAX_PREFIX_SEARCH_LENGTH = 500;

    String CHAR_UNIT = "CHAR";

    String BYTE_UNIT = "BYTE";

    String COMMENT_KW = "comment";

    String DESCRIBE_USAGE = "DESCRIBE [schema.]object";

    String CREATE_KW = "create";

    String ALTER_INDEX_KW = "alter index";

    String ALTER_INDEX_IF_EXISTS_KW = "alter if exists index";

    String ALTER_KW = "alter";

    String DROP_KW = "drop";

    String INDEX_KW = "index";

    String TABLE_KW = "table";

    String IF_NOT_EXISTS_KW = "if not exists";

    String IF_EXISTS_KW = "if exists";

    String RENAME_KW = "rename";

    String SET_KW = "set";

    String SCHEMA_KW = "schema";

    String COLUMN_KW = "column";

    String ADD_KW = "add";

    String CONSTRAINT_KW = "constraint";

    String BLOB = "BLOB";

    String BLOB_WATERMARK = "[BLOB]";

    String BYTEA = "bytea";

    String BYTEA_WATERMARK = "[BYTEA]";

    String CURSOR_WATERMARK = "...";

    String FROM_TABLE = "from";

    String REF_CURSOR = "refcursor";

    String CURSOR = "CURSOR";

    String VOID = "void";

    String RETURN_VOID = "(void)";

    String RETURN_RESULT_COL_VALUE = "Result";

    String VAR_CHAR = "varchar";

    String PARAM = "param";

    String OUT = "OUT";

    String INOUT = "INOUT";

    String RECORD = "record";

    String EXEC = "exec";

    String CALL = "call";

    String QUESTION_MARK = "?";

    String DWS = "DWS";
    String ALTER_TABLE = "ALTER_TABLE";
    String CREATE_VIEW = "CREATE_VIEW";
    String DROP_VIEW = "DROP_VIEW";
    String DROP_TABLE = "DROP_TABLE";
    String CREATE_TABLE = "CREATE_TABLE";
    String ALTER_VIEW = "ALTER_VIEW";
    String SET_SCHEMA_VIEW = "SET_SCHEMA_VIEW";
    String SET_SCHEMA_TABLE = "SET_SCHEMA_TABLE";
    String CREATE_TRIGGER = "CREATE_TRIGGER";
    String ID_EDIT_TERMINAL_VALUE_BUTTON = "ID_EDIT_TERMINAL_VALUE_BUTTON";
    String PREF_USER_DEFINE_DATE_FORMAT = "userdefineddateformat.prefs";
    String PREF_USER_DEFINE_TIME_FORMAT = "userdefinedtimeformat.prefs";
    String PREF_SYSTEM_DATE_FORMAT = "systemdateformat.prefs";
    String PREF_SYSTEM_TIME_FORMAT = "systemtimeformat.prefs";
    String DATE_FORMAT_VALUE = "dateformatvalue.prefs";
    String TIME_FORMAT_VALUE = "timeformatvalue.prefs";
    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    String ML_COMMENT_START = "/*";
    String ML_COMMENT_END = "*/";

    int PBKDF_ITERATIONS = 10000;
    int ONE_KB = 1024;
    double FILESIZE_PERCENT_TO_XMX_VALUE = 0.008;

    int PARALLEL_IMPORT_EXPORT_DEFAULT = 10;
    int PARALLEL_IMPORT_EXPORT_MAX = 20;
    String PARALLEL_IMPORT_EXPORT_PREF = "exportpref.parallelimportexportlimitvalue";
    String DO_NOT_DELETE_PART_LABEL = "Do not delete this part";
    String WITH_CLAUSE_REGEX = "(?i)^with.*as\\s*\\(([^()]*|\\(([^()]*|\\([^()]*\\))*\\))*\\)\\s*.?(\\s*)"
            + "(select|insert|delete|update)";

    String BINARY_FILE_FORMAT = "Binary";

    /**
     * this for debug rollback config
     */
    String DEBUG_PREFERENCE_IF_ROLLBACK = "debug.rollback";
}
