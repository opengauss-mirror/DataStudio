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
import java.io.File;

import object_repository.ConsoleResultElements;
import object_repository.CreateDBElements;
import object_repository.GlobalConstants;
import script_library.DebugOperations;
import script_library.Login;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_Logs {

	public static void main(String sARNumber) throws Exception {

		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_Logs");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_Logs");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);

			if(sExecute.equalsIgnoreCase("Yes")){

				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "SR_V1R2_DS_Logs", i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");


				if(sTestCaseID.equals("PTS_TOR.SPC210.009_Functional_valid_1"))
				{
					File file = new File("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log");

					if(file.exists())
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + " passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Log file is not available in the respective folder. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + " failed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.SPC210.009_Functional_valid_2")){                  //Test case mapped PTS_TOR.SPC210.009_Functional_valid_4

					File file = new File("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log");

					String lineToRemove = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel");
					System.out.println("Line to remove: "+lineToRemove);
					if(lineToRemove.contains("logginglevel")){

						UtilityFunctions.deleteLineFromFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio2.ini", lineToRemove);

					}
					UtilityFunctions.appendToFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel=ERROR");

					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("Select * from firsttable", "Valid");
					Thread.sleep(GlobalConstants.MedWait);

					String logs = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log", "firsttable");

					if(logs.contains("ERROR: relation \"firsttable\" does not exist")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + " passed");

					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Log file is not generated with ERROR level. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + " failed");
					}

					Login.IDELogout();

				}

				if(sTestCaseID.equals("PTS_TOR.SPC210.009_Functional_valid_6")){

					System.load(GlobalConstants.sJacobDLL);

					Login.LaunchIDE(GlobalConstants.sIDEPath);

					String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MaxWait);

					String lineToRemove = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel");

					if(lineToRemove.contains("logginglevel")){

						UtilityFunctions.deleteLineFromFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio2.ini", lineToRemove);

					}
					UtilityFunctions.appendToFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel=ERroR");

					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("Select * from secondtable", "Valid");
					Thread.sleep(GlobalConstants.MedWait);

					String logs = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log", "secondtable");

					if(logs.contains("ERROR: relation \"secondtable\" does not exist")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + " passed");

					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Log file is not generated with ERROR level. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + " failed");
					}

					Login.IDELogout();

				}

				if(sTestCaseID.equals("PTS_TOR.SPC210.009_Functional_valid_7")){

					System.load(GlobalConstants.sJacobDLL);

					Login.LaunchIDE(GlobalConstants.sIDEPath);

					String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MaxWait);

					String lineToRemove = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel");

					if(lineToRemove.contains("logginglevel")){

						UtilityFunctions.deleteLineFromFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio2.ini", lineToRemove);

					}
					UtilityFunctions.appendToFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel=[ERROR ]");

					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("Select * from thirdtable", "Valid");
					Thread.sleep(GlobalConstants.MedWait);

					String logs = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log", "thirdtable");

					if(logs.contains("ERROR: relation \"thirdtable\" does not exist")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + " passed");

					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Log file is not generated with ERROR level. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + " failed");
					}

					Login.IDELogout();
				}

				if(sTestCaseID.equals("PTS_TOR.SPC210.009_Functional_Invalid_1")){

					System.load(GlobalConstants.sJacobDLL);

					Login.LaunchIDE(GlobalConstants.sIDEPath);

					String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MaxWait);

					String lineToRemove = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel");

					if(lineToRemove.contains("logginglevel")){

						UtilityFunctions.deleteLineFromFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio2.ini", lineToRemove);

					}
					UtilityFunctions.appendToFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel=[ERROR $@^$@^@^  ] ");

					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("Select * from fourthtable", "Valid");
					Thread.sleep(GlobalConstants.MedWait);

					String logs = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log", "fourthtable");

					if(logs.contains("ERROR: relation \"fourthtable\" does not exist")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Log file is not generated with ERROR level. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + " failed");


					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + " passed");	
					}

					Login.IDELogout();
				}

				if(sTestCaseID.equals("PTS_TOR.SPC210.009_Functional_valid_5")){

					System.load(GlobalConstants.sJacobDLL);

					Login.LaunchIDE(GlobalConstants.sIDEPath);

					String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MaxWait);

					String lineToRemove = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel");

					if(lineToRemove.contains("logginglevel")){

						UtilityFunctions.deleteLineFromFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio2.ini", lineToRemove);

					}
					UtilityFunctions.appendToFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.ini", "-logginglevel=WARN");

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.SetBreakPoint(6);
					DebugOperations.SetBreakPoint(7);
					DebugOperations.SetBreakPoint(8);
					DebugOperations.DebugOption("Shortcut");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					BaseActions.Winwait("Drop Debug Object");
					BaseActions.Click("Drop Debug Object", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					String logs = UtilityFunctions.searchFile("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log", "WARN");

					if(logs.contains("WARN")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + " passed");

					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Log file is not generated with WARN level. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + " failed");
					}
				}

			}


		}
		for(int i=1;i<=9;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}

}
