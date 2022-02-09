package org.opengauss.automation;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Logger 
{
	private String logFile = "";
    private HeapParams[] paramList = null;
    
    
    public String getLogFile() {
		return logFile;
	}

    
    public HeapParams[] getParamList()
	{
		return paramList;
	}


	public void setParamList(HeapParams[] paramList)
	{
		this.paramList = paramList;
	}
	
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public  void logParametes()
    {
    	try
    	{
            FileWriter 		fw 		= new FileWriter(getLogFile(), true);
            BufferedWriter 	bw 		= new BufferedWriter(fw);
            for(HeapParams heapParam : paramList)
            {
                bw.write(heapParam.toString());
                System.out.print("# ");
            }
            bw.close();
            fw.close();    
            System.out.print("* ");
            paramList = null;
    	}
    	catch(Exception e)
    	{
    		//System.out.println(e);
    		e.printStackTrace();
    	}
    }
}
