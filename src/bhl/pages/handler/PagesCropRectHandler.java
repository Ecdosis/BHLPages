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
import bhl.pages.exception.MissingDocumentException;
import bhl.pages.constants.Params;
import bhl.pages.database.*;
import org.json.simple.*;

/**
 * Get the crop rectangle for a given page-image, or the default
 * @author desmond
 */
public class PagesCropRectHandler {
    /**
     * Make a default crop rect when the page doesn't specify one
     * @return a JSONArray
     */
    private JSONArray getDefaultCropRect()
    {
        JSONArray outer = new JSONArray();
        JSONArray tl = new JSONArray();
        tl.add(0.0);
        tl.add(100.0);
        outer.add(tl);
        JSONArray tr = new JSONArray();
        tr.add(100.0);
        tr.add(0.0);
        outer.add(tr);
        JSONArray br = new JSONArray();
        br.add(100.0);
        br.add(100.0);
        outer.add(br);
        JSONArray bl = new JSONArray();
        bl.add(0.0);
        bl.add(100.0);
        outer.add(bl);
        return outer;
    }
    /**
     * Get the plain text of a page. Assume parameters version1, pageid 
     * (image file name minus extension) and docid. Assume also that there 
     * is a corcode at docid/pages. (Should already have checked for this)
     * @param request the original http request
     * @param response the response
     * @param urn the urn used for the request
     * @throws MissingDocumentException 
     */
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) 
        throws MissingDocumentException
    {
        try
        {
            Connection conn = Connector.getConnection();
            String pageid = request.getParameter(Params.PAGEID);
            String content = conn.getFromDbByField( Database.PAGES, 
                JSONKeys.BHL_PAGE_ID, pageid );
            JSONObject jobj = (JSONObject)JSONValue.parse( content );
            JSONArray cropRect = (JSONArray) jobj.get(JSONKeys.CROP_RECT);
            if ( cropRect == null )
                cropRect = getDefaultCropRect();
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println(cropRect.toJSONString());
        }
        catch ( Exception e )
        {
            throw new MissingDocumentException(e);
        }
    }
}
