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

/**
 * A simple filter for converting plain text to HTML
 * @author desmond
 */
public class DefaultHtmlFilter implements Filter
{
    public String filter( String text )
    {
        int state = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("<p>");
        for ( int i=0;i<text.length();i++ )
        {
            char token = text.charAt(i);
            switch ( state )
            {
                case 0:
                    if ( token != ' '&&token!='\t'&& token!='\r'&&token!='\n' )
                    {
                        state = 1;
                        sb.append(token);
                    }
                    break;
                case 1: // looking for CR
                    if ( token == '\r' )
                        state = 2;
                    else if ( token == '\n' )
                        state = 3;
                    else
                        sb.append(token);
                    break;
                case 2: // seen CR
                    if ( token == '\n' )
                        state = 3;
                    else if ( token == '\r' )
                        state = 7;
                    else if ( token == ' ' || token == '\t' )
                        state = 4;
                    else 
                    {
                        sb.append('\n');    // normalise to unix line-endings
                        sb.append(token);
                        state = 1;
                    }
                    break;
                case 3: // seen LF
                    if ( token == ' ' || token == '\t' )
                        state = 4;
                    else if ( token == '\r' )
                        state = 5;
                    else if ( token=='\n' )
                        state = 6;
                    else
                    {
                        sb.append("\n");
                        sb.append(token);
                        state = 1;
                    }
                    break;
                case 4: // seen space after LF
                    if ( token != ' '&&token != '\t'&&token!='\r'&&token!='\n' )
                    {
                        sb.append("</p>\n<p>");
                        sb.append(token);
                        state = 1;
                    }
                    break;
                case 5: // seen LF then CR
                    if ( token=='\n' )
                        state = 6;
                    else if ( token != '\r' )
                    {
                        sb.append("</p>\n<p>");
                        sb.append(token);
                        state = 1;
                    }
                    break;
                case 6: // seen two LFs
                    if ( token != ' '&&token != '\t'&&token!='\r'&&token!='\n' )
                    {
                        sb.append("</p>\n<p>");
                        sb.append(token);
                        state = 1;
                    }
                    break;
                case 7: // seen two CRs
                    if ( token != ' '&&token != '\t'&&token!='\r'&&token!='\n' )
                    {
                        sb.append("</p>\n<p>");
                        sb.append(token);
                        state = 1;
                    }
                    break;
            }
        }
        sb.append("</p>");
        return sb.toString();
    }
    public static void main(String[] args )
    {
        String text1="The quick\rbrown fox\rjumps over\r The lazy\r\rdog.";
        String text2="The quick\nbrown fox\njumps over\n The lazy\n\ndog.";
        String text3="The quick\r\nbrown fox\r\njumps over\r\n The lazy\r\n\r\ndog.";
        DefaultHtmlFilter filter = new DefaultHtmlFilter();
        System.out.println("Mac:");
        String html1 = filter.filter(text1);
        System.out.println(html1);
        System.out.println("UNIX:");
        String html2 = filter.filter(text2);
        System.out.println(html2);
        System.out.println("DOS:");
        String html3 = filter.filter(text3);
        System.out.println(html3);
    }
}
