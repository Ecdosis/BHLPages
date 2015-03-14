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

package bhl.pages.filters.tags;

/**
 * Represent a paired (start and end tag with a class name and a core
 * @author desmond
 */
public class PairedTag  extends Tag
{
    /** optional class value */
    String className;
    public PairedTag( String name, String className, int start )
    {
        super(name,start);
        this.className = className;
    }
    /**
     * Convert to HTML
     * @return a HTML string 
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(name);
        if ( className != null )
        {
            sb.append(" class=\"");
            sb.append(className);
            sb.append("\"");
        }
        sb.append(">");
        sb.append(core);
        sb.append("</");
        sb.append(name);
        sb.append(">");
        return sb.toString();
    }
}
