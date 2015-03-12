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

package bhl.pages.handler;
import org.json.simple.JSONObject;
/**
 * Description of page
 * @author desmond
 */
public class PageDesc 
{
    String n;
    String id;
    PageDesc(String n, String id )
    {
        this.n = n;
        this.id = id;
    }
    boolean isInteger( String str )
    {
        try
        {
            int value = Integer.parseInt(str);
            return true;
        }
        catch ( NumberFormatException nfe)
        {
            return false;
        }
    }
    public JSONObject toJSONObject()
    {
        JSONObject obj = new JSONObject();
        if ( isInteger(n) )
            obj.put("n",Integer.parseInt(n));
        else
            obj.put("n",n);
        if ( isInteger(id) )
            obj.put("id",Integer.parseInt(id));
        else
            obj.put("id",id);
        return obj;
    }
}
