package test_scripts;

/*************************************************************************
Remove all the previously saved connections from the New Connection Window before execution
 *************************************************************************/

import java.awt.event.KeyEvent;
import java.io.File;

import object_repository.ConsoleResultElements;
import object_repository.DebugElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.SSlElements;
import script_library.Login;
import script_library.ObjectBrowserPane;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class SR_V1R2_DS_Conn_Profile {

	public static void main(String sARNumber) throws Exception {

		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_ConProf");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_ConProf");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
		String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
		String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
		String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
		String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
		String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
        
		UtilityFunctions.deleteDir(new File(GlobalConstants.sProfilePath));
		
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);

			if(sExecute.equalsIgnoreCase("Yes")){

				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "SR_V1R2_DS_ConProf", i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_1")){              //Testcase Mapped PTS_TOR.320.001_Functional_valid_2 and PTS_TOR.320.001_Functional_valid_18

					
					Thread.sleep(GlobalConstants.MedWait);
					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);


					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "0", "Connection Name");

					if(sFlag.equals(sConnection)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Connection name and details are not added in the new connection window. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_3")){                     

					sFlag = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sPassword);
					if(sFlag.isEmpty()){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is visible in the field. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_1_001")){                          //Testcase Mapped PTS_TOR.320.001_Functional_valid_16

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bClear);
					Thread.sleep(GlobalConstants.MedWait);
					String sFlag1 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName);
					String sFlag2 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sHost);
					String sFlag3 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sHostPort);
					String sFlag4 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sDBName);
					String sFlag5 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sUsername);
					String sFlag6 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sPassword);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.lstSavePassword);
					String sFlag7 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.lstSavePassword);

					if(sFlag1.isEmpty() && sFlag2.isEmpty() && sFlag3.isEmpty() && sFlag4.isEmpty() && sFlag5.isEmpty() && sFlag6.isEmpty() && sFlag7.equals("Current Session Only")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Previously saved connection details are not cleared on clicking Clear button. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_5")){                        //Testcase Mapped PTS_TOR.320.001_Functional_valid_13                    

					ObjectBrowserPane.newConnectionDetails("TestConnection1", sHost, sHostPort, sDBName, sUserName, sPassword);

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
					ObjectBrowserPane.newConnection();
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.removeConnectionWindow();

					if(sConsoleOutput.contains("successfull")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to login with the saved connection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_15")){             

					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName, "Connectionnamewithspecialchars#####^^^&&&!!!");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sConsoleOutput.contains("successfull")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create connection with special chars, init caps and lengthy name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_10")){             


					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.removeConnectionWindow();
					Thread.sleep(GlobalConstants.MinWait);

					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sConsoleOutput.contains("Removed")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to delete saved connection from the window. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_12")){             


					Thread.sleep(GlobalConstants.MinWait);
					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "0", "Connection Name");


					if(!sFlag.equals("Connectionnamewithspecialchars#####^^^&&&!!!")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Connection details still persist even after removing the connection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_14")){             


					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName, "Connectionnamewithspecialchars#####^^^&&&!!!");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.removeConnectionWindow();


					if(sConsoleOutput.contains("successfull")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create connection with special chars, init caps and lengthy name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_6")){             

					String sConnection2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 0);
					String sHost2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 1);
					String sHostPort2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 2);
					String sDBName2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 3);
					String sUserName2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 4);
					String sPassword2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 5, 5);

					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					ObjectBrowserPane.newConnectionDetails(sConnection2, sHost2, sHostPort2, sDBName2, sUserName2, sPassword2);

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");

					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);

					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "1", "Connection Name");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					ObjectBrowserPane.removeConnectionWindow();

					if(sConsoleOutput.contains("successfull") && sFlag.equals(sConnection2)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The connection details are not added for multiple connections to same nodes with different users. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_7")){          //Testcase mapped PTS_TOR.320.001_Functional_valid_20            

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.newConnection();
					ObjectBrowserPane.sslLogin("NO");

					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
					ObjectBrowserPane.newConnection();
					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "1", "Connection Name");
					Thread.sleep(GlobalConstants.MedWait);

					if(sConsoleOutput.contains("successfull") && sFlag.equals(SSlElements.sConnectionName)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to login with the saved connection. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_26")){                     

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					String sFlag1 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sPassword);
					String sFlag2 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", SSlElements.sSSLPassword);

					if(sFlag1.isEmpty() && sFlag2.isEmpty()){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is saved for SSL even when it was not saved while login. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_19")){                      


					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(LoginElements.wDBConnection, "", SSlElements.bEnableSSL);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(LoginElements.wDBConnection, "", SSlElements.bEnableSSL);


					if(BaseActions.ObjExists(LoginElements.wDBConnection, "", SSlElements.sSSlError)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						Thread.sleep(GlobalConstants.MinWait);
						ObjectBrowserPane.newConnection();
						UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
						Thread.sleep(GlobalConstants.MinWait);
						ObjectBrowserPane.removeConnectionWindow();
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"SSL Connection allowed for another instance. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_8")){                      

					String sConnection2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 30, 0);
					String sHost2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 30, 1);
					String sHostPort2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 30, 2);
					String sDBName2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 30, 3);
					String sUserName2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 30, 4);
					String sPassword2 = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 30, 5);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.newConnection();
					ObjectBrowserPane.newConnectionDetails(sConnection2, sHost2, sHostPort2, sDBName2, sUserName2, sPassword2);

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");

					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);

					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "1", "Connection Name");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);


					if(sConsoleOutput.contains("successfull") && sFlag.equals(sConnection2)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						ObjectBrowserPane.removeConnectionWindow();
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The connection details are not added for multiple connections to different nodes in same DB. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_9")){                      


					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.newConnection();
					ObjectBrowserPane.newConnectionDetails("TestConnection", sHost, sHostPort, sDBName, sUserName, sPassword);

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");

					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);

					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "1", "Connection Name");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.removeConnectionWindow();

					if(sConsoleOutput.contains("successfull") && sFlag.equals("TestConnection")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The connection details are not added for multiple connections to same node with different profile name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_Invalid_1")){       //Testcase mapped PTS_TOR.320.001_Functional_Invalid_4                      


					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.newConnection();
					ObjectBrowserPane.newConnectionDetails("TestConnection", sHost, sHostPort, sDBName, sUserName, "WrongPassword");

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					BaseActions.Winwait(LoginElements.wWrongPassword);
					BaseActions.Click(LoginElements.wWrongPassword, "", LoginElements.bWrongPswdOk);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);

					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "1", "Connection Name");
					Thread.sleep(GlobalConstants.MedWait);

					if(!sFlag.equals("TestConnection")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The connection details are added for unsuccessful connections. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_Invalid_3")){

					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.disconnectAll();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.newConnection();
					Thread.sleep(GlobalConstants.MedWait);
					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "0", "Connection Name");
					Thread.sleep(GlobalConstants.MinWait);


					if(sFlag.equals(sConnection)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						Thread.sleep(GlobalConstants.MinWait);
						ObjectBrowserPane.objectBrowserExpansion("SINGLE");
						UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The connection details are removed from the new connection window after disconnecting from the object browser. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}


				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_Invalid_2")){

					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 2);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					AutoItX x = new AutoItX();
					sFlag = x.controlListViewGetText(LoginElements.wDBConnection, "", "SysListView321", "0", "Connection Name");

					if(sFlag.equals(sConnection)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The connection details are removed from the new connection window on removing from the object browser. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}


				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_24")){               //Testcase mapped PTS_TOR.320.001_Functional_valid_22

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.newConnection();
					ObjectBrowserPane.newConnectionDetails("CurrentConnection", sHost, sHostPort, sDBName, sUserName, sPassword);
					BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);

					Thread.sleep(GlobalConstants.MaxWait);

					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					ObjectBrowserPane.newConnectionDetails("TempConnection", sHost, sHostPort, sDBName, sUserName, sPassword);
					BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);	                        			
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					ObjectBrowserPane.disconnectAll();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					boolean b1 = BaseActions.WinExists(DebugElements.sDebugPasswordWindow);

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("DOUBLE");
					ObjectBrowserPane.disconnectAll();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserExpansion("DOUBLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					boolean b2 = BaseActions.WinExists(DebugElements.sDebugPasswordWindow);

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);

					ObjectBrowserPane.objectBrowserRefresh("TRIPLE");
					ObjectBrowserPane.disconnectAll();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserExpansion("TRIPLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					boolean b3 = BaseActions.WinExists(DebugElements.sDebugPasswordWindow);

					if(b1==false && b2==false && b3==true){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);
						System.out.println(sTestCaseID + "passed");
					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password window is prompted for 'Current Session' and 'Permanent' when disconnected and then connected again. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_25")){                                        //Testcase mapped PTS_TOR.320.001_Functional_valid_23 and PTS_TOR.320.001_Functional_valid_17


					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					ObjectBrowserPane.newConnection();
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);

					AutoItX x = new AutoItX();
					boolean b1 = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
					ObjectBrowserPane.newConnection();
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					boolean b2 = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.removeTempConnectionWindow();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					boolean b3 = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);

					if(b1==true && b2==false && b3==false){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						Thread.sleep(GlobalConstants.MedWait);
						ObjectBrowserPane.removeTempConnectionWindow();
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						System.out.println(sTestCaseID + "passed");
					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is not prompted for 'Current Session' when the instance is closed and launched again. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_27")){

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.newConnection();
					BaseActions.Winwait(LoginElements.wDBConnection);
					ObjectBrowserPane.sslLogin("SESSION");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.objectBrowserRefresh("DOUBLE");
					ObjectBrowserPane.disconnectAll();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserExpansion("DOUBLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					boolean b = BaseActions.WinExists(DebugElements.sDebugPasswordWindow);

					if(b == false){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password window is prompted for 'Current Session' for SSL when disconnected and then connected again. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.320.001_Functional_valid_28")){

					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					ObjectBrowserPane.newConnection();
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					String sFlag1 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.sPassword);
					String sFlag2 = BaseActions.ControlGetText(LoginElements.wDBConnection, "", SSlElements.sSSLPassword);

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
					if(sFlag1.isEmpty() && sFlag2.isEmpty()){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is saved for SSL login 'CURRENT SESSION' on launching the instance again. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}
				}

			}
		}

		for(int i=1;i<=21;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}

	}

}
