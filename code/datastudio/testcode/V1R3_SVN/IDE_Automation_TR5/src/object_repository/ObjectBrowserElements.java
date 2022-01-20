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

/*************************************************************************
TITLE - OBJECT REPOSITORY OF OBJECT BROWSER
DESCRIPTION - COLLECTION OF EACH OBJECTS WITH THEIR IDENTIFIABLE-
PROPERTIES IN OBJECT BROWSER WINDOW 
TEST CASES COVERED - NA
 *************************************************************************/
package object_repository;

public class ObjectBrowserElements {

	/*Parameters for object browser elements*/
	public static String wTitle="Data Studio";
	public static String sControlID="SysTreeView321";
	public static String sButton="left";
	public static int nclicks=1;
	public static int xcord=96;//11;
	public static int ycord=9;
	public static String sToolbarControlID="ToolbarWindow3217";
	public static int iToolbarExportxcord=178;
	public static int iToolbarExportycord=11;
	public static int iToolbarFormatxcord=386;//335;//356;
	public static int iToolbarFormatycord=10;
	public static int iNewConnxcord=10;
	public static int iNewConnycord=12;

	/*Parameters for Create Database*/
	public static String wCreateDatabase = "Create database";
	public static String sDBName = "Edit1";
	public static String bOK = "Button2";
	public static String bCancel = "Button3";
	public static int ObjBrowserxcord=99;
	public static int ObjBrowserycord=9;

	//Browser refresh elements

	/*public static String wTitle="Data Studio";
	public static String sControlID="SysTreeView321";
	public static String sButton="left";
	public static int nclicks=1;*/
	public static int xRefreshcord=77;  
	public static int yRefreshcord=10;  

	//Table Refresh Elements

	public static int xTableRefreshcord=120;  
	public static int yTableRefreshcord=100;

	//Browser Expansion Elements

	public static int excord=13;
	public static int eycord=10;

	//Connect to new DB Elements
	public static int xDBcord=98;
	public static int yDBcord=27;

	//new connection password window Elements

	public static String wPasswordTitle="Data Studio";
	public static String sPasswordControlID="Edit1";
	public static int iNewTerminalxcord=310;
	public static int iNewTerminalycord=10;

	/*Parameters for Create Schema*/
	public static String wCreateSchema = "Create Schema";
	public static String wExportFinish = "Export Finished";
	public static String wRenameSchema = "Rename Schema";
	public static String wDropSchema = "Drop Schema";
	public static String wExportFailed = "Export Failed";
	public static String wFileOverwrite = "File Overwrite Confirmation.";
	public static String sShemaName = "SWT_Window03";
	public static String sRenameSchema = "SWT_Window03";
	public static String bSchemaOK = "Button1";
	public static String bSchemaCancel = "Button2";
	public static String bExportFinishOK = "Button1";
	public static String bRenameSchemaOK = "Button1";
	public static String bDropSchemaOK = "Button1";
	public static String bExportFailedOK = "Button1";
	public static String bFileOverwriteYes = "Button1";
	public static String wSetSchema = "Set Schema";
	public static String dSetSchema = "ComboBox1";
}
