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
 
package object_repository;

public class ExpQueryElements {

	/*Parameters for Toolbar export and import a query*/
	public static String wToolbarSQLWindow = "Data Studio";
	public static String sToolbarSQLControlID = "ToolbarWindow3218"; //ToolbarWindow3217
	public static String sButton="left";
	public static int nclicks=1;
	public static int iToolbarSQLExportxcord= 158; //133;
	public static int iToolbarSQLExportycord= 10;

	public static int iToolbarSQLOpenxcord=156;
	public static int iToolbarSQLOpenycord=10;

	//query save as elements

	public static String wSQLSaveasWindow = "Save As";
	public static String sSQLSaveasControlID = "Edit1";
	public static String sSaveButton="Button1";
	public static String sCancelButton="Button2";

	//Query open elements

	public static String wSQLOpenWindow = "Open";
	public static String sSQLOpenControlID = "Edit1";
	public static String sOpenButton="Button1";

	//open sql Elements

	public static String wSQLAppendWindow = "Open SQL";
	public static String sSQLAppendControlID = "Edit1";
	public static String sAppendButton = "Button1";
	public static String sOverWriteButton = "Button2";

	//Truncate Table window elements
	public static String wTruncateTableWindow = "Truncate Table";
	public static String bTruncateTableOK = "Button1";
	public static String bTruncateTableCancel = "Button2";

	//Import Invalid Table Data Format Window
	public static String wImportTableDataWindow = "Import Table Data";
	public static String bImportTableOK = "Button1";

	public static String wFileOpenWindow = "Open";
	
	public static String wFileOverwriteWindow = "File Overwrite Confirmation";
	public static String bYesButton = "Button1";
	public static String sOpenOKButton = "Button1";
	public static String wInvalidSQLWindow = "Save SQL";
	public static String sSaveOKButton = "Button1";
}

