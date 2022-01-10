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
import script_library.DebugOperations;
import script_library.EditTableDataFunctions;
import script_library.ExecutionHistoryFunctions;
import script_library.Login;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_310_EXE_HTY {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_310_EXE_HTY");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_310_EXE_HTY");
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

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_001"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					if(!BaseActions.WinExists("SQL Execution History - DSConnSP2"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Execution history window is enabled by default. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_002"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(3500);
					if(BaseActions.WinExists("SQL Execution History - DSConnSP2"))
					{
						ExecutionHistoryFunctions.selectExeHistoryQuery(1);
						Thread.sleep(GlobalConstants.MinWait);
						ExecutionHistoryFunctions.exeHistoryOperations("DELETEALL");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Execution history window is not opened through toolbar. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.closeExeHistory();
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_003")) //need to script after the shortcut key for copy is provided by commenting the last step of closing the history window
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(1);
					if(sFlag1.contains(""))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The details are not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}


				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_004")) //Testcases covered SDV_FUN_VAL_DS_SQLExec_Hist_014,SDV_FUN_VAL_DS_SQLExec_Hist_017
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SetFunction("select * from pg_am;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",2).trim();
					if(sFlag1.equals("select * from pg_am;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Executed query is not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_005"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select eid from auto.auto_largedata;", "InValid");
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(sFlag1.equals("select eid from auto.auto_largedata;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"pseudo coloumn query is not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_007"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(2);
					if(sFlag1.contains("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else

					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Query is not marked as success. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_044")) //SDV_FUN_VAL_DS_SQLExec_Hist_017
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",2).trim();
					if(sFlag1.equals("select eid from auto.auto_largedata;")&&sFlag2.contentEquals("select * from pg_am;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else

					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The new & existing query is not avalaible. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_006"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					QueryEditor.SingleQueryExe("select * from pg_am;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(sFlag1.equals("select * from pg_am;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Same Executed query is not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_012")) //Test case covered SDV_FUN_VAL_DS_SQLExec_Hist_025
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select * from public.viewname;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(sFlag1.equals("select * from public.viewname;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The View is not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_014")) //testcase covers SDV_FUN_VAL_DS_SQLExec_Hist_023
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("SELECT auto.auto1();", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(sFlag1.equals("SELECT auto.auto1();"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"PLSQL Function is not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_016")) //test case mapped SDV_FUN_VAL_DS_SQLExec_Hist_021
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SetFunction("select a.ename,b.dept from auto.auto_largedata a,public.auto_largedata b where a.eid=b.eid;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(sFlag1.equals("select a.ename,b.dept from auto.auto_largedata a,public.auto_largedata b where a.eid=b.eid;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"join query is not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_028"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(6);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("DELETE");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(6);
					if(!sFlag1.contains("select * from pg_am;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The deleted query is still showing in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_033"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(5);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("PIN");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(1);
					if(sFlag1.contains("Pinned"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The pinned Sql Query status is not showing in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_035")) //Testcase covered SDV_FUN_VAL_DS_SQLExec_Hist_041
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("DELETE");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(sFlag1.equals("select eid from auto.auto_largedata;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The pinned Sql query is getting deleted in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_043"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.disconnectDB(1);
					Thread.sleep(GlobalConstants.ModWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(1);
					if(sFlag1.contains("Pinned"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The pinned Sql Query status is changed in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_050"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.disconnectDB(1);
					Thread.sleep(GlobalConstants.ModWait);
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click("Data Studio", "", "Button3"); //clicking execute history button
					BaseActions.Winwait("SQL Execution History - DSConnSP2");
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("LOAD");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					sFlag1=QueryEditor.CopyEditor().trim();
					if(sFlag1.equals("select eid from auto.auto_largedata;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The pinned Sql Query is not loaded in sql terminal. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_SQLExec_Hist_014"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("select amname from pg_am;");
					QueryEditor.ExecuteButton();
					BaseActions.Winwait("SQL Execution");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					Thread.sleep(GlobalConstants.MinWait);
					if(!sFlag1.equals("select amname from pg_am;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The query is saved in sql history when there is no connection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
				}


				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_045"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(4);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("LOAD");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					sFlag1=QueryEditor.CopyEditor().trim();
					if(sFlag1.equals("select * from public.viewname;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Query is not loaded in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_049"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("LOAD");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					sFlag1=QueryEditor.CopyEditor().trim();
					if(sFlag1.equals("select eid from auto.auto_largedata;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Pinned Query is not loaded in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_051"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					/**
					 * Code to open the SQL History Window without clearEditor
					 */
					BaseActions.Focus("Data Studio", "", "Button3");
					BaseActions.Click("Data Studio", "", "Button3");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(3);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("LOAD");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					sFlag1=QueryEditor.CopyEditor().trim();
					if(sFlag1.contains("select eid from auto.auto_largedata;")&&sFlag1.contains("SELECT auto.auto1();"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The exsisting query is over written in by the new query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_054"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("LOADANDCLOSE");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1=QueryEditor.CopyEditor().trim();
					if(sFlag1.equals("select eid from auto.auto_largedata;")&&!BaseActions.WinExists("SQL Execution History - DSConnSP2"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Pinned Query is not loaded in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_038"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("UNPIN");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(4);
					if(!sFlag1.contains("Pinned"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The query is not unpinned. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();

				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_039"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(5);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("DELETE");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",4).trim();
					if(!sFlag1.contains("select eid from auto.auto_largedata;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Unpinned query is not deleted. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}
				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_SQLExec_Hist_001")) //Testcase Covered SDV_FUN_INVAL_DS_SQLExec_Hist_002
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MaxWait);
					QueryEditor.SingleQueryExe("select eid from auto.auto_large;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(1).trim();
					if(sFlag1.contains("Failure"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The failed Query is marked as Success. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}
				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_SQLExec_Hist_004"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select * from utf82;;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(!sFlag1.equals("select * from utf82;;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The query is saved with multiple semi colons. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}
				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_SQLExec_Hist_005"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("--select * from public.employee;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(!sFlag1.equals("--select * from public.employee;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The commenetd query is saved in execution history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_SQLExec_Hist_009"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.CloseTerminal(2);
					Login.DebugWindows();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(!sFlag1.contains("CREATE OR REPLACE FUNCTION auto.auto1()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The PLSQL is saved in execution history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();

				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_SQLExec_Hist_011"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("select count(*) from public.largedata;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.CancelQuery("SHORTCUT");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(sFlag1.contains("select count(*) from public.largedata;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The cancelled query is not saved in execution history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_SQLExec_Hist_013"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(!sFlag1.contains("select * from autotable.rowtable;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The edit table data query is saved in execution history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_011")) //Testcase Covered SDV_FUN_VAL_DS_SQLExec_Hist_009
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("insert into public.utf82 values('A','AA');");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("update public.utf82 set coll2 = 'BB' where coll1 ='A';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("delete public.utf82 where coll1 = 'A';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1=ExecutionHistoryFunctions.copyExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = ExecutionHistoryFunctions.copyExeHistoryQuery(2);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag3 = ExecutionHistoryFunctions.copyExeHistoryQuery(3);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					if(sFlag1.contains("delete public.utf82 where coll1 = 'A';")&&sFlag2.contains("update public.utf82 set coll2 = 'BB' where coll1 ='A';")&&sFlag3.contains("GMT")||sFlag3.contains("IST") )
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The DML Statements are not saved in execution history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_015"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("DO $$BEGIN RAISE NOTICE 'Hello %', SESSION_USER; END; $$;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1).trim();
					if(sFlag1.equals("DO $$BEGIN RAISE NOTICE 'Hello %', SESSION_USER; END; $$;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Anonymous block is not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_053"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("select a.empid,a.ename,a.salary,b.empid,b.ename,b.salary,");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("c.empid,c.ename,c.salary,d.empid,d.ename,d.salary,");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("e.empid,e.ename,e.salary,f.empid,f.ename,f.salary from ");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("public.employee as a ,public.employee as b,public.employee as c ");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction(",public.employee as d ,public.employee as e,public.employee as f;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("LOADANDCLOSE");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryEditor.CopyEditor().trim();
					if(sFlag1.contains("select a.empid,a.ename,a.salary,b.empid,b.ename,b.salary,")&&sFlag1.contains(",public.employee as d ,public.employee as e,public.employee as f;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to load the lenghty query from SQL Termianl. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_052"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("select * /*文件,文件*/ from auto.auto_largedata;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("LOADANDCLOSE");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryEditor.CopyEditor().trim();
					if(sFlag1.equals("select * /*文件,文件*/ from auto.auto_largedata;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"chinese char query not able to load in the active sql terminal not showing up in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_018"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.OpenQuery("SHORTCUT", "OLD", "SQL_History_Data.sql", "OPEN", "OVERWRITE");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MaxWait);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(50);	
					Thread.sleep(GlobalConstants.ModWait);
					if(sFlag1.contains("50"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"history window is not maintianing 50 SQL queries by default. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_022")) //Testcase covered SDV_FUN_VAL_DS_SQLExec_Hist_040
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("select * from autotable.rowtable;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyExeHistoryQuery(50);
					if(sFlag1.contains("select * from public.viewname;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The old query in history window is not skipped when the max query limit is reached. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_036"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(50);
					Thread.sleep(GlobalConstants.ModWait);
					ExecutionHistoryFunctions.exeHistoryOperations("PIN");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("select * from autotable.zempty;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2", 1).trim();
					if(sFlag1.equals("select * from public.viewname;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Pinned Query is getting deleted.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.exeHistoryOperations("UNPIN");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_064"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.removeConnection();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Focus("Data Studio", "", "Button3");
					BaseActions.Click("Data Studio", "", "Button3");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",2);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					if(!sFlag1.equals("select * from autotable.zempty;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"After removing the connection the query is still showing in the sql history. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_042"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MaxWait);
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					if(sFlag1.equals("select * from autotable.zempty;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"the query details are not retained in history window. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				} 

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_072"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "select * from updatetable;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(2);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2", 1).trim();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					MultipleTerminal.SelectTerminal(1);
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2", 1).trim();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					if(sFlag1.equals("select * from updatetable;")&&sFlag2.equals("select * from updatetable;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Query executed from multiple terminal from same profile is not shown in the History window . Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_066")) //test cases mapped SDV_FUN_VAL_DS_SQLExec_Hist_070,SDV_FUN_VAL_DS_SQLExec_Hist_030
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.newConnection();
					Thread.sleep(GlobalConstants.MinWait);
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					Login.multipleLogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("DELETEALL");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select * from utf82 limit 10;", "INValid");
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.SetFunction(2, "select * from pg_am;"); //select * from auto.auto_largedata;
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(2);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP3",1).trim();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					if(!sFlag1.contains("select * from utf82 limit 10;")&&sFlag1.equals("select * from pg_am;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Check the Query executed in SQL terminal is not shown in the respective profile Execution History window. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					Thread.sleep(GlobalConstants.MinWait);	
					ObjectBrowserPane.objectBrowserRefresh("DOUBLE");
					ObjectBrowserPane.removeConnection();
					Thread.sleep(GlobalConstants.MedWait);
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_069"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.newConnection();
					Thread.sleep(GlobalConstants.MinWait);
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 6, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 6, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 6, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 6, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 6, 5);
					Login.multipleLogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("DELETEALL");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select * from utf82 limit 10;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "select * from pg_amop;"); //select * from auto.auto_largedata;
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(2);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP3",1).trim();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					if(!sFlag1.contains("select * from utf82 limit 10;")&&sFlag1.equals("select * from pg_amop;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"SQL History Window multiple connection to different DB. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_068"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					Thread.sleep(GlobalConstants.MinWait);	
					ObjectBrowserPane.objectBrowserRefresh("DOUBLE");
					ObjectBrowserPane.removeConnection();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.SelectTerminal(2);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = "select * from pg_am;";
					if(sFlag1.equals("select * from pg_am;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"When the connection profile is removed the queries in history window are not removed.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.CloseTerminal(2);
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_SQLExec_Hist_076"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.newConnection();
					Thread.sleep(GlobalConstants.MinWait);
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					Login.multipleLogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(2);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP3", 1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					if(sFlag1.equals("select * from pg_am;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"When the connection profile is connected again the queries in history window are removed.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
				}


			}
		}
		for(int i=1;i<=43;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus = UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}
