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
import object_repository.CreateTableWizardElements;
import object_repository.GlobalConstants;
import script_library.CreateTableWizardFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_Column_Edit {
	
public static void main(String sARNumber) throws Exception {
		
		//Creating the Test Result File for Reporting
				String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_Column_Edit");
				//Creating the Test Result File for TMSS
				String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_Column_Edit");
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
					
						sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "SR_V1R2_DS_Column_Edit", i,1);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
						
						if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_001")){
							
							CreateTableWizardFunctions.openCreateTableWizard();
							BaseActions.Winwait(CreateTableWizardElements.wTitle);
							CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
							Thread.sleep(GlobalConstants.MedWait);
							CreateTableWizardFunctions.Button("NEXT");
							CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
							CreateTableWizardFunctions.Button("ADD");
							Thread.sleep(GlobalConstants.MedWait);
							UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
							UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
							Thread.sleep(GlobalConstants.MedWait);
							UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
							CreateTableWizardFunctions.Button("EDIT");
							
							sFlag = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "",CreateTableWizardElements.sCloumnControlID);
							
							if(sFlag.equals("FirstColumn")){
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								
							}
							
							else{
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Column details are not populated in the input field after clicking on Edit button. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						
						}
						
						if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_002")){             //Testcase Mapped SDV_FUN_VAL_DS_Editing_A_Column_004
							
							AutoItX x = new AutoItX();
							sFlag = x.controlListViewGetText(CreateTableWizardElements.wTitle,"", CreateTableWizardElements.sColumnsID, "0", "Column Name");
							
							if(sFlag.equals("FirstColumn")){
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								
							}
							
							else{
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Column name is not available in the table of columns. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						
						if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_003")){
							
							if(BaseActions.ObjExists(CreateTableWizardElements.wTitle, "Edit", CreateTableWizardElements.bEditControlID)){
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Edit button still exists after clicking on it. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								
								
							}
							
							else{
								
								if(BaseActions.ObjExists(CreateTableWizardElements.wTitle, "Update", CreateTableWizardElements.bUpdateControlID) &&
										BaseActions.ObjExists(CreateTableWizardElements.wTitle, "Cancel", CreateTableWizardElements.bCancelControlID)){
									
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
									
								}
								else{
									
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Update and Cancel button are not available on clicking Edit button. Please refer screenshot "+sTestCaseID+".jpg");
									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								}
								
								
							
							}
						}
						
                           if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_005")){             
							
							AutoItX x = new AutoItX();
							boolean b1 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bBackcontrolID);
							boolean b2 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bNextcontrolID);
							
							if(b1==false && b2==false){
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								
							}
							
							else{
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Back and Next buttons are still enabled on clicking Edit. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
                           
                           if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_007")){             
   							
                        	  
							AutoItX x = new AutoItX();
							boolean b = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bFinishcontrolID);
							
							
							if(b==false){
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								
							}
							
							else{
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Finish button is still enabled after clicking on Edit button. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
                           
                           if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_006")){             
      						
                        	   AutoItX x = new AutoItX();
                        	BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xDataCord, CreateTableWizardElements.yDataCord);
                        	
                        	 Thread.sleep(GlobalConstants.MaxWait);
                        	
                        	 boolean b1 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", "SWT_Window012");
                        	 
                        	Thread.sleep(GlobalConstants.MaxWait);
                        	BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xTableCord, CreateTableWizardElements.yTableCord);
                        	
                        	boolean b2 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", "SWT_Window013");
                        	
                        	Thread.sleep(GlobalConstants.MaxWait);
                        	BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
                        	
							boolean b3 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", "SWT_Window018");
							Thread.sleep(GlobalConstants.MedWait);
							
							
							if(b1==false && b2==false && b3==false){
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								Thread.sleep(GlobalConstants.MedWait);
								UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
								UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
								
								
							}
							
							else{
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Wizard allows to edit other tabs even after clicking on Edit button. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
								UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
							}
						}
                           
                           if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_008")){
                        	   
                        	 CreateTableWizardFunctions.openCreateTableWizard();
   							BaseActions.Winwait(CreateTableWizardElements.wTitle);
   							CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
   							CreateTableWizardFunctions.Button("NEXT");
   							CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
   							CreateTableWizardFunctions.Button("ADD");
   							Thread.sleep(GlobalConstants.MedWait);
   							UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
   							UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
   							UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
   							UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
   							Thread.sleep(GlobalConstants.MedWait);
   							UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
   							UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
   							CreateTableWizardFunctions.Button("EDIT");
   							Thread.sleep(GlobalConstants.MinWait);
   							CreateTableWizardFunctions.AddCloumn("FirstColumnEdited", "No");
   							CreateTableWizardFunctions.Button("UPDATE");
   							Thread.sleep(GlobalConstants.MedWait);
   							
   							if(BaseActions.ObjExists(CreateTableWizardElements.wTitle, "Update", CreateTableWizardElements.bUpdateControlID) &&
   									BaseActions.ObjExists(CreateTableWizardElements.wTitle, "Cancel", CreateTableWizardElements.bCancelControlID)){
   								
   								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
   								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Update and Cancel button still exist even after clicking on Update button. Please refer screenshot "+sTestCaseID+".jpg");
   								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
   								
   								
   							}
   							
   							else{
   								
   								if(BaseActions.ObjExists(CreateTableWizardElements.wTitle, "Add", CreateTableWizardElements.bColumnAddcontrolID)){
   									
   									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
   									
   								}
   								else{
   									
   									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
   									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Add button is not available on clicking Update button. Please refer screenshot "+sTestCaseID+".jpg");
   									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
   								}
   								
   								
   							
   							}
   						}
                           
	                       if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_009")){            
							
							AutoItX x = new AutoItX();
							sFlag = x.controlListViewGetText(CreateTableWizardElements.wTitle,"", CreateTableWizardElements.sColumnsID, "0", "Column Name");
							
							if(sFlag.equals("FirstColumnEdited")){
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								
							}
							
							else{
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Updated column name is not available in the table of columns after clicking on Update button. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
	                       
	                       if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_010")){            
								
								Thread.sleep(GlobalConstants.MedWait);
								CreateTableWizardFunctions.Button("ADD");
	   							Thread.sleep(GlobalConstants.MedWait);
	   							UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
	   							UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
	   							UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
	   							UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
	   							Thread.sleep(GlobalConstants.MedWait);
	   							UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
	   							UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
	   							CreateTableWizardFunctions.Button("EDIT");
	   							Thread.sleep(GlobalConstants.MinWait);
	   							CreateTableWizardFunctions.Button("CANCEL");
	   							
	   							AutoItX x = new AutoItX();
	   							String sFlag1 = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "",CreateTableWizardElements.sCloumnControlID);
	   							String sFlag2 = x.controlListViewGetText(CreateTableWizardElements.wTitle,"", CreateTableWizardElements.sColumnsID, "0", "Column Name");
	   							
	   							if(sFlag1.length()!=0 && !sFlag2.equals("FirstColumnEdited")){
									
	   								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Top section doesnot get cleared and the bottom table viewer gets changed on clicking Cancel button. Please refer screenshot "+sTestCaseID+".jpg");
									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
	   								
									
								}
								
								else{
									
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								}
							}
	                       
	                       if(sTestCaseID.equals("SDV_FUN_VAL_DS_Editing_A_Column_011")){            
								
	 								
	                    	        Thread.sleep(GlobalConstants.MedWait);
	                    	        CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
	                    	        CreateTableWizardFunctions.Button("ADD");
	                    	        Thread.sleep(GlobalConstants.MedWait);
		   							UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		   							UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		   							UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		   							UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		   							Thread.sleep(GlobalConstants.MedWait);
		   							UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
		   							UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
		   							CreateTableWizardFunctions.Button("EDIT");
		   							Thread.sleep(GlobalConstants.MinWait);
		   							
		   							AutoItX x = new AutoItX();
	                    	         
		   							sFlag = x.winGetText(CreateTableWizardElements.wTitle);
		   							
	 	   							
	 	   							
	 	   							if(sFlag.contains(CreateTableWizardElements.sEditError)){
	 									
	 	   							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
	 	   						    Thread.sleep(GlobalConstants.MedWait);
	 	   						    CreateTableWizardFunctions.Button("CANCEL");
	 	   								
	 	   							}
	 								
	 								else{
	 									
	 									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
	 									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Wizard allows to make other changes while Edit is in operation. Please refer screenshot "+sTestCaseID+".jpg");
	 									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
	 									CreateTableWizardFunctions.Button("CANCEL");
	 								}
	 							}
	                       
	                       if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Editing_A_Column_001")){
	                    	   
	                    	Thread.sleep(GlobalConstants.MedWait);
                   	        CreateTableWizardFunctions.AddCloumn("ThirdColumn*****", "No");
                   	        Thread.sleep(GlobalConstants.MedWait);
                   	        CreateTableWizardFunctions.Button("ADD");
                   	        Thread.sleep(GlobalConstants.MedWait);
                   	        CreateTableWizardFunctions.Button("FINISH");
                   	        
                   	 	    AutoItX x = new AutoItX();
           	         
							sFlag = x.winGetText(CreateTableWizardElements.wTitle);
							Thread.sleep(GlobalConstants.MinWait);
							UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
   							UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
							
							if(sFlag.contains("ERROR")){
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
 	   						    Thread.sleep(GlobalConstants.MedWait);
							}
							else{
								
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Wizard does not show error message on giving invalid data while adding column. Please refer screenshot "+sTestCaseID+".jpg");
									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								
							}
	                       }
							
						
						
			
			}
				}
				
				for(int i=1;i<=11;i++)
				{
					sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
					sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
					String sFinalStatus = sTestCaseID+" "+sStatus;
					if(!sStatus.isEmpty())
						UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
				}

}
}
