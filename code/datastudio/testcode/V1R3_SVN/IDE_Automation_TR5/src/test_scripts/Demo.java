package test_scripts;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.management.Query;

import org.apache.xmlbeans.impl.store.QueryDelegate;

import autoitx4java.AutoItX;
import object_repository.ConsoleResultElements;
import object_repository.CreateTableWizardElements;
import object_repository.EditWindowElements;
import object_repository.ExecQueryElements;
import object_repository.ExpQueryElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import object_repository.SaveAsElements;
import object_repository.TablePropertyElements;
import object_repository.TablespaceElements;
import script_library.CreateTableWizardFunctions;
import script_library.DataMgmtFunctions;
import script_library.DebugOperations;
import script_library.EditTableDataFunctions;
import script_library.ExecutionHistoryFunctions;
import script_library.Login;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.TablespaceFunctions;
import script_library.ViewFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class Demo {

	public static void main(String[] args) throws Exception {

		Thread.sleep(GlobalConstants.MaxWait);

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sSQLOpenControlID ,"path");
		String sButton = null;
		if(sButton.equalsIgnoreCase("OPEN"))
			BaseActions.Click(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sOpenButton);
		else
			BaseActions.Click(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sCancelButton);
		Thread.sleep(GlobalConstants.MedWait);


		/*UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.Auto_Table_Navigation();
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.setSchema();
		Thread.sleep(GlobalConstants.MedWait);			
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
		BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.Auto_Table_Navigation();
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		Thread.sleep(GlobalConstants.MinWait);
		ViewFunctions.createViewObjectBrowser();
		Thread.sleep(GlobalConstants.MinWait);
		MultipleTerminal.TerminalEditorClear(2);
		Thread.sleep(GlobalConstants.MinWait);
		MultipleTerminal.SetFunction(2, "CREATE VIEW auto.sameview AS select * from auto.auto_largedata;");
		Thread.sleep(GlobalConstants.MinWait);
		QueryEditor.ExecuteButton();
		Thread.sleep(GlobalConstants.MinWait);
		String sFlag1 = MultipleTerminal.TerminalConsoleCopy(2);
		//System.out.println(sFlag1);
		MultipleTerminal.CloseTerminal(2);
		Thread.sleep(GlobalConstants.MedWait);
		ObjectBrowserPane.Auto_Import_Navigation();
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.setSchema();
		Thread.sleep(GlobalConstants.MedWait);			
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
		BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);
		 */


		/*
	public static String searchtext()
	{
		try {
			String stringSearch = "02/22/17";
			// Open the file c:\test.txt as a buffered reader
			BufferedReader bf = new BufferedReader(new FileReader("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log"));

			// Start a line count and declare a string to hold our current line.
			int linecount = 0;
			String line;

			// Let the user know what we are searching for
			System.out.println("Searching for " + stringSearch + " in file...");

			// Loop through each line, stashing the line into our line variable.
			while (( line = bf.readLine()) != null){
				// Increment the count and find the index of the word
				linecount++;
				int indexfound = line.indexOf(stringSearch);

				// If greater than -1, means we found the word
				if (indexfound > -1) {
					System.out.println("Word was found at position " + indexfound + " on line " + linecount);
				}
			}

			// Close the file after done searching
			bf.close();
		}
		catch (IOException e) {
			System.out.println("IO Error Occurred: " + e.toString());
		}

		return null ;
	}

	public static void main(String[] args) throws Exception {



		try {
			String stringSearch = "02/22/17";
			// Open the file c:\test.txt as a buffered reader
			BufferedReader bf = new BufferedReader(new FileReader("C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\logs\\Data Studio.log"));

			// Start a line count and declare a string to hold our current line.
			int linecount = 0;
			String line;

			// Let the user know what we are searching for
			System.out.println("Searching for " + stringSearch + " in file...");

			// Loop through each line, stashing the line into our line variable.
			while (( line = bf.readLine()) != null){
				// Increment the count and find the index of the word
				linecount++;
				int indexfound = line.indexOf(stringSearch);

				// If greater than -1, means we found the word
				if (indexfound > -1) {
					System.out.println("Word was found at position " + indexfound + " on line " + linecount);
				}
			}

			// Close the file after done searching
			bf.close();
		}
		catch (IOException e) {
			System.out.println("IO Error Occurred: " + e.toString());
		}*/
	}


}



