/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
