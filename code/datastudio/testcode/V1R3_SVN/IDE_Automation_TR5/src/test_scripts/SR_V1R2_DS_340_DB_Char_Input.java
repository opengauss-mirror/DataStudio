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

package test_scripts;

import java.awt.event.KeyEvent;

import autoitx4java.AutoItX;
import object_repository.ConsoleResultElements;
import object_repository.CreateDBElements;
import object_repository.GlobalConstants;
import script_library.ObjectBrowserPane;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_340_DB_Char_Input {

	public static void main(String sARNumber) throws Exception {

		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_340");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_340");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);

			if(sExecute.equalsIgnoreCase("Yes")){

				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "SR_V1R2_DS_DB_Char_Input", i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");


				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Create_Encoding_Of_Database_001"))
				{
					ObjectBrowserPane.objectBrowserRefresh("Single");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MinWait);

					sFlag = "Database Encoding"; 

					if(BaseActions.ControlGetText(CreateDBElements.sWindowName, "", CreateDBElements.sLabel).equalsIgnoreCase(sFlag) && BaseActions.ObjExists(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Combo box for encoding does not exist. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Create_Encoding_Of_Database_004"))
				{


					if(BaseActions.ControlGetText(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName).isEmpty())
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Default value of encoding is not null. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Create_Encoding_Of_Database_007"))
				{


					BaseActions.SetText(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName, "someText");
					sFlag = BaseActions.ControlGetText(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName );


					if(!sFlag.equals("someText"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Database encoding character set is editable when null.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Create_Encoding_Of_Database_005"))
				{

					String sFlag1,sFlag2,sFlag3;
					BaseActions.MouseClick(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName, CreateDBElements.sMouseButton, CreateDBElements.iClick, CreateDBElements.iComboxcord, CreateDBElements.iComboycord); 

					UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U, 1);


					if (BaseActions.ControlGetText(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName).trim().equals("UTF-8")){

						sFlag1 = "passed";
					}

					else {

						sFlag1 = "failed";
					}

					UtilityFunctions.KeyPress(KeyEvent.VK_G, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_G, 1);


					if(BaseActions.ControlGetText(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName).trim().equals("GBK")){

						sFlag2 = "passed";
					}

					else {

						sFlag2 = "failed";
					}

					UtilityFunctions.KeyPress(KeyEvent.VK_L, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_L, 1);


					if(BaseActions.ControlGetText(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName).trim().equals("LATIN1")){

						sFlag3 = "passed";
					}

					else {

						sFlag3 = "failed";
					}

					if(sFlag1.equals("passed") && sFlag2.equals("passed") && sFlag3.equals("passed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Widely used character set UTF-8, GBK, Latin1 are not listed first in the drop down list. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}




				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Create_Encoding_Of_Database_006"))
				{

					BaseActions.MouseClick(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName, CreateDBElements.sMouseButton, CreateDBElements.iClick, CreateDBElements.iComboxcord, CreateDBElements.iComboycord); 

					UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U, 1);

					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);



					BaseActions.SetText(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName, "someText");
					sFlag = BaseActions.ControlGetText(CreateDBElements.sWindowName, "", CreateDBElements.sComboBoxName );

					BaseActions.MouseClick(CreateDBElements.sWindowName, "", CreateDBElements.bCancel, CreateDBElements.sMouseButton, CreateDBElements.iClick, CreateDBElements.iCancelxcord, CreateDBElements.iCancelycord);


					if(!sFlag.equals("someText"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Database encoding character set is editable when null.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Create_Encoding_Of_Database_002"))
				{
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					sFlag=ObjectBrowserPane.createDBObjectBrowser("sampledb", "UTF-8","Yes");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.contains("[INFO] Created 'sampledb' database successfully."))
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

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Create_Encoding_Of_Database_008"))
				{
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					sFlag = ObjectBrowserPane.RenameDBObjectBrowser("\"SAMPLEDB\"", "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.contains("[INFO] Renamed sampledb database to 'SAMPLEDB'."))
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

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Create_Encoding_Of_Database_003"))
				{
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					if(UtilityFunctions.GetClipBoard().contains("UTF8")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");

					}
					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Encoding does not match with the DB Properties. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}

				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Create_Encoding_Of_Database_002"))
				{

					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_O, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_O, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);


					if(UtilityFunctions.GetClipBoard().contains("OID")){

						sFlag = "failed";
						ObjectBrowserPane.DropDB("\"SAMPLEDB\"");
					}
					else {

						sFlag = "passed";
						ObjectBrowserPane.ObjectBrowserRefresh();
						UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
						ObjectBrowserPane.DropDBObjectBrowserConnected();

					}
					if(sFlag.equals("passed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else 
					{   
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Property window doesnot close on disconnecting the database. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}

		}
		for(int i=1;i<=9;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}
}




