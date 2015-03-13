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


package bhl.pages.filters;

import java.util.HashSet;

/**
 * Represent a block location
 * @author desmond
 */
public class Location implements Block 
{
    static HashSet<String> locations;
    
    static
    {
        Location.locations = new HashSet<String>();
        Location.locations.add("concord");
        Location.locations.add("massachusetts");
        Location.locations.add("umbagog");
        Location.locations.add("lake");
        Location.locations.add("maine");
    }
    StringBuilder location;
    Location()
    {
        this.location = new StringBuilder();
    }
    /**
     * Test if this is a location
     * @param text the putative location-text
     * @return true if it conforms to the criteria
     */
    static boolean isLocation( String text )
    {
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        for ( int i=0;i<words.length;i++ )
            if ( !locations.contains(words[i]) )
                return false;
        return true;
    }
    /**
     * Add a line to the location,assuming that it already is
     * @param line the line to add
     * @return true if the line fit into the location else false
     */
    public boolean addLine( String line )
    {
        if ( isLocation(line) )
        {
            if ( location.length()>0 )
                location.append(" ");
            location.append(line );
            return true;
        }
        else
            return false;
    }
    /**
     * Unimplemented in this class
     */
    public void markHyphen( boolean hard )
    {
    }
    public String toString()
    {
        return "<div class=\"location\">"+location+"</div>";
    }
}
