/************************************************************************************************
TITLE - IDE AUTOMATION SMOKE DRIVER
DESCRIPTION - THIS PROGRAM CONTROLS THE ENTIRE SMOKE AUTOMATION REGRESSION FOR IDE
AR's COVERED ARE LISTED BELOW:
1. AR.Tools.IDE.020.007
2. AR.Tools.IDE.030.004 
3. AR.Tools.IDE.040.003
4. AR.Tools.IDE.020.001
5. AR.Tools.IDE.030.006
6. AR.Tools.IDE.030.002
7. Debug Operations
 **************************************************************************************************/

package ide_driver;

import object_repository.GlobalConstants;
import script_library.Login;
import script_library.ObjectBrowserPane;
import smoke_test.SmokeTestAutomation;
import support_functions.UtilityFunctions;

public class IDESmokeAutomationDriver {

	public static void main(String args[]) throws Exception {

		//Start Time
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Started: "));
		//Loading required DLL for AutoIT

		System.load(GlobalConstants.sJacobDLL);
		// Creating the Test Result File for Reporting
		String sResultExcel = UtilityFunctions.CreateResultFile("SmokeTest","New_Features");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("SmokeTest","New_Features");
		Login.LaunchIDE(GlobalConstants.sIDEPath);
		//Getting Login Credentials from IDE_Smoke_Test_Data file and
		String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 0);
		String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 1);
		String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 2);
		String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 3);
		String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 4);
		String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 5);
		//Login into IDE Tool
		Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
		/*SmokeTestAutomation.Syntax_Highlight_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		Login.IDELogout();
		Thread.sleep(GlobalConstants.MedWait);
		
		
		Login.LaunchIDE(GlobalConstants.sIDEPath);
		//Getting Login Credentials from IDE_Smoke_Test_Data file and
		UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 0);
		UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 1);
		UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 2);
		UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 3);
		UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 4);
		UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 5);
		//Login into IDE Tool
		Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
		SmokeTestAutomation.Query_Format_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		Login.IDELogout();*/
		
		
		
		
		//SmokeTestAutomation.Query_Exe_Status_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Plan_Cost_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Mutiple_Query_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Auto_Filter_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Save_Open_SQL_Smoke("Smoke_Test_Data", "Smoke_Test_Results", sResultExcel, sTextResultFile);
		//SmokeTestAutomation.Import_Table_Data_Smoke("Smoke_Test_Data", "Smoke_Test_Results", sResultExcel, sTextResultFile);
		//SmokeTestAutomation.Export_CSV_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Auto_Suggest_Smoke("Smoke_Test_Data", "Smoke_Test_Results", sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Cancel_Query_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Cancel_Export_Import_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Object_Property_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Edit_Table_Data_Smoke("Smoke_Test_Data", "Smoke_Test_Results", sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Base_Features_Smoke("Smoke_Test_Data","Smoke_Test_Results",sResultExcel,sTextResultFile);
		//SmokeTestAutomation.Debug_Operation_Smoke("Smoke_Test_Data", "Smoke_Test_Results", sResultExcel,sTextResultFile);
		SmokeTestAutomation.V1R3_New_Features("Smoke_Test_Data", "Smoke_Test_Results", sResultExcel,sTextResultFile);


		//Logout from IDE Tool after Execution
		Login.IDELogout();
		//Save the Excel result
		//UtilityFunctions.SaveResult(sResultExcel,"");
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Completed: "));
	}
}
