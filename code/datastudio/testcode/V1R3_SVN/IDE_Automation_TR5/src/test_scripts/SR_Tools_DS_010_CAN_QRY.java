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
import object_repository.DebugElements;
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import script_library.DebugOperations;
import script_library.Login;
import script_library.ObjectBrowserPane;
import script_library.PlanCost;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_Tools_DS_010_CAN_QRY {

	public static void main(String sARNumber) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.020.007 - Query Format,Validation check through Menu, Tool bar and Shortcut options
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Cancel_Query");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Cancel_Query");
		//Variable Declarations
		String sFlag,sStatus,sTestCaseID,sInputQuery,sExecute,sFormatType,sExpectedQuery,sExecutionPlan,sValidationMessage;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		String DebugPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
		//Loop to iterate through each Test Case in Test Data Sheet
		sFlag = QueryEditor.SingleQueryExe("DELETE FROM public.updatetable where eid=11;","Valid");
		QueryEditor.ClearEditor();
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2 );
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				sInputQuery = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_SNGLLONGRUN_002")) //SDV_USABLTY_DS_CNLQRY_007,SDV_USABLTY_DS_CNLQRY_009,SDV_USABLTY_DS_CNLQRY_012 is covered
				{
					QueryEditor.SetFunction("select count(*) from largedata");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Cancel the Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_MULTQRY_UPDATE_004"))
				{
					BaseActions.ClearConsole("No");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("update public.updatetable set dept = 'CHN';");
					QueryEditor.ExecuteButton();
					Thread.sleep(28000);
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("update public.updatetable set dept = 'BLR';update public.updatetable set dept = 'CHN';update public.updatetable set dept = 'MUM';");
					Thread.sleep(10000);
					QueryEditor.ExecuteButton();
					Thread.sleep(29000);
					QueryEditor.CancelQuery("BUTTON");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select count (*) from public.updatetable where dept ='BLR'");
					QueryEditor.ExecuteButton();
					Thread.sleep(20000);
					sFlag = QueryResult.CopyContent();
					if(sFlag.contains("2758656"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Records are not updated. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_MULTIPLECS_005")) //SDV_FUNVAL_DS_CNLQRY_UPD_DEL_006,SDV_FIA_DS_CNLQRY_004 This testcase is covered
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetQuery("Select count (*) from largedata;");
					QueryEditor.SetFunction("Insert into updatetable(select * from public.updatetable where eid = 23);");
					QueryEditor.SetFunction("delete from updatetable;");
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("BUTTON");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select count (*) from public.updatetable;");
					QueryEditor.ExecuteButton();
					Thread.sleep(20000);
					sFlag = QueryResult.CopyContent();
					if(sFlag.contains("2758656"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Records are getting updated. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);						
					}
				}


				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_MULTQRY_INSERT_007"))//testcase covered SDV_FIA_DS_CNLQRY_005
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("insert into public.updatetable values (11,'a2','PUNE');insert into public.updatetable values (12,'a3','PUNE');");
					QueryEditor.SetFunction("insert into public.updatetable values (13,'a4','PUNE');insert into public.updatetable values (14,'a5','PUNE');");
					QueryEditor.SetFunction("insert into public.updatetable values (15,'a6','PUNE');insert into public.updatetable values (16,'a7','PUNE');");
					Thread.sleep(GlobalConstants.MaxWait);
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("BUTTON");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select * from public.updatetable where dept ='PUNE'");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"SDV_FUNVAL_DS_CNLQRY_MULTQRY_INSERT_007.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"SDV_FUNVAL_DS_CNLQRY_MULTQRY_INSERT_007.csv");
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"SDV_FUNVAL_DS_CNLQRY_MULTQRY_INSERT_007.csv");
						if(RecordCount >= 1)
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							QueryEditor.ClearEditor();
							QueryEditor.SetFunction("delete from public.updatetable where dept ='PUNE'");
							Thread.sleep(GlobalConstants.MinWait);
							QueryEditor.ExecuteButton();
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Exported file is empty eventhough the query result has values. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Exported file is not saved in the desired location. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}		

				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_INSERT_RLBK_008"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("Insert into public.updatetable(select * from public.updatetable where eid = 21);");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						QueryEditor.ClearEditor();
						QueryEditor.SetFunction("select count (*) from public.updatetable;");
						QueryEditor.ExecuteButton();
						Thread.sleep(20000);
						QueryResult.ResultWindow();
						sFlag = QueryResult.CopyContent();
						if(sFlag.contains("2758656"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Record count is not matching. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Records are getting updated in the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_MULTQRY_010"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("update public.updatetable set dept = 'BLR';");
					QueryEditor.SetFunction("Insert into updatetable(select * from public.updatetable where eid = 23);");
					QueryEditor.SetFunction("delete from updatetable;");
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						QueryEditor.ClearEditor();
						QueryEditor.SetFunction("select count (*) from public.updatetable;");
						Thread.sleep(GlobalConstants.MedWait);
						QueryEditor.ExecuteButton();
						Thread.sleep(20000);
						QueryResult.ResultWindow();
						sFlag = QueryResult.CopyContent();
						if(sFlag.contains("2758656"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Record count is not matching. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Records are getting updated in the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_FETCHING_013"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select * from public.largedata;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.ExeNextRecord();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.CancelQuery("SHORTCUT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Cancel Button is Enabled for next record button Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_VIEWS_015"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select count(*)  from viewname;");
					QueryEditor.SetFunction("select count(*)  from viewname;");
					QueryEditor.SetFunction("select count(*)  from viewname;");
					QueryEditor.SetFunction("select count(*)  from viewname;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Records are getting updated. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);						
					}
				}
				if(sTestCaseID.equals("SDV_FUNINVAL_DS_CNLQRY_STATUSBAR_002"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					QueryEditor.SetFunction("--select count (*) from public.updatetable;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("SHORTCUT");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Commented queries are executing. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);						
					}
				}
				if(sTestCaseID.equals("SDV_USABLTY_DS_CNLQRY_005")||sTestCaseID.equals("SDV_USABLTY_DS_CNLQRY_006"))
				{
					switch(sTestCaseID)
					{
					case "SDV_USABLTY_DS_CNLQRY_005":
						QueryEditor.ClearEditor();
						BaseActions.ClearConsole("Normal");
						QueryEditor.SetFunction("select count (*) from largedata;");
						QueryEditor.ExecuteButton();
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.CancelQuery("SHORTCUT");
						break;
					case "SDV_USABLTY_DS_CNLQRY_006":
						QueryEditor.ClearEditor();
						BaseActions.ClearConsole("Normal");
						QueryEditor.SetFunction("select count (*) from largedata;");
						QueryEditor.ExecuteButton();
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.CancelQuery("CONTEXTMENU");
						break;
					default:	
						break;
					}
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Cancel Option is not avaliable. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);						
					}
				}
				if(sTestCaseID.equals("SDV_USABLTY_DS_CNLQRY_01"))
				{
					QueryEditor.ClearEditor();
					BaseActions.ClearConsole("Normal");
					QueryEditor.SetFunction("select count (*) from largedata;");
					QueryEditor.SetFunction("select count (*) from largedata;");
					QueryEditor.SetFunction("select count (*) from largedata;");
					QueryEditor.SetFunction("select count (*) from largedata;");
					QueryEditor.SetFunction("select count (*) from largedata;");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("SHORTCUT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Cancel Button is not enabled. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);						
					}
				}
				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_007"))
				{
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("BUTTON");
					QueryEditor.SaveQuery("SHORTCUT","SDV_FIA_DS_CNLQRY_007.sql", "OVERWRITE");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.") || sFlag.contains("[INFO] SQL successfully saved to"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to save the sql. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);						
					}
				}

				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_023"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						if(sFlag.contains("[INFO] Execution Time"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Execution Time is not displayed for sucessfully executed queries. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Cancel Query Failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_023_673"))
				{
					sExecutionPlan=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
					sFormatType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,5);
					sValidationMessage=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,7);
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						PlanCost.PlanCostClick();
						PlanCost.ExecutionPlan(sFormatType,sExecutionPlan);
						Thread.sleep(GlobalConstants.MedWait);
						if(BaseActions.WinExists("Execution Plan For Multiple Queries"))
						{	
							UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);		
							sFlag=PlanCost.PlanCostValidation(sFormatType, sValidationMessage);
							if(sFlag.equals("Success"))
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");							
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error message is not displayed to user while executing multiple queries with Execution Plan options selected. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error window is not displayed to user while executing multiple queries with Execution Plan options selected. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Cancel Query Failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_022"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
						String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
						String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
						String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
						String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
						BaseActions.ClearConsole("Basic");
						UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
						Thread.sleep(GlobalConstants.MinWait);
						Login.IDELogin("New Connection", sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
						Thread.sleep(GlobalConstants.MinWait);
						sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("connected successfully"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Connected successfully to new connection after Cancel Query. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Cancel Query Failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_021"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						ObjectBrowserPane.ObjectBrowser();
						ObjectBrowserPane.BrowserExport();
						Thread.sleep(GlobalConstants.MedWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
						Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"BrowserExportResult.csv");
						BaseActions.Winwait("Data Exported Successfully");
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						Thread.sleep(GlobalConstants.MinWait);
						String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sConsoleOutput.contains("successfully exported"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Browser Export failed after Query Cancelation. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Query Cancelled Message is not displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}	

				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_020"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						QueryResult.CurrentExport();

						File file = new File(GlobalConstants.sCsvExportPath+"multiplequery.csv");
						if(file.exists())
							file.delete();
						Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"multiplequery.csv");
						if(file.exists())
						{
							int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"multiplequery.csv");
							if(RecordCount >= 127)
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Result set does not have the details of the last successfully executed query. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Result set does not have the details of the last successfully executed query. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Query Cancelled Message is not displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_019"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "Cancel_Query.sql", "OPEN", "OVERWRITE");
					Thread.sleep(GlobalConstants.MedWait);
					//Format the Query
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);

					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MaxWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed")){
						QueryResult.CurrentExport();
						File file = new File(GlobalConstants.sCsvExportPath+"multiplequery.csv");
						if(file.exists())
							file.delete();
						Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"multiplequery.csv");
						if(file.exists())
						{
							int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"multiplequery.csv");
							if(RecordCount >= 127)
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Result set does not have the details of the last successfully executed query. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Result set does not have the details of the last successfully executed query. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Query Cancelled Message is not displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_018"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						//Create and Drop PLSQL Functions
						ObjectBrowserPane.CreateFunctionProcedure("public", "cancelauto");
						Thread.sleep(1000);
						sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
						if(sFlag.contains("[INFO] Executed Successfully..."))
						{
							sFlag = QueryEditor.SingleQueryExe("select public.cancelauto();","Valid");
							if(sFlag.equals("Success"))
							{
								sFlag = QueryEditor.SingleQueryExe("drop function public.cancelauto();","Valid");
								if(sFlag.equals("Success"))
								{
									sFlag = QueryEditor.SingleQueryExe("select public.cancelauto();","Invalid");
									if(sFlag.contains("ERROR: function public.cancelauto() does not exist"))
									{
										UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
									}
									else
									{
										UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
										UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Function Drop Failed after Cancel Query. Please refer screenshot "+sTestCaseID+".jpg");
										UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
									}
								}
								else
								{
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Function Drop Failed after Cancel Query. Please refer screenshot "+sTestCaseID+".jpg");
									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								}
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Function add failed after Cancel Query. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Query Cancelled Message is not displayed. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Query Cancelled Message is not displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				/*************************************************************************
				Test Cases Covered
				1. SDV_FIA_DS_CNLQRY_010
				2. SDV_FIA_DS_CNLQRY_012
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_012"))
				{
					//Getting Login Credentials from IDE_Smoke_Test_Data file and user logs into IDE Tool
					String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login into IDE Tool
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed")){
						DebugOperations.DebugObjectBrowser_Open();
						DebugOperations.SetBreakPoint(10);
						DebugOperations.DebugSession();
						String sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
						if(sDebugConnection.equals("DebugConnectionSuccessful"))
						{
							DebugOperations.ClickContinue();
							Thread.sleep(GlobalConstants.MedWait);
							sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
							if(sFlag.contains("Debugging completed.") && sFlag.contains("Executed Successfully.."))
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error occur while doing DebugOperation. Refer screenshot:"+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Query Cancelled Message is not displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_011"))
				{
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					Login.DebugWindows();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugSession();
					String sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
						QueryEditor.CancelQuery("SHORTCUT");
						Thread.sleep(GlobalConstants.MedWait);
						Login.DebugWindows();
						Thread.sleep(GlobalConstants.MedWait);
						sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
						DebugOperations.TerminateDebugging();
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
						Thread.sleep(GlobalConstants.MinWait);
						if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
						{
							Login.DebugWindows();
							Thread.sleep(GlobalConstants.MinWait);
							sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
							if(sFlag.contains("[INFO] Debug Terminated."))
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug is not terminated after Cancelling the Query. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Query is not Canceled while Debug is in progress. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug failed while Query Cancel is in progress. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}	

				if(sTestCaseID.equals("SDV_FUNINVAL_DS_CNLQRY_PLSQL_001"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(DebugElements.wDebugpane,"",DebugElements.wDebugFunction,DebugElements.sMouseButton,DebugElements.iButtonClick,125,11);
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugSession();
					String sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
						QueryEditor.CancelQuery("SHORTCUT");
						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.MouseClick(DebugElements.wDebugpane,"",DebugElements.wDebugFunction,DebugElements.sMouseButton,DebugElements.iButtonClick,125,11);
						Thread.sleep(GlobalConstants.MedWait);
						DebugOperations.TerminateDebugging();
						sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Cancel Button is enabled while debugging a plsql function. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}	
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_FIA_DS_CNLQRY_008"))
				{
					BaseActions.ClearConsole("Normal");
					QueryEditor.ClearEditor();
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "pg_am.sql", "OPEN", "OVERWRITE");
						sExpectedQuery = QueryEditor.CopyEditor();
						if(sExpectedQuery.equals("select * from pg_am;"))
						{
							if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully loaded to SQL Terminal."))
							{
								UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Passed");
							} else
							{
								UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
								UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("Success message is not displayed in Console. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("Error occured while opening query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("Cancel Query Failed. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
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

}
