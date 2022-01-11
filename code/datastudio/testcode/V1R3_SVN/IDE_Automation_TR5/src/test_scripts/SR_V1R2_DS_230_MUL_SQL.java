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

package test_scripts;

import java.awt.event.KeyEvent;

import object_repository.DebugElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import script_library.DebugOperations;
import script_library.Login;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_230_MUL_SQL {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_MUL_SQL");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_MUL_SQL");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag1,sFlag2,sFlag3,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			if(sExecute.equals("Yes"))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_016")) //Testcases covered SDV_FUN_VAL_DS_Multiple_SQL_Terminal_010,SDV_FUN_VAL_DS_Multiple_SQL_Terminal_011
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					sFlag1 = QueryEditor.SingleQueryExe("select * from pg_am;","Valid");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to execute the Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.TerminalConsoleClear(1);
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_017")) //testcase covered SDV_FUN_VAL_DS_Multiple_SQL_Terminal_018,SDV_FUN_VAL_DS_Multiple_SQL_Terminal_008
				{

					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					MultipleTerminal.OpenNewTerminalFromDB(1); //need to change the function once the defect is fixed
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.TerminalSetText(2,"Select * from pg_amop;");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to execute the Query in multiple sql terminal. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.TerminalConsoleClear(1);
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_019"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					MultipleTerminal.SelectTerminal(1);
					QueryEditor.SingleQueryExe("select count(*) from public.auto_largedata;","Normal");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.CancelQuery("SHORTCUT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(1);
					MultipleTerminal.TerminalSetText(2,"select count(*) from public.auto_largedata;");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.CancelQuery("SHORTCUT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = MultipleTerminal.TerminalConsoleCopy(2);
					if(sFlag1.contains("[INFO] Canceled the query on user request.")&&sFlag2.contains("[INFO] Canceled the query on user request."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Cancel Button is not enabled in multiple terminals. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_020")) //Test case covered SDV_FUN_INVAL_DS_Multiple_SQL_Terminal_002
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait("New Database Connection");
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 5);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName,sConnection);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, sHost);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, sHostPort);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, sDBName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, sUserName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, sPassword);
					BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					Thread.sleep(GlobalConstants.MaxWait);
					MultipleTerminal.SetFunction(2,"select * from public.utf8;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = MultipleTerminal.TerminalConsoleCopy(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SelectTerminal(1);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select * from public.ztable;", "Normal");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(1);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SelectTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					if((sFlag1.contains("[ERROR] Execution failed."))&&(sFlag2.contains("[ERROR] Execution failed.")))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to execute quieres which are not present the current database. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.TerminalConsoleClear(1);
					Thread.sleep(GlobalConstants.MinWait);//Terminal two is still open
					MultipleTerminal.TerminalConsoleClear(2);
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Multiple_SQL_Terminal_003"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					MultipleTerminal.SelectTerminal(1);
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select * from pg_am;");
					QueryEditor.SetFunction("select * from public.ztable;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = QueryResult.ReadConsoleOutput("TERMINAL").replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("ERROR: relation public.ztable does not exist"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to execute quieres which are not present the current database. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.TerminalConsoleClear(1);
					Thread.sleep(GlobalConstants.MinWait);
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_030")) //SDV_FUN_VAL_DS_Multiple_SQL_Terminal_023
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					MultipleTerminal.SelectTerminal(1);
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("select * from pg_am;");
					QueryEditor.SetFunction("Select * from pg_amop;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = QueryResult.ReadConsoleOutput("TERMINAL").trim();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "select * from pg_class;");
					MultipleTerminal.SetFunction(2, "select * from pg_attribute;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag2 = MultipleTerminal.TerminalConsoleCopy(2).trim();
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag1.contains("Records fetched: 6")&&sFlag1.contains("Records fetched: 581")&&sFlag2.contains("Records fetched: 1,000"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to execute the quieres in multiple Sql Terminal. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.TerminalConsoleClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalConsoleClear(1);
				}

				/**
				 * SDV_FUN_VAL_DS_Multiple_SQL_Terminal_032 needs to be scripted at this position
				 */

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_032")) //Testcase Covered SDV_FUN_VAL_DS_Multiple_SQL_Terminal_033,SDV_FUN_VAL_DS_Multiple_SQL_Terminal_034,SDV_FUN_INVAL_DS_Multiple_SQL_Terminal_004,SDV_FUN_INVAL_DS_Multiple_SQL_Terminal_005,SDV_FUN_VAL_DS_Multiple_SQL_Terminal_031
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					MultipleTerminal.SelectTerminal(1);
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("insert into public.utf82 values('A','AA');");
					QueryEditor.SetFunction("update public.utf82 set coll2 = 'BB' where coll1 ='A';");
					QueryEditor.SetFunction("delete public.utf82 where coll1 = 'A';");
					Thread.sleep(GlobalConstants.ModWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = QueryResult.ReadConsoleOutput("TERMINAL").trim();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "insert into public.ztable values('A','AA');");
					MultipleTerminal.SetFunction(2, "update public.ztable set col2 = 'BB' where col1 ='A';");
					MultipleTerminal.SetFunction(2, "delete public.ztable where col1 = 'A';");
					Thread.sleep(GlobalConstants.ModWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag2 = MultipleTerminal.TerminalConsoleCopy(2).trim();
					if(sFlag1.contains("[INFO] Executed Successfully...")&&sFlag1.contains("[INFO] Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to execute the DML quieres in multiple Sql Terminal. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.TerminalConsoleClear(1);
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.TerminalConsoleClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("DOUBLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.removeConnection();
					Thread.sleep(GlobalConstants.MedWait);
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_003")) //testcase Covered SDV_FUN_VAL_DS_Multiple_SQL_Terminal_013
				{
					BaseActions.Focus("Data Studio", "", "ComboBox1");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = BaseActions.ControlGetText("Data Studio", "", "ComboBox1");
					if(sFlag1.equals("postgres@DSConnSP2"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5," connection name is incorrect. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}


				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_039")) //Test cases Covered SDV_FUN_VAL_DS_Multiple_SQL_Terminal_040,SDV_FUN_VAL_DS_Multiple_SQL_Terminal_009
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MinWait);
					DebugOperations.DebugOption("ObjectBrowser");		
					Thread.sleep(GlobalConstants.MinWait);
					Login.DebugWindows();
					Thread.sleep(GlobalConstants.ModWait);
					sFlag1 = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("9")&&sFlag2.contains("Connection profile 'Debug' connected successfully"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug is not opening in a new window .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_041"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
							150,80);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					String sFlag = UtilityFunctions.GetClipBoard().replace("\"", "");
					if(sFlag.equals("SELECT auto.auto1()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Query is not matching with the procedure .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_042"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					sFlag1 = MultipleTerminal.TerminalResultTabOperations(2, "COPY"); //co-ordinates needs to be changed
					if(sFlag1.contains("9"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Result is not updated with the correct value.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.CloseTerminal(2);
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_043"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
							150,80);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("CREATE OR REPLACE FUNCTION auto.auto1()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"PL SQL is not loaded.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_044"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

					BaseActions.MouseClick(DebugElements.wDebugpane, "","SWT_Window0116", DebugElements.sMouseButton,DebugElements.iClick, 6, 144);//setting breakpoint at 9
					Thread.sleep(GlobalConstants.MedWait);
					Login.DebugWindows();
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Menu");
					Thread.sleep(GlobalConstants.MedWait);
					if(BaseActions.ObjExists(LoginElements.wIDEWindow,"","SysListView322"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"breakpoint/callstack/Variable windows are not loaded "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Multiple_SQL_Terminal_006")) //Test case covered SDV_FUN_INVAL_DS_Multiple_SQL_Terminal_007,SDV_FUN_VAL_DS_Multiple_SQL_Terminal_012
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Login.DebugWindows();//Breakpoint window should be closed
					Thread.sleep(GlobalConstants.ModWait);
					if(BaseActions.ObjExists(LoginElements.wIDEWindow,"","SysListView322"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"breakpoint/callstack/Variable windows are not disabled "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Multiple_SQL_Terminal_008")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					sFlag1 = DebugOperations.GetBreakpoint();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag2 = DebugOperations.GetCallStack();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag3 = DebugOperations.GetVariables();
					if(sFlag1.contains("false,9,auto1() - integer")&&sFlag2.contains("auto1() - integer [Line : 9]")&&sFlag3.contains("false,m,integer,5"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Shortcuts are not working "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.ClickContinue();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.CloseTerminal(2);
				}


				/**
				 * THIS SHOULD BE THE LAST TESTCASE
				 */
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_025")) //Test Cases Covered SDV_FUN_VAL_DS_Multiple_SQL_Terminal_026,SDV_FUN_VAL_DS_Multiple_SQL_Terminal_027,SDV_FUN_VAL_DS_Multiple_SQL_Terminal_004
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Login.DebugWindows();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.newConnection();
					Thread.sleep(GlobalConstants.ModWait);
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 5);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName,sConnection);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, sHost);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, sHostPort);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, sDBName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, sUserName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, sPassword);
					BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					Thread.sleep(GlobalConstants.MaxWait);
					QueryEditor.SelectDBConnection();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalOperations(3, "CLOSE");
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalOperations(2, "CLOSE_OTHERS");
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalOperations(1, "CLOSE_ALL");
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.SetFunction(1, "select * from pg_am");
					QueryEditor.ExecuteButton();
					sFlag1= MultipleTerminal.TerminalConsoleCopy(1);
					if(sFlag1.contains("Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to Execute the query without SQL Editor. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Multiple_SQL_Terminal_015")) //Test case covered SDV_FUN_VAL_DS_Multiple_SQL_Terminal_014
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					BaseActions.Winwait("New Database Connection");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Focus("Data Studio", "", "ComboBox1");
					sFlag1 = BaseActions.ControlGetText("Data Studio", "", "ComboBox1");
					if(sFlag1.equals("-- Connections --"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Connections still exsists.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Multiple_SQL_Terminal_001"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait("New Database Connection");
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 8, 5);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName,sConnection);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, sHost);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, sHostPort);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, sDBName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, sUserName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, sPassword);
					BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					BaseActions.Winwait("An error occurred while creating connection.");
					if(BaseActions.WinExists("An error occurred while creating connection."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User able to login with in correct password.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
				}

			}
		}
		for(int i=1;i<=17;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}

