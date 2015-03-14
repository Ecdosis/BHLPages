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
import bhl.pages.filters.tags.*;
import calliope.AeseSpeller;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.HashMap;
/**
 * A HTML paragraph <p>...</p>
 * @author desmond
 */
public class Paragraph implements Block
{
    /** the content of the paragraph being gradually built up */
    StringBuilder content;
    /** if true, don't append \n to line-end */
    boolean lastWasHyphen;
    /** paired tags to recognise */
    static HashMap<String,TagTemplate> pairedTags;
    /** single tags to recognise */
    static HashMap<String,TagTemplate> singleTags;
    static {
        pairedTags = new HashMap<String,TagTemplate>();
        singleTags = new HashMap<String,TagTemplate>();
        // move these into the config file later
        pairedTags.put("margin",new TagTemplate("div","margin",null));
        pairedTags.put("delete",new TagTemplate("span","delete",null));
        singleTags.put("male",new TagTemplate(null,null,"&#9794;"));
        singleTags.put("female",new TagTemplate(null,null,"&#9792;"));
    }
    Paragraph()
    {
        content = new StringBuilder();
    }
    public boolean addLine( String line )
    {
        if ( lastWasHyphen )
            lastWasHyphen = false;
        else if ( content.length()== 0 )
            content.append("<p>");
        else
            content.append("\n");
        content.append( line );
        return true;
    }
    /**
     * Mark trailing hyphen
     */
    public void markHyphen( boolean hard )
    {
        if ( content.length()>0 && content.charAt(content.length()-1)=='-' )
        {
            if ( !hard )
            {
                content.setLength(content.length()-1);
                content.append("<span class=\"soft\">-\n</span>");
            }
            else
                // NB: also marked as soft but hyphen is now outside
                content.append("<span class=\"soft\">\n</span>");
            lastWasHyphen = true;
        }
    }
    /**
     * Is a word not in the dictionary and length > 4?
     * @param word the word to test
     * @param speller the spell-checker to use
     * @return true if it is a name by this definition
     */
    private boolean isName( String word, AeseSpeller speller )
    {
        word = word.replaceAll("[\\.\\,;\\?!]","");
        for ( int i=0;i<word.length();i++ )
        {
            char letter = word.charAt(i);
            if ( !Character.isLetter(letter) )
                return false;
        }
        return !speller.hasWord(word.toLowerCase(),"en_US")&&word.length()>4;
    }
    /**
     * A Genus name is a name that starts with a capital letter
     * @param word the possible genus name
     * @param speller the spell-checker
     * @return true if it matches
     */
    private boolean isGenusName( String word, AeseSpeller speller)
    {
        return isName(word,speller)&&Character.isUpperCase(word.charAt(0));
    }
    /**
     * A genus name can be a single capital letter followed by a full-stop
     * @param word the "word" to test
     * @return true if it is an abbreviated Genus name
     */
    private boolean isGenus( String word )
    {
        return word.length()==2 && Character.isUpperCase(word.charAt(0))
            &&word.charAt(1)=='.';
    }
    /**
     * Is the word the name of a known paired tag?
     * @param name the name to test
     * @return true if it is defined as such
     */
    private boolean isPairedTag( String name )
    {
        return pairedTags.containsKey(name);
    }
    /**
     * Is the word the name of a known single tag?
     * @param name the name to test
     * @return true if it is in the list
     */
    private boolean isSingleTag( String name )
    {
        return singleTags.containsKey(name);
    }
    /**
     * Look for species names and tags in the paragraph text; convert to HTML
     * @param the spell-checker
     * @param raw text with species names or basic [] tags
     * @return the same string with spans added for species names
     */
    private String replaceTags( AeseSpeller speller, String raw )
    {
        StringTokenizer st = new StringTokenizer(raw,"[]/ \n",true);
        int state = 0;
        int offset = 0;
        int start = 0;
        String genus = "";
        String tag = "";
        StringBuilder core = new StringBuilder();
        ArrayList<Tag> list = new ArrayList<Tag>();
        while ( st.hasMoreTokens() )
        {
            String token = st.nextToken();
            switch ( state )
            {
                case 0:
                    if ( token.equals("[") )
                    {
                        start = offset;
                        state = 1;
                    }
                    else if ( isGenusName(token,speller) || isGenus(token) )
                    {
                        genus = token;
                        start = offset;
                        state = 2;
                    }
                    break;
                case 1: // seen a "["
                    if ( isPairedTag(token) )
                    {
                        tag = token;
                        core.setLength(0);
                        state = 3;
                    }
                    else if ( isSingleTag(token) )
                    {
                        tag = token;
                        state = 4;
                    }
                    else
                        state = 0;
                    break;
                case 2: // seen a possible genus
                    if ( isName(token,speller) )
                    {
                        Tag pt = new PairedTag("span","species",start);
                        pt.setEnd( offset+token.length());
                        pt.setCore( genus+" "+token);
                        list.add( pt );
                        state = 0;
                    }
                    else if ( !token.equals(" ") )
                        state = 0;
                    break;
                case 3: // paired tag
                    if ( token.equals("]") )
                        state = 5;
                    else
                        state= 0;
                    break;
                case 4: // single tag
                    if ( token.equals("]") )
                    {
                        TagTemplate tt = singleTags.get(tag);
                        SingleTag t = new SingleTag(null,start);
                        t.setCore(tt.core);
                        t.setEnd(offset+1);
                        list.add(t);
                    }
                    else
                        state = 0;
                    break;
                case 5: //core of paired tag
                    if ( token.equals("[") )
                        state = 6;
                    else
                        core.append( token );
                    break;
                case 6: // seen start-tag, core, "["
                    if ( token.equals("/") )
                        state = 7;
                    else
                        state= 0;
                    break;
                case 7: // seen start-tag,core,"[/"
                    if ( token.equals(tag) )
                        state = 8;
                    else
                        state = 0;
                    break;
                case 8: // seen start-tag,core,"[/",end-tag-name
                    if ( token.equals("]") )
                    {
                        TagTemplate tt = pairedTags.get(tag);
                        PairedTag pt = new PairedTag(tt.name,tt.className,start);
                        pt.setCore(core.toString());
                        pt.setEnd(offset+1);
                        list.add(pt);
                    }
                    state = 0;
                    break;
            }
            offset += token.length();
        }
        StringBuilder converted = new StringBuilder();
        int from = 0;
        for ( int i=0;i<list.size();i++ )
        {
            Tag t = list.get(i);
            if ( t.start > from )
                converted.append(raw.substring(from,t.start));
            converted.append(t.toString());
            from = t.end;
        }
        if ( from < raw.length() )
            converted.append(raw.substring(from));
        return converted.toString();
    }
    /**
     * Convert the paragraph to a String
     * @param speller the spell-checker
     * @return an HTML paragraph
     */
    @Override
    public String toText( AeseSpeller speller )
    {
        content.append("</p>");
        String converted = replaceTags( speller, content.toString() );
        return converted;
    }
}
