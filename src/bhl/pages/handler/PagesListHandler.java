/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bhl.pages.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bhl.pages.constants.Params;
import bhl.pages.exception.MissingDocumentException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

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
