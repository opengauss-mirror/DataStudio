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

import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import object_repository.SaveAsElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class ViewFunctions {

	public static void Auto_Table_View_Navigation() throws Exception{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void Auto_Import_View_Navigation() throws Exception{
		/*************************************************************************
		FUNCTION NAME : CreateDB()
		DESCRIPTION : Function to Drop a Database
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void Temp_Schema_Navigation() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		Thread.sleep(GlobalConstants.MinWait);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,15);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,15);
		Thread.sleep(GlobalConstants.ModWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
		BaseActions.Winwait("Drop Schema");
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click("Drop Schema", "", "Button1");
	}


	public static void Public_Table_View_Navigation() throws Exception{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,15);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,15);
		Thread.sleep(GlobalConstants.ModWait);

		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void pg_catalog_View_Navigation() throws Exception{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,120,280);//pg_catalog schema co-ordinates
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void createViewObjectBrowser() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);

	}

	public static void renameViewObjectBrowser(String sNewName) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.Winwait("Rename View");
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText("Rename View", "", "SWT_Window03", sNewName);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click("Rename View", "", "Button1");
	}

	public static void setSchema(String sSchemaname) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.Winwait("Set Schema");
		Thread.sleep(GlobalConstants.MinWait);
		if(sSchemaname.equalsIgnoreCase("auto")||sSchemaname.equalsIgnoreCase("auto_import"))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.Click("Set Schema", "", "Button1");
		}
		else
		{
			BaseActions.Click("Set Schema", "", "Button2");
		}
	}

	public static void showDDL() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}

	public static void dropViewObjeBrowser(String sSelection) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);	
		if(sSelection.equalsIgnoreCase("DROP"))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			BaseActions.Winwait("Drop View ");
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.Click("Drop View ", "", "Button1");
		}
		else
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			BaseActions.Winwait("Drop View ");
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.Click("Drop View ", "", "Button1");
		}
	}
	public static String exportDDL(String sPath) throws Exception{

		String sFlag = null;
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1); 
		UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
		if(BaseActions.WinExists(SaveAsElements.wDisclaimer))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
		BaseActions.Winwait("Save As");
		BaseActions.SetText("Save As", "", "Edit1", sPath);
		BaseActions.Click("Save As", "", "Button1");
		Thread.sleep(GlobalConstants.MaxWait);

		if(BaseActions.WinExists(ObjectBrowserElements.wExportFinish)){

			BaseActions.Click(ObjectBrowserElements.wExportFinish, "", ObjectBrowserElements.bExportFinishOK);
			sFlag= "Success";
		}
		else if(BaseActions.WinExists(ObjectBrowserElements.wExportFailed)) {


			BaseActions.Click(ObjectBrowserElements.wExportFailed, "", ObjectBrowserElements.bExportFailedOK);
			sFlag = "Failed";
		}
		return sFlag;

	}

	public static void Temp_Schema_Delete() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_P,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_P,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
		BaseActions.Winwait("Drop Schema");
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click("Drop Schema", "", "Button1");
	}
	
	public static void view_Refresh() throws Exception
	{
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 116, 99);//to click on the view tab
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		Thread.sleep(GlobalConstants.MinWait);
	}
	
}
