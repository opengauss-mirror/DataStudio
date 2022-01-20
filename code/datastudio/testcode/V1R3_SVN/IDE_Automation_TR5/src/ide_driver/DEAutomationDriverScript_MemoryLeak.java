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
TITLE - IDE AUTOMATION DRIVER
DESCRIPTION - THIS PROGRAM CONTROLS THE ENTIRE SDV AUTOMATION REGRESSION FOR IDE
AR's COVERED ARE LISTED BELOW:
1. AR.Tools.IDE.020.007
2. AR.Tools.IDE.030.004 
3. AR.Tools.IDE.040.003
4. AR.Tools.IDE.020.001
5. AR.Tools.IDE.030.006
6. AR.Tools.IDE.030.002
EACH AR IS CONTROLLED VIA IDE_DRIVER TEST DATA SHEET IN IDE_Functional_Test_Data.xlsx DATA SHEET
 **************************************************************************************************/

package ide_driver;

import object_repository.GlobalConstants;
import script_library.Login;
import support_functions.UtilityFunctions;
import test_scripts.AR_Tools_IDE_020_001_AUT_FTR;
import test_scripts.AR_Tools_IDE_020_007_QRY_FMT;
import test_scripts.AR_Tools_IDE_030_002_MUL_QRY;
import test_scripts.AR_Tools_IDE_030_004_EXE_STS;
import test_scripts.AR_Tools_IDE_030_006_PLN_CST;
import test_scripts.AR_Tools_IDE_040_003_EXP_CSV;
import test_scripts.Base_DataMgnt;
import test_scripts.Base_SchemaMgnt;
import test_scripts.Base_TableMgnt;
import test_scripts.DS_STRY_010_001_01;
import test_scripts.DS_STRY_020_001_01;
import test_scripts.Edit_Data_Filter_Wizard;
import test_scripts.Edit_Table_Data;
import test_scripts.PTS_TOR_080_001_CAN_EXP;
import test_scripts.SR_Tools_DS_010_CAN_QRY;
import test_scripts.SR_Tools_DS_220_OBJ_PRPTY;
import test_scripts.SR_V1R2_DS_001_STR1;
import test_scripts.PTS_SR_V1R2_DS_001_STR_3_IMP_TBL_DAT;
import test_scripts.SR_V1R2_DS_001_STR_2_SVE_OPN_SQL;
import test_scripts.SR_V1R2_DS_230_MUL_SQL;
import test_scripts.SR_V1R2_DS_260_VIEW;

public class DEAutomationDriverScript_MemoryLeak {

	public static void main(String args[]) throws Exception
	{
		//Loading required DLL for AutoIT
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Started: "));
		System.load(GlobalConstants.sJacobDLL);
		//Launching the IDE Tool Application
		//Login.LaunchIDE(GlobalConstants.sIDEPath);// *****
		//Getting Login Credentials from IDE_Smoke_Test_Data file and user logs into IDE Tool
		Thread.sleep(GlobalConstants.MinWait);

		for(int i=1;i<=2;i++)
		{
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDE_Driver", i+1, 1);
			String sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDE_Driver", i+1, 3);
			SR_V1R2_DS_230_MUL_SQL.main("SR_V1R2_DS_MUL_SQL");
		}
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Completed: "));
	}

}
