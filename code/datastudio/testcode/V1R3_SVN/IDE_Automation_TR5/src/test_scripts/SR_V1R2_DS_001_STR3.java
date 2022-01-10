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
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_001_STR3 {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Import_Table");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Import_table");
		//Variable Declarations	
		String sInputQuery,sStatus,sTestCaseID,sExecute,sFlag,sQueryType;
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
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_valid_7"))
				{
					BaseActions.ClearConsole("Valid");
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.ObjectBrowserRefresh();
					DebugOperations.ObjectbrowserImport();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"SR.V1R2.DS.001_STR_3_PartianData.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Total rows imported: 44"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to import the csv file to the partition table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("DROP table auto_import.measurement;DROP table auto_import.measurement1;", sQueryType);
					ObjectBrowserPane.TableRefresh();
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_valid_9"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"hash_table1.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Total rows imported: 29"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Import the csv file to the different data distribution. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("drop table auto_import.hashtable;drop table auto_import.hashtable1;", sQueryType);
					ObjectBrowserPane.TableRefresh();
				}
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_1"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"primarytable.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("ERROR: duplicate key value violates unique constraint \"col1_pk\"")) 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to import the csv data when the primary key constraints violate. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_2"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					sFlag = QueryResult.ReadConsoleOutput("LOCAL");//CHanged as per the new error message
					if(sFlag.contains(" Error Code:[0]ERROR: FOREIGN KEY ... REFERENCES constraint is not yet supported.")) 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to import the csv data when the foriegn key constraints violate. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("drop table auto_import.foriegnTable;drop table auto_import.primaryTable;", sQueryType);
					ObjectBrowserPane.TableRefresh();
				}

				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_3"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"uniquetable.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("ERROR: duplicate key value violates unique constraint \"col1_uq\"")) 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to Import the csv data when the unique key constraints violate. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("DROP TABLE auto_import.uniquetable", sQueryType);
					ObjectBrowserPane.TableRefresh();
				}
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_4"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"checktable.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("ERROR: new row for relation \"checktable\" violates check constraint \"ck\"")) 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to Import the csv data when the check constraints violate. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("DROP TABLE auto_import.checktable", sQueryType);
					ObjectBrowserPane.TableRefresh();
				}
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_5"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"notnulltable.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("ERROR: null value in column \"col1\" violates not-null constraint")) 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to Import the csv data when the NotNull constraints violate. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("DROP TABLE auto_import.notnulltable", sQueryType);
					ObjectBrowserPane.TableRefresh();
				}
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_6"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"defaulttable.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Data succesfully imported to the table 'defaulttable'.")&&sFlag.contains("[INFO] Total rows imported: 30")) 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"UnAble to Import the csv data when the default constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("DROP TABLE auto_import.defaulttable", sQueryType);
					ObjectBrowserPane.TableRefresh();
				}
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_8"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"columnmismatch.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("ERROR: invalid input syntax for integer: \"A100\"")&&sFlag.contains("Where: COPY columnmismatch, line 2, column col1: \"A100\"")) 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to Import when csv file and table column mismatch. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("DROP TABLE auto_import.columnmismatch", sQueryType);
					ObjectBrowserPane.TableRefresh();
				}
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_9"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImport(GlobalConstants.sCsvImportPath+"datatypemismatch.csv", "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("ERROR: invalid input syntax for integer: \"varchar\"")&&sFlag.contains("Where: COPY datatypemismatch, line 2, column col2: \"varchar\"")) 
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to Import when csv file and table column have data type mismatch. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("DROP TABLE auto_import.datatypemismatch", sQueryType);
					ObjectBrowserPane.TableRefresh();
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


