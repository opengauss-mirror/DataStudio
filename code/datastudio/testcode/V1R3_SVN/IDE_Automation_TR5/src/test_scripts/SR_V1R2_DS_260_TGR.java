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

import object_repository.GlobalConstants;
import script_library.CreateTableWizardFunctions;
import script_library.ExecutionHistoryFunctions;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.ViewFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_260_TGR {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_260_TGR");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_260_TGR");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag1,sFlag2,sStatus;
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
				if(sTestCaseID.equals("PTS_TOR.260.002_Functional_valid_1"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.CreateTableWizard();
					Thread.sleep(GlobalConstants.MinWait);
					if(BaseActions.WinExists("Create New table"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Triggers are not removed in user schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
				}

				if(sTestCaseID.equals("PTS_TOR.260.002_Functional_valid_3"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_UP, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = UtilityFunctions.GetClipBoard().replace("\"", "");
					if(sFlag1.contains("CREATE [OR REPLACE] FUNCTION auto.function_name ([ parameter datatype[,parameter datatype] ])"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Create Trigger Function is not removed from the Functions/Procedures user group context menu. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.TerminalOperations(2, "CLOSE");
					Thread.sleep(GlobalConstants.MinWait);
				}


				if(sTestCaseID.equals("PTS_TOR.260.002_Functional_valid_2"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.pg_catalog_View_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MinWait);
					if(BaseActions.WinExists("Create New table"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Triggers are not removed in system schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
				}

				if(sTestCaseID.equals("PTS_TOR.260.002_Functional_valid_4"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.pg_catalog_View_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = UtilityFunctions.GetClipBoard().replace("\"", "");  
					if(sFlag1.contains("CREATE [OR REPLACE] FUNCTION pg_catalog.function_name"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Create Trigger Function is not removed from the Functions/Procedures system group context menu. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.TerminalOperations(2, "CLOSE");
					Thread.sleep(GlobalConstants.MinWait);

				}

				if(sTestCaseID.equals("PTS_TOR.260.002_Functional_valid_5"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SetFunction("CREATE OR REPLACE FUNCTION auto.test()");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SetFunction("RETURNS trigger LANGUAGE plpgsql");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SetFunction("AS $$ DECLARE");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("BEGIN END $$ ");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag1 = QueryResult.ReadConsoleOutput("TERMINAl");
					if(sFlag1.contains("[INFO] Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create triggers. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("PTS_TOR.260.002_FEATURE.130.006")) //after sql history this will be done
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(sFlag1.contains("CREATE OR REPLACE FUNCTION auto.test()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Triggers are not saved in the SQl History. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);

				}

				if(sTestCaseID.equals("PTS_TOR.260.002_FEATURE.130.007"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
					if(sFlag1.contains("ERROR: trigger functions can only be called as triggers"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create triggers. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
				}
				if(sTestCaseID.equals("PTS_TOR.260.002_FT.AUTOFILL.001"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SetQuery("SELECT auto.");
					QueryEditor.AutoSuggestInvoke();
					sFlag1 = QueryEditor.AutoSuggestCopy();
					if(sFlag1.contains("test() - <unknown> - auto - Function"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Ale to see triggers in the autosuggest. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					QueryEditor.SingleQueryExe("drop function auto.test();", "Invalid");

				}
			}
		}
		for(int i=1;i<=iRowCount;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus = UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}
