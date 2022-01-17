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

import autoitx4java.AutoItX;
import object_repository.CreateTableWizardElements;
import object_repository.ExecQueryElements;
import object_repository.GlobalConstants;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class CreateTableWizardFunctions {

	public static void CreateTableWizard() throws Exception
	{
		BaseActions.MouseClick(CreateTableWizardElements.wCreateTitle, "", CreateTableWizardElements.sCreateControlID, CreateTableWizardElements.sButton, CreateTableWizardElements.nclicks, 95, 45);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);	
		

	}
	
	public static void openCreateTableWizard() throws Exception {

		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		ObjectBrowserPane.ObjectBrowserRefresh();
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);

		}

	public static void TableName(String sTableName,String Quoted) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);	
		if(Quoted.equalsIgnoreCase("Yes"))
		{
			BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTableControlID,sTableName);
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sQuotedControlID);
		}
		else
			BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTableControlID,sTableName);
	}
	
	 public static void upIndexColumn() throws Exception{
   	  BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bUpIndex);
   	  Thread.sleep(GlobalConstants.MinWait);
		
	}
     public static void downIndexColumn() throws Exception{
   	  BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bDownIndex);
   	  Thread.sleep(GlobalConstants.MinWait);
		
	}
     
     public static void deleteConstraint(int columnNumber) throws Exception{
			
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.availableConstraint);
			
			if (columnNumber==1) {
				UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
				BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintDelete);
				}
			else if (columnNumber>1) {
				for (int i = 1; i < columnNumber; i++) {
					
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintDelete);
					
				}
				
			}
				
		}
     
     public static void addConstraint() throws Exception
		{
BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumnsConstraint);
				UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
				UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
				BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.selectColumnConstraint );
			
