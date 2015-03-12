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
import java.util.HashSet;

/**
 * A Date entry in the Brew journal
 * @author desmond
 */
public class Date implements Block {
    static 
    {
        Date.months = new HashSet<String>();
        Date.months.add("january");
        Date.months.add("jan");
        Date.months.add("february");
        Date.months.add("feb");
        Date.months.add("march");
        Date.months.add("mar");
        Date.months.add("april");
        Date.months.add("apr");
        Date.months.add("may");
        Date.months.add("june");
        Date.months.add("jun");
        Date.months.add("july");
        Date.months.add("jul");
        Date.months.add("august");
        Date.months.add("aug");
        Date.months.add("september");
        Date.months.add("sept");
        Date.months.add("october");
        Date.months.add("oct");
        Date.months.add("november");
        Date.months.add("nov");
        Date.months.add("december");
        Date.months.add("dec");
        Date.days = new HashSet<String>();
        Date.days.add("monday");
        Date.days.add("tuesday");
        Date.days.add("wednesday");
        Date.days.add("thursday");
        Date.days.add("friday");
        Date.days.add("saturday");
        Date.days.add("sunday");
    }
    int year;
    int state;
    String date;
    String dayName;
    static HashSet<String> months;
    static HashSet<String> days;
    /**
     * Convert a text year into a numerical one
     * @param text a text year maybe with punctuation and spaces
     * @return the year value
     */
    static int toYear( String text )
    {
        String trimmed = text.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        return Integer.parseInt(trimmed);
    }
    /**
     * Is this line a year?
    */
    static boolean isYear( String text )
    {
        String trimmed = text.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        if ( trimmed.length()==4 )
        {
            try
            {
                int year = Integer.parseInt(trimmed);
                if ( year > 1800 && year <1900 )
                    return true;
                else
                    return false;
            }
            catch ( NumberFormatException nfe )
            {
                return false;
            }
        }
        else
            return false;
    }
    static boolean isMonth( String text )
    {
        return months.contains( text.toLowerCase() );
    }
    static boolean isDay( String text )
    {
        try
        {
            int num = Integer.parseInt(text.trim());
            if ( num > 0 && num <= 31 )
                return true;
            else
                return false;
        }
        catch ( NumberFormatException nfe )
        {
            return false;
        }
    }
    static boolean isDate( String text )
    {
        text = text.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        String[] parts = text.split(" ");
        if ( parts.length==2 )
        {
            String month = null;
            int day = 0;
            if ( Date.isMonth(parts[0]) )
                month = parts[0];
            if ( Date.isDay(parts[1]) )
                day = Integer.parseInt(parts[1]);
            return month != null && day != 0;
        }
        else
            return false;
    }
    boolean isDayName( String text )
    {
        String trimmed = text.replaceAll("[^a-zA-Z ]", "").toLowerCase();
        if ( Date.days.contains(trimmed) )
            return true;
        else
            return false;
    }
    /**
     * Add a line to the date
     * @param line the line to add
     * @return true if the line fit into the date else false
     */
    public boolean addLine( String line )
    {
        switch ( state )
        {
            case 0: // look for year
                if ( Date.isYear(line) )
                {
                    this.year = Date.toYear(line);
                    state = 1;
                }
                else if ( Date.isDate(line) )
                {
                    this.date = line;
                    state = 2;
                }
                else
                    return false;
                break;
            case 1: // look for date
                if ( isDate(line) )
                {
                    this.date = line;
                    state = 2;
                }
                else
                    return false;
                break;
            case 2: case 3:// look for location or dayName
                if ( isDayName(line) )
                {
                    this.dayName = line;
                    state = 3;
                }
                else
                    return false;
                break;
        }
        return true;
    }
    public String toString()
    {
        StringBuilder sb =new StringBuilder();
        sb.append("<div class=\"date\">");
        if ( year !=0 )
        {
            sb.append( year );
            sb.append("<br>");
        }
        if ( date != null )
        {
            sb.append(date);
            sb.append("<br>");
        }
        if ( dayName != null )
        {
            sb.append(dayName);
            sb.append("<br>");
        }
        sb.append("</div>");
        return sb.toString();
    }
    /**
     * Unimplemented in this class
     */
    public void markHyphen( boolean hard )
    {
    }
    public static void main(String[]args)
    {
        Date d = new Date();
        d.addLine("1871");
        d.addLine("May 13");
        d.addLine("Sunday");
        d.addLine("Concord, Massachusetts");
        System.out.println(d.toString());
    }
}
