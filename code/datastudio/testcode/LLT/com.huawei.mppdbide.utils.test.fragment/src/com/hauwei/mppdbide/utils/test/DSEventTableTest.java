package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.DSEventWithCount;
import com.huawei.mppdbide.utils.observer.IDSListener;

public class DSEventTableTest
{
    private DSEventTable                       eventTable       = null;
    private DSEvent                            dsEvent          = null;
    private DSEventWithCount                   countEvent = null;
    IDSListener newListener ;
    
    @Before
    public void setUp() throws Exception
    {
        eventTable = new DSEventTable();
        dsEvent = new DSEvent(1, "name");
        countEvent = new DSEventWithCount(2, "dummy", 5);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void ds_event_table_Test_1()
    {
        if (eventTable != null)
        {
            eventTable.hook(1, newListener);
            eventTable.hook(2, newListener);
            dsEvent.getObject();
            dsEvent.getType();
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    @Test
    public void ds_event_table_Test_2()
    {
        if (eventTable != null)
        {
            eventTable.hook(0, newListener);
            eventTable.hook(2, newListener);
            eventTable.sendEvent(dsEvent);
            assertNotNull(true);

        }
        assertNotNull(false);
    }

    @Test
    public void ds_event_table_Test_3()
    {
        if (eventTable != null)
        {
            eventTable.hook(1, newListener);
            eventTable.unhookall();
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    @Test
    public void ds_event_table_Test_4()
    {
        if (eventTable != null)
        {
            eventTable.hook(0, newListener);
            eventTable.hook(2, newListener);
            eventTable.unhook(1, newListener);
            assertNotNull(true);
        }
        assertNotNull(false);
    }
    
    @Test
    public void ds_event_with_count_test()
    {
        assertEquals(countEvent.getCount(), 5);
        assertEquals(2, countEvent.getType());
        assertEquals("dummy", countEvent.getObject().toString());        
    }
    
    @Test
    public void ds_eventTable_sendEvent_test_001()
    {
        newListener = new MockListener();
        if (eventTable != null)
        {
            eventTable.hook(0, newListener);
            eventTable.hook(1, newListener);
            eventTable.hook(2, newListener);
            assertEquals(2, countEvent.getType());
            eventTable.sendEvent(countEvent);
            assertNotNull(true);
        }
        assertNotNull(false);
    }
    
    class MockListener implements IDSListener
    {

        @Override
        public void handleEvent(DSEvent event)
        {
            assertTrue(true);           
        }
        
    }
    
}
