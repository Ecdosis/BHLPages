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
 *
 * @author desmond
 */
public class KeyPair implements Comparable<KeyPair> {
    int key;
    int value;
    KeyPair( int key, int value )
    {
        this.key = key;
        this.value = value;
    }
    public int compareTo( KeyPair other )
    {
        return (this.key<other.key)?-1:(this.key>other.key)?1:0;
    }
}
