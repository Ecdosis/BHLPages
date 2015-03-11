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
import java.util.ArrayList;


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
            if ( PagesGetHandler.docMap == null )
                PagesGetHandler.loadDocMap();
            if ( docid != null )
            {
                ArrayList pages = docMap.get( docid );
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for ( int i=0;i<pages.size();i++ )
                {
                    KeyPair page = (KeyPair)pages.get(i);
                    sb.append("\"");
                    sb.append(page.value);
                    sb.append("\"");
                    if ( i < pages.size()-1 )
                        sb.append(", ");
                }
                sb.append("]");
                response.setContentType("application/json");
                response.getWriter().print(sb.toString());
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
