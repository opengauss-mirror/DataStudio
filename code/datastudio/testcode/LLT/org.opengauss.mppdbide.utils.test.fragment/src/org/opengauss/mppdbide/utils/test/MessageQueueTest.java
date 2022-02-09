package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.GlobaMessageQueueUtil;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;
import org.opengauss.mppdbide.utils.messaging.MessageType;



public class MessageQueueTest
{

    @Before
    public void setUp() throws Exception
    {
    	MPPDBIDELoggerUtility.setArgs(null);
        while (!GlobaMessageQueueUtil.getInstance().getMessageQueue().isEmpty())
        {
            GlobaMessageQueueUtil.getInstance().getMessageQueue().pop();
        }
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void test_SingleMessageQueue()
    {
        Message message = new Message(MessageType.INFO, "Dummy message");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message);
        Message returnMessage = GlobaMessageQueueUtil.getInstance().getMessageQueue().pop();
        assertEquals(message.getMessage(), returnMessage.getMessage());
        assertEquals(message.getType(), returnMessage.getType());
        
        message = new Message(MessageType.WARN, "Dummy message");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message);
        returnMessage = GlobaMessageQueueUtil.getInstance().getMessageQueue().pop();
        assertEquals(message.getMessage(), returnMessage.getMessage());
        assertEquals(message.getType(), returnMessage.getType());

        message = new Message(MessageType.ERROR, "Dummy message");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message);
        returnMessage = GlobaMessageQueueUtil.getInstance().getMessageQueue().pop();
        assertEquals(message.getMessage(), returnMessage.getMessage());
        assertEquals(message.getType(), returnMessage.getType());
    }

    @Test
    public void test_MultiMessageQueue()
    {
        Message message1 = new Message(MessageType.INFO, "INFO message 1");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message1);
        
        Message message2 = new Message(MessageType.WARN, "WARN message");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message2);
        Message message3 = new Message(MessageType.ERROR, "ERROR message");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message3);
        Message message4 = new Message(MessageType.INFO, "INFO message 2");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message4);
        
        assertFalse(GlobaMessageQueueUtil.getInstance().getMessageQueue().isEmpty());
        Message returnMessage = GlobaMessageQueueUtil.getInstance().getMessageQueue().pop();
        assertEquals(message1.getMessage(), returnMessage.getMessage());
        assertEquals(message1.getType(), returnMessage.getType());
        
        assertFalse(GlobaMessageQueueUtil.getInstance().getMessageQueue().isEmpty());
        returnMessage = GlobaMessageQueueUtil.getInstance().getMessageQueue().pop();
        assertEquals(message2.getMessage(), returnMessage.getMessage());
        assertEquals(message2.getType(), returnMessage.getType());
        
        assertFalse(GlobaMessageQueueUtil.getInstance().getMessageQueue().isEmpty());
        returnMessage = GlobaMessageQueueUtil.getInstance().getMessageQueue().pop();
        assertEquals(message3.getMessage(), returnMessage.getMessage());
        assertEquals(message3.getType(), returnMessage.getType());
        
        assertFalse(GlobaMessageQueueUtil.getInstance().getMessageQueue().isEmpty());
        returnMessage = GlobaMessageQueueUtil.getInstance().getMessageQueue().pop();
        assertEquals(message4.getMessage(), returnMessage.getMessage());
        assertEquals(message4.getType(), returnMessage.getType());
        
        assertTrue(GlobaMessageQueueUtil.getInstance().getMessageQueue().isEmpty());
    }
    
    @Test
    public void test_ClearMessageQueue()
    {
        Message message1 = new Message(MessageType.INFO, "INFO message 1");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message1);
        
        Message message2 = new Message(MessageType.WARN, "WARN message");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message2);
        Message message3 = new Message(MessageType.ERROR, "ERROR message");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message3);
        Message message4 = new Message(MessageType.INFO, "INFO message 2");
        GlobaMessageQueueUtil.getInstance().getMessageQueue().push(message4);
        
        assertFalse(GlobaMessageQueueUtil.getInstance().getMessageQueue().isEmpty());
        assertNotNull(GlobaMessageQueueUtil.getInstance().getMessageQueue().pop());
        assertNotNull(GlobaMessageQueueUtil.getInstance().getMessageQueue().pop());
        assertNotNull(GlobaMessageQueueUtil.getInstance().getMessageQueue().pop());
        assertNotNull(GlobaMessageQueueUtil.getInstance().getMessageQueue().pop());
        assertTrue(GlobaMessageQueueUtil.getInstance().getMessageQueue().isEmpty());
        try
        {
            assertNull(GlobaMessageQueueUtil.getInstance().getMessageQueue().pop());
        }
        catch (NoSuchElementException e)
        {
            fail("Expected null but got exception.");
        }
    }
    
    @Test
    public void test_MessageType()
    {
        MessageType[] messageType = MessageType.values();
        assertTrue(messageType.length==4);
        assertTrue("INFO".equalsIgnoreCase(MessageType.valueOf("INFO").toString()));
    }
    
    @Test
    public void test_size(){
    	MessageQueue mq=new MessageQueue();
    	assertEquals(0,mq.size());
    }
    
    @Test
    public void test_setMessageQueue(){
    	Message message1 = new Message(MessageType.INFO, "INFO message 1");
    	MessageQueue msgQ=new MessageQueue();
    	msgQ.push(message1);
    }   
    
}
