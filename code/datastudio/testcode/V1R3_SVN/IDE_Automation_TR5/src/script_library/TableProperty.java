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
 
package script_library;



import java.awt.event.KeyEvent;

import object_repository.TablePropertyElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class TableProperty {
	
	public static void WinActivateTableProperty() throws Exception
	    {
	        BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
	        String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
	        BaseActions.WinActivate(sWinName);
	        BaseActions.Click(sWinName,"",TablePropertyElements.sPropertyTab);
	    }
	
	public static void PropertyTabNavigation(String sTab) throws Exception
    {
		TableProperty.WinActivateTableProperty();
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 5);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 5);
		UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 5);
		UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 5);
		switch (sTab) {
        case "GENERAL":
        	UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
    		UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
    		break;
        case "COLUMNS":
        	UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
    		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
    		break;
        case "DATA DISTRIBUTION":
        	UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
    		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 2);
    		break;
        case "TABLE CONSTRAINTS":
        	UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
    		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
    		break;
        case "INDEXES":
        	UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
    		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
    		break;
        }
    }
	
	public static String GeneralTabValidation(String sTestCaseID,String ResultExcel) throws Exception
    {
        boolean sFlag1,sFlag2,sFlag3,sFlag4,sFlag5,sFlag6,sFlag7,sFlag8,sFlag9,sFlag10;
        String sFlag;
		AutoItX x = new AutoItX();
        BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
        String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
        TableProperty.PropertyTabNavigation("GENERAL");
        //Field Validation
        sFlag1 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sSchemaName);
        sFlag2 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sTableSpace);
        sFlag3 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sTableType);
        sFlag4 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sTableName);
        sFlag5 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sTableDescription);
        sFlag6 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnTblNameQuoted);
        sFlag7 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnOptNotExists);
        sFlag8 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnOptWitOIDS);
        sFlag9 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnOptColOrtn);
        sFlag10 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sFillFactor);
        if(sFlag1||sFlag2||sFlag3||sFlag4||sFlag5||sFlag6||sFlag7||sFlag8||sFlag9||sFlag10)
        {
        	sFlag="Fail";
        	UtilityFunctions.TakeScreenshot(sTestCaseID+"GENERAL", ResultExcel);
        }
        else
        	sFlag="Pass";
        return sFlag;
    }
	
	public static String ColumnsTabValidation(String sTestCaseID,String ResultExcel) throws Exception
    {
        boolean sFlag1,sFlag2,sFlag3,sFlag4,sFlag5,sFlag6,sFlag7,sFlag8,sFlag9,sFlag10,sFlag11,sFlag12,sFlag13,sFlag14,sFlag15;
        String sAFlag, sBFlag;
        String sCol1, sCol2, sCol3;
		AutoItX x = new AutoItX();
        BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
        String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
        TableProperty.PropertyTabNavigation("COLUMNS");
        //Field Validation
        sFlag1 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sColumnName);
        sFlag2 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sDataTypeSchema);
        sFlag3 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sDataType);
        sFlag4 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnDown);
        sFlag5 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sColumnNameQuoted);
        sFlag6 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.lstArrayDimension);
        sFlag7 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.edtArrayDimention);
        sFlag8 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.lstPrecision);
        sFlag9 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.lstScale);
        sFlag10 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sTypeDescription);
        sFlag11 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.grpColumnConstraints);
        sFlag12 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnAdd);
        sFlag13 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnDelete);
        sFlag14 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnEdit);
        sFlag15 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnUp);
        if(sFlag1||sFlag2||sFlag3||sFlag4||sFlag5||sFlag6||sFlag7||sFlag8||sFlag9||sFlag10||sFlag11||sFlag12||sFlag13||sFlag14||sFlag15)
        	sAFlag="Fail";
        else
        	sAFlag="Pass";
        
        sCol1 = x.controlListViewGetText(sWinName, "", "SysListView321","0","Column Name");
		sCol2 = x.controlListViewGetText(sWinName, "", "SysListView321","1","Column Name");
		sCol3 = x.controlListViewGetText(sWinName, "", "SysListView321","2","Column Name");
		if(sCol1.trim().equals("empid")&&sCol2.trim().equals("ename")&&sCol3.trim().equals("salary"))
			sBFlag="Pass";
		else
			sBFlag = "Fail";
		
		
		if(sAFlag.equals("Pass")&&sBFlag.equals("Pass"))
			return "Pass";
		else
		{
        	UtilityFunctions.TakeScreenshot(sTestCaseID+"COLUMNS", ResultExcel);
        	return "Fail";
        }
    }
	
	public static String DataDistrTabValidation(String sTestCaseID,String ResultExcel) throws Exception
    {
        boolean sFlag1,sFlag2;
        String sFlag;
		AutoItX x = new AutoItX();
        BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
        String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
        TableProperty.PropertyTabNavigation("DATA DISTRIBUTION");
        //Field Validation
        sFlag1 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sDistributionType);
        sFlag2 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sAvailableColumns);
        if(sFlag1||sFlag2)
        {
        	sFlag="Fail";
        	UtilityFunctions.TakeScreenshot(sTestCaseID+"DATA DISTRIBUTION", ResultExcel);
        }
        else
        	sFlag="Pass";
        return sFlag;
    }
	
	public static String TableConstrTabValidation(String sTestCaseID,String ResultExcel) throws Exception
    {
        boolean sFlag1,sFlag2,sFlag3,sFlag4,sFlag5,sFlag6,sFlag7;
        String sFlag;
		AutoItX x = new AutoItX();
        BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
        String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
        TableProperty.PropertyTabNavigation("TABLE CONSTRAINTS");
        //Field Validation
        sFlag1 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sConstraintName);
        sFlag2 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sConstraintType);
        sFlag3 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sColumnsAvailable);
        sFlag4 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sConstraints);
        sFlag5 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnConAdd);
        sFlag6 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnConEdit);
        sFlag7 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnConDelete);
        if(sFlag1||sFlag2||sFlag3||sFlag4||sFlag5||sFlag6||sFlag7)
        {
        	sFlag="Fail";
        	UtilityFunctions.TakeScreenshot(sTestCaseID+"TABLE CONSTRAINTS", ResultExcel);
        }
        else
        	sFlag="Pass";
        return sFlag;
    }
	
	public static String IndexesTabValidation(String sTestCaseID,String ResultExcel) throws Exception
    {
        boolean sFlag1,sFlag2,sFlag3,sFlag4,sFlag5,sFlag6,sFlag7,sFlag8,sFlag9,sFlag10,sFlag11;
        String sFlag;
		AutoItX x = new AutoItX();
        BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
        String sWinName = BaseActions.WinGetTitle(TablePropertyElements.sPropertywindow);
        TableProperty.PropertyTabNavigation("INDEXES");
        //Field Validation
        sFlag1 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sIndexName);
        sFlag2 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sAccessMethod);
        sFlag3 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sIndexTableSpace);
        sFlag4 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sIndexFillFactor);
        sFlag5 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sIndexColumnsAvailable);
        sFlag6 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.chkUniqueIndex);
        sFlag7 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnIndAdd);
        sFlag8 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnIndEdit);
        sFlag9 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.btnIndDelete);
        sFlag10 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sIndexColumns);
        sFlag11 = x.controlCommandIsEnabled(sWinName, "",TablePropertyElements.sIndexDefinition);
        
        if(sFlag1||sFlag2||sFlag3||sFlag4||sFlag5||sFlag6||sFlag7||sFlag8||sFlag9||sFlag10||sFlag11)
        {
        	sFlag="Fail";
        	UtilityFunctions.TakeScreenshot(sTestCaseID+"INDEXES", ResultExcel);
        }
        else
        	sFlag="Pass";
        return sFlag;
    }
	
	

}
