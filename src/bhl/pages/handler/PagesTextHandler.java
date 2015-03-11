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
 *  (c) copyright Biodiversity Heritage Library 2015 
 *  http://www.biodiversitylibrary.org/
 */

package bhl.pages.handler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bhl.pages.exception.MissingDocumentException;
import bhl.pages.constants.Params;


/**
 * Get the text of a page
 * @author desmond
 */
public class PagesTextHandler extends PagesGetHandler {
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
            String docid = request.getParameter(Params.DOCID);
            String pageid = request.getParameter(Params.PAGEID);
            String content = getPageContent( docid, pageid );
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println(content);
        }
        catch ( Exception e )
        {
            throw new MissingDocumentException(e);
        }
    }
}
