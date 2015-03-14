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
 * Simple single tag that resolves to plain text
 * @author desmond
 */
public class SingleTag extends Tag
{
    /**
     * Create a single tag like [male]
     * @param name the HTML name of the tag (required by Tag, can be null)
     * @param start the start offset in the raw text
     */
    public SingleTag( String name, int start )
    {
        super(name,start);
    }
    /**
     * Convert to HTML 
     * @return an entity or plain text
     */
    public String toString()
    {
        return core;
    }
}
