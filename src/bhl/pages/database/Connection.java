/*
 * This file is part of MML.
 *
 *  MML is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  MML is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MML.  If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Desmond Schmidt 2014
 */
package bhl.pages.database;

import bhl.pages.constants.Database;
import bhl.pages.constants.JSONKeys;
import bhl.pages.exception.DbException;

/**
 * Abstract database API for various databases/repositories
 * @author desmond
 */
public abstract class Connection 
{
    String user;
    String password;
    String host;
    int dbPort;
    int wsPort;
    protected String databaseName;
    
    public Connection( String user, String password, String host, 
        String dbName, int dbPort, int wsPort )
    {
        this.user = user;
        this.password = password;
        this.host = host;
        this.dbPort = dbPort;
        this.wsPort = wsPort;
        this.databaseName = dbName;
    }
    public final int getDbPort()
    {
        return dbPort;
    }
    public final int getWsPort()
    {
        return wsPort;
    }
    public final String getHost()
    {
        return host;
    } 
    /**
     * Remove the rightmost segment of the path and resource
     * @return the remains of the path
     */
    public static String chomp( String path )
    {
        String popped = "";
        int index = path.lastIndexOf( "/" );
        if ( index != -1 )
            popped = path.substring( 0, index );
        return popped;
    }
    public abstract String[] listCollectionByKey( String collName, String key ) 
        throws DbException;
    public abstract String getFromDbByIntField( String collName, int value, 
        String field ) throws DbException;
    public abstract String getPageContent( String docid, String pageid ) 
        throws DbException;
    public abstract String[] getDistinctKeys( String collection, String field )
        throws DbException;
    public abstract String[] listCollectionBySubKey( String collection, 
        String subKey, String subValue, String field  ) throws DbException;
}
