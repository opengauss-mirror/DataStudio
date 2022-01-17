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
TITLE - Auto Suggest Feature
DESCRIPTION - THIS PROGRAM COVERS THE BELOW TEST SCRIPTS
 *************************************************************************/


package test_scripts;

import java.awt.event.KeyEvent;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import script_library.DebugOperations;
import script_library.Login;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class DS_002_AUTO_SUGGEST {
	public static void main(String sARNumber) throws Exception{
		//Scripts to Test DS_002_AUTO_SUGGEST - Auto Suggest feature
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Auto_Suggest");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Auto_Suggest");
		//Variable Declarations	
		String sFlag,sFlag1,sFlag2,sStatus,sInputQuery,sQueryType,sTestCaseID,sExecute,sContents;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet
		QueryEditor.SingleQueryExe("DROP DATABASE qdatabase;", "Valid");
		ObjectBrowserPane.AutoSuggestSchemaOpen();
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				
				QueryEditor.SetQuery(sInputQuery);
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_16"))
				{
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					if(sContents.contains("col1 - autoschema123.table1 - Column")&&sContents.contains("col2 - autoschema123.table1 - Column"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Column objects are not displayed under table during auto suggest. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_3"))
				{
					QueryEditor.AutoSuggestInvoke();
					Thread.sleep(GlobalConstants.MedWait);
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.contains("auto_import - Schema")&&sContents.contains("auto_import_table - Schema")&&sContents.contains("auto_largedata - public - Table"))
						sFlag1="Pass";
					else
						sFlag1="Fail";
					
					if(sContents.contains("auto1() - integer - auto - Function"))
						sFlag2="Fail";
					else
						sFlag2="Pass";
					
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Progressive auto suggest is not displayed after CTRL+SPACE operation. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_4"))
				{
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.contains("auto_import - Schema")&&sContents.contains("auto_import_table - Schema")&&sContents.contains("auto - Schema"))
						sFlag1="Fail";
					else
						sFlag1="Pass";
					
					if(sContents.contains("auto_largedata - public - Table"))
						sFlag2="Fail";
					else
						sFlag2="Pass";
					
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest for database object is not working. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_6"))//Limitation Test Case
				{
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.contains("space1 - autoschema123 - Table")&&sContents.contains("space2 - autoschema123 - Table"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest for table with space object is not working. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_7"))
				{
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.contains("col1 - autoschema123.table1 - Column")&&sContents.contains("col2 - autoschema123.table1 - Column"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest for table available in multiple schema is not working. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_8"))
				{
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.contains("col1 - autoschema123.various - Column")&&sContents.contains("col2 - autoschema123.various - Column"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest for table available in multiple schema is not working. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_9"))
				{
					
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.CopyEditor();
					if(sContents.equals("automationschema123.smoke1()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest for object with only one child item failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_10"))
				{
					
					QueryEditor.SetTerminalFocus();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.AutoSuggestInvoke();
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					if(sContents.contains("auto - Schema")&&sContents.contains("auto1() - integer - auto - Function")&&sContents.contains("auto_largedata - public - Table"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest without any object does not show all type of child objects. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_11"))
				{
					
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.contains("pg_database - pg_catalog - Table")&&sContents.contains("master_table - public - Table"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest does not show system and partition tables. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_15"))
				{
					QueryEditor.SetQuery("autoschema123.");
					
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.contains("suggest() - integer - autoschema123 - Function")&&sContents.contains("table1 - autoschema123 - Table"))
						sFlag1="Pass";
					else
						sFlag1="Fail";
						
					QueryEditor.SetQuery("autoschema123.table1.");
					
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
						
					if(sContents.contains("col1 - autoschema123.table1 - Column")&&sContents.contains("col2 - autoschema123.table1 - Column"))
						sFlag2="Pass";
					else
						sFlag2="Fail";
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))						
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Progressive auto suggest for Schema and Table are not working. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_Invalid_1"))
				{
					QueryEditor.SetQuery("select.");
					
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.isEmpty())
						sFlag1="Pass";
					else
						sFlag1="Fail";

					QueryEditor.SetQuery("delete.");
					
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.isEmpty())
						sFlag2="Pass";
					else
						sFlag2="Fail";
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))						
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto Suggest displays values for pre-defined key words. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_Invalid_2"))
				{
					
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.isEmpty())
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto Suggest displays values for non existing object. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_Invalid_3"))
				{
					
					QueryEditor.AutoSuggestInvoke();
					
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.isEmpty())
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto Suggest displays values for function objects. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_17"))
				{
					//Create Schema, Table and Function
					ObjectBrowserPane.CreateSchema("autsugschema");
					ObjectBrowserPane.CreateTable("autsugschema", "autsugtable");
					ObjectBrowserPane.CreateFunctionProcedure("autsugschema", "autsugfunction");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryEditor.SingleQueryExe("DROP TABLE autsugschema.autsugtable;", "Valid");
					sFlag1 = QueryEditor.SingleQueryExe("DROP FUNCTION autsugschema.autsugfunction;", "Valid");
					sFlag2 = QueryEditor.SingleQueryExe("DROP SCHEMA autsugschema;", "Valid");
					
					if(sFlag.equals("Success")&&sFlag1.equals("Success")&&sFlag2.equals("Success"))
					{
						QueryEditor.SetQuery("");
						QueryEditor.AutoSuggestInvoke();
						
						sContents = QueryEditor.AutoSuggestCopy();
						if(sContents.contains("autsugfunction() - integer - autsugschema - Function")&&sContents.contains("autsugtable - autsugschema - Table")&&sContents.contains("autsugschema - Schema"))
							sFlag1="Pass";
						else
							sFlag2="Fail";
						Thread.sleep(GlobalConstants.MedWait);
						ObjectBrowserPane.ObjectBrowserRefresh();
						Thread.sleep(GlobalConstants.MedWait);
						QueryEditor.SetTerminalFocus();
						QueryEditor.AutoSuggestInvoke();
						
						sContents = QueryEditor.AutoSuggestCopy();
						if(sContents.contains("autsugfunction() - integer - autsugschema - Function")&&sContents.contains("autsugtable - autsugschema - Table")&&sContents.contains("autsugschema - Schema"))
							sFlag1="Fail";
						else
							sFlag2="Pass";
						if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Droped objects are not getting displayed before object browser refresh. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Create Schema, Table and PLSQL Function failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_Invalid_4"))
				{
					QueryEditor.ExecuteButton();
					QueryEditor.SetQuery("automationschema123.");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.CopyEditor();
					QueryEditor.CancelQuery("BUTTON");
					if(sContents.equals("automationschema123.smoke1()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest for object with only one child item failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_Invalid_5"))
				{
					String sDebugPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					DebugOperations.DebugObjectBrowser_Open();
					DebugOperations.DebugSession();
					String sDebugConnection = DebugOperations.DebugConnection(sDebugPassword);
					MultipleTerminal.SelectTerminal(2);
					QueryEditor.CloseActiveEditor();
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						Thread.sleep(GlobalConstants.MedWait);
						QueryEditor.SetQuery("automationschema123.");
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						QueryEditor.AutoSuggestInvoke();
						sContents = QueryEditor.CopyEditor();
						Login.DisplayDebugWindows();
						if(sContents.contains("automationschema123.smoke1()"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto suggest for object during debug failed. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug operation is failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_12"))
				{
					QueryEditor.SetQuery("autoschema123.");
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					
					if(sContents.contains("suggest() - integer - autoschema123 - Function")&&sContents.contains("table1 - autoschema123 - Table"))
						sFlag1="Pass";
					else
						sFlag1="Fail";
					
					ObjectBrowserPane.createDBObjectBrowser("qdatabase","","Yes");
					QueryEditor.SelectDBConnection();
					MultipleTerminal.OpenNewTerminal();
					MultipleTerminal.TerminalSetText(2, "autoschema123.");
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					ObjectBrowserPane.disconnectDB(2);
					QueryEditor.SingleQueryExe("DROP DATABASE qdatabase;", "Valid");
					if(sContents.contains("suggest() - integer - autoschema123 - Function")&&sContents.contains("table1 - autoschema123 - Table"))
						sFlag2="Fail";
					else
						sFlag2="Pass";
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto Suggest failed after changing the DB Connection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_21"))
				{
					
					DebugOperations.RemoveConnection();
					QueryEditor.SetTerminalFocus();
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					if(sContents.contains("auto1() - integer - auto - Function"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Auto Suggest failed when removed all connections. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
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