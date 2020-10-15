package test_scripts;

import java.awt.event.KeyEvent;

import autoitx4java.AutoItX;



import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import object_repository.TablePropertyElements;
import script_library.DebugOperations;
import script_library.Login;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.TableProperty;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_Tools_DS_220_OBJ_PRPTY {

	public static void main(String sARNumber) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.020.007 - Query Format,Validation check through Menu, Tool bar and Shortcut options
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Object_Property");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Object_Property");
		//Variable Declarations
		String sPropFlag, sFlag,sText1,sText2,sTestCaseID,sExecute,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet
		QueryEditor.ClearEditor();
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2 );
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_004"))
				{

					sPropFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.property1","Table",1);
					if(sPropFlag.equals("Pass"))
					{
						BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
						Thread.sleep(GlobalConstants.MedWait);
						String sTable = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.sTableName);
						if(sTable.trim().equals("property1"))
						{	
							sText1 = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.btnOK);
							sText2 = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.btnTblNameQuoted);
							if(sText1.contains("Finish")||sText2.contains("Next"))
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User able to perform operation in property view mode. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
							else
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}	
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Selected table property window name is not matching. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property window is not displayed. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_005"))//SDV_FUNVAL_DS_OBJ_Prop_007
				{
					String sFlag1, sFlag2,sFlag3,sFlag4, sFlag5, sFlag6,sFlag7;
					sPropFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.property1","Table",1);
					if(sPropFlag.equals("Pass"))
					{
						//Validate Property Window Name
						BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
						Thread.sleep(GlobalConstants.MedWait);
						String sTable = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.sTableName);
						String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
						if(sWinName.equals(sTable.trim()+" Properties"))
							sFlag1="Pass";
						else
							sFlag1="Fail";

						//Validate the tabs
						AutoItX x = new AutoItX();
						TableProperty.WinActivateTableProperty();
						UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 5);
						UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 5);
						Thread.sleep(GlobalConstants.MedWait);
						String sTab1= x.controlCommandCurrentTab("property1 Properties", "","SysTabControl321");
						UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 5);
						UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 5);
						Thread.sleep(GlobalConstants.MedWait);
						String sTab2 = x.controlCommandCurrentTab("property1 Properties", "","SysTabControl321");
						if(sTab1.equals("5")&&sTab2.equals("1"))
							sFlag2="Pass";
						else
							sFlag2="Fail";

						//Validate Buttons
						sText1 = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.btnOK);
						sText2 = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.btnTblNameQuoted);
						if(sText1.contains("Finish")||sText2.contains("Next"))
							sFlag3="Fail";
						else if(sText1.contains("OK"))
							sFlag3="Pass";
						else
							sFlag3="Fail";

						//Validate General Tab
						if(TableProperty.GeneralTabValidation(sTestCaseID, ResultExcel) == "Pass")
							sFlag4="Pass";
						else
							sFlag4="Fail";

						//Validate Columns Tab
						if(TableProperty.ColumnsTabValidation(sTestCaseID, ResultExcel) == "Pass")
							sFlag4="Pass";
						else
							sFlag4="Fail";
						//Validate Data Distribution Tab
						if(TableProperty.DataDistrTabValidation(sTestCaseID, ResultExcel) == "Pass")
							sFlag5="Pass";
						else
							sFlag5="Fail";

						//Validate Table Constraints Tab
						if(TableProperty.TableConstrTabValidation(sTestCaseID, ResultExcel) == "Pass")
							sFlag6="Pass";
						else
							sFlag6="Fail";

						//Validate Indexes Tab
						if(TableProperty.IndexesTabValidation(sTestCaseID, ResultExcel) == "Pass")
							sFlag7="Pass";
						else
							sFlag7="Fail";

						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						if(sFlag1.equals("Pass")&&sFlag2.equals("Pass")&&sFlag3.equals("Pass")&&sFlag4.equals("Pass")&&sFlag5.equals("Pass")&&sFlag6.equals("Pass")&&sFlag7.equals("Pass"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Failure while viewing table properties. Please refer screenshots "+sTestCaseID+".jpg");
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table Property Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_008"))
				{
					AutoItX x = new AutoItX();
					BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
					QueryEditor.SetQuery("tblduplicate");
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					Thread.sleep(GlobalConstants.ModWait);
					//x.mouseMove(414, 153,50);
					x.mouseMove(414, 128,50);
					Thread.sleep(GlobalConstants.ModWait);
					UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					//x.mouseClick("left", 427, 180, 1,50);
					x.mouseClick("left", 460, 155, 1,50);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					Thread.sleep(GlobalConstants.ModWait);
					String sSchema1 = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.sSchemaName);
					BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					Thread.sleep(GlobalConstants.ModWait);
					//x.mouseMove(414, 153,50);
					x.mouseMove(414, 128,50);
					Thread.sleep(GlobalConstants.ModWait);
					//x.mouseClick("left", 422, 202, 1,50);
					x.mouseClick("left", 460, 175, 1,50);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					Thread.sleep(GlobalConstants.ModWait);
					String sSchema2 = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.sSchemaName);
					BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
					if(sSchema1.trim().equals("autotableproperty")&&sSchema2.trim().equals("public"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is not prompted with available schema for same object in different schemas. Please refer screenshots "+sTestCaseID+".jpg");
					}						
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_009"))//covers SDV_FUNINVAL_DS_OBJ_Prop_002
				{
					ObjectBrowserPane.CreateTable("autotableproperty", "cachetbl");
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.DropTable("autotableproperty", "cachetbl");

					sPropFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.cachetbl","Table",1);
					if(sPropFlag.equals("Pass"))
					{
						//Validate Property Window Name
						BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
						Thread.sleep(GlobalConstants.MedWait);
						String sTable = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.sTableName);
						String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						if(sWinName.equals(sTable.trim()+" Properties"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{	
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property is not displayed from cache. Please refer screenshots "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property window is not displayed. Please refer screenshots "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}	

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_010"))//covers SDV_FUNINVAL_DS_OBJ_Prop_003
				{
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.CreateTable("autotableproperty", "cachetbl");
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.DropTable("autotableproperty", "cachetbl");
					ObjectBrowserPane.ObjectBrowserRefresh();
					sPropFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.cachetbl","Table",1);
					if(sPropFlag.equals("Pass"))
					{	
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property is displayed even after droping the table. Please refer screenshots "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
				}


				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_012"))//SDV_FUNVAL_DS_OBJ_Prop_015
				{
					sPropFlag = QueryEditor.objPropertyTerminalInvoke("pg_catalog.pg_app_workloadgroup_mapping","Table",1);
					if(sPropFlag.equals("Pass"))
					{
						//Validate Property Window Name
						BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
						Thread.sleep(GlobalConstants.MedWait);
						String sTable = BaseActions.ControlGetText(TablePropertyElements.sPropertywindow, "", TablePropertyElements.sTableName);
						String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						if(sWinName.equals(sTable.trim()+" Properties"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{	
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property window name is not matching. Please refer screenshots "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property window is not displayed. Please refer screenshots "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_017"))
				{
					String sQuery, sWinName,sCol1,sCol2,sCol3,sCol4,sCol5,sFlag1,sFlag2;
					AutoItX x = new AutoItX();
					BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
					ObjectBrowserPane.DropTable("autotableproperty", "newtbl");
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.CreateTable("autotableproperty", "newtbl");
					ObjectBrowserPane.ObjectBrowserRefresh();
					sPropFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.newtbl","Table",1);
					if(sPropFlag.equals("Pass"))
					{
						sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
						TableProperty.PropertyTabNavigation("COLUMNS");
						sCol1 = x.controlListViewGetText(sWinName, "", "SysListView321","0","Column Name");
						sCol2 = x.controlListViewGetText(sWinName, "", "SysListView321","1","Column Name");
						sCol3 = x.controlListViewGetText(sWinName, "", "SysListView321","2","Column Name");
						sCol4 = x.controlListViewGetText(sWinName, "", "SysListView321","3","Column Name");
						sCol5 = x.controlListViewGetText(sWinName, "", "SysListView321","4","Column Name");
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						if(sCol1.trim().equals("empid")&&sCol2.trim().equals("ename")&&sCol3.trim().equals("salary")&&sCol4.trim().equals("deptno"))
						{
							if(sCol5.trim().equals("deptname"))
								sFlag1="Fail";
							else
								sFlag1="Pass";
						}
						else
							sFlag1="Fail";
					}
					else
						sFlag1="Fail";

					sQuery = "ALTER TABLE autotableproperty.newtbl ADD (deptname text)";
					QueryEditor.SingleQueryExe(sQuery, "Valid");
					ObjectBrowserPane.ObjectBrowserRefresh();
					sPropFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.newtbl","Table",1);
					if(sPropFlag.equals("Pass"))
					{
						sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
						TableProperty.PropertyTabNavigation("COLUMNS");
						sCol1 = x.controlListViewGetText(sWinName, "", "SysListView321","0","Column Name");
						sCol2 = x.controlListViewGetText(sWinName, "", "SysListView321","1","Column Name");
						sCol3 = x.controlListViewGetText(sWinName, "", "SysListView321","2","Column Name");
						sCol4 = x.controlListViewGetText(sWinName, "", "SysListView321","3","Column Name");
						sCol5 = x.controlListViewGetText(sWinName, "", "SysListView321","4","Column Name");
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						if(sCol1.trim().equals("empid")&&sCol2.trim().equals("ename")&&sCol3.trim().equals("salary")&&sCol4.trim().equals("deptno")&&sCol5.trim().equals("deptname"))
							sFlag2="Pass";
						else
							sFlag2="Fail";
					}
					else
						sFlag2="Fail";

					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Newly added columns for a table is not getting displayed. Please refer screenshots "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("autotableproperty", "newtbl");
					ObjectBrowserPane.ObjectBrowserRefresh();
				}

				if(sTestCaseID.equals("SDV_FUNINVAL_DS_OBJ_Prop_001"))//covers SDV_FUNINVAL_DS_OBJ_Prop_004
				{
					String sFlag1,sFlag2,sFlag3,sFlag4,sFlag5,sFlag6;
					BaseActions.SetAutoItOption("WinTitleMatchMode", "2");

					//Obj Prop for Database
					QueryEditor.SetQuery("postgres");
					BaseActions.ControlMouseClick("left",397,156,1,50);
					if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
					{
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						sFlag1 = "Fail";
					}
					else
						sFlag1 = "Pass";

					//Obj Prop for column name
					QueryEditor.SetQuery("ename");
					BaseActions.ControlMouseClick("left",397,156,1,50);
					if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
					{
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						sFlag2 = "Fail";
					}
					else
						sFlag2 = "Pass";

					//Obj Prop for Select KeyWord
					QueryEditor.SetQuery("SELECT");
					BaseActions.ControlMouseClick("left",397,156,1,50);
					if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
					{
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						sFlag3 = "Fail";
					}
					else
						sFlag3 = "Pass";

					//Obj Prop for Insert KeyWord
					QueryEditor.SetQuery("INSERT");
					BaseActions.ControlMouseClick("left",397,156,1,50);
					if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
					{
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						sFlag4 = "Fail";
					}
					else
						sFlag4 = "Pass";

					//Obj Prop for Create KeyWord
					QueryEditor.SetQuery("CREATE");
					BaseActions.ControlMouseClick("left",397,156,1,50);
					if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
					{
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						sFlag5 = "Fail";
					}
					else
						sFlag5 = "Pass";

					//Obj Prop for database.schema.function object
					QueryEditor.SetQuery("postgres.auto.auto1");
					BaseActions.ControlMouseClick("left",501,154,1,50);
					if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
					{
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						sFlag6 = "Fail";
					}
					else
						sFlag6 = "Pass";

					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass")&&sFlag3.equals("Pass")&&sFlag4.equals("Pass")&&sFlag5.equals("Pass")&&sFlag6.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property window name is displayed for database, columns and keywords. Please refer screenshots "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_017_330_new"))
				{
					ObjectBrowserPane.DropTable("autotableproperty", "refreshtbl");
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.CreateTable("autotableproperty", "refreshtbl");
					sPropFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.refreshtbl","Table",1);
					if(sPropFlag.equals("Pass"))
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property window name is displayed before refresh. Please refer screenshots "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
				}	

				if(sTestCaseID.equals("SDV_FUNVAL_DS_OBJ_Prop_013"))
				{
					String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 1);
					String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 2);
					String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 3);
					String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 4);
					String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 2, 5);
					BaseActions.ClearConsole("GLOBAL");
					//BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sToolbarControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,ObjectBrowserElements.iNewConnxcord, ObjectBrowserElements.iNewConnycord);
					BaseActions.CreateNewConnection();
					Login.IDELogin("New Connection", sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("connected successfully"))
					{
						sFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.testtable","Table",2);
						sPropFlag = QueryEditor.objPropertyTerminalInvoke("autotableproperty.testtable","Table",1);
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						if(sPropFlag.equals("Pass")&&sFlag.equals("Fail"))
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						else
						{	
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property window name is not displayed. Please refer screenshots "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Multiple Connection Failed. Please refer screenshots "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNINVAL_DS_OBJ_Prop_005_new"))//SDV_FUNINVAL_DS_OBJ_Prop_006_new
				{
					//Obj Prop for objects after disconnecting connection
					String sFlag1,sFlag2;
					QueryEditor.SetQuery("autotableproperty.property1;autotableproperty.propfunc1()");
					DebugOperations.RemoveConnection();
					DebugOperations.RemoveConnection();
					//Checking for Table 
					BaseActions.ControlMouseClick("left", 552, 160, 1,50);
					if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
					{
						BaseActions.Click(TablePropertyElements.sPropertywindow,"",TablePropertyElements.btnOK);
						sFlag1 = "Fail";
					}
					else
						sFlag1 = "Pass";
					//Checking for Function 
					BaseActions.ControlMouseClick("left", 771, 155, 1,50);
					BaseActions.Click("Data Studio","","[CLASS:SWT_Window0]");
					String sContent = UtilityFunctions.GetClipBoard();
					if(sContent.contains("autotableproperty.propfunc2();"))
						sFlag2="Fail";
					else
						sFlag2 = "Pass";
					if(sFlag1.equals("Pass")&&sFlag2.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{	
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table property window and Functiona property is displayed even after disconnecting connection. Please refer screenshots "+sTestCaseID+".jpg");
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

}
