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

import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class PTS_TOR_080_001_CAN_EXP {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Cancel_Export");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Cancel_Export");
		//Variable Declarations	
		String sInputQuery,sTestCaseID,sQueryType,sExecute,sFlag = null,sFlag1 = null,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
			sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
			if(sExecute.equals("Yes"))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);

				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_2")) //Testcase mapped PTS_TOR.080.001_Functional_valid_3,PTS_TOR.080.001_Usability Test_1,PTS_TOR.080.001_Usability Test_2
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					Thread.sleep(GlobalConstants.MinWait);
					QueryResult.ExportButton();
					Thread.sleep(GlobalConstants.MinWait);
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_2.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_2.csv");
					QueryEditor.CancelImportExport("Yes");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled Data Export on user request."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is not able to cancel. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("TERMINAL");
				}
				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_10")) //test case covered PTS_TOR.080.001_Load/Stress Test
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.Auto_Table_Navigation();
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"PS_TOR.080.001_Functional_valid_4.csv", "Open");
					QueryEditor.CancelImportExport("Yes");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.contains("[INFO] Canceled Data Import on user request."))//&&sFlag1.contains("5517312"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5," inserted records are not rolled back. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_5")) //Testcase Covered PTS_TOR.080.001_Functional_valid_13
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					Thread.sleep(GlobalConstants.MinWait);
					QueryResult.ExportButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_5.csv");
					QueryEditor.CancelImportExport("Yes");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_5.csv");
					if(file.exists()&&sFlag.contains("[INFO] Canceled Data Export on user request."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"CSV file created even though the export job is canceled . Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					BaseActions.ClearConsole("TERMINAL");
				}

				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_7")) //test case covered PTS_TOR.080.001_Response Time_3
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select * from updatetable");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.TruncateTable(145, 100);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio","", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"PTS_TOR.080.001_Functional_valid_7.csv", "Open");
					QueryResult.ExportButton();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_7.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_7.csv");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL").replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag1.contains("Data successfully imported to the table auto.auto_largedata")&&sFlag.contains("Data successfully exported"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to export/import from different tables. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("TERMINAL");
				}

				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_8")) // Testcase mapped PTS_TOR.080.001_Response Time_1
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");       
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.Auto_Table_Navigation();
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_8.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_8.csv");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_8.csv");
						if(RecordCount-1 == 5517312&&sFlag.contains("5,517,312"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
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
					BaseActions.ClearConsole("TERMINAL");
				}

				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_9"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_9.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_9.csv");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.reIndex();
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("[INFO] The table auto.auto_largedata has been successfully reindexed."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5," unable to reindex the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_11"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_11.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_11.csv");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.analyze();
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("[INFO] The table auto.auto_largedata has been successfully analyzed."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to reanalyze the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_12"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_12.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_12.csv");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					QueryEditor.SetFunction("select * from public.amop;");
					QueryEditor.SetFunction("select * from pg_am;");
					QueryEditor.ExecuteButton();
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);

					Thread.sleep(GlobalConstants.MinWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("data successfully exported.")|| sFlag.contains("[INFO] Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to run mutiple query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_Invalid_1"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"BrowserExport.csv");
					if(file.exists())
						file.delete();
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"BrowserExport.csv");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.contains("data successfully exported."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to export simultaneously. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_Invalid_2"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_Invalid_1.csv");
					if(file.exists())
						file.delete();
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_Invalid_1.csv");
					BaseActions.MouseClick("Data Studio","", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.DropTableObjectBrowser();
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");

					if(sFlag.contains("Drop Table auto.auto_largedata is successful."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to drop the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_Invalid_3")) //Testcase covered PTS_TOR.080.001_Response Time_2
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("CREATE TABLE auto.auto_largedata(eid bigint,ename varchar,dept varchar);");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"PTS_TOR.080.001_Functional_Invalid_3.csv", "Open");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio","", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.DropTableObjectBrowser();
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("[INFO] Drop Table auto.auto_largedata is successful."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to drop the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");

				}
				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_Invalid_4"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("CREATE TABLE auto.auto_largedata(eid bigint,ename varchar,dept varchar);");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.Auto_Table_Navigation();
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"PTS_TOR.080.001_Functional_Invalid_3.csv", "Open");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio","", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_Invalid_4.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_Invalid_4.csv");
					QueryEditor.CancelImportExport("Yes");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick("Data Studio","", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.DropTableObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("[INFO] Canceled Data Export on user request.")&&sFlag.contains("Drop Table auto.auto_largedata is successful."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to drop the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_Invalid_5")) //Testcase Covered PTS_TOR.080.001_SFT (System Failure Testing)_1
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("CREATE TABLE auto.auto_largedata(eid bigint,ename varchar,dept varchar);");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.ModWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.Auto_Table_Navigation();
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"PTS_TOR.080.001_Functional_Invalid_3.csv", "Open");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio","", "SysTreeView321", "left", 1, 145, 100);
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_Invalid_4.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_Invalid_4.csv");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
							85, 27);
					ObjectBrowserPane.disconnectDB();
					BaseActions.Winwait("Data Export failed");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Error while exporting table data..")&&sFlag.contains("Error :This connection has been closed."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to export when connection is removed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}
			}
		}
		for(int i=1;i<=14;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}


