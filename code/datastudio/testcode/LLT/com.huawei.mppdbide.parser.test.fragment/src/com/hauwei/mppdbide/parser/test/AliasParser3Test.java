package com.hauwei.mppdbide.parser.test;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.huawei.mppdbide.parser.alias.AliasParserErrorListener;
import com.huawei.mppdbide.parser.alias.LexerErrorListener;

public class AliasParser3Test
{
    //private PostgresParser parser;
    //private LexerErrorListener lexerErrlorListener;
    //private AliasParserErrorListener parserErrlorListener;

//    @Before
//    public void setUp() throws Exception
//    {
//        lexerErrlorListener = new LexerErrorListener();
//        parserErrlorListener = new AliasParserErrorListener();
//        
//        ANTLRInputStream input = new ANTLRInputStream("SELECT * FROM PUBLIC.ABC as A");
//        PostgresLexer lexer = new PostgresLexer(input);
//        lexer.removeErrorListeners();
//        lexer.addErrorListener(lexerErrlorListener);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        PostgresParser parser = new PostgresParser(tokens);
//        parser.setErrorHandler(new RunTimeExceptionCheckErrorStrategy());
//        parser.removeErrorListeners();
//        parser.addErrorListener(parserErrlorListener);
//    }
    
    @Test
    public void test_alias_parser_error_listener_null()
    {
        try
        {
            AliasParserErrorListener aliasParserErrorListener = new AliasParserErrorListener();
            aliasParserErrorListener.syntaxError(null, null, 0, 0, null, null);
            
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_alias_parser_error_listener_exception_1()
    {
        try
        {
            AliasParserErrorListener aliasParserErrorListener = new AliasParserErrorListener();
            aliasParserErrorListener.syntaxError(null, null, 0, 0, null, null);
            
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test_alias_parser_error_listener_exception_2()
    {
        try
        {
            LexerErrorListener  lexerErrorListener  = new LexerErrorListener ();
            lexerErrorListener.syntaxError(null, null, 0, 0, null, null);
            
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
}