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
package bhl.pages.filters;

/**
 * A HTML paragraph <p>...</p>
 * @author desmond
 */
public class Paragraph implements Block
{
    StringBuilder content;
    Paragraph()
    {
        content = new StringBuilder();
    }
    public boolean addLine( String line )
    {
        if ( content.length()>0 )
            content.append("<br>\n");
        else
            content.append("<p>");
        content.append( line );
        return true;
    }
    /**
     * Convert the paragraph to a String
     * @return an HTML paragraph
     */
    public String toString()
    {
        content.append("</p>");
        return content.toString();
    }
}
