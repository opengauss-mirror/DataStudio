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

import java.io.FileWriter;
import java.io.Writer;

import object_repository.ConsoleResultElements;
import object_repository.ExpQueryElements;
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class Import_Table_Data {
	public static void main(String sARNumber) throws Exception{
		//Scripts to Test AR.Tools.IDE.030.004 - Query execution status on status bar
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Import_Table_Data");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Import_Table_Data");
		//Variable Declarations	
		String sFlag,sStatus,sTestCaseID,sExecute;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);

				/*************************************************************************
				Test Cases Covered
				1. TC_SR.V1R2.DS.001_STR_3_Functional_valid_1
				2. TC_SR.V1R2.DS.001_STR_3_Functional_valid_2
				 *************************************************************************/

				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_valid_1TC_SR.V1R2.DS.001_STR_3_Functional_valid_2"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					ObjectBrowserPane.Auto_Import_Table_Navigation();
					sFlag = ObjectBrowserPane.TruncateTable(143,153);
					if(sFlag.equals("Success"))
					{
						BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
						ObjectBrowserPane.TableImport(sFileName, "OPEN");
						sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Data succesfully imported to the table 'employee'.")&&sFlag.contains("Total rows imported: 5"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
							BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
							ObjectBrowserPane.TableImport(sFileName, "OPEN");
							sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
							if(sFlag.contains("Data succesfully imported to the table 'employee'.")&&sFlag.contains("Total rows imported: 5"))
							{
								sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee;", "Table");
								if(sFlag.contains("Total result records fetched: 10"))
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
				1. TC_SR.V1R2.DS.001_STR_3_Functional_valid_3				
				 *************************************************************************/
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_valid_3"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");

					String sFileName = GlobalConstants.sCsvImportPath+"emp_special.csv";
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
					ObjectBrowserPane.TableImport(sFileName, "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Data succesfully imported to the table 'employee'.")&&sFlag.contains("Total rows imported: 4"))
					{
						sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee where ename='Dave,Steve*&^&*^';", "Table");
						if(sFlag.contains("Total result records fetched: 1"))
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
				}	

				/*************************************************************************
				Test Cases Covered
				1. TC_SR.V1R2.DS.001_STR_3_Functional_valid_5				
				 *************************************************************************/
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_valid_5"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sSourceFile = GlobalConstants.sCsvImportPath+"employee.csv";
					String sDestinationFile = GlobalConstants.sCsvImportPath+"emp_append.csv";
					UtilityFunctions.CopyFile(sSourceFile, sDestinationFile);
					Writer output;
					output = new FileWriter(sDestinationFile,true);
					output.append("\"6\",\"M\",\"DaveTest\",\"25000\",\"10\"");
					output.close();
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
					ObjectBrowserPane.TableImport(sDestinationFile, "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Data succesfully imported to the table 'employee'.")&&sFlag.contains("Total rows imported: 6"))
					{
						sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee where ename='DaveTest';", "Table");
						if(sFlag.contains("Total result records fetched: 1"))
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
				}	

				/*************************************************************************
				Test Cases Covered
				1. TC_SR.V1R2.DS.001_STR_3_Functional_valid_6				
				 *************************************************************************/
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_valid_6"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					ObjectBrowserPane.TruncateTable(143,153);
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
					ObjectBrowserPane.TableImport(sFileName, "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Data succesfully imported to the table 'employee'.")&&sFlag.contains("Total rows imported: 5"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Table Data Import Failed after table truncate. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				1. TC_SR.V1R2.DS.001_STR_3_Functional_valid_10				
				 *************************************************************************/
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_valid_10"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"emp_chinese.csv";
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
					ObjectBrowserPane.TableImport(sFileName, "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Data succesfully imported to the table 'employee'.")&&sFlag.contains("Total rows imported: 5"))
					{
						sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee where ename='´óÎÀ';", "Table");
						if(sFlag.contains("Total result records fetched: 5"))
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
				}

				/*************************************************************************
				Test Cases Covered
				1. TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_7				
				 *************************************************************************/
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_7"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"emp_txt.txt";
					ObjectBrowserPane.TruncateTable(143,153);
					BaseActions.ClearConsole("Query");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
					ObjectBrowserPane.TableImport(sFileName, "OPEN");
					if(BaseActions.WinExists(ExpQueryElements.wImportTableDataWindow))
					{
						BaseActions.Click(ExpQueryElements.wImportTableDataWindow,"",ExpQueryElements.bImportTableOK);
						sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Data succesfully imported to the table 'employee'.")&&sFlag.contains("Total rows imported: 5"))
						{
							sFlag = QueryEditor.SingleQueryExe("select * from auto_import_table.employee where ename='Dave';", "Table");
							if(sFlag.contains("Total result records fetched: 5"))
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Data Imported from Text file. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Data succesfully imported message is displayed for an import from text file. Please refer screenshot "+sTestCaseID+".jpg");
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
				}

				/*************************************************************************
				Test Cases Covered
				1. TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_10				
				 *************************************************************************/
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_10"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"emp_data_length.csv";
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
					ObjectBrowserPane.TableImport(sFileName, "OPEN");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Error while importing table data.")&&sFlag.contains("Error :ERROR: value too long for type character(1)"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Error message not displayed while Importing when the table has less data length than the csv file. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				1. TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_13				
				 *************************************************************************/
				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_13"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					String sFileName = GlobalConstants.sCsvImportPath+"emp_junk.csv";
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
					ObjectBrowserPane.TableImport(sFileName, "OPEN");
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
				1. TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_12
				 *************************************************************************/

				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_12"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");

					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					sFlag="Success";
					if(sFlag.equals("Success"))
					{
						BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,172);
						ObjectBrowserPane.TableImport(sFileName, "OPEN");
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
				}

				/*************************************************************************
				Test Cases Covered
				1. TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_11
				 *************************************************************************/

				if(sTestCaseID.equals("TC_SR.V1R2.DS.001_STR_3_Functional_Invalid_11"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					sFlag = ObjectBrowserPane.DropTable("auto_import_table","employee");
					if(sFlag.equals("Success"))
					{
						BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,143,153);
						ObjectBrowserPane.TableImport(sFileName, "OPEN");
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
				}
			}//end of if loop for Execute flag
		}//end of for loop
		//Generate text file report for TMSS integration
		for(int i=1;i<=16;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}//end of main
}//end of class

