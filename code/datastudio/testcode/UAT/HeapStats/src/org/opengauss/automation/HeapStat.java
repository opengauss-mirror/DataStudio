package org.opengauss.automation;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeapStat
{
    public static String 				logFile  		= "";
    private HeapParams[] 			    heapList 		= null;
    private	HeapParams 				    heapParameters 	= null;
    private HeapInsert 					insertHeap 		= null;
    private int                         insertSize      = 0;
    private String 						logging			= "";
    private String 						basePath		= "";    
    private String 						hostIP 			= "";
    private String 						portNo 			= "";
    private String 						userName 		= "";
    private String 						dbName  		= "";
    private String 						password 		= "";
    private	String 						connectDB 		= "";
    private int 						waitTime		= 1; //Seconds
    private Logger					    logger			= null;
    public  HeapStat()
    {

        basePath	= new File("").getAbsolutePath();

		if(new File(basePath+File.separatorChar+"config.json").exists() )
		{		   
		   try
		   {
			   String configData = JSONProcessor.readFile(basePath+File.separatorChar+"config.json");
			   connectDB 	= JSONProcessor.getValue(configData, "connectdb").trim();
			   hostIP 	 	= JSONProcessor.getValue(configData, "host").trim();
			   portNo 	 	= JSONProcessor.getValue(configData, "port").trim();
			   userName  	= JSONProcessor.getValue(configData, "user").trim();
			   dbName  	 	= JSONProcessor.getValue(configData, "db").trim();
			   password  	= JSONProcessor.getValue(configData, "password").trim();
			   logging		= JSONProcessor.getValue(configData, "logging").trim(); 
			   insertSize   = Integer.parseInt(JSONProcessor.getValue(configData, "insertsize").trim());
			   waitTime		= Integer.parseInt(JSONProcessor.getValue(configData, "waittime").trim()); 
		   }
		   catch(Exception e)
		   {
			   System.out.println("Invalid Configurations Input");
			   System.exit(0);
		   }
		}			


        
		if(connectDB.equalsIgnoreCase("yes"))
		{
	        insertHeap 	= new HeapInsert();
	        insertHeap.setHostIP		(hostIP);
	        insertHeap.setPortNo		(portNo);
	        insertHeap.setUserName		(userName);
	        insertHeap.setDbName		(dbName);
	        insertHeap.setPassword		(password);			
		}
		
    	if(logging.equalsIgnoreCase("yes"))
    	{
    		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    		logger = new Logger();
    		logger.setLogFile(basePath+File.separator+"HeapStat_"+ft.format(new Date())+".log");                     
    	}


    }

	public static long getValue(String line)
    {
        try
        {
            if (line.indexOf("=") != -1) line = line.substring( line.indexOf("=")+1, line.length() ).trim();
            if (line.indexOf("(") != -1) line = line.substring( 0, line.indexOf("(") ).trim();
            return new Long(line)  / (1024*1024);
        }
        catch(Exception e)
        {
            return 0;
        }
    }


    public  void checkHeapSize() 
    {

        long 				maxHeapSize 	= 0;
        long   				newSize 		= 0;
        long    			maxNewSize 		= 0;
        long    			oldSize 		= 0;
        long    			edenUsed		= 0;
        long    			fromUsed		= 0;
        long    			toUsed			= 0;
        boolean 			edenFlag 		= false;
        boolean 			fromFlag		= false;
        boolean 			toSpace 		= false;


        try
        {
            //************************************************************************//
            //************************************************************************//
            int index = 0;
            while (true)
            {
                String 		processID = getProcessID("Data Studio.exe","Console");
                
                if ( null == heapList || index == 0 )     heapList 	= new HeapParams[insertSize];
                
                if(processID.trim().length() > 1)
                {
                    if(index == 0) System.out.print("\n");
                    String[] 	commands  = {"jmap","-heap",processID};
                    Runtime 	rt 	      = Runtime.getRuntime();
                    Process 	proc 	  = rt.exec(commands);
                    proc.waitFor();
                    
                    InputStream 		stdin 	= proc.getInputStream();
                    InputStreamReader 	isr		= new InputStreamReader(stdin);
                    BufferedReader 		br 		= new BufferedReader(isr);
                    String 				line    = null;
                    //************************************************************************//
                    //************************************************************************//


                    while ( (line = br.readLine()) != null)
                    {
                        line = line.trim();
                        if(line.startsWith("MaxHeapSize")) 	maxHeapSize	= getValue(line);
                        if(line.startsWith("NewSize")) 		newSize 	= getValue(line);
                        if(line.startsWith("MaxNewSize")) 	maxNewSize 	= getValue(line);
                        if(line.startsWith("OldSize"))		oldSize 	= getValue(line);

                        if(line.startsWith("Eden Space:"))
                        {
                            edenFlag 	= true;
                            fromFlag	= false;
                            toSpace 	= false;
                        }

                        else if(line.startsWith("From Space:"))
                        {
                            edenFlag 	= false;
                            fromFlag	= true;
                            toSpace 	= false;
                        }
                        else if(line.startsWith("To Space:"))
                        {
                            edenFlag 	= false;
                            fromFlag	= false;
                            toSpace 	= true;
                        }

                        if(edenFlag &&  line.startsWith("used"))	 	edenUsed 		= getValue(line);
                        if(fromFlag &&  line.startsWith("used"))	 	fromUsed 		= getValue(line);
                        if(toSpace  &&  line.startsWith("used"))	 	toUsed 			= getValue(line);
                        


                    }
                    //Clean Connection
                    br.close();
                    isr.close();
                    stdin.close();
                    proc.destroy(); 
                    
                    heapParameters = new HeapParams();
                    heapParameters.setSystemDate(new Date());
                    heapParameters.setEdenUsed(edenUsed);
                    heapParameters.setFromUsed(fromUsed);
                    heapParameters.setMaxHeapSize(maxHeapSize);
                    heapParameters.setMaxNewSize(maxNewSize);
                    heapParameters.setOldSize(oldSize);
                    heapParameters.setProcessID(processID);
                    heapParameters.setSystemDate(new Date());
                    heapParameters.setTotalUsed(edenUsed+fromUsed+toUsed);
                    heapParameters.setNewSize(newSize);

                    if (index < insertSize)
                    {
                        heapList[index] = heapParameters;
                        System.out.print(index + " : " + heapList[index]);
                    }
                    else
                    {
                    	

                        
                    	if(null != insertHeap   && connectDB.equalsIgnoreCase("yes")   && index == insertSize)
                    	{
                        	insertHeap.setParamList(heapList);
                            new Thread(() ->	
                            {
                            	insertHeap.insertParams();                            	
                            }).start();                  		
                    	}
                    	
                    	if(null != insertHeap  && logging.equalsIgnoreCase("yes") && logger != null && index == insertSize )
                    	{
                			logger.setParamList(heapList);
                    		new Thread(() ->	
                    		{
                    			logger.logParametes();                    			
                    		}).start();
                    	}   
                    	
                        index	 = -1;  
                    	heapList = null;
                    	System.gc(); 
                    	  
                    }

                    


                }
                else
                {
                    System.out.print("* ");
                }

                index++;
                heapParameters 	=   null;
                processID   	=   "";
                maxHeapSize		=	0;
                newSize			=	0;
                maxNewSize		=	0;
                oldSize			=	0;
                edenUsed		=	0;
                fromUsed		=	0;
                toUsed			=	0;
                Thread.sleep(waitTime * 1000);

            }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }


    public static void main(String[] args)
    {
        HeapStat hs = new HeapStat();
        try
        {
            hs.checkHeapSize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getProcessID(String processName, String sessionName)
    {
        try
        {
            String processID = "";
            Runtime runtime = Runtime.getRuntime();
            String cmds[] = {"cmd", "/c", "tasklist"};
            Process proc = runtime.exec(cmds);
            InputStream inputstream = proc.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            String line;
            while ((line = bufferedreader.readLine()) != null)
            {
                if(line.indexOf(processName)!= -1 && line.indexOf(sessionName)!= -1) // "Data Studio.exe" && "Console"
                {
                    processID  = line.substring(line.indexOf(processName)+ processName.length(), line.indexOf(sessionName)).trim();
                    return processID;
                }
            }
            proc.destroy();
            bufferedreader.close();
            inputstreamreader.close();
            inputstream.close();
            return "";
        }
        catch(Exception e)
        {
            return "";
        }
    }


}
