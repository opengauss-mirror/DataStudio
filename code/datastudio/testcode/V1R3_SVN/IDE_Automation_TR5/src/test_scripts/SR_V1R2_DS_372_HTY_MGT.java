package test_scripts;

import object_repository.GlobalConstants;
import script_library.ExecutionHistoryFunctions;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_372_HTY_MGT {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_372_HTY_MGT");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_372_HTY_MGT");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag1,sStatus;
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

				if(sTestCaseID.equals("PTS_TOR.372.001_Functional_valid_1"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("DELETEALL");
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("CREATE ROLE manager IDENTIFIED BY 'Gaussdba@Mpp';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("ALTER ROLE manager IDENTIFIED BY 'Datastudio@Mpp';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(!sFlag1.equals("CREATE ROLE manager IDENTIFIED BY 'Gaussdba@Mpp';")&&!sFlag1.equals("ALTER ROLE manager IDENTIFIED BY 'Datastudio@Mpp';"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Role Passwords are stored in SQL History. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP ROLE manager", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
				}
				if(sTestCaseID.equals("PTS_TOR.372.001_Functional_valid_2"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("CREATE USER manager IDENTIFIED BY 'Gaussdba@Mpp';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("ALTER USER manager IDENTIFIED BY 'Datastudio@Mpp';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(!sFlag1.equals("CREATE USER manager IDENTIFIED BY 'Gaussdba@Mpp';")&&!sFlag1.equals("ALTER USER manager IDENTIFIED BY 'Datastudio@Mpp';"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User Passwords are stored in SQL History. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP USER manager", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP SCHEMA manager", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("PTS_TOR.372.001_Functional_valid_3"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("SESSION SET SESSION AUTHORIZATION joe PASSWORD '123@abcd';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(!sFlag1.equals("SESSION SET SESSION AUTHORIZATION joe PASSWORD '123@abcd';"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"SESSION Passwords are stored in SQL History. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
				}

				if(sTestCaseID.equals("PTS_TOR.372.001_Functional_valid_4")) //Test cases mapped PTS_TOR.372.001_Functional_Invalid_1
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("SET ROLE paul PASSWORD '1234@abcd';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(!sFlag1.equals("SET ROLE paul PASSWORD '1234@abcd';"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Role Passwords are stored in SQL History. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
				}

				if(sTestCaseID.equals("PTS_TOR.372.001_Functional_valid_5"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("CREATE OR REPLACE FUNCTION public.func()");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SetFunction("RETURNS integer LANGUAGE plpgsql");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SetFunction("AS $$ DECLARE BEGIN");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("SET ROLE paul PASSWORD '1234@abcd';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("end $$");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(!sFlag1.contains("CREATE OR REPLACE FUNCTION public.func()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"valid PLSQL are storing are stored in SQL History. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("Drop function public.func();", "InValid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("PTS_TOR.372.001_Functional_Invalid_2"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("CREAAAATE OR REPLACE FUNCTION public.func()");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SetFunction("RETURNS integer LANGUAGE plpgsql");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SetFunction("AS $$ DECLARE BEGIN");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("SET ROLE paul PASSWORD '1234@abcd';");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("end $$");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = ExecutionHistoryFunctions.copyQueryExeHistoryTray("DSConnSP2",1);
					if(!sFlag1.contains("CREAAAATE OR REPLACE FUNCTION public.func()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"in valid PLSQL procedures are stored in SQL History. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ExecutionHistoryFunctions.closeExeHistory();
					Thread.sleep(GlobalConstants.MinWait);
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
