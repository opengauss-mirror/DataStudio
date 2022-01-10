package test_scripts;

import java.awt.event.KeyEvent;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import script_library.CreateTableWizardFunctions;
import script_library.DataMgmtFunctions;
import script_library.Login;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class Base_TableMgnt {

	public static void main(String sARNumber) throws Exception {


		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Table_Mgnt");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Table_Mgnt");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sFlag1,sStatus,sInputQuery;
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

				if(sTestCaseID.equals("GaussIDE_SMOKE_EditTable_020_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					ObjectBrowserPane.CreateTable("auto", "Testtable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					sFlag = ObjectBrowserPane.AlterTable("auto", "TestTable", "TestTablea");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("GaussIDE_SMOKE_DropTable_021_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					sFlag = ObjectBrowserPane.DropTable("auto", "TestTablea");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					if(sExecute.equals("Yes"))
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to drop the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_022_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					ObjectBrowserPane.CreateTable("auto", "PK_Testtable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.InsertTable("auto", "PK_Testtable", 10);
					sFlag= QueryResult.ReadConsoleOutput("TERMINAL");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.contains("Executed Successfully"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "PK_Testtable");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

				}
				/**
				 * Testcase covered GaussIDE_SMOKE_ViewTable_023_070
				 */
				if(sTestCaseID.equals("GaussIDE_SMOKE_ViewTable_023_070")) //Needs to be modified
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.CreateTable("auto", "test");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = DataMgmtFunctions.openDBProperty();
					Thread.sleep(GlobalConstants.MinWait);
					DataMgmtFunctions.closeTableProperty();
					if(sFlag.contains("OID::"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					ObjectBrowserPane.DropTable("auto", "test");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_025_070GaussIDE_FUNC_CreateTable_026_070GaussIDE_FUNC_CreateTable_027_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,3,"Yes");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("TestTable", "No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.TableSpace("pg_default");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("C1", "No");
					CreateTableWizardFunctions.DataType("Varchar");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("C2", "No");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.contains("TABLESPACE pg_default")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The TableSpace Name is different Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to create the table Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5," Unable to created the table without Quoted option.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "TestTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_028_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("TestTable","Yes");
					CreateTableWizardFunctions.AddCloumn("C2", "No");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("\"TestTable\"")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"The TableName is displaying without quotation Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "\"TestTable\"");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_029_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("unloggedtable", "No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.TableType("UNLOGGED");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("Emp","No");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("CREATE UNLOGGED TABLE")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"The TableName is displaying without Unlogged table type.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					ObjectBrowserPane.DropTable("auto", "unloggedtable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_030_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("TestTable","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("emp","No");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"Unable to Create table without Unlogged table type.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}
					ObjectBrowserPane.DropTable("auto", "testtable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");						

				}			

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_031_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("ExsistsTable","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("IFEXSISTS");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("emp","No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("CREATE TABLE IF NOT EXISTS")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"Unable to create the table with exsists option.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "ExsistsTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}		

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_032_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("OIDSTable","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("OIDS");	
					CreateTableWizardFunctions.AddCloumn("emp","No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("WITH (OIDS=TRUE);")&&sFlag1.contains("[INFO] Table created successfully."))

					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"Unable to create the table with OIDS option.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "OIDSTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_033_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("IFOIDSTable","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("OIDS");
					CreateTableWizardFunctions.Button("IFEXSISTS");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("EMP","No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("CREATE TABLE IF NOT EXISTS")&&sFlag.contains("WITH (OIDS=TRUE);")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"Unable to create the table with OIDS and IF EXSITS option.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "IFOIDSTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_034_070"))//testcases covered GaussIDE_FUNC_CreateTable_036_070,GaussIDE_FUNC_CreateTable_037_070
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("filltable","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.fillFactor("50");
					CreateTableWizardFunctions.AddCloumn("EMP","No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("WITH (fillfactor=50);")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"Unable to create the table with fill factor option.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "filltable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_035_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("DescTable","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters.");
					CreateTableWizardFunctions.TableDesc("Create a table with table name as TestTable and Description of table text ");
					Thread.sleep(GlobalConstants.MaxWait);
					CreateTableWizardFunctions.AddCloumn("EMP","No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("COMMENT ON TABLE")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"Description is not Matching.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "DescTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_038_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("ColTable","No");
					CreateTableWizardFunctions.AddCloumn("TestColumn","Yes");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("\"TestColumn\"")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with quoted option.Please refer screenshot" +sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "ColTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_039_070")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("ArrayTable","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("Emp", "No");
					CreateTableWizardFunctions.ArrayDim("1");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.ArrayDim("2");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("Emp2", "No");
					CreateTableWizardFunctions.ArrayDim("10");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("Emp3", "No");
					CreateTableWizardFunctions.ArrayDim("100");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Emp1 bigint[][]")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create column with array dimension.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "ArrayTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				/**
				 * Test cases Covered GaussIDE_FUNC_CreateTable_040_070,GaussIDE_FUNC_CreateTable_041_070
				 */

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_043_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("precision","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.DataType("Varchar");
					CreateTableWizardFunctions.Precision("2");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp2", "No");
					CreateTableWizardFunctions.DataType("Decimal");
					CreateTableWizardFunctions.Precision("2");
					CreateTableWizardFunctions.Scale("2");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("Finish");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Emp1 varchar(2)")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create column with precision and scale Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "precision");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				/**
				 * Test cases Covered GaussIDE_FUNC_CreateTable_042_070
				 */
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_044_070"))	
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("MultiTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.DataType("Varchar");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp2", "No");
					CreateTableWizardFunctions.DataType("Decimal");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp3", "No");
					CreateTableWizardFunctions.DataType("Char");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp4", "No");
					CreateTableWizardFunctions.DataType("text");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with multiple columns.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "MultiTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_045_070")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("NotNullTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.Columnconstarints("NOTNULL");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Emp1 bigint NOT NULL")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with NotNull constraint.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "NotNullTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_046_070")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("UniqueTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.Columnconstarints("Unique");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Emp1 bigint UNIQUE")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with not constraint.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "UniqueTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_047_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("DefaultTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.DataType("CHAR");
					CreateTableWizardFunctions.DefaultConstraint("HELLO", "Yes");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp2", "No");
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.DefaultConstraint("100","No");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp3", "No");
					CreateTableWizardFunctions.DataType("BOOLEAN");
					CreateTableWizardFunctions.DefaultConstraint("TRUE","No");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Emp2 integer DEFAULT 100")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with defalut constraint.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "DefaultTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				/**
				 * Testcase Covered GaussIDE_FUNC_CreateTable_049_070
				 */

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_048_070")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("QuotedTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.DataType("CHAR");
					CreateTableWizardFunctions.DefaultConstraint("Hello", "Yes");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp2", "No");
					CreateTableWizardFunctions.DataType("TEXT");
					CreateTableWizardFunctions.DefaultConstraint("'Hi'", "No");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("'Hello'")&&sFlag.contains("'Hi'")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with Quoted/not Quoted defalut constraint .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "QuotedTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				/**
				 * Testcase Covered GaussIDE_FUNC_CreateTable_051_070
				 */

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_050_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("ConstraintTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.CheckConstraint("100");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp2", "No");
					CreateTableWizardFunctions.DataType("BOOLEAN");
					CreateTableWizardFunctions.CheckConstraint("true");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp3", "No");
					CreateTableWizardFunctions.DataType("VARCHAR");
					CreateTableWizardFunctions.CheckConstraint("10");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp4", "No");
					CreateTableWizardFunctions.DataType("TEXT");
					CreateTableWizardFunctions.CheckConstraint("15");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("Emp5", "No");
					CreateTableWizardFunctions.DataType("TEXT");
					CreateTableWizardFunctions.Button("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Emp2 boolean CHECK ( true )")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with column constraint .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "ConstraintTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_064_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("CheckTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.ConstraintName("test");
					CreateTableWizardFunctions.ConstraintExpression("emp1>100");
					CreateTableWizardFunctions.ConstraintButton("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("CONSTRAINT test CHECK (emp1>100)")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with Check constraint .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "CheckTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				/**
				 * Testcase Covered GaussIDE_FUNC_CreateTable_065_070
				 */
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_066_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					CreateTableWizardFunctions.TableName("uniqueTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.TableConstraint("UNIQUE");
					CreateTableWizardFunctions.ConstraintName("Test1");
					CreateTableWizardFunctions.uniqueConstriantcolumn("ADD");
					CreateTableWizardFunctions.ConstraintButton("ADD");
					CreateTableWizardFunctions.ConstraintName("Test2");
					CreateTableWizardFunctions.uniqueConstriantcolumn("ADD");
					CreateTableWizardFunctions.constraintTableSpace("PG_DEFAULT");
					CreateTableWizardFunctions.ConstraintButton("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("CONSTRAINT Test1 UNIQUE (Emp1)")&&sFlag.contains("CONSTRAINT Test2 UNIQUE (Emp1)USING INDEX TABLESPACE pg_default)")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with Unique constraint .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "uniqueTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_067_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("fillfactorTable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Emp1", "No");
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.TableConstraint("UNIQUE");
					CreateTableWizardFunctions.ConstraintName("Test1");
					CreateTableWizardFunctions.uniqueConstriantcolumn("ADD");
					CreateTableWizardFunctions.tableFillFactor("100");
					CreateTableWizardFunctions.tableDeffered("DEFERABLE");
					CreateTableWizardFunctions.tableDeffered("INDEFERABLE");
					CreateTableWizardFunctions.ConstraintButton("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("CONSTRAINT Test1 UNIQUE (Emp1)WITH (fillfactor=100) DEFERRABLE INITIALLY DEFERRED")&&sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with fillfactor .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "fillfactorTable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_068_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,3,"Yes");
					CreateTableWizardFunctions.CreateTableWizard();
					CreateTableWizardFunctions.TableName("defertable","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("E1", "No");
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("E2", "No");			
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("E3", "No");
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.TableConstraint("UNIQUE");
					CreateTableWizardFunctions.ConstraintName("Test1");
					CreateTableWizardFunctions.uniqueConstriantcolumn("ADD");
					CreateTableWizardFunctions.tableDeffered("DEFERABLE");
					CreateTableWizardFunctions.ConstraintButton("ADD");
					CreateTableWizardFunctions.uniqueConstriantcolumn("ADD");
					CreateTableWizardFunctions.tableDeffered("DEFERABLE");
					CreateTableWizardFunctions.tableDeffered("INDEFERABLE");
					CreateTableWizardFunctions.ConstraintButton("ADD");
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("CONSTRAINT Test1 UNIQUE (E1) DEFERRABLE INITIALLY IMMEDIATE,")&&sFlag.contains("UNIQUE (E1) DEFERRABLE INITIALLY DEFERRED)")&& sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with DEFER/UNIQUE .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "defertable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

				}

			}
		}
		for(int i=1;i<=28;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
		
		TableMgnt_New.main("TableMgnt_New");
	}
}



