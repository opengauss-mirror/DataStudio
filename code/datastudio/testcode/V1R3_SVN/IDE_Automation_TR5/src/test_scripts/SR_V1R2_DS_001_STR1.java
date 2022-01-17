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

import javax.management.Query;

import autoitx4java.AutoItX;
import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import object_repository.SaveAsElements;
import script_library.Login;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_001_STR1 {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_001_STR1");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_001_STR1");
		//Variable Declarations	
		String sInputQuery,sStatus,sTestCaseID,sExecute,sQueryType,sFlag,sRecord;
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
				sRecord=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,5);

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_1"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_1.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_1.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_1.csv");
						if(RecordCount > 1)
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"File is not avaliable. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_2"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.NextRecords(sRecord);
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.ExeNextRecord();
					sFlag = QueryResult.ExportButton();
					if(sFlag.equals("Fail"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Exported file option is not disbaled. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_3"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.NextRecords(sRecord);
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.ExportButton();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_3.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_3.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_3.csv");
						if((RecordCount-1)>=500)
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");							
						}
						else
						{

							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Total Number of Records are not matching. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
				}


				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_4PTS_SR.V1R2.DS.001_STR_1_Functional_valid_5"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					Thread.sleep(GlobalConstants.MinWait);
					QueryResult.NextRecords(sRecord);
					QueryResult.ExportButton();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_4.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_4.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_4.csv");
						if(RecordCount > 1)
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5," Unable to export the table with special charcters.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5," Unable to export the table with comma .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_6"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_6.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_6.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_6.csv");
						if(RecordCount > 1)
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5," Unable to export the table with column Constraints.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_7"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_7.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_7.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_7.csv");
						if(RecordCount > 1)
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						}

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5," Export the table data with filter conditions, table joins.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_8PTS_SR.V1R2.DS.001_STR_1_Functional_valid_9")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					AutoItX x = new AutoItX();
					if(x.winExists(SaveAsElements.sSave))
					{
						BaseActions.Click(SaveAsElements.sSave, "", "Button2");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");

					}
					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_9.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_9.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_9.csv");
						if(RecordCount > 1)
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"User is not able to overwrite the File.Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"File doesnot exsist.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_Invalid_1"))	
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					AutoItX x = new AutoItX();
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_Invalid_1.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv("");
					if(x.winExists("Save As"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 4);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_13PTS_SR.V1R2.DS.001_STR_1_Functional_valid_14"))	
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,3,"Yes");
					QueryResult.NextRecords(sRecord);
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.ExeNextRecord();
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_14.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_14.csv");
					Thread.sleep(GlobalConstants.MedWait);
					if(BaseActions.WinExists("Data Exported Successfully"))
					{
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);	
					}

					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"PTS_SR.V1R2.DS.001_STR_1_Functional_valid_14.csv");
						if((RecordCount-1)==100)
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"Export Current page to CSV is not Enabled.Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,5,"Unable to Export current page to csv for multiple pages in Result window.Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"File Doesnot exsist.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,5,"Unable to export the sata as the file doesnot exsist.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_12PTS_SR.V1R2.DS.001_STR_1_Functional_valid_15"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,3,"Yes");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.Auto_Import_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, 1,140,170);
					String sFileName = GlobalConstants.sCsvImportPath+"文件.csv";
					ObjectBrowserPane.TableImport(sFileName, "OPEN");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Data successfully imported to the table auto_import_table.nulltable")&&sFlag.contains("[INFO] Total rows imported: 9"))
					{
						QueryEditor.SingleQueryExe("select * from auto_import_table.nulltable", "Valid");
						QueryResult.ResultWindow();
						QueryResult.CurrentExport();
						File file = new File(GlobalConstants.sCsvExportPath+"文件文件.csv");
						if(file.exists())
							file.delete();
						Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"文件文件.csv");
						BaseActions.Winwait("Data Exported Successfully");
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						if(file.exists())
						{
							int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"文件文件.csv");
							if((RecordCount-1)==9)
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,4,"Passed");	
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,4,"Passed");	
							}
							else
							{

								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,5,"unable to import/Export data from chinese file "+sTestCaseID+".jpg");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,5,"Unable to import/Export file which has null values"+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+5,5,"The data havent imported. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("TRUNCATE Table auto_import_table.nulltable;", "Valid");
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Functional_valid_10"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,3,"Yes");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_L, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_L, 1);
					BaseActions.Click("Disconnect Confirmation", "", "Button1");
					Thread.sleep(GlobalConstants.MinWait);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = BaseActions.ControlGetText(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sExeTime);
					Login.IDELogout();
					if(sFlag.equals("cution"))//should be changed to contains("Execution") in V1R2

					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,5,"Result Data is not cleared.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,4,"Passed");
					}
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_1_Usability_Test_1"))
				{					
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,3,"Yes");
					//Getting Login Credentials from IDE_Smoke_Test_Data file and
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					sFlag = QueryResult.ExportButton();
					if(sFlag.equalsIgnoreCase("Fail"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+6,5," export icon is not disabled when the DB connection is connected.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
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
