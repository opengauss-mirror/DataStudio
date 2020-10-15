/*************************************************************************
TITLE - SMOKE TEST AUTOMATION SUITE
DESCRIPTION - THIS AUTOMATION SUITE COVERS THE SMOKE TEST CASES-
-BASED ON EACH DROP
AUTHORS - AWX321822, AWX321824
CREATED DATE - 18-NOV-2015
LAST UPDATED DATE - 24-NOV-2015
TEST CASES COVERED - REFER IDE_Smoke_Test_Data.xlsx IN TEST DATA FOLDER
 *************************************************************************/


package smoke_test;


import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import autoitx4java.AutoItX;
import object_repository.ConsoleResultElements;
import object_repository.ExecQueryElements;
import object_repository.GlobalConstants;
import object_repository.ImportExportFeatures;
import object_repository.ObjectBrowserElements;
import object_repository.SaveAsElements;
import object_repository.TablePropertyElements;
import script_library.DebugOperations;
import script_library.EditTableDataFunctions;
import script_library.Login;
import script_library.ObjectBrowserPane;
import script_library.PlanCost;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SmokeTestAutomation {


	public static void Query_Format_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Smoke Scripts to Test AR.Tools.IDE.020.007 - Query Format,Validation check through Menu, Tool bar and Shortcut options
		//Variable Declarations
		String sFlag,sTestCaseID,sInputQuery,sExecute,sExpectedQuery;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		//Loop to iterate through each Test Case in Test Data Sheet
		for(int i=1;i<=iRowCount;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3 );
			if(sARNumber.equals("AR.Tools.IDE.020.007")&&(sExecute.equals("Yes")))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,5);
				sExpectedQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,6);
				UtilityFunctions.WriteToExcel(ResultExcel,sSheetName,i+2,3,"Yes");
				String sMultiLineQuery[] = sInputQuery.split(":");
				//String sFormat[] = sExpectedQuery.split(":");
				QueryEditor.SetMultiLineQuery(sMultiLineQuery);
				//BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sToolbarControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,ObjectBrowserElements.iToolbarFormatxcord, ObjectBrowserElements.iToolbarFormatycord);
				UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
				UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
				UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
				UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
				sFlag = QueryEditor.QueryFormatValidation(sExpectedQuery, sInputQuery);
				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");
				//Logic to update the result excel and capture screenshot, for errors	
				if (sFlag == "Mismatch")
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
					UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Mismatch in Expected and Actual Formated Query. Please refer screenshot "+sTestCaseID+".jpg");
				}
				else
					UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "AR.Tools.IDE.020.007");
	}//end of function

	public static void Query_Exe_Status_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.030.004 - Query execution status on status bar
		//Variable Declarations		
		String sFlag, sInputQuery,sQueryType,sExecutionType,sQueryCount,sTestCaseID,sExecute,sQuerySelection;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3);

			if(sARNumber.equals("AR.Tools.IDE.030.004")&&(sExecute.equals("Yes")))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,5);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,9);
				sExecutionType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,11);
				sQueryCount=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,12);
				sQuerySelection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,13);
				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");

				if(sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECSTATUS_STATUS_001_687"))
				{
					sFlag = QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					if(sFlag.equals("Success"))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while executing query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FIA_QUERYEXECSTATUS_MULTIPLERESULTWINDOW_009_651"))
				{
					sFlag = QueryEditor.MultipleQueryExe(sInputQuery,sQueryType,sExecutionType,sQueryCount,sQuerySelection);
					if(sFlag.equals("Success"))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while executing query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "AR.Tools.IDE.030.004");
	}//end of function

	public static void Export_CSV_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.040.003 - Export to CSV
		//Variable Declarations	
		String sInputQuery,sTestCaseID,sExecute,sQueryType;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3);

			if(sARNumber.equals("AR.Tools.IDE.040.003")&&(sExecute.equals("Yes")))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,5);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,9);

				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_001_421")||sTestCaseID.equals("SDV_FUNVAL_PLIDE_RSLTWDW_EXPORT_003_421"))
				{
					BaseActions.MouseClick(ConsoleResultElements.wConsoleResult,"",ConsoleResultElements.sMouseClick,
							ConsoleResultElements.sMouseButton,ConsoleResultElements.iClick,ConsoleResultElements.iConsolexcord,ConsoleResultElements.iConsoleycord);
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"ExportResult.csv");
					if(file.exists())
						file.delete();
					//Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"ExportResult.csv");
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"ExportResult.csv");
						if(RecordCount > 0)
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Exported file is empty eventhough the query result has values. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Exported file is not saved in the desired location. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_ExportTable_179"))
				{
					ObjectBrowserPane.DropTable("public","ExportTable1");
					String sFlag = ObjectBrowserPane.CreateTable("public","ExportTable1");
					if(sFlag.equals("Success"))
					{	
						QueryEditor.SingleQueryExe("select * from public.ExportTable1;","Valid");
						QueryResult.CurrentExport();
						File file = new File(GlobalConstants.sCsvExportPath+"EmptyResult.csv");
						if(file.exists())
							file.delete();
						//Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"EmptyResult.csv");
						if(file.exists())
						{
							int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"EmptyResult.csv");
							if(RecordCount == 1)
							{	
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
								ObjectBrowserPane.DropTable("public","ExportTable1");
							}	
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Exported files have either no header or more data than header for a query with no result. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
							ObjectBrowserPane.DropTable("public","ExportTable1");
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_SMOKE_ExportTable_177"))
				{
					ObjectBrowserPane.DropTable("public","ExportTable2");
					String sFlag = ObjectBrowserPane.CreateTable("public","ExportTable2");
					if(sFlag.equals("Success"))
					{
						for(int j=1;j<=2;j++)
						{
							QueryEditor.SingleQueryExe("INSERT INTO public.ExportTable2(empid,ename,salary,deptno) values("+j+",'Dave',25000,10);","Valid");
							//Thread.sleep(GlobalConstants.MinWait);
						}

						QueryResult.NextRecords("3");
						Thread.sleep(GlobalConstants.MinWait);
						QueryEditor.SingleQueryExe("select * from public.ExportTable2;","Valid");
						QueryResult.CurrentExport();
						File file = new File(GlobalConstants.sCsvExportPath+"ExportResult.csv");
						if(file.exists())
							file.delete();
						//Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"ExportResult.csv");
						if(file.exists())
						{
							int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"ExportResult.csv");
							if(RecordCount-1 == 2)
							{	
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
								ObjectBrowserPane.DropTable("public","ExportTable2");
							}	
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Exported file does not have the newly created data set. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Exported file is not saved in the desired location. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "AR.Tools.IDE.040.003");
	}//end of function

	public static void Plan_Cost_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.030.006 - Display query execution plan and cost

		//Variable Declarations	
		String sFlag,sExecute,sInputQuery,sExecutionPlan,sValidationMessage,sFormatType,sTestCaseID;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3);

			if(sARNumber.equals("AR.Tools.IDE.030.006")&&(sExecute.equals("Yes")))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,5);
				sExecutionPlan=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,7);
				sFormatType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,8);
				sValidationMessage=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,10);
				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");
				QueryEditor.SetQuery(sInputQuery);
				PlanCost.PlanCostClick();
				PlanCost.ExecutionPlan(sFormatType,sExecutionPlan);

				if(sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_959")|| sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECPLANCOST_DIALOGBOX_001_959_665"))
				{
					sFlag=PlanCost.PlanCostValidation(sFormatType, sValidationMessage);
					if(sFlag.equals("Success"))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Discrepancy in the Execution Plan and Cost display in console. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "AR.Tools.IDE.030.006");
	}//end of function


	public static void Auto_Filter_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.020.001 - Auto fill, auto suggest feature in SQL editor

		//Variable Declarations	
		String sInputQuery,sTestCaseID,sExecute,sActualQuery,sExpectedQuery;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3);
			if(sARNumber.equals("AR.Tools.IDE.020.001")&&(sExecute.equals("Yes")))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,5);
				sExpectedQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,6);
				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_002_333")||sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_005_333")||sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_003_333"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.trim().equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"The Actual Query After Autofill is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_007_333"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.trim().equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"The Actual Query After Auto Suggestion is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_011_333"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.trim().equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"The Actual Query After Auto Suggestion is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_009_333"))
				{
					ObjectBrowserPane.ObjectBrowserSchemaOpen(1);
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.trim().equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"The Actual Query After Auto Suggestion is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_008_333"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.trim().equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"The Actual Query After Auto Suggestion is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "AR.Tools.IDE.020.001");
	}//end of function


	public static void Mutiple_Query_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.030.002 - Multiple query execution

		//Variable Declarations	
		String sFlag,sInputQuery,sQueryType,sExeType,iQueryCount,sTestCaseID,sQuerySelection,sExecute;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3);

			if(sARNumber.equals("AR.Tools.IDE.030.002")&&(sExecute.equals("Yes")))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,5);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,9);
				sExeType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,11);
				iQueryCount=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,12);
				sQuerySelection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,13);
				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");
				sFlag = QueryEditor.MultipleQueryExe(sInputQuery,sQueryType,sExeType,iQueryCount,sQuerySelection);
				if(sFlag.equals("Success"))
					UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
				else
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
					UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while executing multiple queries. Please refer screenshot "+sTestCaseID+".jpg");
					UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "AR.Tools.IDE.030.002");
	}//end of function

	public static void Syntax_Highlight_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.020.006 - Syntax Highlight

		//Variable Declarations 
		String sTestCaseID,sExecute;
		Boolean sFlag1, sFlag2, sFlag3;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		//Loop to iterate through each Test Case in Test Data Sheet 
		for(int i=1;i<=iRowCount;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3);

			if(sARNumber.equals("AR.Tools.IDE.020.006")&&(sExecute.equals("Yes")))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");

				if((sTestCaseID.equals("SDV_USAB_PLIDE_SQLDEVOPE_SYNHGL_022")))
				{
					/* code for UnReserved keywords*/
					ObjectBrowserPane.UnReservedKeywords();
					UtilityFunctions.Screenshot("UnReservedActual");
					sFlag2 = UtilityFunctions.CompareImage("UnReservedExpectedWC", "UnReservedExpectedWOC","UnReservedActual","Syntax_Highlight");
					QueryEditor.ClearEditor();
					/* code for Reserved keywords*/
					BaseActions.ClearConsole("TERMINAL");
					ObjectBrowserPane.ReservedKeywords(); 
					sFlag1 = UtilityFunctions.CompareImage("ReservedExpectedWC","ReservedExpectedWOC","ReservedActual","Syntax_Highlight");
					QueryEditor.ClearEditor();
					/* code for Predicate and Constant keywords*/
					ObjectBrowserPane.Predicate();
					sFlag3 = UtilityFunctions.CompareImage("PredicateExpectedWC","PredicateExpectedWOC","PredicateActual","Syntax_Highlight");
					QueryEditor.ClearEditor();
					if(sFlag1.equals(true)&&sFlag2.equals(true)&&sFlag3.equals(true))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"There is a mismatch in actual Reserved Keyword image. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "AR.Tools.IDE.020.006");
	}//end of function


	public static void Debug_Operation_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Scripts to Test Debug OPerations
		//Getting Login Credentials from IDE_Smoke_Test_Data file and
		String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 0);
		String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 1);
		String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 2);
		String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 3);
		String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 4);
		String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 5);
		String sDebugPreCondition = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 7);
		Login.LaunchIDE(GlobalConstants.sIDEPath);
		Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
		//Login.DisplayDebugWindows();

		//Variable Declarations	
		String sTestCaseID,sExecute,sStatus,sFlag,sDebugConnection;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		Thread.sleep(1000);
		//OPening Function to Debug
		DebugOperations.DebugObjectBrowser_Open();
		for(int i=1;i<=iRowCount;i++)
			//for(int i=28;i<=42;i++)
			//for(int i=43;i<=54;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String DebugPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin",1,5);
			String DebugPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1,6);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3);

			if(sARNumber.equals("Debug_Operations")&&(sExecute.equals("Yes")))
			{   
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");

				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_DEBUG_FUNC_03"))
				{
					DebugOperations.SetBreakPoint(10);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.ClickContinue();
						Thread.sleep(GlobalConstants.MedWait);

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debugging completed.") && sFlag.contains("Executed Successfully.."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occur while doing DebugOperation. Refer screenshot:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();

				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_CALLSTCK_11"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(11);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						sFlag = DebugOperations.GetCallStack();
						if(sFlag.contains("auto1() - integer [Line : 11]"))
						{
							DebugOperations.StepIn();
							sFlag = DebugOperations.GetCallStack();
							if(sFlag.contains("auto1() - integer [Line : 12]"))
							{	
								DebugOperations.StepOut();
								sFlag = DebugOperations.GetCallStack();
								if(sFlag.contains("auto1() - integer [Line : 13]"))
									sStatus = "Passed";
								else
									sStatus = "Call Stack is not getting updated after Step-Out operation";
							}
							else
								sStatus = "Call Stack is not getting updated after Step-In operation";
						}
						else
							sStatus = "Call Stack is not getting updated once the Debug is started.";
						if(sStatus == "Passed")
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,sStatus+" Refer Screenshot:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}	
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();	
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_VAROPR_FUNC_025"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(11);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						UtilityFunctions.ScrollDown("Data Studio", "", "SysListView323", 5);
						DebugOperations.SetVariableClick();
						DebugOperations.SetVariableValue("8");
						sFlag = DebugOperations.GetVariables();
						if(sFlag.contains("false,m,integer,8"))
						{
							DebugOperations.StepIn();
							sFlag = DebugOperations.GetVariables();
							if(sFlag.contains("false,m,integer,9"))
								sStatus = "Passed";
							else
								sStatus = "Variable value is not getting updated after Step-In.";
						}
						else
							sStatus = "Variable value is not getting updated after Setting the variable during debug.";
						if(sStatus == "Passed")
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,sStatus+" Refer Screenshot:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_VAROPR_PROC_024"))
				{
					//DebugOperations.DebugObjectBrowser();						
					DebugOperations.SetBreakPoint(10);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						sFlag=DebugOperations.GetVariables();
						if(sFlag.contains("false,m,integer,6"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Local Variable has a mismatch after clciking Continue Operation. Please check screenshot:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_PROC_020"))
				{
					//DebugOperations.DebugObjectBrowser();0
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepOver();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("connected successfully"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6," Error Occur While StepOver Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_FUNC_021"))
				{
					DebugOperations.DebugObjectBrowser_Close();
					DebugOperations.SetBreakPoint(11);
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.ClickContinue();
						sFlag=DebugOperations.GetVariables();
						if(sFlag.contains("false,m,integer,8"))
						{
							DebugOperations.ClickContinue();	

							sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
							if(sFlag.contains("Executed Successfully.."))
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur While clicking Continue Operation:"+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur While clicking Continue Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_FUNC_018"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(11);
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugSession();

					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.ClickContinue();
						DebugOperations.StepOut();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Executed Successfully.."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"The value of the variable is not highlighted:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_PROC_021"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepOver();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("connected successfully"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6," Error Occur While StepOver Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_DEBUG_PROC_01"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(12);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepIn();
						DebugOperations.TerminateDebugging();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Executed Successfully.."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Mismatched results After TerminateDebugging Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_PROC_017"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(12);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepIn();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("connected successfully"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Mismatched results After StepIn Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_BRKMGMT_04"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(14);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.ClickContinue();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Executed Successfully.."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Mismatched results After clicking Continue Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_VAROPR_FUNC_024"))
				{
					//DebugOperations.DebugObjectBrowser();						
					DebugOperations.SetBreakPoint(10);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						sFlag=DebugOperations.GetVariables();
						if(sFlag.contains("false,m,integer,6"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Local Variable has a mismatch after clciking Continue Operation. Please check screenshot:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_FUNC_017"))
				{
					//DebugOperations.DebugObjectBrowser();						
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepIn();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debug started."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing StepIn Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_PROC_019"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepOver();
						sFlag=DebugOperations.GetVariables();
						if(sFlag.contains("false,m,integer,7"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing StepOver Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}		
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_VIEWSRCCOD_0009001"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.ClickContinue();
						DebugOperations.TerminateDebugging();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debug started."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Terminate Debug Session:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_DEBUG_PROC_03"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.ClickContinue();
						DebugOperations.ClickContinue();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Executed Successfully.."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing Debugging Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_BRKMGMT_08"))
				{
					ArrayList<String> lstStatus = new ArrayList<String>();
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debug started."))
							lstStatus.add("Success");
						else
							lstStatus.add("Failure");
						DebugOperations.CheckboxClick(0);
						DebugOperations.DisableBreakPoint();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("line number 10 disabled."))
							lstStatus.add("Success");
						else
							lstStatus.add("Failure");
						DebugOperations.CheckboxClick(0);
						DebugOperations.EnableBreakPoint();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("line number 10 enabled."))
							lstStatus.add("Success");
						else
							lstStatus.add("Failure");
						DebugOperations.CheckboxClick(0);
						DebugOperations.DeleteBreakpoint();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("line number 10 deleted."))
							lstStatus.add("Success");
						else
							lstStatus.add("Failure");
						DebugOperations.SetBreakPoint(10);
						if(lstStatus.contains("Failure"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing CheckboxClick:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
						else
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}

				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_VIEWSRCCOD_0009001_748"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.ClickContinue();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debug started."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing Continue Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}

				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_BRKMGMT_PROC_05"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.CheckboxClick(2);
						DebugOperations.DisableBreakPoint();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("number 13 disabled."))
						{	
							sFlag = DebugOperations.GetBreakpoint();
							if((sFlag.contains("false,10,auto1() - integer")) && (sFlag.contains("true,13,auto1() - integer")))
								sStatus = "Passed";
							else
								sStatus = "Details of Enabled and Disabled Breakpoints are not visible.";
						}	
						else
							sStatus = "Breakpoint disable failed.";
					}
					else
					{
						sStatus = "Failed to connect to Debug Server.";
					}
					if(sStatus == "Passed")
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,sStatus+" Refer Screenshot:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}

				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_BRKMGMT_FUNC_05"))
				{
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.CheckboxClick(2);
						Thread.sleep(GlobalConstants.MinWait);
						DebugOperations.DisableBreakPoint();
						Thread.sleep(GlobalConstants.MinWait);

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("number 13 disabled."))
						{	
							sFlag = DebugOperations.GetBreakpoint();
							if((sFlag.contains("false,10,auto1() - integer")) && (sFlag.contains("true,13,auto1() - integer")))
								sStatus = "Passed";
							else
								sStatus = "Details of Enabled and Disabled Breakpoints are not visible.";
						}	
						else
							sStatus = "Breakpoint disable failed.";
					}
					else
					{
						sStatus = "Failed to connect to Debug Server.";
					}
					if(sStatus == "Passed")
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,sStatus+" Refer Screenshot:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}


				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_DEBUG_FUNC_01"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(12);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepIn();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debug started."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing StepIn Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_DEBUG_PROC_06"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepIn();
						DebugOperations.TerminateDebugging();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Executed Successfully.."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Mismatched results After TerminateDebugging Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_PROC_018"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepOver();
						sFlag=DebugOperations.GetVariables();
						if(sFlag.contains("false,m,integer,7"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing StepOver Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_FUNC_019"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						Thread.sleep(GlobalConstants.MinWait);
						DebugOperations.StepOver();
						sFlag=DebugOperations.GetVariables();
						if(sFlag.contains("false,m,integer,7"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing StepOver Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_BRKMGMT_06"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.CheckboxClick(0);
						DebugOperations.DisableBreakPoint();
						DebugOperations.ClickContinue();
						DebugOperations.CheckboxClick(0);
						DebugOperations.EnableBreakPoint();

						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Executed Successfully.."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Enabling BreakPoint:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_STEPOPR_FUNC_020"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.StepOver();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debug started."))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing StepOver Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_BRKMGMT_07"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.RemoveBreakPoint(13);
						DebugOperations.ClickContinue();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debugging completed."))
							sStatus = "Passed";
						else
							sStatus = "Debug is not completed after deleting the breakpoint";
						if(sStatus=="Passed")
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,sStatus+" Refer Screenshot"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_VAROPR_PROC_025"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(11);
					DebugOperations.SetBreakPoint(13);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						UtilityFunctions.ScrollDown("Data Studio", "", "SysListView323", 5);
						Thread.sleep(GlobalConstants.MedWait);
						DebugOperations.SetVariableClick();
						DebugOperations.SetVariableValue("8");
						sFlag = DebugOperations.GetVariables();
						if(sFlag.contains("false,m,integer,8"))
						{
							DebugOperations.StepIn();
							sFlag = DebugOperations.GetVariables();
							if(sFlag.contains("false,m,integer,9"))
								sStatus = "Passed";
							else
								sStatus = "Variable value is not getting updated after Step-In.";
						}
						else
							sStatus = "Variable value is not getting updated after Setting the variable during debug.";
						if(sStatus == "Passed")
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,sStatus+" Refer Screenshot:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_BRKMGMT_10"))
				{
					//DebugOperations.DebugObjectBrowser();
					DebugOperations.SetBreakPoint(10);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.SetBreakPoint(11);
						DebugOperations.ClickContinue();
						sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
						if(sFlag.contains("Debugging completed."))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Performing Debugging Operation:"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
						else
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					DebugOperations.DebugObjectBrowser_Close();
				}	
				if(sTestCaseID.equals("SDV_SMOKE_PLIDE_DBG_DEBUG_PROC_05"))
				{
					DebugOperations.DebugObjectBrowser_Close();
					DebugOperations.SetBreakPoint(11);
					DebugOperations.DebugSession();
					sDebugConnection = DebugOperations.DebugConnection(DebugPassword);
					if(sDebugConnection.equals("DebugConnectionSuccessful"))
					{
						DebugOperations.RemoveConnection();
						Thread.sleep(GlobalConstants.MedWait.intValue());
						if(BaseActions.WinExists("Debug Cancellation Confirmation"))
						{
							BaseActions.Click("Debug Cancellation Confirmation", "", "Button1");
							Thread.sleep(GlobalConstants.MedWait.intValue());
							sFlag=QueryResult.ReadConsoleOutput("GLOBAL");
							if(sFlag.contains("Removed"))
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error Occur while Disconnecting Client from Server :"+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Cancellation Confirmation window is not displayed."+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Debug Connection Error:"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "Debug_Operations");
	}//end of function




	public static void Base_Features_Smoke(String sSheetName,String sResultSheetName,String ResultExcel,String sTextResultFile) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.030.004 - Query execution status on status bar
		//Variable Declarations	
		String sFlag, sInputQuery,sQueryType,sTestCaseID,sExecute;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Get the ARNumber and Execute flag from data sheet and execute the test case based on the Execute flag
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,3);

			if(sARNumber.equals("Base_Features")&&(sExecute.equals("Yes")))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,2);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,5);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,9);
				UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,4,"Yes");

				/*if(sTestCaseID.equals("GaussIDE_SMOKE_ConnMgmt_001"))
				{
					ObjectBrowserPane.newConnection();
					Thread.sleep(GlobalConstants.MinWait);
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 2, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 2, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 2, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 2, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 2, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 2, 5);
					BaseActions.ClearConsole("GLOBAL");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sToolbarControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,ObjectBrowserElements.iNewConnxcord, ObjectBrowserElements.iNewConnycord);
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("connected successfully"))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while making multiple connection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}*/

				if(sTestCaseID.equals("GaussIDE_SMOKE_ViewObjects_170"))
				{
					QueryEditor.SingleQueryExe("DROP FUNCTION automationschema123.smoke1;", "Valid");
					QueryEditor.SingleQueryExe("DROP FUNCTION public.smoke2;", "Valid");
					ObjectBrowserPane.DropSchema("automationschema123");
					ObjectBrowserPane.CreateSchema("automationschema123");
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.CreateFunctionProcedure("automationschema123", "smoke1");
					ObjectBrowserPane.CreateFunctionProcedure("public", "smoke2");
					ObjectBrowserPane.ObjectBrowserRefresh();
					String sFlag1 = QueryEditor.SingleQueryExe("select * from automationschema123.smoke1()","Valid");
					String sFlag2 = QueryEditor.SingleQueryExe("select * from public.smoke2()","Valid");
					if(sFlag1.equals("Success")&&sFlag2.equals("Success"))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Created procedures/functions are not getting reflected in object browser. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_NONFUNCVAL_PLIDE_DB_Con_001"))
				{
					sFlag = QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					if(sFlag.equals("Success"))
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while executing query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_SMOKE_CreateDB_001"))
				{
					ObjectBrowserPane.DropDB("SmokeDB1");
					sFlag = ObjectBrowserPane.CreateDB("SmokeDB1");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating DB. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropDB("SmokeDB1");
				}

				if(sTestCaseID.equals("GaussIDE_Smoke_DropDB_005"))
				{
					ObjectBrowserPane.DropDB("SmokeDB2");
					sFlag = ObjectBrowserPane.CreateDB("SmokeDB2");
					if(sFlag.equals("Success"))
					{
						sFlag = ObjectBrowserPane.DropDB("SmokeDB2");
						if(sFlag.equals("Success"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while dropping DB. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating DB for drop. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateSchema_010"))
				{
					ObjectBrowserPane.DropSchema("automationschema1");
					sFlag = ObjectBrowserPane.CreateSchema("automationschema1");
					if(sFlag.equals("Success"))
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_Smoke_DropSchema_014"))
				{
					ObjectBrowserPane.DropSchema("automationschema2");
					sFlag = ObjectBrowserPane.CreateSchema("automationschema2");
					if(sFlag.equals("Success"))
					{
						sFlag = ObjectBrowserPane.DropSchema("automationschema2");
						if(sFlag.equals("Success"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while dropping schema. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating schema for drop. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_Func_Val_CreateTbl_001"))
				{
					ObjectBrowserPane.DropTable("public","SmokeTable1");
					sFlag = ObjectBrowserPane.CreateTable("public","SmokeTable1");
					if(sFlag.equals("Success"))
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_SMOKE_DropTable_021_070"))
				{
					ObjectBrowserPane.DropTable("public","SmokeTable2");
					sFlag = ObjectBrowserPane.CreateTable("public","SmokeTable2");
					if(sFlag.equals("Success"))
					{
						sFlag = ObjectBrowserPane.DropTable("public","SmokeTable2");
						if(sFlag.equals("Success"))
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while dropping table. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating table for drop. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_Create_Columns_122_812"))
				{
					ObjectBrowserPane.DropTable("public","UniqueTable1");
					sFlag = ObjectBrowserPane.CreateTable("public","UniqueTable1");
					if(sFlag.equals("Success"))
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Passed");
					}	
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,5,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Error occured while creating table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "Base_Features");
	}//end of function

	public static void Save_Open_SQL_Smoke(String sSheetName, String sResultSheetName, String ResultExcel, String sTextResultFile)
			throws Exception
			{
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sARNumber = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 3);
			if(sARNumber.equals("Save_Open_SQL") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				String sInputQuery = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 5);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Smoke_001"))
				{
					QueryEditor.SetQuery(sInputQuery);
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("SmokeSave.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "SmokeSave.sql", "SAVE");
					if(file.exists())
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Error occured while saving query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Smoke_002"))
				{
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "pg_am.sql", "OPEN", "OVERWRITE");
					String sExpectedQuery = QueryEditor.CopyEditor();
					if(sExpectedQuery.equals("select * from pg_am;"))
					{
						if(QueryResult.ReadConsoleOutput("TERMINAL").contains("SQL successfully loaded to SQL Terminal."))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Success message is not displayed in Console.  Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Error occured while opening query.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Smoke_003"))
				{
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "pg_am.sql", "OPEN", "APPEND");
					String sExpectedQuery = QueryEditor.CopyEditor();
					if(sExpectedQuery.length() == 42)
					{
						if(QueryResult.ReadConsoleOutput("TERMINAL").contains("SQL successfully loaded to SQL Terminal."))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Success message is not displayed in Console.  Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6, "Error occured while opening query.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "Save_Open_SQL");
			}//end of function

	public static void Import_Table_Data_Smoke(String sSheetName, String sResultSheetName, String ResultExcel, String sTextResultFile)
			throws Exception
			{
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sARNumber = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 3);
			if(sARNumber.equals("Import_Table_Data") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_3_Smoke_001"))
				{
					QueryEditor.SingleQueryExe("select * from pg_am;", "Valid");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "employee.sql", "OPEN", "OVERWRITE");
					String sExpectedQuery = QueryEditor.CopyEditor();
					if(sExpectedQuery.contains("select * from auto_import_table.employee;"))
					{
						QueryEditor.ExecuteButton();
						//BaseActions.Click(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sExeButton);
						Thread.sleep(GlobalConstants.MedWait);
						QueryResult.CurrentExport();
						File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("employee.csv").toString());
						if(file.exists())
						{
							file.delete();
						}
						Thread.sleep(GlobalConstants.MedWait);
						QueryResult.SaveCsv((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("employee.csv").toString());
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.Winwait("Data Exported Successfully");
						BaseActions.Click("Data Exported Successfully", "", "Button1");
						if(file.exists())
						{
							ObjectBrowserPane.ObjectBrowserRefresh();
							UtilityFunctions.KeyPress(37, 1);
							UtilityFunctions.KeyRelease(37, 1);
							ObjectBrowserPane.Auto_Import_Table_Navigation();
							BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 140, 135);//employee table co-ordinates
							ObjectBrowserPane.TableImport((new StringBuilder(String.valueOf(GlobalConstants.sCsvExportPath))).append("employee.csv").toString(), "OPEN");
							String sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
							if(sFlag.contains("Data successfully imported to the table employee") && sFlag.contains("Total rows imported: 1"))
							{
								UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
							} else
							{
								UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
								UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Success message is not displayed in Console after import.  Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Exported file is not saved under desired location.  Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"SQL not opened successfully.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "Import_Table_Data");
			}//end of function	


	public static void Cancel_Query_Smoke(String sSheetName, String sResultSheetName, String ResultExcel, String sTextResultFile)
			throws Exception
			{
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sARNumber = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 3);
			String sFlag;
			if(sARNumber.equals("Cancel_Query") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_001"))
				{
					String auto = "\"auto\"";
					String loop = "\"insert_select_loop\"";
					QueryResult.ResultWindow();
					QueryResult.NextRecords("0");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("SELECT" +auto+"."+loop+"(10000)");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request.")||sFlag.contains("[INFO] One or more queries after the canceled query are not executed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Cancel Single Query Operation Failed.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_MLTP_SEL_003"))
				{
					BaseActions.ClearConsole("TERMINAL");
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("select * from pg_am;select * from pg_amop;select * from pg_aggregate;select * from largedata;");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					QueryEditor.CancelQuery("SHORTCUT");
					Thread.sleep(GlobalConstants.MinWait);
					QueryResult.CurrentExport();
					Thread.sleep(GlobalConstants.MedWait);
					File file = new File(GlobalConstants.sCsvExportPath+"multiplequery.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"multiplequery.csv");
					if(file.exists())
					{
						int RecordCount = QueryResult.RecordCount(GlobalConstants.sCsvExportPath+"multiplequery.csv");
						if(RecordCount >= 127)
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Cancel Multiple Queries Operation Failed.  Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Cancel Multiple Queries Operation Failed.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_CNLQRY_SEL_PLSQL_009"))
				{
					BaseActions.ClearConsole("TERMINAL");

					String auto = "\"auto\"";
					String loop = "\"insert_select_loop\"";
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("SELECT" +auto+"."+loop+"(10000)");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.CancelQuery("BUTTON");
					sFlag = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sFlag.contains("[INFO] Canceled the query on user request."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Cancel PLSQL Function Operation Failed.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "Cancel_Query");
			}//end of function



	public static void Auto_Suggest_Smoke(String sSheetName, String sResultSheetName, String ResultExcel, String sTextResultFile)
			throws Exception
			{
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sARNumber = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 3);
			String sContents;
			if(sARNumber.equals("Auto_Suggest") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_1"))
				{
					QueryEditor.SetTerminalFocus();
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(sContents.contains("auto - NAMESPACE")&&sContents.contains("auto1() - integer - auto - FUNCTION"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"CTRL+SPACE operation failed in Auto Suggest.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_2"))
				{
					QueryEditor.SetQuery("auto.");
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					if(sContents.contains("auto1() - integer - auto - FUNCTION"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Child objects display failed in Auto Suggest.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_18"))
				{
					String sFlag1, sFlag2;
					DebugOperations.DebugObjectBrowser_Open();
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(sContents.contains("auto - NAMESPACE")&&sContents.contains("auto1() - integer - auto - FUNCTION"))
						sFlag1 = "Pass";
					else
						sFlag1 = "Fail";
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sFunctionEditor,"auto.");
					sContents = QueryEditor.AutoSuggestCopy();
					//QueryEditor.CloseActiveEditor();
					if(sContents.contains("auto - NAMESPACE")&&sContents.contains("auto1() - integer - auto - FUNCTION"))
						sFlag2 = "Pass";
					else
						sFlag2 = "Fail";

					if(sFlag1.equals("Pass")&&sFlag2.contains("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"CTRL+SPACE operation and child objects display failed in Auto Suggest for PL/SQL Function.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.040.001_Functional_valid_5"))
				{
					String sFlag1, sFlag2;
					QueryEditor.SetQuery("autoschema123.");
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					if(sContents.contains("table1 - autoschema123 - TABLEMETADATA")&&sContents.contains("table2 - autoschema123 - TABLEMETADATA")&&sContents.contains("table3 - autoschema123 - TABLEMETADATA")&&sContents.contains("various - autoschema123 - TABLEMETADATA"))
						sFlag1="Pass";
					else
						sFlag1="Fail";
					QueryEditor.SetQuery("autoschema123.tab");
					QueryEditor.AutoSuggestInvoke();
					sContents = QueryEditor.AutoSuggestCopy();
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(sContents.contains("various - autoschema123 - TABLEMETADATA"))
						sFlag2="Fail";
					else
						sFlag2="Pass";
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Progressive suggestion is failed.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "Auto_Suggest");
			}//end of function

	public static void Object_Property_Smoke(String sSheetName, String sResultSheetName, String ResultExcel, String sTextResultFile)
			throws Exception
			{
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sARNumber = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 3);
			String sFlag1, sFlag2;
			if(sARNumber.equals("Object_Property") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_001"))
				{
					sFlag1 = QueryEditor.objPropertyTerminalInvoke("autotableproperty.property1","Table",1);
					if(sFlag1.equals("Pass"))
					{
						BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
						Thread.sleep(GlobalConstants.MedWait);
						String sTable = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.sTableName);
						if(sTable.trim().equals("property1"))
							sFlag1="Pass";
						else
							sFlag1="Fail";
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
					}
					else
						sFlag1="Fail";
					sFlag2 = QueryEditor.objPropertyTerminalInvoke("autotableproperty.propfunc1","Function",1);
					if(sFlag2.equals("Pass"))
					{
						BaseActions.CloseActiveTerminal();
					}
					else
						sFlag2="Fail";
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Table/Function Property view is failed on CTRL+CLICK from SQL Terminal Window.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
				}	

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_002"))
				{
					sFlag1 = QueryEditor.objPropertyFunctionInvoke("Table");
					if(sFlag1.equals("Pass"))
					{
						BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
						String sTable = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.sTableName);
						if(sTable.trim().equals("description"))
							sFlag1="Pass";
						else
							sFlag1="Fail";
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
					}
					else
						sFlag1="Fail";
					sFlag2 = QueryEditor.objPropertyFunctionInvoke("Function");
					if(sFlag2.equals("Pass"))
					{
						BaseActions.Click("Data Studio","","[CLASS:SWT_Window0]");
						String sContent = UtilityFunctions.GetClipBoard();
						if(sContent.contains("CREATE OR REPLACE FUNCTION autotableproperty.propfunc2()"))
							sFlag2 = "Pass";
						else
							sFlag2 = "Fail";
						BaseActions.CloseActiveTerminal();
						BaseActions.CloseActiveTerminal();
					}
					else
						sFlag2="Fail";
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 6,"Table/Function Property view is failed on CTRL+CLICK from PLSQL Function window.  Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "Object_Property");
			}//end of function

	public static void Cancel_Export_Import_Smoke(String sSheetName, String sResultSheetName, String ResultExcel, String sTextResultFile)
			throws Exception
			{
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sARNumber = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 3);
			String sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,5);
			String sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i,9);
			String sFlag,sFlag1;
			if(sARNumber.equals("Cancel_Export_Import") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_1"))
				{
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.BrowserExport();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_1.csv");
					QueryEditor.CancelImportExport("Yes");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					File file = new File(GlobalConstants.sCsvExportPath+"PTS_TOR.080.001_Functional_valid_1.csv");
					if(file.exists()&&sFlag.contains("[INFO] Canceled Data Export on user request."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"CSV file created even though the export job is canceled . Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.080.001_Functional_valid_4"))
				{
					//BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 155, 118);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					ObjectBrowserPane.TableSmokeImport(GlobalConstants.sCsvImportPath+"auto_largedata.csv", "OPEN");
					QueryEditor.CancelImportExport("Yes");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.CopyContentSpecial();
					if(sFlag.contains("[INFO] Canceled Data Import on user request.")&&sFlag1.contains("5517312"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6," inserted records are not rolled back. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("TERMINAL");
				}
			}//end of Execute Flag if loop
		}//end of for loop
		UtilityFunctions.UpdateTMSSResult(ResultExcel, sTextResultFile, "Cancel_Export_Import");
			}//end of function

	public static void Edit_Table_Data_Smoke(String sSheetName, String sResultSheetName, String ResultExcel, String sTextResultFile)
			throws Exception
			{
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sARNumber = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 3);
			String sFlag1, sFlag2;
			if(sARNumber.equals("Edit_Table_Data") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_001"))
				{
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EXECUTE");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "3");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ROllBACK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = EditTableDataFunctions.copyEditTableResultData();
					if(sFlag1.contains("Rollback Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Results are not displayed for row table.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_002"))//testcase to be mapped SDV_FUNVAL_DS_Edit_Table_001
				{
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 3);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EXECUTE");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "3");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ROllBACK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = EditTableDataFunctions.copyEditTableResultData();
					if(sFlag1.contains("Rollback Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Results are not displayed for column store table.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_002"))
				{
					QueryEditor.SingleQueryExe("select count (*) from autotable.ctable where eid = 6;", "Normal");
					sFlag1 = QueryResult.EditCopyContent().replace("*", "");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 3);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP,2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP,2);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "6");
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("DELETE");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select count (*) from autotable.ctable where eid = 6;", "Normal");
					sFlag2 = QueryResult.EditCopyContent().replace("*", "");
					if(sFlag1.matches(sFlag2))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Able to edit the first column.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}

		}

			}
	public static void V1R3_New_Features(String sSheetName, String sResultSheetName, String ResultExcel, String sTextResultFile)
			throws Exception
			{
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile, sSheetName);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sARNumber = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 3);
			String sFlag1, sFlag2;
			if(sARNumber.equals("V1R3_New_Features_SQL_Terminal") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("PTS_TOR.120.002_Functional_valid_4"))
				{
					QueryEditor.SetQuery("select * from all_all_tables;");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);

					BaseActions.MouseClick(ImportExportFeatures.wTitle, "", ImportExportFeatures.wTerminal, "left", 1, ImportExportFeatures.xCordToolbarUpper, ImportExportFeatures.yCordToolbarUpper);

					String sFlag =  UtilityFunctions.GetClipBoard();

					Thread.sleep(GlobalConstants.MinWait);

					BaseActions.MouseClick(ImportExportFeatures.wTitle, "", ImportExportFeatures.wTerminal, "left", 1, ImportExportFeatures.xCordToolbarLower, ImportExportFeatures.yCordToolbarLower);

					sFlag1 =  UtilityFunctions.GetClipBoard();


					if(sFlag.contains("SELECT * FROM ALL_ALL_TABLES;") && sFlag1.contains("select * from all_all_tables;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Unable to change the case of the selected text .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.120.002_Functional_valid_3"))
				{
					QueryEditor.SetQuery("select * from all_all_tables;");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);

					Thread.sleep(GlobalConstants.MinWait);

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);

					UtilityFunctions.KeyPress(KeyEvent.VK_U, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U, 2);

					String sFlag =  UtilityFunctions.GetClipBoard();

					Thread.sleep(GlobalConstants.MinWait);

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);

					UtilityFunctions.KeyPress(KeyEvent.VK_L, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_L, 1);

					sFlag1 =  UtilityFunctions.GetClipBoard();


					if(sFlag.contains("SELECT * FROM ALL_ALL_TABLES;") && sFlag1.contains("select * from all_all_tables;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Unable to change the case of the selected text .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_TOR.120.002_Functional_valid_5"))
				{
					QueryEditor.SetQuery("select * from all_all_tables;");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);

					Thread.sleep(GlobalConstants.MinWait);

					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);

					String sFlag =  UtilityFunctions.GetClipBoard();

					Thread.sleep(GlobalConstants.MinWait);

					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);

					sFlag1 =  UtilityFunctions.GetClipBoard();


					if(sFlag.contains("SELECT * FROM ALL_ALL_TABLES;") && sFlag1.contains("select * from all_all_tables;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Unable to change the case of the selected text .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					if(sTestCaseID.equals("PTS_TOR.120.001_Functional_valid_3"))
					{

						Thread.sleep(GlobalConstants.MaxWait);
						ObjectBrowserPane.objPropertyFunctionOpen();
						UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_G, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_G, 1);

						BaseActions.SetText(ImportExportFeatures.goToLineTitle, "", ImportExportFeatures.goToLineText, "10");

						BaseActions.Click(ImportExportFeatures.goToLineTitle, "", ImportExportFeatures.goToLineOK );

						AutoItX x = new AutoItX();

						String xCord = Integer.toString(x.controlGetPosX(ImportExportFeatures.wTitle, "", ImportExportFeatures.terminalName));

						String yCord = Integer.toString(x.controlGetPosY(ImportExportFeatures.wTitle, "", ImportExportFeatures.terminalName));

						if(xCord.equals("512") && yCord.equals("48"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Able to edit the first column.Please refer screenshot"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}

					if(sTestCaseID.equals("PTS_TOR.120.003_Functional_valid_3_001"))  //Test case mapped PTS_TOR.120.003_Functional_valid_3_004, PTS_TOR.120.004_Functional_valid_3_001, PTS_TOR.120.004_Functional_valid_3_004
					{

						Thread.sleep(GlobalConstants.MaxWait);

						UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);

						BaseActions.SetText("Find and Replace", "", "Edit1", "m");

						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);

						AutoItX x = new AutoItX();

						int initialXCord = x.controlGetPosX(ImportExportFeatures.wTitle, "", ImportExportFeatures.terminalName);

						int initialYCord = x.controlGetPosY(ImportExportFeatures.wTitle, "", ImportExportFeatures.terminalName);

						UtilityFunctions.KeyPress(KeyEvent.VK_F3, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_F3, 1);

						int forwardXCord = x.controlGetPosX(ImportExportFeatures.wTitle, "", ImportExportFeatures.terminalName);

						int forwardYCord = x.controlGetPosY(ImportExportFeatures.wTitle, "", ImportExportFeatures.terminalName);

						Thread.sleep(GlobalConstants.MedWait);

						UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_F3, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_F3, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);

						int backwardXCord = x.controlGetPosX(ImportExportFeatures.wTitle, "", ImportExportFeatures.terminalName);

						int backwardYCord = x.controlGetPosY(ImportExportFeatures.wTitle, "", ImportExportFeatures.terminalName);



						if(forwardXCord>initialXCord && forwardYCord>initialYCord && initialXCord>backwardXCord && initialYCord>backwardYCord)
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Able to edit the first column.Please refer screenshot"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
				}
			}

			if(sARNumber.equals("V1R3_New_Features_Export") && sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, sSheetName, i, 2);
				UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 4, "Yes");
				if(sTestCaseID.equals("PTS_TOR.120.002_Functional_valid_3"))
				{
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MinWait);

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.Auto_Table_Navigation();

					ObjectBrowserPane.BrowserExport();

					Thread.sleep(GlobalConstants.MedWait);

					boolean b = BaseActions.WinExists(ImportExportFeatures.exportTitle);

					String sFlag3 = BaseActions.ControlGetText(ImportExportFeatures.exportTitle, "", ImportExportFeatures.formatCombo);

					Thread.sleep(GlobalConstants.MedWait);

					AutoItX x = new AutoItX();

					boolean b1 = x.controlCommandIsChecked(ImportExportFeatures.exportTitle,"", ImportExportFeatures.bIncludeHeader);

					boolean b2 = x.controlCommandIsEnabled(ImportExportFeatures.exportTitle, "", ImportExportFeatures.bQuotes);

					boolean b3 = x.controlCommandIsEnabled(ImportExportFeatures.exportTitle, "", ImportExportFeatures.bEscape);

					boolean b4 = x.controlCommandIsEnabled(ImportExportFeatures.exportTitle, "", ImportExportFeatures.bReplaceNull);

					Thread.sleep(GlobalConstants.MedWait);

					boolean b5 = x.controlCommandIsChecked(ImportExportFeatures.exportTitle,"", "Button1");

					BaseActions.Click(ImportExportFeatures.exportTitle, "",ImportExportFeatures.formatCombo);

					UtilityFunctions.KeyPress(KeyEvent.VK_B, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_B, 1);

					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					boolean b6 = x.controlCommandIsEnabled(ImportExportFeatures.exportTitle, "", ImportExportFeatures.bQuotes);

					boolean b7 = x.controlCommandIsEnabled(ImportExportFeatures.exportTitle, "", ImportExportFeatures.bEscape);

					boolean b8 = x.controlCommandIsEnabled("Export Table Data :auto.auto_largedata", "", "Edit3");

					boolean b9 = x.controlCommandIsChecked(ImportExportFeatures.exportTitle, "", ImportExportFeatures.bReplaceNull);

					Thread.sleep(GlobalConstants.MedWait);

					BaseActions.MouseClick(ImportExportFeatures.exportTitle, "", ImportExportFeatures.wSelectedColumnsHeader, "left", 1, 18, 13);

					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

					String sFlag4 = BaseActions.ControlGetText(ImportExportFeatures.exportTitle, "", ImportExportFeatures.wSelectedColumnsList);

					BaseActions.Click(ImportExportFeatures.exportTitle, "", ImportExportFeatures.bExport);

					BaseActions.Winwait(SaveAsElements.sSave);

					BaseActions.Click(SaveAsElements.sSave, "", "Button1");

					BaseActions.Winwait("Data Exported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);

					String sFlag5 = QueryResult.ReadConsoleOutput("GLOBAL");

					if(sFlag5.contains("successfully exported") && b==true && sFlag3.equals("CSV") && b1==true && b2==true 
							&& b3==true && b4==true && b5==true && b6==false && b7==false && b8==false && b9==false)
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Export window parameters are not correct .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_TOR.110.001_Functional_valid_1"))
				{
					QueryEditor.SingleQueryExe("Truncate table auto.auto_largedata;","IN");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MinWait);

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.Auto_Table_Navigation();

					ObjectBrowserPane.BrowserImport();

					Thread.sleep(GlobalConstants.MedWait);

					boolean b = BaseActions.WinExists(ImportExportFeatures.importTitle);

					BaseActions.Click(ImportExportFeatures.importTitle, "", ImportExportFeatures.bBrowse);

					boolean b1 = BaseActions.WinExists("Open");
					Thread.sleep(GlobalConstants.MedWait);

					BaseActions.SetText("Open", "", ImportExportFeatures.editFile, ImportExportFeatures.filePath);

					BaseActions.Click("Open", "", "Button1");

					Thread.sleep(GlobalConstants.MedWait);

					String fileLoc = BaseActions.ControlGetText(ImportExportFeatures.importTitle, "", ImportExportFeatures.filePath);

					BaseActions.SetText(ImportExportFeatures.importTitle, "", ImportExportFeatures.editFile,"Editable");

					String editable = BaseActions.ControlGetText(ImportExportFeatures.importTitle, "", ImportExportFeatures.editFile);

					Thread.sleep(GlobalConstants.MedWait);

					String sFlag3 = BaseActions.ControlGetText(ImportExportFeatures.importTitle, "", ImportExportFeatures.formatCombo);

					Thread.sleep(GlobalConstants.MedWait);

					AutoItX x = new AutoItX();

					boolean b2 = x.controlCommandIsChecked(ImportExportFeatures.importTitle,"", ImportExportFeatures.bComboImport);

					boolean b3 = x.controlCommandIsEnabled(ImportExportFeatures.importTitle, "", ImportExportFeatures.bQuotesImport);

					boolean b4 = x.controlCommandIsEnabled("Import Table Data :auto.auto_largedata", "", ImportExportFeatures.bEscapeImport);

					boolean b5 = x.controlCommandIsEnabled("Import Table Data :auto.auto_largedata", "", ImportExportFeatures.bReplaceNullImport);

					Thread.sleep(GlobalConstants.MedWait);

					boolean b6 = x.controlCommandIsChecked(ImportExportFeatures.importTitle,"", ImportExportFeatures.bIncludeHeaderImport);

					BaseActions.Click(ImportExportFeatures.importTitle, "", ImportExportFeatures.formatCombo);

					UtilityFunctions.KeyPress(KeyEvent.VK_B, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_B, 1);

					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					boolean b7 = x.controlCommandIsEnabled(ImportExportFeatures.importTitle, "", ImportExportFeatures.bQuotesImport);

					boolean b8 = x.controlCommandIsEnabled("Import Table Data :auto.auto_largedata", "", ImportExportFeatures.bEscapeImport);

					boolean b9 = x.controlCommandIsEnabled("Import Table Data :auto.auto_largedata", "", ImportExportFeatures.bEscapeImport);

					boolean b10 = x.controlCommandIsChecked("Import Table Data :auto.auto_largedata", "", ImportExportFeatures.bReplaceNullImport);

					Thread.sleep(GlobalConstants.MedWait);

					BaseActions.Click(ImportExportFeatures.importTitle, "", ImportExportFeatures.bImportOK);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Winwait("Data Imported Successfully");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);

					String sFlag5 = QueryResult.ReadConsoleOutput("GLOBAL");

					if(sFlag5.contains("successfully imported") && b==true && sFlag3.equals("CSV") && b1==true && b2==true 
							&& b3==true && b4==true && b5==true && b6==true && b7==false && b8==false && b9==false && b10==true && sFlag3.equals("CSV") &&
							!editable.equals("Editable") && fileLoc.equals("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Test_Data\\CSV_Files\\auto_largedata"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sResultSheetName, i + 2, 5, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sResultSheetName,i+2,6,"Import window parameters are not correct .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


			}


		}

			} 



}//end of class
