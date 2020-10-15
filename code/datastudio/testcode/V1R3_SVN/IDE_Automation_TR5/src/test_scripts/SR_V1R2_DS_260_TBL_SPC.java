package test_scripts;

import java.awt.event.KeyEvent;

import object_repository.CreateTableWizardElements;
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import object_repository.TablespaceElements;
import script_library.CreateTableWizardFunctions;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.TablespaceFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_260_TBL_SPC {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_260_TBL_SPC");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_260_TBL_SPC");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag1,sFlag2,sStatus,sUser;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			sUser = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4); 
			if(sExecute.equals("Yes"))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);

				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_2")) //Testcases Covered PTS_TOR.260.003_Functional_valid_1,PTS_TOR.260.003_Functional_valid_3
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					TablespaceFunctions.tablespace_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_F5,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_F5,1);
					Thread.sleep(GlobalConstants.MinWait);
					//BaseActions.Winwait("Refresh in progress");
					/*UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE,1);*/
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.tablespace_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.openTableSpace();
					Thread.sleep(GlobalConstants.MedWait);
					boolean b = BaseActions.WinExists("Create Tablespace");
					if(sFlag1.contains("[INFO] Tablespaces refreshed")||b)
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5," Refresh and tablespace options are not available.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	

					TablespaceFunctions.button("CANCEL");
				}

				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_4"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					TablespaceFunctions.tablespace_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.openTableSpace();
					BaseActions.Focus(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceCombobox);
					sFlag1 = BaseActions.ControlGetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceCombobox);
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.tablespaceCreation("FILEPATH", "HDFS");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = BaseActions.ControlGetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceCombobox);
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.equals("General")&&sFlag2.equals("HDFS"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"HDFS and General options are not available.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					TablespaceFunctions.button("CANCEL");
				}
				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_13")) //Test cases covered PTS_TOR.260.003_Functional_valid_12,PTS_TOR.260.003_Functional_valid_5
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					TablespaceFunctions.tablespace_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.openTableSpace();
					BaseActions.Winwait(TablespaceElements.wTablespaceTitle);
					TablespaceFunctions.tablespaceCreation("NAME", "Auto1");
					TablespaceFunctions.tablespaceCreation("LOCATION", "/home/"+sUser+"/test/demo1/demo2/DEMO3/AUTO1/AUto2/AUTO3");
					Thread.sleep(GlobalConstants.MedWait);
					TablespaceFunctions.maxSize("1000", 'M');
					TablespaceFunctions.button("OK");
					BaseActions.Winwait("Tablespace Successfully Created");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag1.contains("[INFO] Created 'Auto1' tablespace successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create a tablespace with lenghty path.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_7")) //Testcase covered PTS_TOR.260.003_Functional_valid_24,PTS_TOR.260.003_Functional_valid_16
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					sFlag1 = QueryEditor.SingleQueryExe("CREATE TABLESPACE general_auto LOCATION '/home/"+sUser+"/test' MAXSIZE '1024000 K' WITH ( filesystem=general )", "Valid");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag1.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create a tablespace through SQL terminal.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MinWait);
				}
				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_17"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					TablespaceFunctions.tablespace_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.showDDL();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = UtilityFunctions.GetClipBoard();
					if(sFlag2.contains("CREATE TABLESPACE auto1 LOCATION '/home/"+sUser+"/test/demo1/demo2/DEMO3/AUTO1/AUto2/AUTO3' MAXSIZE '1024000 K' WITH ( filesystem=general )"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"DDL shown is incorrect.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					MultipleTerminal.CloseTerminal(2);
				}

				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_18")) //Testcase Covered PTS_TOR.260.003_Functional_valid_23
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,98,44); //Tablespace Co-ordinates
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.renameTablespaceObjectBrowser("auto_space");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag1.contains("[INFO] Renamed 'auto1' table to 'auto_space'."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Rename through object browser is un-successfull.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_29")) // TETSCASE COVERED PTS_TOR.260.003_Functional_valid_27
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.CreateTableWizard();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Focus(CreateTableWizardElements.wTitle, "", "ComboBox2");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "","ComboBox2");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_A,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "", "ComboBox2");
					if(sFlag1.equals("auto_space"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Tablespace name is not reflecting in the create wizard.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE,1);
				}

				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_20"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					TablespaceFunctions.tablespace_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryEditor.SingleQueryExe("ALTER TABLESPACE auto_space RENAME TO auto;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					if(sFlag1.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"unable to rename tablespace through sql terminal.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_21"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					TablespaceFunctions.tablespace_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,98,44); //Tablespace Co-ordinates
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.setTableSpaceOption("20", "20");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,98,44); //Tablespace Co-ordinates
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.showDDL();
					sFlag2 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("[INFO] Tablespace options changed for 'auto' tablespace.")&&sFlag2.contains("random_page_cost=20,seq_page_cost=20"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The set table space is not reflecting in the show DDL.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.ClearConsole("GLOBAL");
				}
				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_22"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,98,44); //Tablespace Co-ordinates
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.setTableMaxsize("200",'M');
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,98,44); //Tablespace Co-ordinates
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.showDDL();
					sFlag2 = UtilityFunctions.GetClipBoard();
					if(sFlag1.contains("[INFO] Maxsize for the tablespace 'auto' changed to 200M.")&&sFlag2.contains("MAXSIZE '204800 K'"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The set table space is not reflecting in the show DDL.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_25")) //General_auto tablespace is dropped --only auto tablespace is left
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,98,44); //Tablespace Co-ordinates
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.dropTableSpaceObjeBrowser();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1=QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag1.contains(" [INFO] Drop Tablespace 'general_auto' is successful."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to drop the tablespace through object Browser.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_30"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.CreateTableWizard();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Focus(CreateTableWizardElements.wTitle, "", "ComboBox2");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "","ComboBox2");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_G,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_G,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "", "ComboBox2");
					if(!sFlag1.equals("general_auto"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Tablespace name is reflecting in the create wizard even the tablespace is dropped.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE,1);
				}

				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_28"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.CreateTableWizard();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Focus(CreateTableWizardElements.wTitle, "", "ComboBox2");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "","ComboBox2");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_A,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "", "ComboBox2");
					if(sFlag1.equals("auto"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Tablespace name is not reflecting in the create wizard.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE,1);
				}
				if(sTestCaseID.equals("PTS_TOR.260.003_Functional_valid_33"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					TablespaceFunctions.tablespace_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.openTableSpace();
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.tablespaceCreation("NAME", "AUTO_UNLIMITED");
					TablespaceFunctions.tablespaceCreation("LOCATION", "/home/"+sUser+"/unlimited/");
					TablespaceFunctions.tablespaceCreation("UNLIMITEDSIZE", "");
					TablespaceFunctions.button("OK");
					BaseActions.Winwait("Tablespace Successfully Created");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,98,44); //Tablespace Co-ordinates
					TablespaceFunctions.openTableSpace();
					Thread.sleep(GlobalConstants.MinWait);
					TablespaceFunctions.tablespaceCreation("NAME", "AUTO_EMPTY");
					TablespaceFunctions.tablespaceCreation("LOCATION", "/home/"+sUser+"/empty/");
					TablespaceFunctions.maxSize("", 'K');
					TablespaceFunctions.button("OK");
					BaseActions.Winwait("Tablespace Successfully Created");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag1.contains("[INFO] Created 'AUTO_UNLIMITED' tablespace successfully.")&&sFlag1.contains("[INFO] Created 'AUTO_EMPTY' tablespace successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Tablespace is not created with unlimited option.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					//					MultipleTerminal.CloseTerminal(2);
					//				Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ClearEditor();
					QueryEditor.SetFunction("DROP TABLESPACE auto_unlimited;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("DROP TABLESPACE auto_empty;");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SetFunction("DROP TABLESPACE auto;");
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
				}
			}
		}
		for(int i=1;i<=iRowCount;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus = UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}