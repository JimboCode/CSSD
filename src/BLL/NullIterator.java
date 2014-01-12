package BLL;

import java.util.Iterator;

/**
 * Null Iterator used by leaf nodes in the BLL tree to conform with the interface
 * when iterating over the tree
 * 
 * @author James Staite
 */
public class NullIterator implements Iterator
{
    /*
     * Always returns false because they have no children
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /*
     * Always returns null because they is no next child
     */
    @Override
    public Object next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
