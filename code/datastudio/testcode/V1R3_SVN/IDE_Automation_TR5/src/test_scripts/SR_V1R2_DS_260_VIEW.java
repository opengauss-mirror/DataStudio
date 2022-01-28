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
import object_repository.ObjectBrowserElements;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.ViewFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_260_VIEW {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_260_VIEW");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_260_VIEW");
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
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_02"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SetFunction("select * from sys.all_views;");
					QueryEditor.SetFunction("select * from pg_catalog.all_views;");
					QueryEditor.SetFunction("select * from information_schema.attributes;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag1.contains("Executed Successfully...")&&(!sFlag1.contains("Execution failed.")))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"view are not available. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_06")) //Test case mapped PTS_TOR.260.001_Functional_valid_15
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
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
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create the view from same schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_07"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "create view auto.diffview as select * from public.utf82;");
					Thread.sleep(GlobalConstants.MinWait);
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create the view from different schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
					Thread.sleep(GlobalConstants.MinWait);
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_08"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.view1 as select * from auto.diffview;");
					Thread.sleep(GlobalConstants.MinWait);
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create the view from diff view in same schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.view1;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_09"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.view2 AS select * from pg_catalog.all_views;");
					Thread.sleep(GlobalConstants.MinWait);
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create the view from different view in different schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.view2;", "InValid");
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_11"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.sameview1 as select * from auto.sameview;");
					Thread.sleep(GlobalConstants.MinWait);
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create the view from different view in different schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.sameview1;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_10")) //Test case covered PTS_TOR.260.001_Functional_valid_21,PTS_TOR.260.001_Functional_valid_33,PTS_TOR.260.001_Functional_valid_17
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.multiview");
					MultipleTerminal.SetFunction(2, "AS SELECT a.ename,b.dept");
					MultipleTerminal.SetFunction(2, "FROM auto.auto_largedata a , public.auto_largedata b");
					MultipleTerminal.SetFunction(2, "WHERE a.eid = b.eid;");
					Thread.sleep(GlobalConstants.MedWait);
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Create a view from another table, view from the multiple schema.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.multiview;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_12")) //creating temp view
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE TEMP VIEW tview AS select * from auto.sameview;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Temp view can be created.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2,"DROP VIEW tview;");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.Temp_Schema_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					ViewFunctions.Temp_Schema_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
				}
				/**
				 * Create a view from system view. Check it is successfully created
				 */
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_13")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.sysview AS select * from pg_catalog.all_views;");
					Thread.sleep(GlobalConstants.MedWait);
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Create a view from system view.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.sysview;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_14"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

					QueryEditor.SingleQueryExe("create temp table temptable(col1 int,col2 int);", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					QueryEditor.SingleQueryExe("CREATE VIEW tempview AS select * from temptable;","InValid");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag1 = QueryResult.ReadConsoleOutput("TERMINAL").replace("\"", "");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag1.contains(" [NOTICE] view tempview will be a temporary view"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Create a view for temp table.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("drop view tempview;");
					QueryEditor.SetFunction("drop table temptable;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.Temp_Schema_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					ViewFunctions.Temp_Schema_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
				}

				/**
				 * Create a view without the table.
				 */
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_16"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryEditor.SingleQueryExe("CREATE VIEW auto.notableview AS SELECT 'Hello World';","Valid");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag2 = QueryResult.ReadConsoleOutput("TERMINAL").replace("\"", "");
					if(sFlag1.equals("Success")&&sFlag2.contains("[NOTICE] column ?column? has type unknown"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Create a view without table.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.notableview;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_18")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.subsetview AS select eid,dept from auto.auto_largedata;");
					Thread.sleep(GlobalConstants.MedWait);
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Create a view for subset of columns.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.subsetview;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_19")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto."+'"'+"@SChar*(view)"+'"'+" AS SELECT * FROM auto.auto_largedata;");
					Thread.sleep(GlobalConstants.MedWait);    
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Create a view with special characters.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto."+'"'+"@SChar*(view)"+'"'+";", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_23")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.chineseview /*'文件','文件*/ AS SELECT * FROM auto.auto_largedata;/*'文件','文件*/;");
					Thread.sleep(GlobalConstants.MedWait);    
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Create a view with comments in the view.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.chineseview;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_24"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE VIEW auto.replaceview AS SELECT eid from auto.auto_largedata;");
					Thread.sleep(GlobalConstants.MedWait);    
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalConsoleClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SelectTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "CREATE OR REPLACE VIEW auto.replaceview AS SELECT * from auto.auto_largedata;");
					Thread.sleep(GlobalConstants.MedWait);    
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to update the view.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop view auto.replaceview;", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.view_Refresh();
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_27")) //Testcase Mapped PTS_TOR.260.001_Functional_valid_29
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("Drop view auto.diffview;", "InValid"); //only one view exists
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.Auto_Table_View_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.renameViewObjectBrowser("updatedview");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("[INFO] View auto.sameview renamed successfully"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the view.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_41")) //Show DDL after the renaming the view
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.showDDL();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("CREATE OR REPLACE VIEW auto.updatedview"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Show DDL is not updated with the new name;.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_28"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					sFlag1 = QueryEditor.SingleQueryExe("ALTER VIEW auto.updatedview RENAME TO "+'"'+"s8P*cial(vi&ew)"+'"'+";","Valid");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag1.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the view to special characters through SQL Terminal;.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_30"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.view_Refresh();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.renameViewObjectBrowser("viewname");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("[INFO] View auto.s8P*cial(vi&ew) renamed successfully"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the view with same name in different schema;.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_31")) //testcase covered PTS_TOR.260.001_Functional_valid_32
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.setSchema("auto_import");
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("[INFO] Moved auto.viewname view to auto_import schema.")) // need to check on the message after the defect Fix
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to move the view to different schema;.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_42"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Import_View_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.showDDL();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("CREATE OR REPLACE VIEW auto_import.viewname"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The show DDL is not displaying the updated schema name.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.Auto_Import_View_Navigation();				
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.setSchema("auto");
					Thread.sleep(GlobalConstants.MinWait);
				}


				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_37")) //Test case covered PTS_TOR.260.001_Functional_valid_38
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.showDDL();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("CREATE OR REPLACE VIEW auto.viewname"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The view DDL is different and not editable;.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_39"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.showDDL();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("CREATE OR REPLACE VIEW auto.viewname"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The view DDL for same view is not opening in differernt window;.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(3);
					Thread.sleep(GlobalConstants.MinWait);	
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_40"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Public_Table_View_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.showDDL();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("CREATE OR REPLACE VIEW public.viewname"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Show DDL for same view name is not opening in new window.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(3);
					Thread.sleep(GlobalConstants.MinWait);	
					MultipleTerminal.CloseTerminal(2);
				}

				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_43"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.pg_catalog_View_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.showDDL();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("CREATE OR REPLACE VIEW pg_catalog.all_all_tables"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Show DDL is not working for system views.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);	
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_34")) //Test case covered PTS_TOR.260.001_Functional_valid_35
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("CREATE VIEW auto.sameschema as select * from auto.viewname;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);	
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag1 = QueryEditor.SingleQueryExe("DROP VIEW auto.viewname cascade;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag2 = QueryEditor.SingleQueryExe("select * from auto.sameschema;", "Valid").replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.equals("Success")&&sFlag2.contains("relation auto.sameschema does not exist"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The View is dropping.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_36")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("CREATE VIEW auto.diffschema as select * from public.auto_largedata;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);	
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					QueryEditor.SingleQueryExe("CREATE VIEW public.diffschema as select * from auto.diffschema;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag1 = QueryEditor.SingleQueryExe("DROP VIEW auto.diffschema cascade;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag2 = QueryEditor.SingleQueryExe("select * from auto.diffschema;", "Valid").replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.equals("Success")&&sFlag2.contains("relation auto.diffschema does not exist"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The View is dropping.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_44"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Auto_Table_View_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.createViewObjectBrowser();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.TerminalEditorClear(2);
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.SetFunction(2, "CREATE OR REPLACE FUNCTION auto.auto2()");
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.SetFunction(2, "RETURNS integer LANGUAGE plpgsql");
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "AS $$ DECLARE");
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "m int;BEGIN m := 5;");
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2, "return m;end $$");
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SelectTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
					if(sFlag1.contains("[INFO] Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"unable to create PLSQL Function.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					QueryEditor.SingleQueryExe("drop function auto.auto2();", "Valid");
					Thread.sleep(GlobalConstants.MinWait);

				}
				if(sTestCaseID.equals("PTS_TOR.260.001_Functional_valid_22"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ViewFunctions.Public_Table_View_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					ViewFunctions.showDDL();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.removeConnection();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.SelectTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					String sFlag = UtilityFunctions.GetClipBoard();
					if(sFlag.contains("CREATE OR REPLACE VIEW public.viewname"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Show DDL windoe is closed.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
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

