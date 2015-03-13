/*
 * This file is part of BHLPages.
 *
 *  BHLPages is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  BHLPages is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BHLPages.  If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Biodiversity Heritage Library 2015 
 *  http://www.biodiversitylibrary.org/
 */

package bhl.pages.database;

import bhl.pages.constants.Database;
import bhl.pages.exception.*;
import bhl.pages.constants.JSONKeys;
import java.util.Iterator;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import java.util.List;
import org.json.simple.JSONObject;


/**
 * Database interface for MongoDB
 * @author desmond
 */
public class MongoConnection extends Connection 
{
    MongoClient client;
    static int MONGO_PORT = 27017;
    /** connection to database */
    DB  db;
    public MongoConnection( String user, String password, String host, 
        String dbName, int dbPort, int wsPort )
    {
        super( user, password, host, dbName, dbPort, wsPort );
    }
    /**
     * Connect to the database
     * @throws Exception 
     */
    private void connect() throws Exception
    {
        if ( db == null )
        {
            MongoClient mongoClient = new MongoClient( host, MONGO_PORT );
            db = mongoClient.getDB(this.databaseName);
            //boolean auth = db.authenticate( user, password.toCharArray() );
            //if ( !auth )
            //    throw new DbException( "MongoDB authentication failed");
        }
    }
    /**
     * Get the Mongo db collection object from its name
     * @param collName the collection name
     * @return a DBCollection object
     * @throws DbException 
     */
    private DBCollection getCollectionFromName( String collName )
        throws DbException
    {
        DBCollection coll = db.getCollection( collName );
        if ( coll == null )
            coll = db.createCollection( collName, null );
        if ( coll != null )
            return coll;
        else
            throw new DbException( "Unknown collection "+collName );
    }
    /**
     * List all the documents in a Mongo collection
     * @param collName the name of the collection
     * @param key the document key to retrieve by
     * @return a String array of document keys
     * @throws DbException 
     */
    @Override
    public String[] listCollectionByKey( String collName, String key ) 
        throws DbException
    {
        try
        {
            connect();
        }
        catch ( Exception e )
        {
            throw new DbException( e );
        }
        DBCollection coll = getCollectionFromName( collName );
        BasicDBObject keys = new BasicDBObject();
        keys.put( key, 1 );
        DBCursor cursor = coll.find( new BasicDBObject(), keys );
        if ( cursor.length() > 0 )
        {
            String[] docs = new String[cursor.length()];
            Iterator<DBObject> iter = cursor.iterator();
            int i = 0;
            while ( iter.hasNext() )
            {
                Object obj = iter.next().get( key );
                docs[i++] = obj.toString();
            }
            return docs;
        }
        else
            throw new DbException( "no docs in collection "+collName );
    }
    /**
     * Fetch a resource from the server via a given field value
     * @param collName the collection or database name
     * @param value the value of the field
     * @param field the field name
     * @return the response as a string or null if not found
     */
    @Override
    public String getFromDbByIntField( String collName, int value, String field ) 
        throws DbException
    {
        try
        {
            connect();
            DBCollection coll = getCollectionFromName( collName );
            DBObject query = new BasicDBObject(field,value);
            DBObject obj = coll.findOne( query );
            if ( obj != null )
                return obj.toString();
            else
                return null;
        }
        catch ( Exception e )
        {
            throw new DbException( e );
        }
    }
    /**
     * Make a subset of the documents by a given subkey and value, 
     * then retrieve all the values of the field
     * @param collection the collection to search
     * @param subKey the subKey to make the initial choice
     * @param subValue the value of the subKey to search for
     * @param fields the field names to retrieve
     * @return and array of field values as JSON object strings
     * @throws DbException 
     */
    @Override
    public String[] listCollectionBySubKey( String collection, 
        String subKey, String subValue, String[] fields ) throws DbException
    {
        try
        {
            connect();
            DBCollection coll = getCollectionFromName( collection );
            DBObject query = new BasicDBObject(subKey,subValue);
            BasicDBObject keys = new BasicDBObject();
            for ( int i=0;i<fields.length;i++ )
                keys.put(fields[i],1);
            DBCursor cursor = coll.find( query, keys );
            if ( cursor.length() > 0 )
            {
                String[] array = new String[cursor.length()];
                Iterator iter = cursor.iterator();
                int i = 0;
                while ( iter.hasNext() )
                {
                    DBObject bson = (DBObject)iter.next();
                    JSONObject jobj = new JSONObject();
                    for ( int j=0;j<fields.length;j++ )
                        jobj.put(fields[j],bson.get(fields[j]));
                    array[i++] = jobj.toJSONString();
                }
                return array;
            }
            else
                return new String[0];
        }
        catch ( Exception e )
        {
            throw new DbException(e);
        }
    }
    /**
     * Get the content of the given page and document
     * @param docid the document identifier (an integer in BHL)
     * @param pageid the page identifier (NOT page_sequence) also an int
     * @return the textual content as a String
     * @throws Exception 
     */
    public String getPageContent( String docid, String pageid ) throws DbException
    {
        try
        {
            if ( docid != null && pageid != null )
            {
                connect();
                DBCollection coll = db.getCollection( Database.PAGES );
                BasicDBObject ref = new BasicDBObject();
                ref.put( JSONKeys.IA_IDENTIFIER, docid );
                ref.put( JSONKeys.BHL_PAGE_ID, Integer.parseInt(pageid) );
                BasicDBObject key = new BasicDBObject();
                key.put(JSONKeys.PAGE_SEQUENCE, 1);
                DBObject obj = coll.findOne( ref, key );
                if ( obj != null )
                {
                    Object pobj = obj.get( JSONKeys.PAGE_SEQUENCE );
                    int pageNo = ((Number)pobj).intValue();
                    DBCollection coll2 = db.getCollection( Database.DOCUMENTS );
                    BasicDBObject ref2 = new BasicDBObject();
                    ref2.put(JSONKeys.IA_IDENTIFIER,docid);
                    ref2.put(JSONKeys.PAGE_SEQUENCE,pageNo );
                    BasicDBObject key2 = new BasicDBObject();
                    key2.put(JSONKeys.CONTENT,1);
                    Object obj2 = coll2.findOne( ref2, key2 );
                    if ( obj2 != null )
                        return (String)((DBObject)obj2).get( JSONKeys.CONTENT );
                    else
                        throw new Exception(
                            "could not find content for docid="
                            +docid+", pageid="+pageid);
                }
                else
                    throw new Exception("could not find docid="
                        +docid+", pageid="+pageid);
            }
            else
                throw new Exception("Missing docid or pageid");
        }
        catch ( Exception e )
        {
            throw new DbException(e);
        }
    }
    /**
     * Get the set of distinct keys for a collection
     * @param collection the collection name
     * @param field the field name for distinct values
     * @return and array of field values
     * @throws Exception 
     */
    @Override
    public String[] getDistinctKeys( String collection, String field ) 
        throws DbException
    {
        try
        {
            connect();
            DBCollection coll = db.getCollection( Database.PAGES );
            List keys = coll.distinct(field);
            String[] list = new String[keys.size()];
            keys.toArray(list);
            return list;
        }
        catch ( Exception e )
        {
            throw new DbException(e);
        }
    }
    
}