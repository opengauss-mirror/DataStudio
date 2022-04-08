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

package org.opengauss.mppdbide.view.utils.consts;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface UIConstants.
 *
 * @since 3.0.0
 */
public interface UIConstants {

    /**
     * The Constant UI_MAIN_WINDOW_ID.
     */
    public static final String UI_MAIN_WINDOW_ID = "org.opengauss.mppdbide.window.id.mainwindow";

    /**
     * The Constant UI_PART_PROGRESSBAR_ID.
     */
    public static final String UI_PART_PROGRESSBAR_ID = "org.opengauss.mppdbide.part.id.progressbar";

    /**
     * The Constant UI_PART_OBJECT_BROWSER_ID.
     */
    public static final String UI_PART_OBJECT_BROWSER_ID = "org.opengauss.mppdbide.part.id.objectbrowser";

    /**
     * The Constant PARTSTACK_ID_EDITOR.
     */
    public static final String PARTSTACK_ID_EDITOR = "org.opengauss.mppdbide.partstack.id.editor";

    /**
     * The Constant TEMPLATESTORE_PREFERENCE_KEY.
     */
    public static final String TEMPLATESTORE_PREFERENCE_KEY = "org.opengauss.mppdbide.editor.codetemplate.preferences.sql_templates";

    /**
     * The Constant NAME_MAX_LEN.
     */
    public static final int NAME_MAX_LEN = 32;

    /**
     * The Constant UI_PART_CONSOLE_ID.
     */
    public static final String UI_PART_CONSOLE_ID = "org.opengauss.mppdbide.part.id.console";

    /**
     * The Constant UI_PART_EDITOR_ID.
     */
    public static final String UI_PART_EDITOR_ID = "org.opengauss.mppdbide.part.id.editor";

    /**
     * The Constant UI_PART_PROPERTIES_ID.
     */
    public static final String UI_PART_PROPERTIES_ID = "org.opengauss.mppdbide.part.id.properties";

    /**
     * The Constant UI_HANDLER_REFRESH_DBOBJECT.
     */
    public static final String UI_HANDLER_REFRESH_DBOBJECT = "org.opengauss.mppdbide.handler.id.refreshdbobject";

    /**
     * The Constant UI_HANDLER_DISCONNECT_DBOBJECT.
     */
    public static final String UI_HANDLER_DISCONNECT_DBOBJECT = "org.opengauss.mppdbide.handler.id.disconnectconnectionprofile";

    /**
     * The Constant UI_HANDLER_EXECUTE_DBOBJECT.
     */
    public static final String UI_HANDLER_EXECUTE_DBOBJECT = "org.opengauss.mppdbide.handler.id.executedbobject";

    /**
     * The Constant UI_COMMAND_REFRESH_DBOBJECT.
     */
    public static final String UI_COMMAND_REFRESH_DBOBJECT = "org.opengauss.mppdbide.command.id.refreshdbobject";

    /**
     * The Constant UI_COMMAND_DISCONNECT_DBOBJECT.
     */
    public static final String UI_COMMAND_DISCONNECT_DBOBJECT = "org.opengauss.mppdbide.command.id."
            + "disconnectconnectionprofile";

    /**
     * The Constant UI_COMMAND_EXECUTE_DBOBJECT.
     */
    public static final String UI_COMMAND_EXECUTE_DBOBJECT = "org.opengauss.mppdbide.command.id.executedbobject";

    /**
     * The Constant UI_MENUITEM_CONNECTIONPROFILE_ID.
     */
    public static final String UI_MENUITEM_CONNECTIONPROFILE_ID = "org.opengauss.mppdbide.part.menu.id.objectbrowser";

    /**
     * The Constant UI_MENUITEM_DISCONNECT_CONNECTIONPROFILE_ID.
     */
    public static final String UI_MENUITEM_DISCONNECT_CONNECTIONPROFILE_ID = "org.opengauss.objectbrowser.popupmenu.id.disconnect";

    /**
     * The Constant UI_MENUITEM_REFRESH_DBOBJECT_ID.
     */
    public static final String UI_MENUITEM_REFRESH_DBOBJECT_ID = "org.opengauss.objectbrowser.popupmenu.id.refresh";

