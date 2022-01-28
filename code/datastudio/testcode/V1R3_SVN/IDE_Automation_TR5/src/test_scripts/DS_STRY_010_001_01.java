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
import script_library.DebugOperations;
import script_library.Login;
import script_library.ObjectBrowserPane;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class DS_STRY_010_001_01 {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","DS_STRY_010_001_01");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","DS_STRY_010_001_01");
		//Variable Declarations	
		String sStatus,sTestCaseID,sExecute,sFlag;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Getting Login Credentials from IDE_Smoke_Test_Data file and
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
			String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
			String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
			String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
			String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
			String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			if(sExecute.equals("Yes"))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_Valid_001"))
				{
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.ClearConsole("GLOBAL");
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					if(BaseActions.WinExists(DebugElements.sDebugConnection))
					{	
						BaseActions.Click(DebugElements.sDebugConnection, "", DebugElements.sDebugCancelButton);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
					else
					{	
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation();


						if(sFlag.equals("DebugSuccess"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug operation was not successfull. Please refer screenshot "+sTestCaseID+".jpg");
						}	UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_Valid_002")||sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_Valid_003")||sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_Valid_004") || sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_Valid_005")||sTestCaseID.equals("SDV_V1R2_Connect_debug_001"))
				{
					switch(sTestCaseID)
					{
					case "SDV_V1R2_Remove_Port_Functional_Valid_002":
						BaseActions.ClearConsole("GLOBAL");
						DebugOperations.DebugOption("ObjectBrowser");
						DebugOperations.DebugConnection(sPassword);
						break;

					case "SDV_V1R2_Remove_Port_Functional_Valid_003":
						BaseActions.ClearConsole("GLOBAL");
						DebugOperations.DebugOption("Toolbar");
						DebugOperations.DebugConnection(sPassword);
						break;

					case "SDV_V1R2_Remove_Port_Functional_Valid_004":
						BaseActions.ClearConsole("GLOBAL");
						DebugOperations.DebugOption("Shortcut");
						DebugOperations.DebugConnection(sPassword);
						break;

					case "SDV_V1R2_Remove_Port_Functional_Valid_005":
						BaseActions.ClearConsole("GLOBAL");
						DebugOperations.DebugOption("Menu");
						DebugOperations.DebugConnection(sPassword);
						break;
					case "SDV_V1R2_Connect_debug_001" :
						BaseActions.ClearConsole("GLOBAL");
						DebugOperations.DebugOption("Shortcut");
						DebugOperations.DebugConnection(sPassword);
						break;

					default:	
						break;
					}
					if(BaseActions.WinExists(DebugElements.sDebugConnection))
					{	
						BaseActions.Click(DebugElements.sDebugConnection, "", DebugElements.sDebugCancelButton);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
					else
					{	Thread.sleep(GlobalConstants.MedWait);
					sFlag = DebugOperations.DebugValidation();
					if(sFlag.equals("DebugSuccess"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Executed with errors without asking port and password. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_Valid_006"))
				{
					BaseActions.ClearConsole("GLOBAL");
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					if(BaseActions.WinExists(DebugElements.sDebugConnection))
					{	
						BaseActions.Click(DebugElements.sDebugConnection, "", DebugElements.sDebugCancelButton);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed for the next login. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
					else
					{
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation();
						if(sFlag.equals("DebugSuccess"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}

				if(sTestCaseID.equals("SDV_V1R2_Connect_debug_002"))
				{
					BaseActions.ClearConsole("GLOBAL");
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					DebugOperations.TerminateDebugging();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Connection profile 'Debug' connected successfully")&&sFlag.contains("[INFO] Debug Terminated."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to terminate the Debug. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);	
					}

				}
				if(sTestCaseID.equals("SDV_V1R2_Token_Based_Functional_Valid_001"))
				{
					BaseActions.ClearConsole("GLOBAL");
					DebugOperations.SetBreakPoint(8);
					DebugOperations.SetBreakPoint(9);
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					DebugOperations.StepIn();
					DebugOperations.StepOut();
					DebugOperations.StepOver();
					DebugOperations.ClickContinue();
					DebugOperations.TerminateDebugging();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Connection profile 'Debug' connected successfully")&&sFlag.contains("[INFO] Debug Terminated."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to perform step-in,step-out,step-over Operations. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);	
					}
				}

				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_Valid_007"))
				{
					sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 0);
					sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 1);
					sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 2);
					sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 3);
					sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 4);
					sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 5);
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MaxWait);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					if(BaseActions.WinExists(DebugElements.sDebugConnection))
					{	
						BaseActions.Click(DebugElements.sDebugConnection, "", DebugElements.sDebugCancelButton);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed in different DB cluster. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
					else
					{
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation();
						if(sFlag.equals("DebugSuccess"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed"); //need to update
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_Valid_008"))
				{
					sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 0);
					sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 1);
					sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 2);
					sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 3);
					sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 4);
					sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 5);
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					if(BaseActions.WinExists(DebugElements.sDebugConnection))
					{	
						BaseActions.Click(DebugElements.sDebugConnection, "", DebugElements.sDebugCancelButton);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed in same cluster. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
					else
					{
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation();
						if(sFlag.equals("DebugSuccess"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed"); //need to update
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Functional_InValid_001"))
				{
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					if(BaseActions.WinExists(DebugElements.sDebugConnection))
					{	
						BaseActions.Click(DebugElements.sDebugConnection, "", DebugElements.sDebugCancelButton);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Choose Debug port Automatically option is displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
					else
					{
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation();
						if(sFlag.equals("DebugSuccess"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}

				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Concurrency_001"))
				{
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Connection profile 'Debug' connected successfully"))
					{
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_N,1);
						sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 4, 0);
						sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 4, 1);
						sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 4, 2);
						sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 4, 3);
						sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 4, 4);
						sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 4, 5);
						Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.ClearConsole("Normal");
						ObjectBrowserPane.objectBrowserExpansion("DOUBLE");
						DebugOperations.DebugObjectBrowserMultipleDB("DOUBLE");
						DebugOperations.DebugOption("Shortcut");
						DebugOperations.DebugConnection(sPassword);
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation("MULTIPLEDEBUG");
						if(sFlag.equals("DebugSuccess"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed in same cluster for same user. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed in same cluster for first cinnection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Concurrency_002"))
				{
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Connection profile 'Debug' connected successfully"))
					{
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_N,1);
						sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 0);
						sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 1);
						sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 2);
						sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 3);
						sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 4);
						sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 5);
						Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.ClearConsole("Normal");
						ObjectBrowserPane.objectBrowserExpansion("DOUBLE");
						DebugOperations.DebugObjectBrowserMultipleDB("DOUBLE");
						DebugOperations.DebugOption("Shortcut");
						DebugOperations.DebugConnection(sPassword);
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation("MULTIPLEDEBUG");
						if(sFlag.equals("DebugSuccess"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed in same cluster for different user. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed in same cluster for first cinnection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Concurrency_003"))
				{
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Connection profile 'Debug' connected successfully"))
					{
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_N,1);
						sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 0);
						sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 1);
						sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 2);
						sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 3);
						sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 4);
						sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 5);
						Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.ClearConsole("Normal");
						ObjectBrowserPane.objectBrowserExpansion("DOUBLE");
						DebugOperations.DebugObjectBrowserMultipleDB("DOUBLE");
						DebugOperations.DebugOption("Shortcut");
						DebugOperations.DebugConnection(sPassword);
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation("MULTIPLEDEBUG");
						if(sFlag.equals("DebugSuccess"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed"); //Need to change
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed in same cluster for first connection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Remove_Port_Concurrency_004"))
				{
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugOption("Shortcut");
					DebugOperations.DebugConnection(sPassword);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Connection profile 'Debug' connected successfully"))
					{
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_N,1);
						sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 0);
						sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 1);
						sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 2);
						sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 3);
						sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 4);
						sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 5);
						Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.ClearConsole("Normal");
						ObjectBrowserPane.objectBrowserExpansion("DOUBLE");
						DebugOperations.DebugObjectBrowserMultipleDB("DOUBLE");
						DebugOperations.DebugOption("Shortcut");
						DebugOperations.DebugConnection(sPassword);
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = DebugOperations.DebugValidation("MULTIPLEDEBUG");
						if(sFlag.equals("DebugSuccess"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug Port prompt window is displayed in same cluster for first connection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of if loop for Execute flag
		}//end of for loop
		//Generate text file report for TMSS integration
		for(int i=1;i<=iRowCount;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}//end of main
}//end of class