for(int i=0;i<4;i++){

				Thread.sleep(GlobalConstants.MedWait);
				BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumnsConstraint);
				UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
				BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.selectColumnConstraint);
				
			}
         Thread.sleep(GlobalConstants.MedWait);
        BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintAdd);
			}
			
     public static void resetTableDesc(String sLine) throws Exception{
 		/*************************************************************************
 		FUNCTION NAME		: SetFunction()
 		DESCRIPTION	 		: Function to set the multiline function
 		IN PARAMETERS		: sLine{Input Line for the functions}
 		RETURN PARAMETERS	: None
 		 *************************************************************************/
 		AutoItX x = new AutoItX();
 		x.controlFocus(CreateTableWizardElements.wDescTitle, "", CreateTableWizardElements.resetDescControlID);
 		x.send(sLine);
 		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
 		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
 	}
     
       public static void availbleHashColumn() throws Exception
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sHashColumn);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		}
       
       public static void selectHashColumn() throws Exception{

			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectHashColumn);
			Thread.sleep(GlobalConstants.MinWait);
			}
			
			
			
	
	public static void checkIndexDefinition() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		
	}

	public static void TableDesc(String sLine) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: SetFunction()
		DESCRIPTION	 		: Function to set the multiline function
		IN PARAMETERS		: sLine{Input Line for the functions}
		RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		x.controlFocus(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDescControlID);
		x.send(sLine);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}
	
	public static void changeColumnOrder(int columnNumber) throws Exception{
		
		
		for(int i=0;i<columnNumber;i++){
			
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumnIndex);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		Thread.sleep(GlobalConstants.MedWait);
		CreateTableWizardFunctions.selectIndexColumn();
		Thread.sleep(GlobalConstants.MedWait);
       }
		/*if (condition) {
			
		}*/
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sChangeColumnOrder);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		Thread.sleep(GlobalConstants.MinWait);
		CreateTableWizardFunctions.upIndexColumn();
		
		Thread.sleep(GlobalConstants.MinWait);
		 CreateTableWizardFunctions.indexButton("ADD");
		 
		 
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sChangeColumnOrder);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
		CreateTableWizardFunctions.downIndexColumn();
		
		Thread.sleep(GlobalConstants.MinWait);
		 CreateTableWizardFunctions.indexButton("ADD");
		
		
		}



	public static void TableSpace(String sTableSpaceName) throws Exception	
	{

		if(sTableSpaceName.equalsIgnoreCase("pg_default")) 
		{
			
		//	BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeControlID);
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTSpaceControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}			
		else if(sTableSpaceName.equalsIgnoreCase("pg_global")){
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTSpaceControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_P, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_P, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}		
		else
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTSpaceControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
	}

	public static void fillFactor(String sTableSpaceName) throws Exception	
	{
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sfillFactorcontrolID);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);	
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sfillFactorcontrolID, sTableSpaceName);
	}


	public static void TableType(String sTableTypeName) throws Exception
	{
		if(sTableTypeName.equalsIgnoreCase("UNLOGGED"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTableTypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
		else 
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTableTypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
	}	


	public static void AddCloumn(String sColumnName,String sQuoted) throws Exception
	{
		/*BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bNextcontrolID);
		Thread.sleep(GlobalConstants.MinWait);*/
		if(sQuoted.equalsIgnoreCase("Yes"))
		{		
			BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sCloumnControlID,sColumnName );
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sCloumnQuotedID);
			/*BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bColumnAddcontrolID);
			Thread.sleep(GlobalConstants.MinWait);*/
		}
		else
		{

			BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sCloumnControlID,sColumnName );
			Thread.sleep(GlobalConstants.MinWait);
		}

	}
	
	public static void deleteColumnIndex(int indexNumber) throws Exception{
		
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sIndexDefinition);
		
		if (indexNumber==1) {
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			CreateTableWizardFunctions.indexButton("DELETE");
			}
		else if (indexNumber>1) {
			for (int i = 1; i < indexNumber; i++) {
				
				UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
				UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
				CreateTableWizardFunctions.indexButton("DELETE");
				
			}
			
		}
		
		
	}

	public static void Button(String sButtonToBeClicked) throws Exception
	{

		if(sButtonToBeClicked.equalsIgnoreCase("NEXT"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bNextcontrolID);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if(sButtonToBeClicked.equalsIgnoreCase("BACK"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bBackcontrolID);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if(sButtonToBeClicked.equalsIgnoreCase("FINISH"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bFinishcontrolID);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if(sButtonToBeClicked.equalsIgnoreCase("IFEXSISTS"))
		{

			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.cifexsists);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("IFEXSISTS"))
		{

			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.cifexsists);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("OIDS"))
		{

			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.cOids);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("COLUMNORIENTATION"))
		{

			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.cColumnOrentation);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("ADD"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bColumnAddcontrolID);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("TABLEADD"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bTableAddcontrolID);
			Thread.sleep(GlobalConstants.MinWait);
		}
		
		else if(sButtonToBeClicked.equalsIgnoreCase("EDIT"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bEditControlID);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("UPDATE"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bUpdateControlID);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("CANCEL"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bCancelControlID);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("UP"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bUpControlID);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("DOWN"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bDownControlID);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sButtonToBeClicked.equalsIgnoreCase("DELETE"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bDeleteControlID);
			Thread.sleep(GlobalConstants.MinWait);
		}
	}	

	public static String SQLPreviewCopyCompare(String ToBeCompared) throws Exception
	{
		String sCopy;
		BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.spreviewControlID, CreateTableWizardElements.sButton,
				CreateTableWizardElements.nclicks, CreateTableWizardElements.xprecord,CreateTableWizardElements.yprecord);
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sPreCopyControlID);
		sCopy = UtilityFunctions.GetClipBoard();
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.spreviewControlID, CreateTableWizardElements.sButton, CreateTableWizardElements.nclicks, CreateTableWizardElements.xGencord, CreateTableWizardElements.yGencord);
		if(sCopy.contains(ToBeCompared))
			return "Success";
		else
			return "Failed";
	}

	public static String SQLPreviewCopy() throws Exception
	{

		String sCopy;
		BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.spreviewControlID, CreateTableWizardElements.sButton,
				CreateTableWizardElements.nclicks, CreateTableWizardElements.xprecord,CreateTableWizardElements.yprecord);
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sPreCopyControlID);
		sCopy = UtilityFunctions.GetClipBoard();
		return sCopy;

	}

	public static void DataType(String sDatatype) throws Exception
	{
		switch(sDatatype.toUpperCase())
		{
		case "VARCHAR" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_V, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "DECIMAL" :	
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "CHAR" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "BOOLEAN" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "INTEGER" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "TEXT" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
			
		case "NUMERIC" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;

		case "DEFAULT" :
		}	
	}

	public static void ArrayDim(String sNumber) throws Exception
	{
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sarrayControlID, sNumber);
	}

	public static void Precision(String sNumber) throws Exception
	{
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sPrecisionControlID, sNumber);
	}
	public static void Scale(String sNumber) throws Exception
	{
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sScaleControlID, sNumber);
	}
	public static void Columnconstarints(String sConstraintType) throws Exception
	{
		switch(sConstraintType.toUpperCase())
		{
		case "NOTNULL" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sNotnullControlID);
			break;

		case "UNIQUE":
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sUniqueControlID);
			break;

		case "DEFAULT":
			break;
		}
	}

	public static void DefaultConstraint(String sdefaultValue,String sQuoted) throws Exception
	{
		if(sQuoted.equalsIgnoreCase("Yes"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sColumnQuoteControlID);
			BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDefaultControlID,sdefaultValue );
		}

		else
		{
			BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDefaultControlID,sdefaultValue );
		}
	}
	public static void CheckConstraint(String sCheckValue) throws Exception
	{
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sCheckControlID,sCheckValue );
	}

	public static void DataDistribution(String sDataType) throws Exception
	{
		switch(sDataType.toUpperCase())
		{

		case "REPLICATION" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sdataTypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;

		case "HASH" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sdataTypeControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "DEFAULT":
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sdataTypeControlID);
			break;			

		}
	}

	public static void TableConstraint(String sConstraintType) throws Exception
	{
		switch(sConstraintType.toUpperCase())
		{
		case "UNIQUE" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.stableConstraintControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_U, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "PRIMARY_KEY" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.stableConstraintControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "CHECK" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.stableConstraintControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "DEFAULT" :
			break;
		}
	}
	public static void ConstraintName(String sName) throws Exception
	{
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sConstraintNameControlID, sName);
	}
	public static void ConstraintExpression(String sExpression) throws Exception
	{
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sConstraintExpressionControlID, sExpression);
	}
	public static void ConstraintButton(String sButtonToBeClicked) throws Exception
	{

		if(sButtonToBeClicked.equalsIgnoreCase("ADD"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintAdd);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if(sButtonToBeClicked.equalsIgnoreCase("EDIT"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintEdit);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if(sButtonToBeClicked.equalsIgnoreCase("DELETE"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintDelete);
			Thread.sleep(GlobalConstants.MinWait);
		}
	}
	
	public static void selectConstraintColumn() throws Exception{
		
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectColumn);
		Thread.sleep(GlobalConstants.MinWait);
	}
	
    public static void deSelectConstraintColumn() throws Exception{
		
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bDeselectColumn);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void uniqueConstriantcolumn(String sButtonToBeClicked) throws Exception
	{
		if(sButtonToBeClicked.equalsIgnoreCase("ADD"))
		{
			BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sControlID, CreateTableWizardElements.sButton, CreateTableWizardElements.nclicks, CreateTableWizardElements.xUniqueCord, CreateTableWizardElements.yUniqueCord);
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.buniqueAdd);
			Thread.sleep(GlobalConstants.MinWait);

			if(sButtonToBeClicked.equalsIgnoreCase("REMOVE"))
			{
				BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.buniqueRemove);
				Thread.sleep(GlobalConstants.MinWait);
			}
		}
	}
	
	public static void indexName(String index) throws Exception{
		
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sIndexName,index);
		Thread.sleep(GlobalConstants.MinWait);	
	}
	
	public static void selectAccessMethodIndex(String accessMethod) throws Exception{
		
		switch(accessMethod)
		{
		case "btree" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAccessCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_B, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_B, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "gin" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAccessCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_G, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_G, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "gist" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAccessCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_G, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_G, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "hash" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAccessCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_H, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_H, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "psort" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAccessCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "spgist" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAccessCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "DEFAULT" :
			break;
		}
	}
	
	public static void indexTableSpace(String sType) throws Exception
	{
		switch(sType.toUpperCase())
		{
		case "PG_DEFAULT" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTablespaceCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "PG_GLOBAL" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTablespaceCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "TEST" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTablespaceCombo);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "DEFAULT" :
			break;
		}
	}

	public static void constraintTableSpace(String sType) throws Exception
	{
		switch(sType.toUpperCase())
		{
		case "PG_DEFAULT" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.stableSpaceControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "PG_GLOBAL" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.stableSpaceControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "TEST" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.stableSpaceControlID);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "DEFAULT" :
			break;
		}
	}
	
     public static void selectIndexColumn() throws Exception{
		
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectIndex);
		Thread.sleep(GlobalConstants.MinWait);
	}
	
    public static void deSelectIndexColumn() throws Exception{
		
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bDeselectIndex);
		Thread.sleep(GlobalConstants.MinWait);
	}
    
    public static void indexButton(String sButtonToBeClicked) throws Exception
	{

		if(sButtonToBeClicked.equalsIgnoreCase("ADD"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bIndexAdd);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if(sButtonToBeClicked.equalsIgnoreCase("EDIT"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bIndexEdit);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if(sButtonToBeClicked.equalsIgnoreCase("DELETE"))
		{
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bIndexDelete);
			Thread.sleep(GlobalConstants.MinWait);
		}
	}
    
    public static void indexFillFactor(String sValue) throws Exception
	{
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sFillFactor);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);	
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sFillFactor, sValue);
	}
    
    public static void addColumnIndex(int columnNumber) throws Exception{
    	
    	BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumnIndex);
    	
    	if(columnNumber==1){
    		
    		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
    		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
    		UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
    		UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
    		CreateTableWizardFunctions.selectIndexColumn();
    	}
    	
    	else if(columnNumber>1) {
    		
    		for(int i=1;i<columnNumber;i++){
    			
    		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
        	UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
        	Thread.sleep(GlobalConstants.MinWait);
    		}
    		CreateTableWizardFunctions.selectIndexColumn();
    	}
    	
    	CreateTableWizardFunctions.indexButton("ADD");
    	
    	
    }
    
    public static void indexIconNavigation() throws Exception{
    	
    	UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,5);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,5);
		UtilityFunctions.KeyPress(KeyEvent.VK_T,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_T,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_I,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_I,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		Thread.sleep(GlobalConstants.MinWait);
    }
    
    public static boolean checkIndexObjectBrowser() throws Exception{
    	
    	boolean b;
    	UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R,1);
		Thread.sleep(GlobalConstants.MedWait);
		
		if(BaseActions.WinExists(CreateTableWizardElements.wRenameIndexWindow)){
			
			b = true;
		}
		
		else {
			
			b = false;
		}
		return b;
    }
    
public static void autoColumnsNavigation() throws Exception{
    	
    	UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,5);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,5);
		UtilityFunctions.KeyPress(KeyEvent.VK_T,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_T,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		Thread.sleep(GlobalConstants.MinWait);
    }

	public static void tableFillFactor(String sValue) throws Exception
	{
		BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.stableFillFactor);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);	
		BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.stableFillFactor, sValue);
	}

	public static void tableDeffered(String sType) throws Exception{
		switch(sType.toUpperCase())
		{
		case "DEFERABLE" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.cDefereable);
			Thread.sleep(GlobalConstants.MinWait);
			break;
		case "INDEFERABLE" :
			BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.cIntiallyDeferred);
			Thread.sleep(GlobalConstants.MinWait);
			break;
		case "DEFAULT" :
			break;
		}
	}
}
