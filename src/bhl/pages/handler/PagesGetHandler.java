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

import bhl.pages.constants.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bhl.pages.exception.PagesException;
import bhl.pages.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Handle a GET request for various image types, text, GeoJSON
 * @author desmond
 */
public class PagesGetHandler {
    protected static void sortList( JSONArray a )
    {
        int increment = a.size() / 2;
        while (increment > 0) {
            for (int i = increment; i < a.size(); i++) {
                int j = i;
                JSONObject temp = (JSONObject)a.get(i);
                int tempN = ((Number)temp.get("n")).intValue();
                JSONObject aObj = ((JSONObject)a.get(j-increment));
                int aN = ((Number)aObj.get("n")).intValue();
                while (j >= increment && aN>tempN ) 
                {
                    a.set(j,a.get(j-increment));
                    j = j - increment;
                    if ( j >= increment )
                    {
                        aObj = ((JSONObject)a.get(j-increment));
                        aN = ((Number)aObj.get("n")).intValue();
                    }
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