package test_scripts;

import java.awt.event.KeyEvent;

import object_repository.ConsoleResultElements;
import object_repository.ExecQueryElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import script_library.DataMgmtFunctions;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class Base_DataMgnt {

	public static void main(String sARNumber) throws Exception {

		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Database_Mgnt");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Database_Mgnt");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "Database_Mgnt", i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				if(sTestCaseID.equals("GaussIDE_SMOKE_CreateDB_001"))
				{
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					sFlag=ObjectBrowserPane.createDBObjectBrowser("testdb","", "No");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.contains("[INFO] Created 'testdb' database successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Creation of database Failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_SMOKE_EditDB_002"))
				{
					sFlag = ObjectBrowserPane.RenameDB("testdb", "testdb1");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Renaming of database Failed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("GaussIDE_FUNCERR_CreateDB_180")) // test cases covered -->GaussIDE_FUNC_CreateDB_003
				{
					sFlag = ObjectBrowserPane.CreateDB("CreateDBswithname*containing 64ormorecharactersstartingwithspecialchar");
					Thread.sleep(GlobalConstants.MedWait);	
					if(sFlag.equals("Failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create Database with invalid name Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNCERR_CreateDB_181"))
				{
					sFlag = ObjectBrowserPane.CreateDB("testdb1");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.equals("Failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to create database with same name.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNCERR_CreateDB_182")) //Test cases covered -->GaussIDE_FUNC_EditDB_004
				{

					sFlag = ObjectBrowserPane.RenameDB("testdb1", "te*st_dba");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.equals("Failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to rename database with invalid input name.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*test cases covered
				 * 1.GaussIDE_Func_Val_EditDB_195
				 * 2.GaussIDE_Func_Val_EditDB_196
				 * 3.GaussIDE_Func_Val_EditDB_197
				 * 4.GaussIDE_FuncUsability_CreateDB_239
				 * 5.GaussIDE_Func_Val_CreateDB_240
				 */

				if(sTestCaseID.equals("GaussIDE_FUNCERR_CreateDB_183"))
				{
					ObjectBrowserPane.CreateDB("testdb2");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag = ObjectBrowserPane.RenameDB("testdb2", "testdb1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.RenameDB("testdb1", "testdb");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.equals("Failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to rename database with same name.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
					ObjectBrowserPane.DropDB("testdb2");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNCERR_CreateDB_184"))
				{
					sFlag = ObjectBrowserPane.RenameDB("testdb", "testdb");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.equals("Failed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to rename database with same name.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
				}
				/* Test case covered
				 * 1.GaussIDE_Func_Val_CreateDB_223
				 * 
				 */

				if(sTestCaseID.equals("GaussIDE_SMOKE_ViewDB_008"))
				{

					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.connectToMultipleDB("DOUBLE");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = DataMgmtFunctions.openDBProperty();
					Thread.sleep(GlobalConstants.MedWait);
					DataMgmtFunctions.closeDBProperty();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SelectDBConnection();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					//QueryEditor.ClearEditor();
					MultipleTerminal.SetFunction(2,"SELECT oid as oid, datname AS name, pg_encoding_to_char(encoding) as encoding,");
					MultipleTerminal.SetFunction(2,"datallowconn as allow_conn, datconnlimit as max_conn_limit,");
					MultipleTerminal.SetFunction(2,"(select spcname from pg_tablespace where oid=dattablespace)");
					MultipleTerminal.SetFunction(2,"as default_tablespace, datcollate as collation,");
					MultipleTerminal.SetFunction(2,"datctype as char_type from pg_database where datname ='testdb'");
					QueryEditor.ExecuteButton();
					String s2 = MultipleTerminal.TerminalResultTabOperations(2, "COPY");
					if(s2.contains("oid"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The OID is not Matching.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
				}

				if(sTestCaseID.equals("GaussIDE_Smoke_DropDB_005"))

				{
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("TRIPLE");
					sFlag= ObjectBrowserPane.DropDBObjectBrowserNotConnected();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					if(sFlag.contains("[INFO] Dropped testdb database successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Dropping of Database is not Successfull. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_DropDB_006GaussIDE_FUNC_DropDB_007"))
				{
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					ObjectBrowserPane.CreateDB("testdb1");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.connectToMultipleDB("DOUBLE");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectDBConnection();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SetFunction(2,"CREATE SCHEMA testschema1;");
					MultipleTerminal.SetFunction(2,"CREATE TABLE testschema1.t1(empid bigint,ename text,salary bigint,deptno bigint);");
					MultipleTerminal.SetFunction(2,"INSERT INTO testschema1.t1 VALUES (10,'e1',111,10);");
					MultipleTerminal.SetFunction(2,"CREATE TABLE testschema1.uniquetable(auto1 bigint,auto2 bigint UNIQUE CHECK(123));");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.ModWait);
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.CloseTerminal(2);
					ObjectBrowserPane.objectBrowserRefresh("TRIPLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ClearEditor();
					sFlag = QueryEditor.SingleQueryExe("Drop database testdb1;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.contains("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Dropping of Database is not Successfull. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Dropping of Database is not Successfull with tables having index Constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}

				}
				if(sTestCaseID.equals("GaussIDE_FUNC_DropDB_016"))
				{
					ObjectBrowserPane.createDBObjectBrowser("testdb1","","No");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.connectToMultipleDB("DOUBLE");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SelectDBConnection();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.SetFunction(2,"CREATE SCHEMA testschema1;");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.CloseTerminal(2);
					ObjectBrowserPane.objectBrowserRefresh("TRIPLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ClearEditor();
					sFlag = QueryEditor.SingleQueryExe("Drop database testdb1;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");	
					if(sFlag.contains("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Dropping of Database with one schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateDB_188"))
				{
					ObjectBrowserPane.createDBObjectBrowser("testdb","","No");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("TRIPLE");
					sFlag = ObjectBrowserPane.RenameDBObjectBrowser("testdb1");
					if(sFlag.contains("[INFO] Renamed testdb database to 'testdb1'."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Dropping of Database with one schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}

					ObjectBrowserPane.objectBrowserRefresh("TRIPLE");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.DropDBObjectBrowserNotConnected();
				}

				if(sTestCaseID.equals("GaussIDE_SMOKE_CreateDB_001_217")) 
				{
					ObjectBrowserPane.createDBObjectBrowser("testdb1","","No");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.newConnection();
					String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 7, 0);
					String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 7, 1);
					String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 7, 2);
					String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 7, 3);
					String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 7, 4);
					String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 7, 5);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName,sConnection);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, sHost);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, sHostPort);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, sDBName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, sUserName);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, sPassword);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					Thread.sleep(GlobalConstants.MaxWait);
					QueryEditor.SelectDBConnection();
					MultipleTerminal.OpenNewTerminal();
					Thread.sleep(GlobalConstants.MedWait);
					MultipleTerminal.TerminalSetText(2,"CREATE DATABASE testdb1;");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag=MultipleTerminal.TerminalConsoleCopy(2);
					//sFlag= ObjectBrowserPane.CreateDB("testdb1");
					if(sFlag.contains("Created 'testdb1' database successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to create the schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.DropDB("testdb1");
				}
			}
		}
		for(int i=1;i<=14;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}









