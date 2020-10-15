package com.huawei.mppdbide.mock.presentation;

import org.postgresql.core.Encoding;

public class EncodingHelper extends Encoding
{
    protected EncodingHelper(String encoding)
    {
        super(encoding);
    }
    
    public static Encoding getDatabaseEncoding(String databaseEncoding)
    {
        return new EncodingHelper("");
        
    }
}
