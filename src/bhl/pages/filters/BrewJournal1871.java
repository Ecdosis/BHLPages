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
 *  (c) copyright Desmond Schmidt 2015
 */

package bhl.pages.filters;
import java.util.ArrayList;

/**
 * A simple HTML filter for the Brew journal, 1871.
 * We just format for display of plain text. This is not permanent.
 * @author desmond
 */
public class BrewJournal1871 implements Filter 
{
    @Override
    public String filter( String text )
    {
        String[] lines = text.split("\n");
        ArrayList<Block> blocks = new ArrayList<Block>();
        Block current = null;
        for ( int i=0;i<lines.length;i++ )
        {
            if ( lines[i].startsWith(" ") )
            {
                if ( current != null )
                    blocks.add(current);
                current = new Paragraph();
                current.addLine(lines[i].trim());
            }
            else if ( Date.isYear(lines[i]) )
            {
                if ( current != null )
                    blocks.add(current);
                current = new Date(lines[i]);
            }
            else
                current.addLine( lines[i] );
        }
        if ( current != null )
            blocks.add(current);
        // now string the paras together
        StringBuilder sb = new StringBuilder();
        for ( int i=0;i<blocks.size();i++ )
        {
            sb.append(blocks.get(i));
            if ( i <blocks.size()-1 )
                sb.append("\n");
        }
        return sb.toString();
    }
}
