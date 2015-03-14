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
 * Template for an abstract tag, to be recalled when a specific tag is seen
 * @author desmond
 */
public class TagTemplate {
    public String name;
    public String className;
    public String core;
    /**
     * Create a template
     * @param name the HTML tag-name, e.g. "span"
     * @param className the value of the class attribute or null
     * @param core the default core text (used for single tags)
     */
    public TagTemplate( String name, String className, String core )
    {
        this.name = name;
        this.className = className;
        this.core = core;
    }
}
