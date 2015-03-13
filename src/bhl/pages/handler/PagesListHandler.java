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


package bhl.pages.handler;

import bhl.pages.constants.Database;
import bhl.pages.constants.JSONKeys;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bhl.pages.constants.Params;
import bhl.pages.exception.MissingDocumentException;
import bhl.pages.database.Connection;
import bhl.pages.database.Connector;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;


/**
 * Get a list of pages for a given document
 * @author desmond
 */
public class PagesListHandler extends PagesGetHandler
{
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) 
        throws MissingDocumentException
    {
        try
        {
            String docid = request.getParameter(Params.DOCID);
            if ( docid != null )
            {
                Connection conn = Connector.getConnection();
                String[] keys = new String[2];
                keys[0] = JSONKeys.BHL_PAGE_ID;
                keys[1]= JSONKeys.PAGE_SEQUENCE;
                String[] pages = conn.listCollectionBySubKey( Database.PAGES, 
                    JSONKeys.IA_IDENTIFIER, docid, keys );
                JSONArray list =new JSONArray();
                for ( int i=0;i<pages.length;i++ )
                {
                    JSONObject jobj = (JSONObject)JSONValue.parse(pages[i]);
                    PageDesc ps = new PageDesc(
                        jobj.get(JSONKeys.PAGE_SEQUENCE).toString(),
                        jobj.get(JSONKeys.BHL_PAGE_ID).toString());
                    list.add( ps.toJSONObject() );
                }
                PagesGetHandler.sortList( list );
                response.setContentType("application/json");
                response.getWriter().print(list.toJSONString());
            }
            else 
                throw new Exception("Must specify document identifier");
        }
        catch ( Exception e )
        {
            throw new MissingDocumentException(e);
        }
    }
            
}
