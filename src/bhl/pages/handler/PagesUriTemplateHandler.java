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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bhl.pages.exception.MissingDocumentException;
import java.io.IOException;


/**
 * Get the template of an image URI in accordance with RFC6570
 * @author desmond
 */
public class PagesUriTemplateHandler extends PagesGetHandler
{
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) 
        throws MissingDocumentException
    {
        try
        {
            response.setContentType("text/plain");
            // pretty simple in this case
            response.getWriter().print( getUriTemplate() );
        }
        catch ( IOException ioe )
        {
            throw new MissingDocumentException(ioe);
        }
    }
    public static String getUriTemplate()
    {
        return "http://biodiversitylibrary.org/pageimage/{pageid}";
    }       
}

