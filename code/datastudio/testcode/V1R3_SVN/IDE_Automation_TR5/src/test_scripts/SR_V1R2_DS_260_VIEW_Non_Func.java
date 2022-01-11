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
import object_repository.TablePropertyElements;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.ViewFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_260_VIEW_Non_Func {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_260_VIEW_Non_Func");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_260_VIEW_Non_Func");
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
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_01"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.pg_catalog_View_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_UP,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW pg_catalog.sameview AS select * from public.dept;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("permission denied"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create view in system schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_02"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW pg_catalog.sameview AS select * from public.dept1;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("[ERROR] Execution failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create view table is not available. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_04"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "create view auto.view2(e1,e2) as select empid from public.dept;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("[ERROR] Execution failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create view with column mismatch. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_05"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "create view public.viewname as select * from public.dept;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2).replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("[ERROR] Execution failed")&&sFlag1.contains("ERROR: relation viewname already exists"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create view with same exisiting viewname. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_06"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Public_Table_View_Navigation();
					ViewFunctions.renameViewObjectBrowser("viewname");
					sFlag1= BaseActions.ControlGetText("Rename View", "", "Static2");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Unable to rename view public.viewname."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to rename with same viewname. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_07"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("create view public.viewname1 as select * from public.dept;", "Execute");
					sFlag1 = QueryEditor.SingleQueryExe("ALTER VIEW viewname RENAME TO viewname1;", "Execute").replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("[ERROR] Execution failed")&&sFlag1.contains("ERROR: relation viewname1 already exists"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to rename view with already created view. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view public.viewname1;", "Execute");
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_08"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "create view auto.testview as select * from public.dept;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.Auto_Table_View_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.setSchema();
					Thread.sleep(GlobalConstants.MedWait);			
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1= BaseActions.ControlGetText("Set Schema", "", "Static2");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(sFlag1.contains("Unable to set schema for view auto.testview.")&&sFlag1.contains("ERROR: cannot move objects into system schema"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to set user view to system view. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.testview;", "Execute");
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_09"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.pg_catalog_View_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.setSchema();
					Thread.sleep(GlobalConstants.MedWait);			
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1= BaseActions.ControlGetText("Set Schema", "", "Static2");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(sFlag1.contains("Unable to set schema for view")&&sFlag1.contains("ERROR: permission denied:"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to set view for system view. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_13"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.pg_catalog_View_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.dropViewObjeBrowser("DROP");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag1.contains("Unable to drop view")&&sFlag1.contains("ERROR: permission denied:"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to drop system view. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_Invalid_03"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.setSchema();
					Thread.sleep(GlobalConstants.MedWait);			
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.sameview AS select * from auto.auto_largedata;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
					if(sFlag1.contains("[ERROR] Execution failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create view where table is not available in the schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.Auto_Import_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.setSchema();
					Thread.sleep(GlobalConstants.MedWait);			
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);
				}

			}

		}
		for(int i=1;i<=iRowCount;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}

