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

import object_repository.ErrorElements;
import object_repository.GlobalConstants;
import script_library.Login;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class DS_STRY_020_001_01 {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","DS_STRY_020_001_01");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","DS_STRY_020_001_01");
		//Variable Declarations	
		String sInputQuery,sStatus,sDatabaseQuery,sClientEncoding,sTestCaseID,sExecute,sQueryType,sFlag,sVal;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Getting Login Credentials from IDE_Smoke_Test_Data file and
		String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
		String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
		String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
		String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
		String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
		String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
		//Loop to iterate through each Test Case in Test Data Sheet	
		UtilityFunctions.CopyFile(GlobalConstants.sINISourcePath+"DataStudio_UTF8.ini",GlobalConstants.sINIDestPath );
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);

			if(sExecute.equals("Yes"))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				sDatabaseQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				sClientEncoding=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,5);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,6);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_001")) ///server GBK and client UTF-8
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						MultipleTerminal.TerminalSetText(2,sInputQuery);
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.ExecuteButton();
						sFlag = MultipleTerminal.TerminalResultTabOperations(2, "COPY");

						if(sFlag.contains("文件"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Content is not Matching in GBK Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The client Encoding is not UTF8. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenamegbk;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_002"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						MultipleTerminal.TerminalSetText(2,sInputQuery);
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.ExecuteButton();
						sFlag = MultipleTerminal.TerminalConsoleCopy(2);
						if(sFlag.contains("ERROR: character with byte sequence 0xe6 0x96 0x87 in encoding \"UTF8\" has no equivalent in encoding \"LATIN1"))	

						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Content is not Matching in Latin1 Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The client Encoding is not UTF8. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenamelatin1;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_003")) //SDV_V1R2_Encoding_Functional_Valid_003 -->server utf-8,client utf-8
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						MultipleTerminal.TerminalSetText(2,sInputQuery);
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.ExecuteButton();
						sFlag = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
						if(sFlag.contains("文件"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Content is not Matching in UTF8 Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The client Encoding is not UTF8. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenameutf8;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_004SDV_V1R2_Importq_003"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						MultipleTerminal.TerminalSetText(2,sInputQuery);
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.ExecuteButton();
						sFlag = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
						if(sFlag.contains("文件"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Content is not Matching in ASCII Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Content is not Matching in ASCII Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The client Encoding is not UTF8. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding is not UTF8. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("SDV_V1R2_Expqort_001"))
				{
					MultipleTerminal.TerminalConsoleResultNavigation(1,"RESULT");
					Thread.sleep(GlobalConstants.MinWait);
					QueryResult.NextRecords("5");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.ExportButton();
					File file = new File(GlobalConstants.sCsvExportPath+"SDV_V1R2_Expqort_001.csv");
					if(file.exists())
					{
						file.delete();
					}
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"SDV_V1R2_Expqort_001.csv");
					Thread.sleep(GlobalConstants.MedWait);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"SDV_V1R2_Expqort_001.csv");
						if(RecordCount > 1)
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Exported file is empty eventhough the query result has values. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"File is not avaliable. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_V1R2_Importq_002"))
				{
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"SDV_V1R2_Importq_002.csv");
					if(file.exists())
					{
						file.delete();
					}
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"SDV_V1R2_Importq_002.csv");
					Thread.sleep(GlobalConstants.MedWait);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"SDV_V1R2_Importq_002.csv");
						if(RecordCount > 1)
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Exported file is empty eventhough the query result has values. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"File is not avaliable. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_005"))
				{
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.CopyFile(GlobalConstants.sINISourcePath+"DataStudio_Latin1.ini",GlobalConstants.sINIDestPath );
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					sFlag =QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("[INFO] Your encoding Latin1 is failed, hence Data Studio is set to default UTF8 encoding."))
					{
						QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
						ObjectBrowserPane.objectBrowserRefresh("SINGLE");
						Thread.sleep(GlobalConstants.MinWait);
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						ObjectBrowserPane.connectToDB();
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.SelectConnection();
						Thread.sleep(GlobalConstants.MinWait);
						MultipleTerminal.OpenNewTerminal();
						Thread.sleep(GlobalConstants.MinWait);
						MultipleTerminal.TerminalSetText(2,sClientEncoding);
						QueryEditor.ExecuteButton();
						Thread.sleep(GlobalConstants.MinWait);
						sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
						if(sVal.contains("UTF8"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to Latin1. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"There is no Info for client encoding Change. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenamegbk;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_006"))
				{

					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to Latin1. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenamelatin1;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_007"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to Latin1. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenameutf8;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_008"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to Latin1. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_009")) //****
				{
					UtilityFunctions.CopyFile(GlobalConstants.sINISourcePath+"DataStudio_GBK.ini",GlobalConstants.sINIDestPath );
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("GBK"))
					{
						MultipleTerminal.TerminalSetText(2,sInputQuery);
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.ExecuteButton();
						sFlag = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
						if(sFlag.contains("文件"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Content is not Matching in GBK Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding is not GBK. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenamegbk;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_010"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					if(BaseActions.WinExists("Connection Error"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						Thread.sleep(GlobalConstants.MedWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE,1);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"User is able to Access the Latin1 Data. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenamelatin1;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_011")) 
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("GBK"))
					{
						MultipleTerminal.TerminalSetText(2,sInputQuery);
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.ExecuteButton();
						sFlag = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
						if(sFlag.contains("文件"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Content is not Matching in UTF8 Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding is not GBK. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenameutf8;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_012"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("GBK"))
					{
						MultipleTerminal.TerminalSetText(2,sInputQuery);
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.ExecuteButton();
						sFlag = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
						if(sFlag.contains("文件"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Content is not Matching in ASCII Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding is not GBK. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_013"))
				{
					UtilityFunctions.CopyFile(GlobalConstants.sINISourcePath+"DataStudio_SQL.ini",GlobalConstants.sINIDestPath );
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					sFlag =QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains("[INFO] Your encoding SQLASCII is failed, hence Data Studio is set to default UTF8 encoding."))
					{
						QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
						ObjectBrowserPane.objectBrowserRefresh("SINGLE");
						Thread.sleep(GlobalConstants.MinWait);
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						ObjectBrowserPane.connectToDB();
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.SelectConnection();
						Thread.sleep(GlobalConstants.MinWait);
						MultipleTerminal.OpenNewTerminal();
						Thread.sleep(GlobalConstants.MinWait);
						MultipleTerminal.TerminalSetText(2,sClientEncoding);
						QueryEditor.ExecuteButton();
						Thread.sleep(GlobalConstants.MinWait);
						sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
						if(sVal.contains("UTF8"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to SQLASCII. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"There is no Info for client encoding Change to SQLASCII. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenamegbk;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_014"))
				{

					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to SQLASCII. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenamelatin1;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_015"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to SQLASCII. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databasenameutf8;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_016"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,sClientEncoding);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to SQLASCII. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_018"))
				{
					//Launching the IDE Tool Application
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					//Login to IDE
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					sFlag =QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag.contains(" [INFO] Your encoding SQLASCII is failed, hence Data Studio is set to default UTF8 encoding."))

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"The client Encoding changed to SQLASCII. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Importq_004"))
				{UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
				QueryEditor.SingleQueryExe(sDatabaseQuery,sQueryType);
				ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				Thread.sleep(GlobalConstants.MinWait);
				ObjectBrowserPane.objectBrowserExpansion("SINGLE");
				ObjectBrowserPane.connectToDB();
				Thread.sleep(GlobalConstants.MinWait);
				QueryEditor.SelectConnection();
				Thread.sleep(GlobalConstants.MinWait);
				MultipleTerminal.OpenNewTerminal();
				Thread.sleep(GlobalConstants.MinWait);
				MultipleTerminal.TerminalSetText(2,sClientEncoding);
				QueryEditor.ExecuteButton();
				Thread.sleep(GlobalConstants.MinWait);
				sVal = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
				if(sVal.contains("UTF8"))
				{
					MultipleTerminal.TerminalSetText(2,sInputQuery);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					sFlag = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(sFlag.contains("文件"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Content is not Matching in ASCII Encoding Database. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				else
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The client Encoding is not UTF8. Please refer screenshot "+sTestCaseID+".jpg");
					UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
				}
				
				MultipleTerminal.CloseTerminal(2);
				Thread.sleep(GlobalConstants.MinWait);
				ObjectBrowserPane.disconnectDB();
				Thread.sleep(GlobalConstants.MinWait);
				QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
				ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				UtilityFunctions.CopyFile(GlobalConstants.sINISourcePath+"DataStudio_UTF8.ini",GlobalConstants.sINIDestPath );
				}

			}//end of if loop for Execute flag
		}//end of for loop
		//Generate text file report for TMSS integration
		for(int i=1;i<=21;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}//end of main
}//end of class
