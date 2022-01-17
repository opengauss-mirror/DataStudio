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

/*************************************************************************
TITLE - UTILITY FUNCTIONS
DESCRIPTION - REGULAR REUSABLE FUNCTIONS 
MODIFICATION HISTORY - 
TEST CASES COVERED - NA
 *************************************************************************/
package support_functions;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import object_repository.GlobalConstants;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import autoitx4java.AutoItX;


public class UtilityFunctions {

	public static String GetExcelCellValue(String excelName,String sheetName,int rowNum,int cellNum)
	{
		String CellValue;
		try
		{
			if(excelName.contains("Results"))
			{	
				String sResultFolder = excelName.substring(0,excelName.lastIndexOf("."));
				FileInputStream fis=new FileInputStream(GlobalConstants.TestResultPath.concat(sResultFolder+"/").concat(excelName));
				Workbook wb=WorkbookFactory.create(fis);
				Sheet SheetName=wb.getSheet(sheetName);
				Cell CellName = SheetName.getRow(rowNum).getCell(cellNum);
				if(CellName.getCellType()==0)
				{
					double d = CellName.getNumericCellValue();
					int i = (int) d;
					CellValue = Integer.toString(i);
					return CellValue;
				}
				else
				{
					CellValue=CellName.getStringCellValue();
					return CellValue;
				}
			}
			else
			{		
				FileInputStream fis=new FileInputStream(GlobalConstants.TestDataPath.concat(excelName));
				Workbook wb=WorkbookFactory.create(fis);
				Sheet SheetName=wb.getSheet(sheetName);
				Cell CellName = SheetName.getRow(rowNum).getCell(cellNum);
				if(CellName.getCellType()==0)
				{
					double d = CellName.getNumericCellValue();
					int i = (int) d;
					CellValue = Integer.toString(i);
					return CellValue;
				}
				else
				{
					CellValue=CellName.getStringCellValue();
					return CellValue;
				}
			}
		}
		catch(Exception e)
		{
			return "";
		}
	}

