/*
 * This file is part of TILT.
 *
 *  TILT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TILT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TILT.  If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Desmond Schmidt 2014
 */
package bhl.pages.handler;

import bhl.pages.PagesWebApp;
import calliope.core.handler.AeseVersion;
import bhl.pages.constants.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bhl.pages.exception.PagesException;
import bhl.pages.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import bhl.pages.constants.Database;
import bhl.pages.constants.JSONKeys;
import bhl.pages.constants.Params;
import calliope.core.database.Connection;
import calliope.core.database.Connector;
import calliope.core.exception.DbException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

/**
 * Handle a GET request for various image types, text, GeoJSON
 * @author desmond
 */
public class PagesGetHandler {
    public static HashMap<String,ArrayList<KeyPair>> docMap;
    private static void sortList( ArrayList<KeyPair> a )
    {
        int increment = a.size() / 2;
        while (increment > 0) {
            for (int i = increment; i < a.size(); i++) {
                int j = i;
                KeyPair temp = a.get(i);
                while (j >= increment && a.get(j-increment).compareTo(temp)<0) {
                    a.set(j,a.get(j-increment));
                    j = j - increment;
                }
                a.set(j, temp);
            }
            if (increment == 2) {
                increment = 1;
            } else {
                increment *= (5.0 / 11);
            }
        }
    }
    public static void loadDocMap() throws DbException
    {
        try
        {
            Connection conn = Connector.getConnection();
            String[] pages = conn.listCollectionByKey(Database.PAGES,
                JSONKeys.BHL_PAGE_ID);
            docMap = new HashMap<String,ArrayList<KeyPair>>();
            for ( int i=0;i<pages.length;i++ )
            {
                String doc = conn.getFromDbByField( Database.PAGES, 
                    JSONKeys.BHL_PAGE_ID, pages[i]);
                JSONObject jobj = (JSONObject)JSONValue.parse( doc );
                String docid = (String)jobj.get(JSONKeys.IA_IDENTIFIER);
                Number pageNo = (Number)jobj.get(JSONKeys.PAGE_SEQUENCE);
                if ( docid != null && !docMap.containsKey(docid) )
                {
                    docMap.put( docid, new ArrayList<KeyPair>() );
                }
                ArrayList list = docMap.get(docid);
                list.add( new KeyPair(pageNo.intValue(),
                    Integer.parseInt(pages[i])) );
            }
            // now sort the lists
            Set<String> keys = docMap.keySet();
            Iterator iter = keys.iterator();
            while ( iter.hasNext() )
            {
                String key = (String)iter.next();
                ArrayList list = docMap.get(key);
                sortList( list );
            }
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
    protected String getPageContent( String docid, String pageid )throws Exception
    {
        if ( PagesGetHandler.docMap == null )
            PagesGetHandler.loadDocMap();
        if ( docid != null )
        {
            MongoClient mongoClient = new MongoClient( PagesWebApp.host, 
                PagesWebApp.wsPort );
            DB db = mongoClient.getDB("bhlgaming");
            DBCollection coll = db.getCollection( Database.PAGES );
            BasicDBObject ref = new BasicDBObject();
            ref.put( JSONKeys.IA_IDENTIFIER, docid );
            ref.put( JSONKeys.BHL_PAGE_ID, Integer.parseInt(pageid) );
            BasicDBObject key = new BasicDBObject();
            key.put(JSONKeys.PAGE_SEQUENCE, 1);
            DBCursor cursor = coll.find( ref, key );
            if ( cursor.length() == 1 )
            {
                Iterator<DBObject> iter = cursor.iterator();
                Object obj = iter.next().get( JSONKeys.PAGE_SEQUENCE );
                int pageNo = ((Number)obj).intValue();
                DBCollection coll2 = db.getCollection( Database.DOCUMENTS );
                BasicDBObject ref2 = new BasicDBObject();
                ref2.put(JSONKeys.IA_IDENTIFIER,docid);
                ref2.put(JSONKeys.PAGE_SEQUENCE,pageNo );
                BasicDBObject key2 = new BasicDBObject();
                key2.put(JSONKeys.CONTENT,1);
                DBCursor cursor2 = coll2.find( ref2, key2 );
                if (cursor2.length() == 1 )
                {
                    Iterator<DBObject> iter2 = cursor2.iterator();
                    Object obj2 = iter2.next().get( JSONKeys.CONTENT );
                    return (String)obj2;
                }
                else
                    throw new Exception("could not find content for docid="
                    +docid+", pageid="+pageid);
            }
            else
                throw new Exception("could not find docid="
                    +docid+", pageid="+pageid);
        }
        else
            throw new Exception("Missing docid or not found="+docid);
    }
    /**
     * Get the facs attribute for the given range
     * @param range the range
     * @return its FACS (facsimile) value or null if absent
     */
    protected String getFacs( JSONObject range )
    {
        JSONArray annotations = (JSONArray)range.get(JSONKeys.ANNOTATIONS);
        if ( annotations != null )
        {
            for ( int j=0;j<annotations.size();j++ )
            {
                JSONObject jobj = (JSONObject)annotations.get(j);
                if ( jobj.containsKey(JSONKeys.FACS) )
                    return (String)jobj.get(JSONKeys.FACS);
            }
        }
        // shouldn't happen
        return null;
    }
    /**
     * Get a page range as an offset and length of the underlying text
     * @param docid the document containing the specified page
     * @param pageid the id of the page or null
     * @param vPath the version to look for or null
     * @param encoding the encoding of the data the page range points to
     * @return a page range object
     */
    public PageRange getPageRange( String docid, String pageid, String vPath, 
        String encoding ) throws PagesException
    {
        return null;
    }
    /**
     * Extract the image Id from the full image path
     * @parampath a relative image path and file name
     */
    protected String imageId( String path )
    {
        int slash = path.lastIndexOf("/");
        if( slash != -1 )
            path = path.substring(slash+1);
        int dot = path.lastIndexOf(".");
        if( dot != -1 )
            path = path.substring(0,dot);
        return path;
    }
    /**
     * Handle a request for one of the data types supported here
     * @param request the http request
     * @param response the http response
     * @param urn the remainder of the request urn to be processed
     * @throws PagesException 
     */
    public void handle(HttpServletRequest request,
        HttpServletResponse response, String urn) throws PagesException {
        try {
            String service = Utils.first(urn);
            if (service.equals(Service.IMAGE)) 
                new PagesImageHandler().handle(request, response, Utils.pop(urn));
            else if (service.equals(Service.DOCUMENTS) )
                new PagesDocumentsHandler().handle(request,response,Utils.pop(urn));
            else if (service.equals(Service.TEXT) )
                new PagesTextHandler().handle(request,response,Utils.pop(urn));
            else if (service.equals(Service.HTML) )
                new PagesHtmlHandler().handle(request,response,Utils.pop(urn));
            else if (service.equals(Service.LIST) )
                new PagesListHandler().handle(request,response,Utils.pop(urn));
            else
                    throw new Exception("Unknown service "+service);
        } catch (Exception e) {
            throw new PagesException(e);
        }
    }
}