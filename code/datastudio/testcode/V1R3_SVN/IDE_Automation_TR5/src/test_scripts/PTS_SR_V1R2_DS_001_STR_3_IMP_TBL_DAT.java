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
import java.io.FileWriter;
import java.io.Writer;

import object_repository.ExpQueryElements;
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import script_library.DebugOperations;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class PTS_SR_V1R2_DS_001_STR_3_IMP_TBL_DAT {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Import_Table_Data");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Import_Table_Data");
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
					BaseActions.ClearConsole("GLOBAL");
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.ObjectBrowserRefresh();
					DebugOperations.ObjectbrowserImport();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"SR.V1R2.DS.001_STR_3_PartianData.csv", "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
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
					Thread.sleep(GlobalConstants.MedWait);
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
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"hash_table1.csv", "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
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
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("drop table auto_import.hashtable;drop table auto_import.hashtable1;", sQueryType);
					ObjectBrowserPane.TableRefresh();
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_1"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"primarytable.csv", "OPEN");
					BaseActions.Winwait("Data Import failed");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("ERROR: duplicate key value violates unique constraint col1_pk")) 
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

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_2"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					/** 
					 * Test cases modified as per the changed error message 
					 */
					sFlag = QueryResult.ReadConsoleOutput("LOCAL").replace("\"", "");
					if(sFlag.contains("[ERROR] Execution failed.")) 
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
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_3"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"uniquetable.csv", "OPEN");
					BaseActions.Winwait("Data Import failed");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("ERROR: duplicate key value violates unique constraint col1_uq")) 
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
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_4"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"checktable.csv", "OPEN");
					BaseActions.Winwait("Data Import failed");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("ERROR: new row for relation checktable violates check constraint ck")) 
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
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_5"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"notnulltable.csv", "OPEN");
					BaseActions.Winwait("Data Import failed");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("ERROR: null value in column col1 violates not-null constraint")) 
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
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_6"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"defaulttable.csv", "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Data successfully imported to the table auto_import.defaulttable")&&sFlag.contains("[INFO] Total rows imported: 30")) 
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
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_8"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"columnmismatch.csv", "OPEN");
					BaseActions.Winwait("Data Import failed");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("ERROR: invalid input syntax for integer: A100")&&sFlag.contains("Where: COPY columnmismatch, line 2, column col1: A100")) 
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
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_9"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					ObjectBrowserPane.TableRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"datatypemismatch.csv", "OPEN");
					BaseActions.Winwait("Data Import failed");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("ERROR: invalid input syntax for integer: varchar")&&sFlag.contains("Where: COPY datatypemismatch, line 2, column col2: varchar")) 
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
					Thread.sleep(GlobalConstants.MinWait);

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_valid_1PTS_SR.V1R2.DS.001_STR_3_Functional_valid_2"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					ObjectBrowserPane.DropTable("auto_import_table","employee");
					ObjectBrowserPane.CreateTable("auto_import_table","employee");
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.Auto_Import_Table_Navigation();
					sFlag = ObjectBrowserPane.TruncateTable(143,135);
					if(sFlag.equals("Success"))
					{
						BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
						ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
						BaseActions.Winwait("Data Imported Successfully");
						Thread.sleep(GlobalConstants.MedWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Data successfully imported to the table auto_import_table.employee")&&sFlag.contains("Total rows imported: 5"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
							BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
							ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
							Thread.sleep(GlobalConstants.MedWait);
							BaseActions.Winwait("Data Imported Successfully");
							Thread.sleep(GlobalConstants.MedWait);
							UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
							sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
							if(sFlag.contains("Data successfully imported to the table auto_import_table.employee")&&sFlag.contains("Total rows imported: 5"))
							{
								sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee;", "Table");
								if(sFlag.contains("Records fetched: 10"))
								{
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
								}
								else
								{
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"View table data failed after importing for a table with data. Please refer screenshot "+sTestCaseID+".jpg");
									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								}
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Table Data Import failed for a table with data. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}	
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table Data Import Failed for an empty table. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Table Data Import failed for a table with data. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
				}
				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_valid_3				
				 *************************************************************************/
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_valid_3"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");

					String sFileName = GlobalConstants.sCsvImportPath+"emp_special.csv";
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
					ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Data successfully imported to the table auto_import_table.employee")&&sFlag.contains("Total rows imported: 4"))
					{
						sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee where ename='Dave,Steve*&^&*^';", "Table");
						if(sFlag.contains("Records fetched: 1"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"View table data failed after importing file with comma and special characters. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Table Data Import Failed for a file with comma and special characters. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}	

				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_valid_5				
				 *************************************************************************/
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_valid_5"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sSourceFile = GlobalConstants.sCsvImportPath+"employee.csv";
					String sDestinationFile = GlobalConstants.sCsvImportPath+"emp_append.csv";
					UtilityFunctions.CopyFile(sSourceFile, sDestinationFile);
					Writer output;
					output = new FileWriter(sDestinationFile,true);
					output.append("\"6\",\"M\",\"DaveTest\",\"25000\",\"10\"");
					output.close();
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
					ObjectBrowserPane.TableImportWithoutWait(sDestinationFile, "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Data successfully imported to the table auto_import_table.employee")&&sFlag.contains("Total rows imported: 6"))
					{
						sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee where ename='DaveTest';", "Table");
						
						if(sFlag.contains("Records fetched: 1"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"View table data failed after importing the manually appended file. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Table Data Import Failed for a file after appending value smanually. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}	

				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_valid_6				
				 *************************************************************************/
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_valid_6"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					ObjectBrowserPane.TruncateTable(143,153);
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
					ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Data successfully imported to the table auto_import_table.employee")&&sFlag.contains("Total rows imported: 5"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Table Data Import Failed after table truncate. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}

				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_valid_10			
				 *************************************************************************/
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_valid_10"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"emp_chinese.csv";
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
					ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Data successfully imported to the table auto_import_table.employee")&&sFlag.contains("Total rows imported: 5"))
					{
						sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee where ename='大卫';", "Table");
						if(sFlag.contains("Records fetched: 5"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"View table data failed for imported chinese content. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Table Data Import Failed for file with chinese data. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}

				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_7			
				 *************************************************************************/
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_7"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"emp_txt.txt";
					ObjectBrowserPane.TruncateTable(143,153);
					BaseActions.ClearConsole("Query");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
					ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
					Thread.sleep(GlobalConstants.MinWait);
					if(BaseActions.WinExists(ExpQueryElements.wImportTableDataWindow))
					{
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.Click(ExpQueryElements.wImportTableDataWindow,"",ExpQueryElements.bImportTableOK);
						Thread.sleep(GlobalConstants.MinWait);
						sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Data successfully imported to the table auto_import_table.employee")&&sFlag.contains("Total rows imported: 5"))
						{
							sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee where ename='Dave';", "Table");
							if(sFlag.contains("Records fetched: 5"))
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Data Imported from Text file. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Data successfully imported message is displayed for an import from text file. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}	
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"No warning message while importing from text file. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}

				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_10				
				 *************************************************************************/
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_10"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"emp_data_length.csv";
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
					ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
					BaseActions.Winwait("Data Import failed");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Error while importing table data")&&sFlag.contains("Error :ERROR: value too long for type character(1)"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Error message not displayed while Importing when the table has less data length than the csv file. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}

				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_13			
				 *************************************************************************/
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_13"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"emp_junk.csv";
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
					ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
					BaseActions.Winwait("Data Import failed");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Error while importing table data.")&&sFlag.contains("Error :ERROR: invalid input syntax"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Error message not displayed while importing csv file with junk values. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_12
				 *************************************************************************/

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_12"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");

					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					sFlag="Success";
					if(sFlag.equals("Success"))
					{
						BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,136,152);
						ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
						BaseActions.Winwait("Data Import failed");
						Thread.sleep(GlobalConstants.MedWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Error while importing table data.")&&sFlag.contains("Error :ERROR: extra data after last expected column"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Error message not displayed while importing csv file to a wrong table. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					BaseActions.ClearConsole("GLOBAL");
				}

				/*************************************************************************
				Test Cases Covered
				1. PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_11
				 *************************************************************************/

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Functional_Invalid_11"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");

					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					sFlag = ObjectBrowserPane.DropTable("auto_import_table","employee");
					if(sFlag.equals("Success"))
					{
						BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,135);
						ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
						BaseActions.Winwait("Data Import failed");
						Thread.sleep(GlobalConstants.MedWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Error while importing table data.")&&sFlag.contains("Error :ERROR: relation \"auto_import_table.employee\" does not exist"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Error message not displayed while importing csv file to a dropped table. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
						ObjectBrowserPane.CreateTable("auto_import_table","employee");
					}
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_12SDV_V1R2_Supp_Mut_004"))
				{
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, 1,138,170);
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"文件.csv";
					ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains(" [INFO] Data successfully imported to the table auto_import_table.nulltable")&&sFlag.contains("[INFO] Total rows imported: 9"))
					{
						QueryEditor.SingleQueryExe("select * from auto_import_table.nulltable", "Valid");
						QueryResult.CurrentExport();//export to current export
						File file = new File(GlobalConstants.sCsvExportPath+"文件.csv");
						if(file.exists())
							file.delete();
						Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"文件.csv");
						Thread.sleep(GlobalConstants.MedWait);
						if(file.exists())
						{
							int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"文件.csv");
							if((RecordCount-1)==9)
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");	
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");	
							}
							else
							{

								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The Total Number of Records are not matching/Unbale to export with chinese name. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"The Total Number of Records are not matching/Unbale to export with chinese name. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The data havent imported. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("TRUNCATE Table auto_import_table.nulltable", "Valid");

				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_15SDV_V1R2_Supp_Mut_005"))
				{
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, 1,138,170);
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"NullvaluesImport.csv";
					ObjectBrowserPane.TableImportWithoutWait(sFileName, "OPEN");
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains(" [INFO] Data successfully imported to the table auto_import_table.nulltable")&&sFlag.contains("[INFO] Total rows imported: 9"))
					{
						QueryEditor.SingleQueryExe("select * from auto_import_table.nulltable", "Valid");
						QueryResult.CurrentExport(); //from export to current export
						File file = new File(GlobalConstants.sCsvExportPath+"NullvaluesExport.csv");
						if(file.exists())
							file.delete();
						Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"NullvaluesExport.csv");
						Thread.sleep(GlobalConstants.MedWait);
						if(file.exists())
						{
							int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"NullvaluesExport.csv");
							if((RecordCount-1)==9)
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,4,"Passed");
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"The Total Number of Records are not matching. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,5,"Unable to Import. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The data havent imported. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("TRUNCATE Table auto_import_table.nulltable", "Valid");

				}
				BaseActions.ClearConsole("GLOBAL");
			}
		}
		for(int i=1;i<=25;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}


