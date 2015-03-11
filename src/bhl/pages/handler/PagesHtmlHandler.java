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
import bhl.pages.constants.Params;
import bhl.pages.exception.MissingDocumentException;
import bhl.pages.exception.PagesException;
import bhl.pages.filters.BrewJournal1871;
import bhl.pages.database.*;

/**
 * Return a HTML rendering of the text of just one page
 * @author desmond
 */
public class PagesHtmlHandler extends PagesGetHandler {
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws PagesException
    {
        try
        {
            Connection conn = Connector.getConnection();
            String docid = request.getParameter(Params.DOCID);
            String pageid = request.getParameter(Params.PAGEID);
            String content = conn.getPageContent( docid, pageid );
            // obviously we need to choose a filter here based on the ocid
            // perhaps via a HashMap 
            // which could be loaded from an external resource or hard-coded
            String html = new BrewJournal1871().filter( content );
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println(html);
        }
        catch ( Exception e )
        {
            throw new MissingDocumentException(e);
        }
    }
}
