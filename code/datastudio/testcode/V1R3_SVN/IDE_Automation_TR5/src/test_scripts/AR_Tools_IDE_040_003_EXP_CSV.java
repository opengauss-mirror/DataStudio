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
TITLE - EXPORT QUERY RESULT
DESCRIPTION - THIS PROGRAM COVERS THE BELOW TEST SCRIPTS
1.SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_001-->Export query result to CSV file in the desired location.
2.SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_003-->Export query result to CSV file from the icon
3.SDV_FIA_PLIDE_RSLTWDW_EXPORT_010_587-->Export query result from Object browser.
4.SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_004-->Export query result after moving to different page.
5.SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_002-->Export zero records to csv file from the icon.
 *************************************************************************/

package test_scripts;

import java.awt.event.KeyEvent;
import java.io.File;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import object_repository.SaveAsElements;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class AR_Tools_IDE_040_003_EXP_CSV {

	public static void main(String sARNumber)throws Exception{

		//Scripts to Test AR.Tools.IDE.040.003 - Export query result to a csv file
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Export_CSV");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Export_CSV");
		//Variable Declarations	
		String sInputQuery,sStatus,sTestCaseID,sExecute,sQueryType,sFlag;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			//BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				/*************************************************************************
				Test Cases Covered
				1. SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_004
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_004"))
				{
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					BaseActions.MouseClick("Data Studio", "", "SWT_Window015", "left", 1, 101, 12);
					UtilityFunctions.ScrollDown("Data Studio", "", "SysListView321", 30);
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
				}

				/*************************************************************************
				Test Cases Covered
				2. SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_001
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_001"))
				{
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"ExportResult.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"ExportResult.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"ExportResult.csv");
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Exported file is not saved in the desired location. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				3. SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_002
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_002"))
				{
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					Thread.sleep(GlobalConstants.MinWait);
					QueryResult.CurrentExport();
					Thread.sleep(GlobalConstants.MinWait);
					if(!BaseActions.WinExists(SaveAsElements.sSave))
					{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to export Empty result. Please refer screenshot "+sTestCaseID+".jpg");
					UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					
				}


				/*************************************************************************
				Test Cases Covered
				4.SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_003
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_003"))
				{
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"ExportResult1.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"ExportResult1.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"ExportResult1.csv");
						if(RecordCount > 1)
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Displayed record count and saved record count in csv are mismatching. Please refer screenshot "+sTestCaseID+".jpg");
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

				/*************************************************************************
				Test Cases Covered
				5.SDV_FUNERR_PLIDE_RSLTWDW_EXPORT_013
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNERR_PLIDE_RSLTWDW_EXPORT_013"))
				{
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"TextExport.txt");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					File file = new File(GlobalConstants.sCsvExportPath+"TextExport.txt"); 
					if(file.exists())
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to save the result in text format. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,"AR.Tools.IDE.040.003",i+2,4,"Passed");
					}
				}
				/*************************************************************************
				Test Cases Covered
				6.SDV_FUNERR_PLIDE_RSLTWDW_EXPORT_014
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNERR_PLIDE_RSLTWDW_EXPORT_014"))
				{
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.EmptyCsv();
					sFlag = QueryResult.TextandEmptyValidation();
					if(sFlag.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,"AR.Tools.IDE.040.003",i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to save the result with no filename. Please refer screenshot  "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test cases Covered
				7. SDV_FIA_PLIDE_RSLTWDW_EXPORT_010_587
				 *************************************************************************/

				if(sTestCaseID.equals("SDV_FIA_PLIDE_RSLTWDW_EXPORT_010_587"))
				{
					ObjectBrowserPane.ObjectBrowser();
					ObjectBrowserPane.BrowserExport();
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"BrowserExportResult.csv");
					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sConsoleOutput.contains("successfully exported"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,"AR.Tools.IDE.040.003",i+2,4,"Passed");
					}

					else
					{
						sTestCaseID=UtilityFunctions.GetExcelCellValue("IDE_Functional_Test_Data.xlsx", "AR.Tools.IDE.040.003", 6,1 );
						UtilityFunctions.WriteToExcel(ResultExcel,"AR.Tools.IDE.040.003",i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,"AR.Tools.IDE.040.003",i+2,5,"Exported and Displayed records are not matching. Please refer screenshot "+sTestCaseID+".jpg");
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
}//end of class


