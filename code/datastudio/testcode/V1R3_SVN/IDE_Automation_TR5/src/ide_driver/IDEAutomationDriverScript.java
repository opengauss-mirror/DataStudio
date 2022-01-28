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
import test_scripts.Base_IndexMgnt;
import test_scripts.Base_SchemaMgnt;
import test_scripts.Base_TableMgnt;
import test_scripts.DS_002_AUTO_SUGGEST;
import test_scripts.DS_STRY_010_001_01;
import test_scripts.DS_STRY_020_001_01;
import test_scripts.Edit_Data_Filter_Wizard;
import test_scripts.Edit_Table_Data;
import test_scripts.PTS_TOR_080_001_CAN_EXP;
import test_scripts.SR_300_SHORTCUT_KEYS;
import test_scripts.SR_300_SYNT_FRMT;
import test_scripts.SR_Tools_DS_010_CAN_QRY;
import test_scripts.SR_Tools_DS_220_OBJ_PRPTY;
import test_scripts.SR_V1R2_DS_001_STR1;
import test_scripts.PTS_SR_V1R2_DS_001_STR_3_IMP_TBL_DAT;
import test_scripts.SR_V1R2_DS_001_STR_2_SVE_OPN_SQL;
import test_scripts.SR_V1R2_DS_230_MUL_SQL;
import test_scripts.SR_V1R2_DS_260_TBL_SPC;
import test_scripts.SR_V1R2_DS_260_TGR;
import test_scripts.SR_V1R2_DS_260_VIEW;
import test_scripts.SR_V1R2_DS_260_VIEW_Non_Func;
import test_scripts.SR_V1R2_DS_310_EXE_HTY;
import test_scripts.SR_V1R2_DS_340_DB_Char_Input;
import test_scripts.SR_V1R2_DS_372_HTY_MGT;
import test_scripts.SR_V1R2_DS_Column_Edit;
import test_scripts.SR_V1R2_DS_Conn_Profile;
import test_scripts.SR_V1R2_DS_Debug_Pswd;
import test_scripts.SR_V1R2_DS_Debug_Pwd_Enh;
import test_scripts.SR_V1R2_DS_Export_DDL;

public class IDEAutomationDriverScript {