	public static int GetRowCount(String excelName,String Sheet)
	{
		try
		{
			FileInputStream fis=new FileInputStream(GlobalConstants.TestDataPath.concat(excelName));
			Workbook wb=WorkbookFactory.create(fis);
			Sheet SheetName=wb.getSheet(Sheet);
			int iRowCount = SheetName.getLastRowNum();
			return iRowCount;
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	public static void WriteToExcel(String ExcelName,String SheetName,int RowNum,int CellNum,String CellValue)
	{
		try
		{
			String sResultFolder = ExcelName.substring(0,ExcelName.lastIndexOf("."));
			FileInputStream fis=new FileInputStream(GlobalConstants.TestResultPath.concat(sResultFolder+"/").concat(ExcelName));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			Sheet s=workbook.getSheet(SheetName);
			Cell CellName = s.getRow(RowNum).getCell(CellNum);
			CellName.setCellValue(CellValue);
			fis.close();
			FileOutputStream fos=new FileOutputStream(new File(GlobalConstants.TestResultPath.concat(sResultFolder+"/").concat(ExcelName)));
			workbook.write(fos);
			workbook.close();
			fos.close();
		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
	}

	public static String CreateResultFile(String sExecutionType,String sModule) throws IOException
	{
		String ResultExcel,TemplateExcel,ResultPath,ScreenshotPath;
		if (sExecutionType == "FunctionalTest")
		{
			ResultExcel = "IDE_Functional_Automation_Test_Results_"+sModule;
			TemplateExcel = "Templates/Template_IDE_Functional_Automation_Test_Results.xlsx";

		}
		else
		{
			ResultExcel = "IDE_Smoke_Automation_Test_Results_"+sModule;
			TemplateExcel = "Templates/Template_IDE_Smoke_Automation_Test_Results.xlsx";
		}
		ResultExcel = UtilityFunctions.CurrentDateTime(ResultExcel);
		ResultPath = GlobalConstants.TestResultPath+ResultExcel;
		ScreenshotPath = GlobalConstants.TestResultPath+ResultExcel+"/Screenshots";
		new File(ResultPath).mkdir();
		new File(ScreenshotPath).mkdir();
		File ResultFile = new File(ResultPath.concat("/").concat(ResultExcel+".xlsx"));
		File TemplateFile = new File(GlobalConstants.TestResultPath.concat(TemplateExcel));
		ResultFile.createNewFile();
		FileInputStream fis = new FileInputStream(TemplateFile);
		FileOutputStream fos = new FileOutputStream(ResultFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		workbook.write(fos);
		workbook.close();
		fis.close();
		fos.close();
		return ResultExcel+".xlsx";
	}

	public static String CurrentDateTime(String fileName){

		int day, month,year;
		int second, minute, hour;
		GregorianCalendar date = new GregorianCalendar();
		day = date.get(Calendar.DAY_OF_MONTH);
		month = date.get(Calendar.MONTH);
		year = date.get(Calendar.YEAR);
		second = date.get(Calendar.SECOND);
		minute = date.get(Calendar.MINUTE);
		hour = date.get(Calendar.HOUR);
		String path = fileName+"_"+day+"_"+(month+1)+"_"+year+"_"+hour+"_"+minute+"_"+second;
		return path;
	}

	public static void ScrollDown(String title, String text, String controlId, int iCount) throws Exception
	{
		AutoItX x = new AutoItX();
		x.controlFocus(title, "", controlId);
		Thread.sleep(GlobalConstants.MedWait);
		x.mouseWheel("down",iCount);
	}

	public static void ScrollUp(String title, String text, String controlId, int iCount) throws Exception
	{
		AutoItX x = new AutoItX();
		x.controlFocus(title, "", controlId);
		Thread.sleep(GlobalConstants.MedWait);
		x.mouseWheel("up",iCount);
	}

	public static void TakeScreenshot(String sTestCaseID, String ResultExcel) throws Exception
	{
		Robot r = new Robot();
		String sScreenshotFolder = GlobalConstants.TestResultPath+ResultExcel.substring(0,ResultExcel.lastIndexOf("."))+"/Screenshots/";
		String sFormat = "jpg";
		String sFileName = sScreenshotFolder+sTestCaseID+".jpg";
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
		BufferedImage screenFullImage = r.createScreenCapture(screenRect);
		ImageIO.write(screenFullImage,sFormat,new File(sFileName));
	}
	public static void Screenshot(String Filename) throws Exception
	{
		Robot r = new Robot();
		String sScreenshotFolder = GlobalConstants.ActualImage;
		String sFormat = "png";
		String sFileName = sScreenshotFolder +Filename+ ".png";
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage screenFullImage = r.createScreenCapture(screenRect);
		ImageIO.write(screenFullImage, sFormat, new File(sFileName));
	}

	public static boolean CompareImage(String ExpectedWC,String ExpectedWOC,String Actual) {

		String file1 = GlobalConstants.ActualImage+Actual+".png";
		String file2 = GlobalConstants.ExpectedImage+ExpectedWC+".png";
		String file3 = GlobalConstants.ExpectedImage+ExpectedWOC+".png";
		Boolean Result = null;
		Image image1 = Toolkit.getDefaultToolkit().getImage(file1);
		Image image2 = Toolkit.getDefaultToolkit().getImage(file2);
		Image image3 = Toolkit.getDefaultToolkit().getImage(file3);
		try {

			PixelGrabber grab1 =new PixelGrabber(image1, 0, 0, -1, 150, false);
			PixelGrabber grab2 =new PixelGrabber(image2, 0, 0, -1, 150, false);
			PixelGrabber grab3 =new PixelGrabber(image3, 0, 0, -1, 150, false);

			int[] data1 = null;

			if (grab1.grabPixels()) {
				int width = grab1.getWidth();
				int height = grab1.getHeight();
				data1 = new int[width * height];
				data1 = (int[]) grab1.getPixels();
			}

			int[] data2 = null;

			if (grab2.grabPixels()) {
				int width = grab2.getWidth();
				int height = grab2.getHeight();
				data2 = new int[width * height];
				data2 = (int[]) grab2.getPixels();
			}

			int[] data3 = null;

			if (grab3.grabPixels()) {
				int width = grab3.getWidth();
				int height = grab3.getHeight();
				data3 = new int[width * height];
				data3 = (int[]) grab3.getPixels();
			}

			if(java.util.Arrays.equals(data1, data2)||java.util.Arrays.equals(data1, data3))
				Result = true;
			else 
				Result = false;

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return Result;

	}
	public static void KeyPress(int Key, int Count) throws Exception
	{
		Robot r = new Robot();
		for(int i=1;i<=Count;i++)
		{
			r.keyPress(Key);
			Thread.sleep(GlobalConstants.MinWait);
		}

	}

	public static void KeyRelease(int Key, int Count) throws Exception
	{
		Robot r = new Robot();
		for(int i=1;i<=Count;i++)
		{
			r.keyRelease(Key);
			Thread.sleep(GlobalConstants.MinWait);
		}

	}

	public static void SaveResult(String ExcelName,String sModule) throws Exception
	{
		String sResultFolder = ExcelName.substring(0,ExcelName.lastIndexOf("."));
		String sPath = GlobalConstants.TestResultPath+sResultFolder+"/"+ExcelName;
		String sTitle = "Microsoft Excel - "+ExcelName;
		AutoItX x = new AutoItX();
		if(ExcelName.contains("Smoke"))
		{	
			x.run(GlobalConstants.sExcelExePath64+sPath);
			x.run(GlobalConstants.sExcelExePath32+sPath);
			x.winActivate(sTitle);
			Thread.sleep(GlobalConstants.MedWait);
			x.controlClick(sTitle,"","EXCEL71","left",1, 850,250);
			Thread.sleep(GlobalConstants.MedWait);
			x.controlClick(sTitle,"","EXCEL71","left",2, 540,106);
			Thread.sleep(GlobalConstants.MedWait);
			x.controlClick(sTitle,"","EXCEL71","left",2, 590,106);
			Thread.sleep(GlobalConstants.MedWait);
			x.controlClick(sTitle,"","EXCEL71","left",1, 64,54);
			Thread.sleep(GlobalConstants.MedWait);
		}	
		else
		{
			int	iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, "IDE_Driver");
			int XCord = 880, YCord=150;
			x.run(GlobalConstants.sExcelExePath64+sPath);
			x.run(GlobalConstants.sExcelExePath32+sPath);
			for(int i=1;i<=iRowCount;i++)
			{
				String sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDE_Driver", i+1, 3);
				if(sExecute.equals("Yes"))
				{
					x.winActivate(sTitle);
					Thread.sleep(GlobalConstants.MedWait);
					x.controlClick(sTitle,"","EXCEL71","left",1, XCord,YCord);
					Thread.sleep(GlobalConstants.MedWait);
					x.controlClick(sTitle,"","EXCEL71","left",2, 423,106);
					Thread.sleep(GlobalConstants.MedWait);
					x.controlClick(sTitle,"","EXCEL71","left",2, 472,106);
					Thread.sleep(GlobalConstants.MedWait);
					x.controlClick(sTitle,"","EXCEL71","left",1, 64,54);
					Thread.sleep(GlobalConstants.MedWait);
				}
				YCord=YCord+25;
			}
		}
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_S,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.ProcessClose("EXCEL.EXE");
	}

	public static void ProcessClose(String sProcessName)
	{
		AutoItX x = new AutoItX();
		if(x.processExists(sProcessName)!=0)
			x.processClose(sProcessName);
	}

	public static String GetClipBoard() throws Exception
	{
		AutoItX x = new AutoItX();
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		Thread.sleep(GlobalConstants.MinWait);
		return(x.clipGet());
	}

	public static String CreateTextResultFile(String sExecutionType,String sModule) throws IOException
	{
		String TextResult,ResultPath;
		if (sExecutionType == "FunctionalTest")
			TextResult = "IDE_Functional_Automation_Test_Results_"+sModule;
		else
			TextResult = "IDE_Smoke_Automation_Test_Results_"+sModule;
		ResultPath = GlobalConstants.TestResultPath;
		File ResultFile = new File(ResultPath.concat(TextResult+".txt"));
		ResultFile.createNewFile();
		Writer output;
		output = new FileWriter(ResultFile,false);
		output.write("");
		output.flush();
		output.close();
		return TextResult+".txt";
	}

	public static void WriteToText(String sTextResultFile,String FinalStatus)
	{
		try
		{
			String FileName =GlobalConstants.TestResultPath.concat(sTextResultFile);
			Writer output;
			output = new FileWriter(FileName,true);
			output.append(FinalStatus);
			output.append("\r\n");
			output.close();
		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
	}

	public static void GetCPUPercentage(String sFunction) throws Exception
	{
		String sCPUResultFile = GlobalConstants.TestResultPath+"CPUResult.txt";
		File cpufile = new File(sCPUResultFile);
		cpufile.createNewFile();
		String ss = null;
		String sPercentage=null;
		int j = 3;
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec("wmic cpu get loadpercentage");
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while ((ss = stdInput.readLine()) != null && j > 0) {
			if(j == 1)
				sPercentage = (ss.trim()+"%");
			j--;
		}
		UtilityFunctions.WriteToText("CPUResult.txt", UtilityFunctions.CurrentDateTime("CPU Percentage for "+sFunction+" is: "+sPercentage+" at"));
	}

	public static void UpdateTMSSResult(String sResultExcel,String sTextResultFile,String sFunctionality){
		//Generate text file report for TMSS integration
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sSmokeTestDataFile,"Smoke_Test_Data");
		for(int i=1;i<=iRowCount;i++)
		{
			String sFeature = UtilityFunctions.GetExcelCellValue(sResultExcel,"Smoke_Test_Results",i+2,2);
			if(sFeature.equals(sFunctionality))
			{
				String sTestCaseID=UtilityFunctions.GetExcelCellValue(sResultExcel,"Smoke_Test_Results",i+2,3);
				String sStatus=UtilityFunctions.GetExcelCellValue(sResultExcel,"Smoke_Test_Results",i+2,5);
				if(sStatus.equals("Passed")||sStatus.equals("Failed"))
				{
					String sFinalStatus = sTestCaseID+" Passed";
					UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
				}
			}	
		}
	}
	public static String CopyFile(String sSourceFile, String sDestinationFile) throws IOException
	{
		File iFile = new File(sSourceFile);
		File oFile = new File(sDestinationFile);

		InputStream inStream = new FileInputStream(iFile);
		OutputStream outStream = new FileOutputStream(oFile);

		byte[] buffer = new byte[1024];
		int length;
		while((length = inStream.read(buffer))>0)
		{
			outStream.write(buffer,0 ,length);
		}
		inStream.close();
		outStream.close();
		if(oFile.exists())
			return "Success";
		else
			return "Failure";
	}
	public static boolean CompareImage(String ExpectedWC,String ExpectedWOC,String Actual,String Feature) throws Exception {

		if(Feature.equalsIgnoreCase("Syntax_Highlight"))
		{
			Robot r = new Robot();
			String sScreenshotFolder = GlobalConstants.ActualImage+"/";
			String sFormat = "png";
			String sFileName = sScreenshotFolder+Actual+".png";
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage screenFullImage = r.createScreenCapture(screenRect);
			ImageIO.write(screenFullImage, sFormat, new File(sFileName));
			String file1 = GlobalConstants.ActualImage+"/"+Actual+".png";
			String file2 = GlobalConstants.ExpectedImage+"/"+ExpectedWC+".png";
			String file3 = GlobalConstants.ExpectedImage+"/"+ExpectedWOC+".png";
			Boolean Result = null;
			Image image1 = Toolkit.getDefaultToolkit().getImage(file1);
			Image image2 = Toolkit.getDefaultToolkit().getImage(file2);
			Image image3 = Toolkit.getDefaultToolkit().getImage(file3);
			try {

				PixelGrabber grab1 =new PixelGrabber(image1, 0, 0, -1, 150, false);
				PixelGrabber grab2 =new PixelGrabber(image2, 0, 0, -1, 150, false);
				PixelGrabber grab3 =new PixelGrabber(image3, 0, 0, -1, 150, false);

				int[] data1 = null;

				if (grab1.grabPixels()) {
					int width = grab1.getWidth();
					int height = grab1.getHeight();
					data1 = new int[width * height];
					data1 = (int[]) grab1.getPixels();
				}

				int[] data2 = null;

				if (grab2.grabPixels()) {
					int width = grab2.getWidth();
					int height = grab2.getHeight();
					data2 = new int[width * height];
					data2 = (int[]) grab2.getPixels();
				}

				int[] data3 = null;

				if (grab3.grabPixels()) {
					int width = grab3.getWidth();
					int height = grab3.getHeight();
					data3 = new int[width * height];
					data3 = (int[]) grab3.getPixels();
				}

				if(java.util.Arrays.equals(data1, data2)||java.util.Arrays.equals(data1, data3))
					Result = true;
				else 
					Result = false;

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return Result; 
		}
		else
		{
			Robot r = new Robot();
			String sScreenshotFolder = GlobalConstants.ActualImage+Feature+"/";
			String sFormat = "png";
			String sFileName = sScreenshotFolder +Actual+".png";
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage screenFullImage = r.createScreenCapture(screenRect);
			ImageIO.write(screenFullImage, sFormat, new File(sFileName));

			String file1 = GlobalConstants.ActualImage+Feature+"/"+Actual+".png";
			String file2 = GlobalConstants.ExpectedImage+Feature+"/"+ExpectedWC+".png";
			String file3 = GlobalConstants.ExpectedImage+Feature+"/"+ExpectedWOC+".png";
			Boolean Result = null;
			Image image1 = Toolkit.getDefaultToolkit().getImage(file1);
			Image image2 = Toolkit.getDefaultToolkit().getImage(file2);
			Image image3 = Toolkit.getDefaultToolkit().getImage(file3);
			try {

				PixelGrabber grab1 =new PixelGrabber(image1, 0, 0, -1, 150, false);
				PixelGrabber grab2 =new PixelGrabber(image2, 0, 0, -1, 150, false);
				PixelGrabber grab3 =new PixelGrabber(image3, 0, 0, -1, 150, false);

				int[] data1 = null;

				if (grab1.grabPixels()) {
					int width = grab1.getWidth();
					int height = grab1.getHeight();
					data1 = new int[width * height];
					data1 = (int[]) grab1.getPixels();
				}

				int[] data2 = null;

				if (grab2.grabPixels()) {
					int width = grab2.getWidth();
					int height = grab2.getHeight();
					data2 = new int[width * height];
					data2 = (int[]) grab2.getPixels();
				}

				int[] data3 = null;

				if (grab3.grabPixels()) {
					int width = grab3.getWidth();
					int height = grab3.getHeight();
					data3 = new int[width * height];
					data3 = (int[]) grab3.getPixels();
				}

				if(java.util.Arrays.equals(data1, data2)||java.util.Arrays.equals(data1, data3))
					Result = true;
				else 
					Result = false;

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return Result; 
		}
	}

	public static boolean deleteDir(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return file.delete();
	}
	
	public static void appendToFile(String filePath, String line) throws IOException{

		Writer output;
		output = new BufferedWriter(new FileWriter(filePath, true)); 
		output.append(line);
		output.close();
		}
	
	public static boolean deleteLineFromFile(String inputFilePath, String tempFilePath, String lineToRemove) throws Exception{

		File inputFile = new File(inputFilePath);
		File tempFile = new File(tempFilePath);

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String currentLine;

		if(lineToRemove.equalsIgnoreCase("-logginglevel=INFO")||lineToRemove.equalsIgnoreCase("-logginglevel=TRACE")
		||lineToRemove.equalsIgnoreCase("-logginglevel=DEBUG")||lineToRemove.equalsIgnoreCase("-logginglevel=ERROR")){
		while((currentLine = reader.readLine()) != null) {
		// trim newline when comparing with lineToRemove
		String trimmedLine = currentLine.trim();
		if(trimmedLine.equals(lineToRemove)) continue;
		writer.write(currentLine + System.getProperty("line.separator"));
		}
		}
		writer.close(); 
		reader.close(); 
		inputFile.delete();
		boolean successful = tempFile.renameTo(inputFile);
		return successful;
		}

	public static String searchFile(String filePath, String searchQuery) throws IOException
	{
		searchQuery = searchQuery.trim();
		BufferedReader br = null;
		String found = null;

		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.contains(searchQuery))
				{

					found = line;
					break;

				}
				else
				{
					found = "NOT FOUND";
				}
			}
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
			}
			catch (Exception e)
			{
				System.err.println("Exception while closing bufferedreader " + e.toString());
			}
		}

		return found;
	}

}