    /**
     * The Constant UI_PARTSTACK_OBJECTBROWSER.
     */
    public static final String UI_PARTSTACK_OBJECTBROWSER = "org.opengauss.mppdbide.partstack.id.objectbrowser";

    /**
     * The Constant UI_PARTSTACK_EDITOR.
     */
    public static final String UI_PARTSTACK_EDITOR = "org.opengauss.mppdbide.partstack.id.editor";

    /**
     * The Constant UI_PARTSTACK_CONSOLE.
     */
    public static final String UI_PARTSTACK_CONSOLE = "org.opengauss.mppdbide.partstack.id.console";

    /**
     * The Constant UI_VIEW_MENU_OPTION.
     */
    public static final String UI_VIEW_MENU_OPTION = "org.opengauss.mppdbide.menu.id.view";

    /**
     * The Constant UI_TOGGLE_OBJECT_BROWSER_MENU.
     */
    public static final String UI_TOGGLE_OBJECT_BROWSER_MENU = "org.opengauss.viewmenu.id.sqlterminal";

    /**
     * The Constant UI_TOGGLE_CONSOLE_MENU.
     */
    public static final String UI_TOGGLE_CONSOLE_MENU = "org.opengauss.viewmenu.id.console";

    /**
     * The Constant UI_COMMON_SEARCH_TOOL_CONTROL.
     */
    public static final String UI_COMMON_SEARCH_TOOL_CONTROL = "org.opengauss.mppdbide.view.toolcontrol.search";

    /**
     * The Constant UI_SQL_TERMINAL_MENU_CONNECTION.
     */
    public static final String UI_SQL_TERMINAL_MENU_CONNECTION = "org.opengauss.mppdbide.view.sqlterminaltoolbar.menu.selectconnection";

    /**
     * The Constant UI_OBJ_BROWSER_MENU_COLUMN_NOT_NULL.
     */
    public static final String UI_OBJ_BROWSER_MENU_COLUMN_NOT_NULL = "org.opengauss.objectbrowser.popupmenu.id.columnnotnull";

    /**
     * The Constant UI_CONSOLE_MENU.
     */
    public static final String UI_CONSOLE_MENU = "org.opengauss.mppdbide.view.popupmenu.console";

    /**
     * The Constant UI_SQL_TERMINAL_MENU.
     */
    public static final String UI_SQL_TERMINAL_MENU = "org.opengauss.mppdbide.view.popupmenu.sqlterminalmenu";

    /**
     * The Constant UI_PART_SEARCHWINDOW_ID.
     */
    public static final String UI_PART_SEARCHWINDOW_ID = "org.opengauss.mppdbide.command.id.searchwindow";

    /**
     * The Constant VIS_EXPLAIN_PART_ID.
     */
    public static final String VIS_EXPLAIN_PART_ID = "org.opengauss.mppdbide.part.id.visualexplain";

    /**
     * The Constant UI_PARTSTACK_ID_SQL_ASSISTANT.
     */
    public static final String UI_PARTSTACK_ID_SQL_ASSISTANT = "org.opengauss.mppdbide.partstack.id.dbassistant";

    /**
     * The Constant UI_PART_ID_SQL_ASSISTANT.
     */
    public static final String UI_PART_ID_SQL_ASSISTANT = "org.opengauss.mppdbide.view.part.dbassistant";

    /**
     * The Constant ERROR_ID.
     */
    public static final int ERROR_ID = -1;

    /**
     * The Constant OK_ID.
     */
    public static final int OK_ID = 0;

    /**
     * The Constant RUN_IN_BACK_GROUND_ID.
     */
    public static final int RUN_IN_BACK_GROUND_ID = 4;

    /**
     * The Constant CLEAR_ID.
     */
    public static final int CLEAR_ID = 3;

    /**
     * The Constant DEBUG_ID.
     */
    public static final int DEBUG_ID = 1;

    /**
     * The Constant CANCEL_ID.
     */
    public static final int CANCEL_ID = 2;

    /**
     * The Constant CLOSE_ID.
     */
    public static final int CLOSE_ID = 2;

    /**
     * The Constant CONTINUE_ID.
     */
    public static final int CONTINUE_ID = 0;

}
