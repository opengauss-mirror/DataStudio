package org.opengauss.mppdbide.presentation;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

public class TerminalExecutionConnectionInfra
{
    private DBConnection connection;
    private Database     database;

    public TerminalExecutionConnectionInfra(
            TerminalExecutionConnectionInfra termConnection)
    {
        this.database = termConnection.getDatabase();
    }

    public TerminalExecutionConnectionInfra()
    {
        // TODO Auto-generated constructor stub
    }

    public DBConnection getConnection()
    {
        return connection;
    }

    public void setConnection(DBConnection connection)
    {
        this.connection = connection;
    }

    public Database getDatabase()
    {
        return database;
    }

    public void setDatabase(Database database)
    {
        this.database = database;
    }

    public void resetInformation()
    {
        releaseConnection();

        /*
         * We will not set the database to null for reset. Setting to NULL will
         * lead to force NULL checks (by static tools) and unexpected
         * NullPointerException where it is not done.
         */
        // this.setDatabase(null);
    }

    public void releaseConnection()
    {
        if (null != this.connection)
        {
            try
            {
                if (!this.connection.isClosed())
                {
                }
                else
                {
                    this.getDatabase().getConnectionManager()
                    .removeConnectionFromPool(this.getConnection());
                }
            }
            catch (DatabaseOperationException e)
            {
                // cant do anything. Just ignore and proceed!
                this.setConnection(null);
            }
            this.setConnection(null);
        }
    }

    private boolean isClosed()
    {
        try
        {
            return this.connection.isClosed();
        }
        catch (DatabaseOperationException e)
        {
            return false;
        }
    }

    public boolean isConnected()
    {
        if (null != this.connection && !isClosed())
        {
            return true;
        }
        return false;
    }
}
