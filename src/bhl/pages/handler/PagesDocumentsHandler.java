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

import bhl.pages.constants.Database;
import bhl.pages.constants.JSONKeys;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bhl.pages.exception.PagesException;
import java.util.Set;
import java.util.Iterator;
import bhl.pages.database.Connection;
import bhl.pages.database.Connector;

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
            Connection conn = Connector.getConnection();
            StringBuilder sb = new StringBuilder();
            sb.append("[ ");
            String[] keys = conn.getDistinctKeys( Database.DOCUMENTS, 
                JSONKeys.IA_IDENTIFIER);
            for ( int i=0;i<keys.length;i++ )
            {
                sb.append("\"");
                sb.append(keys[i]);
                sb.append("\"");
                if ( i<keys.length-1 )
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
