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


package org.opengauss.mppdbide.mock.bl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opengauss.mppdbide.debuger.vo.FunctionVo;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

/**
 * Title: MockDebugServiceHelper for use
 *
 * @since 3.0.0
 */
public class MockDebugServiceHelper {
    private PreparedStatementResultSetHandler handler;
    private FunctionVo funcVo;
    public MockDebugServiceHelper(PreparedStatementResultSetHandler handler,
            FunctionVo funcVo) {
        this.handler = handler;
        this.funcVo = funcVo;
    }
    
    public void mockVariable(String sql) {
        String[] variables = new String[] {"name", "varclass", "linenumber",
            "isunique", "isconst", "isnotnull", "dtype",
            "value"};
        Object[] oneRow = new Object[] {"v1", "int", new Integer(1), true, false,
            true, 22L, 1};
        mockOneRow(sql, variables, oneRow);
    }
    
    public void mockStack(String sql) {
        String[] stackColumn = new String[] {"level", "targetname", "func",
            "linenumber", "args"};
        Object[] oneRowValues = new Object[] {1, funcVo.proname, funcVo.oid,
            1, null};
        mockOneRow(sql, stackColumn, oneRowValues);
    }

    public void mockBreakPoint(String sql) {
        mockPositionOneLine(sql);
    }
    
    public void mockPositionOneLine(String sql) {
        String[] breakPointTitle = new String[] {"func", "linenumber", "targetname"};
        Object[] oneRow = new Object[] {new Long(funcVo.oid),
            new Integer(-1),
            funcVo.proname};
        mockOneRow(sql, breakPointTitle, oneRow); 
    }
    
    public void mockAbortDebug(String sql) {
        mockOneRow(sql, new String[] {"result"}, new Object[] {new Boolean(true)});
    }

    public void mockAttachDebug(String sql, Object port) {
        mockOneRow(sql, new String[] {"port"}, new Object[] {port});
    }

    public void mockStartDebug(String sql, Object ret) {
        mockOneRow(sql, new String[] {"result"}, new Object[] {ret});
    }

    public void mockPrepareDebug(String sql) {
        mockHelper(sql, new ResultColumnHelper());
    }
    
    public void mockFunctionVo(String sql, Object[] args) {
        String[] columns = new String[] {"oid", "proname", "proretset",
                "prorettype", "pronargs", "pronargdefaults",
                "proargtypes", "proallargtypes", "proargmodes",
                "proargnames", "proargdefaults", "prodefaultargpos",
                "prosrc"};
        mockOneRow(sql, columns, args);
    }
    
    private void mockOneRow(String sql, String[] columns, Object[] oneRow) {
        ResultColumnHelper helper = new ResultColumnHelper(columns);
        helper.addRow(oneRow);
        mockHelper(sql, helper);
    }

    private void mockHelper(String sql, ResultColumnHelper helper) {
        MockResultSet rs = handler.createResultSet();
        helper.setResultSet(rs);
        handler.prepareResultSet(sql, rs);
    }

    public static class ResultColumnHelper {
        public List<String> columns;
        public List<List<Object>> rows = new ArrayList<List<Object>>(1);
        public ResultColumnHelper() {
            columns = null;
        }

        public ResultColumnHelper(String[] columns) {
            this(Arrays.asList(columns));
        }

        public ResultColumnHelper(List<String> columns) {
            this.columns = columns;
        }
        
        public void addRow(Object[] objRowCol) {
            addRow(Arrays.asList(objRowCol));
        }

        public void addRow(List<Object> rowCol) {
            List<Object> newRow = new ArrayList<Object>(rowCol);
            if (rowCol.size() < columns.size()) {
                for (int i = 0, size = columns.size() - rowCol.size();
                        i < size ; i ++) {
                    newRow.add(null);
                }
            }
            rows.add(newRow);
        }
        
        public void setResultSet(MockResultSet rs) {
            if (columns == null || columns.size() == 0) {
                return;
            }
            for (String col: columns) {
                rs.addColumn(col);
            }
            for (List<Object> row: rows) {
                rs.addRow(row);
            }
        }
    }
    
    public static class FunctionDesc {
        public String proname;
        public List<Object> params;
        public FunctionDesc(String proname, List<Object> params) {
            this.proname = proname;
            this.params = params;
        }
    }
}
