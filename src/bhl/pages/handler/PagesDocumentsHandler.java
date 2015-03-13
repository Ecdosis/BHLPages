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
import bhl.pages.exception.PagesException;
import bhl.pages.database.Connection;
import bhl.pages.database.Connector;

/**
 * Get a list of documents with titles from the database
 * @author desmond
 */
public class PagesDocumentsHandler extends PagesGetHandler 
{
    String concoctTitle( String docid )
    {
        StringBuilder className = new StringBuilder();
        StringBuilder yearName = new StringBuilder();
        StringBuilder author = new StringBuilder();
        int state = 0;
        for ( int i=0;i<docid.length();i++ )
        {
            char token = docid.charAt(i);
            switch ( state )
            {
                case 0: // looking for document classname
                    if ( Character.isDigit(token) )
                    {
                        yearName.append(token);
                        state = 1;
                    }
                    else
                        className.append(token);
                    break;
                case 1: // looking for year
                    if ( Character.isDigit(token) && yearName.length()<4 )
                        yearName.append(token);
                    else
                        state = 2;
                    break;
                case 2: // looking for author
                    if ( Character.isLetter(token) )
                        author.append(token);
                    break;
            }
        }
        StringBuilder total = new StringBuilder();
        total.append(author);
        total.append(" ");
        if ( className.length()>0 && className.charAt(className.length()-1)=='s' )
            className.setLength(className.length()-1);
        total.append(className);
        total.append(" ");
        total.append(yearName);
        return total.toString();
    }
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
                sb.append("{ \"docid\":");
                sb.append("\"");
                sb.append(keys[i]);
                sb.append("\", \"title\": \"");
                sb.append(concoctTitle(keys[i]));
                sb.append("\" }");
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
