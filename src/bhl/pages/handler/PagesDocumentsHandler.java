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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bhl.pages.exception.PagesException;
import java.util.Set;
import java.util.Iterator;

/**
 * Get a list of documents with titles from the database
 * @author desmond
 */
public class PagesDocumentsHandler extends PagesGetHandler 
{
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws PagesException
    {
        try
        {
            if ( PagesGetHandler.docMap == null )
                PagesGetHandler.loadDocMap();
            StringBuilder sb = new StringBuilder();
            sb.append("[ ");
            Set<String> keys = PagesGetHandler.docMap.keySet();
            Iterator<String> iter=keys.iterator();
            while ( iter.hasNext() )
            {
                sb.append("\"");
                sb.append(iter.next() );
                sb.append("\"");
                if ( iter.hasNext() )
                    sb.append(",");
            }
            sb.append(" ]");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(sb.toString());
        }
        catch ( Exception e )
        {
            throw new PagesException(e);
        }
    }
}
