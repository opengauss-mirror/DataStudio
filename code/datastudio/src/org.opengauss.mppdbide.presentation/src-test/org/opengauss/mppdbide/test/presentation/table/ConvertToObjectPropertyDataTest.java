package org.opengauss.mppdbide.test.presentation.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.objectproperties.ConvertToObjectPropertyData;
import org.opengauss.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import static org.junit.Assert.*;
public class ConvertToObjectPropertyDataTest extends BasicJDBCTestCaseAdapter
{
    @Before
    public void setUp() throws Exception
    {
        CommonLLTUtils.runLinuxFilePermissionInstance();
    }
    @After
    public void tearDown() throws Exception
    {

    }
    @Test
    public void test_ConvertToObjectPropertyData_getObjectPropertyDataGeneric(){
        List<List<Object[]>> properties = new ArrayList<List<Object[]>>();
        List<Object[]> list = new ArrayList<Object[]>();
        List<String> strList = new ArrayList<String>();
        strList.add(0, "class");
        strList.add(1, "class1");
        Object[] element = {"name", "type", "column", "table"};
        list.add(0, element);
        properties.add(0, list);
        properties.add(1, list);
        try
        {
            ConvertToObjectPropertyData.getObjectPropertyDataGeneric(strList, properties);
            assertNotNull(ConvertToObjectPropertyData.getObjectPropertyDataGeneric(strList, properties));
        }
        catch (DatabaseOperationException e)
        {
           fail("not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected to come here");
        }
    }
    
    @Test
    public void test_ConvertToObjectPropertyData_getObjectPropertyDataGenericGroupedColumn(){
        
        List<String> strList = new ArrayList<String>();
        strList.add(0, "class");
        strList.add(1, "class1");
        
        List<Object> list = new ArrayList<Object>();
        list.add(0, "name");
        list.add(1, "type");
        
        Map<String, List<Object>> data = new HashMap<String, List<Object>>();
        data.put("name", list);
        data.put("type", list);
        
        List<DNIntraNodeDetailsColumn> colgrp = new ArrayList<DNIntraNodeDetailsColumn>();
        DNIntraNodeDetailsColumn element = new DNIntraNodeDetailsColumn();
        element.setColCount(1);
        element.setGroupColumnName("Columns");
        List<String> colList = new ArrayList<String>();
        colList.add(0, "name");
        colList.add(1, "types");
        colgrp.add(0, element);
        colgrp.add(1, element);
        element.setColnames(colList);
        
        try
        {
            ConvertToObjectPropertyData.getObjectPropertyDataGenericGroupedColumn(strList, data, colgrp);
            assertNotNull(ConvertToObjectPropertyData.getObjectPropertyDataGenericGroupedColumn(strList, data, colgrp));
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }
        catch (DatabaseCriticalException e)
        {
            fail("not expected to come here");
        }
    }
}
