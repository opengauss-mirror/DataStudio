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
TITLE - OBJECT REPOSITORY OF QUERY EDITOR
DESCRIPTION - COLLECTION OF EACH OBJECTS WITH THEIR IDENTIFIABLE-
PROPERTIES IN QUERY EDITOR WINDOW 
TEST CASES COVERED - NA
*************************************************************************/
package object_repository;

public class ExecQueryElements {

	/*Parameters for executing the query*/
	public static String wSQLTerminal = "Data Studio";
	public static String sSQLEditor = "SWT_Window026"; //"SWT_Window013"; 
	public static String sEditorHeader = "SWT_Window016";
	public static String sExeButton = "Button1";
	
	/* parameters for connection selection */
	public static String sConnection = "SWT_Window010";
	public static String sConnCombo = "ComboBox1";
	public static String sButton = "left";
	public static int iClick = 1;		
	public static int ixcord = 175;
	public static int iycord = 10;
	
	/* Parameters for cancel query */
	
	public static String sCancelButton = "Button2";
	
	/*parameters for cancel export/import*/
	
	public static String sCancel = "Button4";
	
	
	//parameters for cancel Opeartion popup
	
	public static String wCancelWindow = "Cancel Operation";
	public static String sOKButton = "Button1";
	public static String sNoButton = "Button2";
	public static String sFunctionEditor = "SWT_Window019";
	
}
