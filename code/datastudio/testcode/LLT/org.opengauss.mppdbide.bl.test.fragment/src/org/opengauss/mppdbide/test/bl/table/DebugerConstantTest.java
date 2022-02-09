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

package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.vo.FunctionVo;
import org.opengauss.mppdbide.debuger.vo.PositionVo;
import org.opengauss.mppdbide.debuger.vo.SessionVo;
import org.opengauss.mppdbide.debuger.vo.SourceCodeVo;
import org.opengauss.mppdbide.debuger.vo.StackVo;
import org.opengauss.mppdbide.debuger.vo.TotalSourceCodeVo;
import org.opengauss.mppdbide.debuger.vo.VariableVo;

/**
 * Title: DebugerDebugOptTest for use
 *
 * @since 3.0.0
 */
public class DebugerConstantTest {
    @Test
    public void testNoParam() {
        String sql1 = DebugConstants.getSql(DebugOpt.DEBUG_ON);
        String compareResults = "select * from pldbg_on()";
        assertEquals(compareResults, sql1);
    }

    @Test
    public void testOneParam() {
        String sql1 = DebugConstants.getSql(DebugConstants.DebugOpt.START_SESSION);
        assertEquals(sql1, "select * from plpgsql_oid_debug(?)");
    }

    @Test
    public void testThreeParam() {
        String sql1 = DebugConstants.getSql(DebugConstants.DebugOpt.SET_BREAKPOINT);
        assertEquals(sql1, "select * from pldbg_set_breakpoint(?,?,?)");
    }

    @Test
    public void testAnotherInput() {
        String sql1 = DebugConstants.getSql("test_function", 3);
        String result = "select * from test_function(?,?,?)";
        assertEquals(sql1, result);
    }
    
    @Test
    public void testVoCreate() {
        FunctionVo funVo = new FunctionVo();
        assertNotNull(funVo);

        PositionVo positionVo = new PositionVo();
        assertNotNull(positionVo);

        PositionVo positionVo2 = new PositionVo(new Long(0), 1, null);
        assertNotNull(positionVo2);
        assertEquals(positionVo2.linenumber.intValue(), 1);
        assertNotEquals(PositionVo.title(), "");
        assertNotEquals(positionVo2.formatSelf(), "");

        SessionVo sessionVo = new SessionVo();
        assertNotNull(sessionVo);

        SourceCodeVo codeVo = new SourceCodeVo();
        assertNotNull(codeVo);
        codeVo.pldbg_get_source = "test";
        assertEquals(codeVo.getSourceCode(), "test");

        StackVo stackVo = new StackVo();
        assertNotNull(stackVo);
        stackVo.level = new Integer(1);
        stackVo.targetname = "name";
        assertNotEquals(stackVo.toString(), "");

        TotalSourceCodeVo totalSourceCodeVo = new TotalSourceCodeVo();
        assertNotNull(totalSourceCodeVo);
        totalSourceCodeVo.definition = "test11";
        totalSourceCodeVo.headerlines = new Integer(1);
        assertEquals(totalSourceCodeVo.getHeadlines(), 1);
        assertEquals(totalSourceCodeVo.getSourceCode(), "test11/");

        VariableVo variableVo = new VariableVo();
        assertNotNull(variableVo);
        variableVo.name = "var1";
        variableVo.linenumber = new Integer(1);
        variableVo.value = "value";
        variableVo.dtype = new Long(3);
        variableVo.isnotnull = new Boolean(true);
        assertNotEquals(VariableVo.title(), "");
        assertNotEquals(variableVo.formatSelf(), "");
    }
}
