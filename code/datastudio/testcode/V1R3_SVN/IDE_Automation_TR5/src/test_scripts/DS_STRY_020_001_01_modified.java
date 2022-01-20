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

import java.io.File;
import object_repository.GlobalConstants;
import script_library.*;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class DS_STRY_020_001_01_modified
{

	public DS_STRY_020_001_01_modified()
	{
	}

	public static void main(String sARNumber)
			throws Exception
			{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest", "DS_STRY_020_001_01");
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest", "DS_STRY_020_001_01");
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
		String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
		String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
		String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
		String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
		String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
		UtilityFunctions.CopyFile((new StringBuilder(String.valueOf(GlobalConstants.sINISourcePath))).append("DataStudio_UTF8.ini").toString(), GlobalConstants.sINIDestPath);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 2);
			if(sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 1);
				String sDatabaseQuery = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 3);
				String sClientEncoding = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 4);
				String sInputQuery = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 5);
				String sQueryType = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 6);
				UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 3, "Yes");
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_001"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContentFirstTime();
					if(sVal.contains("UTF8"))
					{
						BaseActions.ClearConsole("Normal");
						QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryResult.ResultWindow();
						String sFlag = QueryResult.CopyContentFirstTime();
						if(sFlag.contains("\u6587\u4EF6"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("Content is not Matching in GBK Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("The client Encoding is not UTF8. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenamegbk;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_002"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					Thread.sleep(GlobalConstants.MedWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						BaseActions.ClearConsole("Normal");
						String sFlag = QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
						if(sFlag.contains("ERROR: character with byte sequence 0xe6 0x96 0x87 in encoding \"UTF8\" has no e" +
								"quivalent in encoding \"LATIN1\""
								))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("Content is not Matching in Latin1 Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("The client Encoding is not UTF8. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenamelatin1;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_003"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						BaseActions.ClearConsole("Normal");
						QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryResult.ResultWindow();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						String sFlag = QueryResult.CopyContent();
						if(sFlag.contains("\u6587\u4EF6"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("Content is not Matching in UTF8 Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("The client Encoding is not UTF8. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenameutf8;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_004SDV_V1R2_Importq_003"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						BaseActions.ClearConsole("Normal");
						QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryResult.ResultWindow();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						String sFlag = QueryResult.CopyContent();
						if(sFlag.contains("\u6587\u4EF6"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Passed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("Content is not Matching in ASCII Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Content is not Matching in ASCII Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("The client Encoding is not UTF8. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding is not UTF8. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Expqort_001"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					QueryResult.ExportButton();
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("SDV_V1R2_Expqort_001.csv").toString());
					if(file.exists())
					{
						file.delete();
					}
					Thread.sleep(GlobalConstants.MedWait.intValue());
					QueryResult.SaveCsv((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("SDV_V1R2_Expqort_001.csv").toString());
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("SDV_V1R2_Expqort_001.csv").toString());
						if(RecordCount > 1)
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Exported file is empty eventhough the query result has values. Please refer scre" +
									"enshot "
									)).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("File is not avaliable. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Importq_002"))
				{
					QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
					QueryResult.CurrentExport();
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("SDV_V1R2_Importq_002.csv").toString());
					if(file.exists())
					{
						file.delete();
					}
					Thread.sleep(GlobalConstants.MedWait.intValue());
					QueryResult.SaveCsv((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("SDV_V1R2_Importq_002.csv").toString());
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("SDV_V1R2_Importq_002.csv").toString());
						if(RecordCount > 1)
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Exported file is empty eventhough the query result has values. Please refer scre" +
									"enshot "
									)).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("File is not avaliable. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_005"))
				{
					UtilityFunctions.CopyFile((new StringBuilder(String.valueOf(GlobalConstants.sINISourcePath))).append("DataStudio_Latin1.ini").toString(), GlobalConstants.sINIDestPath);
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName, sPassword,"PERMENANT");
					String sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Your encoding \"Latin1\" is failed,hence Data Studio is set to default \"" +
							"UTF8\" encoding."
							))
					{
						QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
						ObjectBrowserPane.objectBrowserRefresh("SINGLE");
						Thread.sleep(GlobalConstants.MinWait.intValue());
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						ObjectBrowserPane.connectToDB();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryEditor.SelectConnection();
						QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryResult.ResultWindow();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						String sVal = QueryResult.CopyContentFirstTime();
						if(sVal.contains("UTF8"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to Latin1. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("There is no Info for client encoding Change. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenamegbk;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_006"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to Latin1. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenamelatin1;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_007"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to Latin1. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenameutf8;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_008"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to Latin1. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Valid_009"))
				{
					UtilityFunctions.CopyFile((new StringBuilder(String.valueOf(GlobalConstants.sINISourcePath))).append("DataStudio_GBK.ini").toString(), GlobalConstants.sINIDestPath);
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName, sPassword,"PERMENANT");
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContentFirstTime();
					if(sVal.contains("GBK"))
					{
						BaseActions.ClearConsole("Normal");
						QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryResult.ResultWindow();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						String sFlag = QueryResult.CopyContentFirstTime();
						if(sFlag.contains("\u6587\u4EF6"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Content is not Matching in GBK Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding is not GBK. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenamegbk;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_010"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					if(BaseActions.WinExists("Connection Error"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						Thread.sleep(GlobalConstants.MedWait.intValue());
						UtilityFunctions.KeyPress(27, 1);
						UtilityFunctions.KeyRelease(27, 1);
						BaseActions.Click("Enter Password", "", "Button2");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("User is able to Access the Latin1 Data. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenamelatin1;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_011"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("GBK"))
					{
						BaseActions.ClearConsole("Normal");
						QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryResult.ResultWindow();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						String sFlag = QueryResult.CopyContent();
						if(sFlag.contains("\u6587\u4EF6"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Content is not Matching in UTF8 Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding is not GBK. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenameutf8;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_012"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("GBK"))
					{
						BaseActions.ClearConsole("Normal");
						QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryResult.ResultWindow();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						String sFlag = QueryResult.CopyContent();
						if(sFlag.contains("\u6587\u4EF6"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Content is not Matching in ASCII Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding is not GBK. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_013"))
				{
					UtilityFunctions.CopyFile((new StringBuilder(String.valueOf(GlobalConstants.sINISourcePath))).append("DataStudio_SQL.ini").toString(), GlobalConstants.sINIDestPath);
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName, sPassword,"PERMENANT");
					String sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains(" [INFO] Your encoding \"SQLASCII\" is failed,hence Data Studio is set to default" +
							" \"UTF8\" encoding."
							))
					{
						QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
						ObjectBrowserPane.objectBrowserRefresh("SINGLE");
						Thread.sleep(GlobalConstants.MinWait.intValue());
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						ObjectBrowserPane.connectToDB();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryEditor.SelectConnection();
						QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						String sVal = QueryResult.CopyContentFirstTime();
						if(sVal.contains("UTF8"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to SQLASCII. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("There is no Info for client encoding Change to SQLASCII. Please refer screenshot" +
								" "
								)).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenamegbk;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_014"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to SQLASCII. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenamelatin1;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_015"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to SQLASCII. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databasenameutf8;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_016"))
				{
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryResult.ResultWindow();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContent();
					if(sVal.contains("UTF8"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to SQLASCII. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("SDV_V1R2_Encoding_Functional_Validq_018"))
				{
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName, sPassword,"PERMENANT");
					String sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains(" [INFO] Your encoding \"SQLASCII\" is failed,hence Data Studio is set to default" +
							" \"UTF8\" encoding."
							))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("The client Encoding changed to SQLASCII. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_V1R2_Importq_004"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SingleQueryExe(sDatabaseQuery, sQueryType);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait.intValue());
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SelectConnection();
					QueryEditor.SingleQueryExe(sClientEncoding, sQueryType);
					Thread.sleep(GlobalConstants.MinWait.intValue());
					String sVal = QueryResult.CopyContentFirstTime();
					if(sVal.contains("UTF8"))
					{
						BaseActions.ClearConsole("Normal");
						QueryEditor.SingleQueryExe(sInputQuery, sQueryType);
						Thread.sleep(GlobalConstants.MinWait.intValue());
						QueryResult.ResultWindow();
						Thread.sleep(GlobalConstants.MinWait.intValue());
						String sFlag = QueryResult.CopyContentFirstTime();
						if(sFlag.contains("\u6587\u4EF6"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Content is not Matching in ASCII Encoding Database. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("The client Encoding is not UTF8. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait.intValue());
					QueryEditor.SingleQueryExe("DROP database databaseNamesqlascii;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					UtilityFunctions.CopyFile((new StringBuilder(String.valueOf(GlobalConstants.sINISourcePath))).append("DataStudio_UTF8.ini").toString(), GlobalConstants.sINIDestPath);
				}
			}
		}

		for(int i = 1; i <= 21; i++)
		{
			String sTestCaseID = UtilityFunctions.GetExcelCellValue(ResultExcel, sARNumber, i + 2, 2);
			String sStatus = UtilityFunctions.GetExcelCellValue(ResultExcel, sARNumber, i + 2, 4);
			String sFinalStatus = (new StringBuilder(String.valueOf(sTestCaseID))).append(" ").append(sStatus).toString();
			if(!sStatus.isEmpty())
			{
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
			}
		}

			}
}
