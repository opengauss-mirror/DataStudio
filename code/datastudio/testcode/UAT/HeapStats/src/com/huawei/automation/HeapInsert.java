package com.huawei.automation;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class HeapInsert
{

    private String userName 	= "";
    private String password 	= "";
    private String portNo 		= "";
    private String hostIP		= "";
    private String dbName   	= "";
    private String insertString = "insert into public.heap_statistics (execution_date, process_id, max_heap, eden_max,eden_size, eden_used,old_size,from_used,to_used,total_used) values (?,?,?,?,?,?,?,?,?,?)" ;
    
    private HeapParams[] paramList = null;

    public HeapParams[] getParamList()
	{
		return paramList;
	}


	public void setParamList(HeapParams[] paramList)
	{
		this.paramList = paramList;
	}


	public  void insertParams()
    {
        try
        {
            Class.forName("org.postgresql.Driver");

            String 				connecitonString 	= "jdbc:postgresql://"+hostIP+":"+portNo+"/"+dbName;
            Connection 			connectionObject	= DriverManager.getConnection(connecitonString, userName,password);
            PreparedStatement 	preparedStatement	= connectionObject.prepareStatement(insertString);
            int txnResult = 0;
            for(HeapParams heapParam : paramList)
            {
            	 
                preparedStatement.setTimestamp(1,  new java.sql.Timestamp( heapParam.getSystemDate().getTime() ));
                preparedStatement.setLong(2, Integer.parseInt(heapParam.getProcessID()));
                preparedStatement.setLong(3, heapParam.getMaxHeapSize());
                preparedStatement.setLong(4, heapParam.getMaxNewSize());
                preparedStatement.setLong(5, heapParam.getNewSize());
                preparedStatement.setLong(6, heapParam.getEdenUsed());
                preparedStatement.setLong(7, heapParam.getOldSize());
                preparedStatement.setLong(8, heapParam.getFromUsed());
                preparedStatement.setLong(9, heapParam.getToUsed());
                preparedStatement.setLong(10,heapParam.getTotalUsed());
                txnResult = preparedStatement.executeUpdate();

                if (txnResult > 0)
                {
                    System.out.print("* ");
                }
                txnResult = 0;
            }
            paramList = null;
            preparedStatement.close();
            connectionObject.close();
            System.out.print("\n");
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }


    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPortNo()
    {
        return portNo;
    }

    public void setPortNo(String portNo)
    {
        this.portNo = portNo;
    }

    public String getHostIP()
    {
        return hostIP;
    }

    public void setHostIP(String hostIP)
    {
        this.hostIP = hostIP;
    }

    public String getDbName()
    {
        return dbName;
    }

    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }
}
