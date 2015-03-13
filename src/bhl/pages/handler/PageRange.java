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

/**
 * A page range is an offset and length within plain text
 * @author desmond
 */
public class PageRange {
    /** offset into document bytes */
    int offset;
    /** length of page text in bytes */
    int length;
    /** encoding of the data the page range points to */
    String encoding;
    /**
     * Create a page range
     * @param offset offset within  the base plain text
     * @param length length of the page in textual characters
     * @param encoding the encoding of the string data the pr points to
     */
    public PageRange( int offset, int length, String encoding )
    {
        this.offset = offset;
        this.length = length;
        this.encoding = encoding;
    }
    /**
     * Get the end offset (one index beyond last char)
     * @return the index of the end of the range
     */
    public int end()
    {
        return this.offset+this.length;
    }
}
