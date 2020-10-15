package test_scripts;

import java.awt.event.KeyEvent;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import script_library.DataMgmtFunctions;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class Base_SchemaMgnt {

	public static void main(String sARNumber) throws Exception {

		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Schema_Mgnt");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Schema_Mgnt");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sStatus;
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
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateSchema_010"))
				{
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					sFlag = ObjectBrowserPane.CreateSchema("TestSchema");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to Create the Schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("GaussIDE_FUNCERR_CreateSchema_186"))
				{
					ObjectBrowserPane.CreateSchema("TestSchema");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag = ObjectBrowserPane.CreateSchema("TestSchema");
					if(sFlag.equals("Failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create Schema with same name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	 
				}


				if(sTestCaseID.equals("GaussIDE_SMOKE_EditSchema_011"))
				{
					sFlag = ObjectBrowserPane.RenameSchema("TestSchema", "NewTestSchema");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Rename the Schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNCERR_CreateSchema_189"))
				{
					sFlag = ObjectBrowserPane.RenameSchema("NewTestSchema", "NewTestSchema");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

					if(sFlag.equals("Failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to rename the schema with the same name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_VAL_CreateSchema_196"))
				{
					ObjectBrowserPane.DropSchema("NewTestSchema");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag = ObjectBrowserPane.CreateSchema("NewTestSchema");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create schema which is already dropped. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropSchema("NewTestSchema");
				}

				if(sTestCaseID.equals("GaussIDE_FUNCERR_CreateSchema_185"))
				{
					sFlag = ObjectBrowserPane.CreateSchema("*Create schema'swithnamecontaining64ormorecharactersstartingwith");

					if(sFlag.equals("Failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create Schema with Invalid name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}
				if(sTestCaseID.equals("GaussIDE_FUNC_VAL_CreateSchema_196_628"))
				{
					ObjectBrowserPane.CreateSchema("Testschema1");
					ObjectBrowserPane.CreateSchema("Testschema2");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.DropSchema("Testschema1");
					sFlag = ObjectBrowserPane.RenameSchema("Testschema2", "Testschema1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the Schema in another database. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}		
					ObjectBrowserPane.DropSchema("Testschema1");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_VAL_CreateSchema_221"))
				{
					ObjectBrowserPane.CreateSchema("Testschema1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.RenameSchema("Testschema1", "Testschema");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.CreateTable("Testschema", "t1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.InsertTable("Testschema", "t1", 4);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.DropTable("Testschema", "t1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag = ObjectBrowserPane.DropSchema("Testschema");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to drop the Schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}		
				}
				if(sTestCaseID.equals("GaussIDE_FUNC_VAL_CreateSchema_223"))	
				{
					ObjectBrowserPane.CreateSchema("Testschema1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.CreateTable("Testschema1", "t1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.InsertTable("Testschema1", "t1", 4);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.RenameSchema("Testschema1", "Testschema");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.DropTable("Testschema", "t1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag = ObjectBrowserPane.DropSchema("Testschema");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to drop the Schema after renaming the schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}		
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_VAL_EditSchema_191"))	
				{
					ObjectBrowserPane.CreateSchema("Testschema");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					QueryEditor.SingleQueryExe("create DATABASE AutotestDB","notreq");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					//Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					ObjectBrowserPane.connectToDB();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SelectConnection();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,"Create Schema testschema1");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.TerminalSetText(2,"ALTER SCHEMA testschema1 RENAME TO Testschema;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					sFlag=MultipleTerminal.TerminalConsoleCopy(2);

					if(sFlag.contains("Executed Successfully..."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the schema in different DB. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("DROP DATABASE AutotestDB;", "Valid");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.DropSchema("Testschema");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("GaussIDE_NON_FUNC_Stress_CreateSchema_233"))
				{
					ObjectBrowserPane.CreateSchema("Testschema");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.newConnection();
					Thread.sleep(GlobalConstants.MedWait);
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName,sConnection);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, sHost);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, sHostPort);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, sDBName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, sUserName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, sPassword);
					BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					Thread.sleep(GlobalConstants.MaxWait);
					QueryEditor.SelectDBConnection();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.TerminalSetText(2,"CREATE SCHEMA TestSchema;");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag=MultipleTerminal.TerminalConsoleCopy(2).replace("\"", "");
					if(sFlag.contains("schema testschema already exists"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create schema with the same name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					//QueryEditor.SelectConnection();
					ObjectBrowserPane.DropSchema("Testschema");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.DropSchema("Testschema1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
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