	public static void main(String args[]) throws Exception
	{
		//Loading required DLL for AutoIT
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Started: "));
		System.load(GlobalConstants.sJacobDLL);
		//Launching the IDE Tool Application
		Login.LaunchIDE(GlobalConstants.sIDEPath);// *****
		//Getting Login Credentials from IDE_Smoke_Test_Data file and user logs into IDE Tool
		String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
		String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
		String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
		String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
		String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
		String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
		Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");//********
		Thread.sleep(GlobalConstants.MaxWait);

		//Getting Execute Status from Driver Data Table
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, "IDE_Driver");
	    for(int i=1;i<=iRowCount;i++)
		{
		
			String sARNumber=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDE_Driver", i+1, 1);
			String sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDE_Driver", i+1, 3);
			if (sARNumber.equals("AR.Tools.IDE.020.007") && sExecute.equals("Yes"))
				AR_Tools_IDE_020_007_QRY_FMT.main(sARNumber);
			if (sARNumber.equals("AR.Tools.IDE.030.004") && sExecute.equals("Yes"))
       			AR_Tools_IDE_030_004_EXE_STS.main(sARNumber);
			if (sARNumber.equals("AR.Tools.IDE.020.001") && sExecute.equals("Yes"))
				AR_Tools_IDE_020_001_AUT_FTR.main(sARNumber);
			if (sARNumber.equals("AR.Tools.IDE.030.006") && sExecute.equals("Yes"))
				AR_Tools_IDE_030_006_PLN_CST.main(sARNumber);
			if (sARNumber.equals("AR.Tools.IDE.030.002") && sExecute.equals("Yes"))
				AR_Tools_IDE_030_002_MUL_QRY.main(sARNumber);
			if (sARNumber.equals("AR.Tools.IDE.040.003") && sExecute.equals("Yes"))
				AR_Tools_IDE_040_003_EXP_CSV.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_001_STR1") && sExecute.equals("Yes"))
				SR_V1R2_DS_001_STR1.main(sARNumber);
			if (sARNumber.equals("SR.V1R2.DS.001_STR_2") && sExecute.equals("Yes"))
				SR_V1R2_DS_001_STR_2_SVE_OPN_SQL.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_001_STR3") && sExecute.equals("Yes"))
				PTS_SR_V1R2_DS_001_STR_3_IMP_TBL_DAT.main(sARNumber);
			if (sARNumber.equals("DS_STRY_010_001_01") && sExecute.equals("Yes"))
				DS_STRY_010_001_01.main(sARNumber);		
			if (sARNumber.equals("DS_STRY_020_001_01") && sExecute.equals("Yes"))
				DS_STRY_020_001_01.main(sARNumber);
			if (sARNumber.equals("SR_Tools_DS_010") && sExecute.equals("Yes"))
				SR_Tools_DS_010_CAN_QRY.main(sARNumber);
			if (sARNumber.equals("Schema_Mgnt") && sExecute.equals("Yes"))
				Base_SchemaMgnt.main(sARNumber);
			if (sARNumber.equals("Database_Mgnt") && sExecute.equals("Yes"))
				Base_DataMgnt.main(sARNumber);
			if (sARNumber.equals("Table_Mgnt") && sExecute.equals("Yes"))
				Base_TableMgnt.main(sARNumber);
			if (sARNumber.equals("SR_Tools_DS_220_OBJ_PRPTY") && sExecute.equals("Yes"))
				SR_Tools_DS_220_OBJ_PRPTY.main(sARNumber);
			if (sARNumber.equals("PTS_TOR_080_001_CAN_EXP") && sExecute.equals("Yes"))
				PTS_TOR_080_001_CAN_EXP.main(sARNumber);
			if (sARNumber.equals("Edit_Data_Filter_Wizard") && sExecute.equals("Yes"))
				Edit_Data_Filter_Wizard.main(sARNumber);
			if (sARNumber.equals("Edit_Table_Data") && sExecute.equals("Yes"))
				Edit_Table_Data.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_MUL_SQL") && sExecute.equals("Yes"))
				SR_V1R2_DS_230_MUL_SQL.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_260_View") && sExecute.equals("Yes"))
				SR_V1R2_DS_260_VIEW.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_260_VIEW_Non_Func") && sExecute.equals("Yes"))
				SR_V1R2_DS_260_VIEW_Non_Func.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_260_TBL_SPC") && sExecute.equals("Yes"))
				SR_V1R2_DS_260_TBL_SPC.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_260_TGR") && sExecute.equals("Yes"))
				SR_V1R2_DS_260_TGR.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_310_EXE_HTY") && sExecute.equals("Yes"))
				SR_V1R2_DS_310_EXE_HTY.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_372_HTY_MGT") && sExecute.equals("Yes"))
				SR_V1R2_DS_372_HTY_MGT.main(sARNumber);
			if (sARNumber.equals("DS_002_AUTO_SUGGEST") && sExecute.equals("Yes"))
				DS_002_AUTO_SUGGEST.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_DB_Char_Input") && sExecute.equals("Yes"))
				SR_V1R2_DS_340_DB_Char_Input.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_Debug_Pswd") && sExecute.equals("Yes"))
				SR_V1R2_DS_Debug_Pswd.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_Debug_Pwd_Enh") && sExecute.equals("Yes"))
				SR_V1R2_DS_Debug_Pwd_Enh.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_Column_Edit") && sExecute.equals("Yes"))
				SR_V1R2_DS_Column_Edit.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_ConProf") && sExecute.equals("Yes"))
				SR_V1R2_DS_Conn_Profile.main(sARNumber);
			if (sARNumber.equals("SR_V1R2_DS_Export_DDL") && sExecute.equals("Yes"))
				SR_V1R2_DS_Export_DDL.main(sARNumber);
			if (sARNumber.equals("Base_IndexMgnt") && sExecute.equals("Yes"))
				Base_IndexMgnt.main(sARNumber);
			if (sARNumber.equals("SR_300_SYNT_FRMT") && sExecute.equals("Yes"))
				SR_300_SYNT_FRMT.main(sARNumber);
			if (sARNumber.equals("SR_300_SHORTCUT_KEYS") && sExecute.equals("Yes"))
				SR_300_SHORTCUT_KEYS.main(sARNumber);
		}
		//Logout from IDE Tool after Execution
		Login.IDELogout();
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Completed: "));
	}

}
