package org.opengauss.automation;

import java.util.Date;

public class HeapParams
{
    public long getMaxHeapSize() {
        return maxHeapSize;
    }
    public void setMaxHeapSize(long maxHeapSize) {
        this.maxHeapSize = maxHeapSize;
    }
    public long getNewSize() {
        return newSize;
    }
    public void setNewSize(long newSize) {
        this.newSize = newSize;
    }
    public long getMaxNewSize() {
        return maxNewSize;
    }
    public void setMaxNewSize(long maxNewSize) {
        this.maxNewSize = maxNewSize;
    }
    public long getOldSize() {
        return oldSize;
    }
    public void setOldSize(long oldSize) {
        this.oldSize = oldSize;
    }
    public long getEdenUsed() {
        return edenUsed;
    }
    public void setEdenUsed(long edenUsed) {
        this.edenUsed = edenUsed;
    }
    public long getFromUsed() {
        return fromUsed;
    }
    public void setFromUsed(long fromUsed) {
        this.fromUsed = fromUsed;
    }
    public long getToUsed() {
        return toUsed;
    }
    public void setToUsed(long toUsed) {
        this.toUsed = toUsed;
    }
    public String getProcessID() {
        return processID;
    }
    public void setProcessID(String processID) {
        this.processID = processID;
    }
    public Date getSystemDate() {
        return systemDate;
    }
    public void setSystemDate(Date systemDate) {
        this.systemDate = systemDate;
    }

    public long getTotalUsed() {
        return totalUsed;
    }
    public void setTotalUsed(long totalUsed) {
        this.totalUsed = totalUsed;
    }

    public String toString()
    {
        String heapParams = "";
        try
        {
            heapParams 			=
                    		getProcessID()		+ "\t\t"+
                            getSystemDate() 	+ "\t\t"+
                            getMaxHeapSize()	+ "\t\t"+
                            getNewSize() 		+ "\t\t"+
                            getOldSize()    	+ "\t\t"+
                            getEdenUsed()		+ "\t\t"+
                            getFromUsed()		+ "\t\t"+
                            getToUsed()			+ "\t\t"+
                            getTotalUsed()		+ "\n";
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return heapParams;
    }
    private long 	maxHeapSize 	= 0;
    private long   	newSize 		= 0;
    private long    maxNewSize 		= 0;
    private long    oldSize 		= 0;
    private long    edenUsed		= 0;
    private long    fromUsed		= 0;
    private long    toUsed			= 0;
    private String 	processID   	= "";
    private Date	systemDate		= null;
    private long	totalUsed		= 0;

}
