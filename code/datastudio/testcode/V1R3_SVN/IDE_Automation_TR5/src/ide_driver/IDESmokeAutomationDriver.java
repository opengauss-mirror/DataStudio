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
		
		SmokeTestAutomation.V1R3_New_Features("Smoke_Test_Data", "Smoke_Test_Results", sResultExcel,sTextResultFile);


		//Logout from IDE Tool after Execution
		Login.IDELogout();
		//Save the Excel result
		//UtilityFunctions.SaveResult(sResultExcel,"");
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Completed: "));
	}
}
