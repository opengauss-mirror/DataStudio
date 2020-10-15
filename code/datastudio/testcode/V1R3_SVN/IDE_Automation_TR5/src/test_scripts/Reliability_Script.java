package test_scripts;

import script_library.QueryEditor;
import support_functions.UtilityFunctions;

public class Reliability_Script {
	
	//Method to run single query
	public static void Single_Query_Exe(String ResultExcel,String sDataSheetName,String sResultSheetName) throws Exception
	{
		String sQuery;
		//sQuery = UtilityFunctions.GetExcelCellValue(ResultExcel,sDataSheetName,3,3);
		sQuery = "select * from pg_attribute;";
		int iCount = Integer.valueOf(UtilityFunctions.GetExcelCellValue(ResultExcel,sDataSheetName,3,2));
		for(int i=1;i<=iCount;i++)
		{
			UtilityFunctions.GetCPUPercentage("Single_Query_Execution"+i);
			QueryEditor.SingleQueryExe(sQuery,"Valid");
		}
	}	
	//Method to create tables
	public static void Create_Table(String ResultExcel,String sDataSheetName,String sResultSheetName) throws Exception
	{
		String sTableName, sCreateTableQuery;
		int iCount = Integer.valueOf(UtilityFunctions.GetExcelCellValue(ResultExcel, sDataSheetName, 1,2));
		for(int i=1;i<=iCount;i++)
		{
			UtilityFunctions.GetCPUPercentage("Create_Table"+i);
			sTableName = "autoemp"+i;
			sCreateTableQuery = "CREATE TABLE public."+sTableName+"(empid bigint,ename text,salary bigint,deptno bigint)";
			QueryEditor.SingleQueryExe(sCreateTableQuery,"Valid");
		}
	}
	
	//Method to create tables
		public static void Drop_Table(String ResultExcel,String sDataSheetName) throws Exception
		{
			String sTableName, sDropTableQuery;
			int iCount = Integer.valueOf(UtilityFunctions.GetExcelCellValue(ResultExcel, sDataSheetName, 1,2));
			for(int i=1;i<=iCount;i++)
			{
				UtilityFunctions.GetCPUPercentage("Drop_Table"+i);
				sTableName = "autoemp"+i;
				sDropTableQuery = "DROP TABLE public."+sTableName;
				QueryEditor.SingleQueryExe(sDropTableQuery,"Valid");
			}
		}
	
	
	//Method to create functions
		public static void Create_Function(String ResultExcel,String sDataSheetName,String sResultSheetName) throws Exception
		{
			String sFunctionName, sCreateFunctionQuery;
			int iCount = Integer.valueOf(UtilityFunctions.GetExcelCellValue(ResultExcel, sDataSheetName, 2,2));
			for(int i=1;i<=iCount;i++)
			{
				UtilityFunctions.GetCPUPercentage("Create_Function"+i);
				sFunctionName = "autofunc"+i;
				sCreateFunctionQuery = "CREATE OR REPLACE FUNCTION public."+sFunctionName+"()RETURNS integer LANGUAGE plpgsql AS $$ DECLARE c INT; d INT; dat date; BEGIN c := 0; c := 50; c := c + 10; d := c + 20; dat := current_date + 2; return d; END;$$";
				QueryEditor.SingleQueryExe(sCreateFunctionQuery,"Valid");
			}
		}
	//Method to drop functions
		public static void Drop_Function(String ResultExcel,String sDataSheetName) throws Exception
		{
			String sFunctionName, sDropFunctionQuery;
			int iCount = Integer.valueOf(UtilityFunctions.GetExcelCellValue(ResultExcel, sDataSheetName, 2,2));
			for(int i=1;i<=iCount;i++)
			{
				UtilityFunctions.GetCPUPercentage("Drop_Function"+i);
				sFunctionName = "autofunc"+i;
				sDropFunctionQuery = "DROP FUNCTION public."+sFunctionName;
				QueryEditor.SingleQueryExe(sDropFunctionQuery,"Valid");
			}
		}
	}
