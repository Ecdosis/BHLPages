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
import calliope.AeseSpeller;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A simple HTML filter for the Brew journal, 1871.
 * We just format the plain text for display. This is not permanent.
 * @author desmond
 */
public class BrewJournal1871 implements Filter 
{
    ArrayList<Block> blocks;
    AeseSpeller speller;
    protected HashSet<String> compounds;
    
    public BrewJournal1871()
    {
        blocks = new ArrayList<Block>();
        try
        {
            this.speller = new AeseSpeller( "en_US" );
            this.compounds = new HashSet<String>();
        }
        catch ( Exception e )
        {
            System.out.println("speller not initialized:" +e.getMessage());
        }
    }
    /**
     * We really should cleanup the speller before we go
     */
    protected void finalize()
    {
        if ( this.speller != null )
            this.speller.cleanup();
    }
    private Block transition( Block current, String className, String line, boolean change )
    {
        try
        {
            String fullName = "bhl.pages.filters."+className;
            if ( current == null )
            {
                Class tClass = Class.forName( fullName );
                current = (Block)tClass.newInstance();
                current.addLine(line);
            }
            else if ( change || !current.addLine(line) )
            {
                blocks.add( current );
                Class tClass = Class.forName( fullName );
                current = (Block)tClass.newInstance();
                current.addLine(line);
            }
            // else it was already added
            return current;
        }
        catch ( Exception e )
        {
            System.out.println("invalid class "+className);
            return current;
        }
    }
    /**
     * Should we hard-hyphenate two words or part-words?
     * @param last the previous 'word'
     * @param next the word on the next line
     * @return true for a hard hyphen else soft
     */
    public boolean isHardHyphen( String last, String next )
    {
        if ( last.endsWith("-") )
            last = last.substring(0,last.length()-1);
        String compound = last+next;
        if ( speller.hasWord(last,"en_US")
            &&speller.hasWord(next,"en_US")
            &&(!speller.hasWord(compound,"en_US")
                ||compounds.contains(compound)))
            return true;
        else
            return false;
    }
    String getLastWord( String line )
    {
        int state = 0;
        String word = "";
        int end = line.length();
        for ( int i=line.length()-1;i>=0;i-- )
        {
            char token = line.charAt(i);
            switch ( state )
            {
                case 0:
                    if ( token=='-' )
                    {
                        state = 1;
                        end = i+1;
                    }
                    else if (Character.isLetter(token) )
                    {
                        end = i+1;
                        state =1;
                    }
                    else if ( !Character.isWhitespace(token) )
                        state = -1;
                    break;
                case 1: // nothing but spaces until nothing but letters ends
                    if ( !Character.isLetter(line.charAt(i)) )
                    {
                        word = line.substring(i+1,end);
                        state = -1;
                    }
                    break;
            }
            if ( state == -1 )
                break;
        }
        return word;
    }
    String getFirstWord( String line )
    {
        int state = 0;
        String word = "";
        int start = 0;
        for ( int i=0;i<line.length();i++ )
        {
            char token = line.charAt(i);
            switch ( state )
            {
                case 0:
                    if ( !Character.isWhitespace(token) 
                        && Character.isLetter(token) )
                    {
                        state = 1;
                        start = i+1;
                    }
                    else
                        state = -1;
                    break;
                case 1: // nothing but spaces until nothing but letters ends
                    if ( !Character.isLetter(line.charAt(i)) )
                    {
                        word = line.substring(0,i);
                        state = -1;
                    }
                    break;
            }
            if ( state == -1 )
                break;
        }
        return word;
    }
    @Override
    public String filter( String text )
    {
        String[] lines = text.split("\n");
        Block current = null;
        String firstWord = null;
        String lastWord = null;
        for ( int i=0;i<lines.length;i++ )
        {
            firstWord = getFirstWord(lines[i]);
            if ( current instanceof Paragraph && lastWord != null 
                && firstWord != null  && lastWord.endsWith("-") )
                if ( isHardHyphen(lastWord,firstWord) )
                    current.markHyphen(true);
                else
                    current.markHyphen(false);
            if ( Date.isYear(lines[i]) )
                current = transition(current,"Date",lines[i],true);
            else if ( Location.isLocation(lines[i]) )
                current= transition(current,"Location",lines[i],true);
            else if ( lines[i].startsWith(" ") )
                current = transition(current,"Paragraph",lines[i].trim(),true);
            else
                current = transition(current,"Paragraph",lines[i],false);
            lastWord = getLastWord(lines[i]);
        }
        if ( current != null )
            blocks.add(current);
        // now string the paras together
        StringBuilder sb = new StringBuilder();
        for ( int i=0;i<blocks.size();i++ )
        {
            sb.append(blocks.get(i).toText(this.speller));
            if ( i <blocks.size()-1 )
                sb.append("\n");
        }
        return sb.toString();
    }
}
