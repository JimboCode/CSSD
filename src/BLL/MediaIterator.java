package BLL;

import java.util.Iterator;
import java.util.Stack;

/**
 * Iterator used by parent nodes in the BLL tree to conform with the interface
 * when iterating over the tree
 * 
 * @author James Staite
 */
public class MediaIterator implements Iterator
{
    // stack used to store the iterator of parent node found for later use
    Stack stack = new Stack();
    
    /**
     * Store the iterator found on the stack for later use
     * @param iterator iterator to be stored
     */
    public MediaIterator(Iterator iterator)
    {
        stack.push(iterator);
    }
    
    /**
     * confirms if they is another object to iterate over
     * @return true - another object / false - no further objects
     */
    @Override
    public boolean hasNext() 
    {
        // if stack empty finished
        if(stack.empty())
        {
            return false;
        }
        else
        {
            // get iterator off stack and check to see if it has another object
            Iterator iterator = (Iterator) stack.peek();
            if (!iterator.hasNext())
            {
                // pop iterator of stack and call hasNext() recursively
                stack.pop();
                return hasNext();
            }
            else
            {
                // otherwise there is a next item
                return true;
            }
        }
    }

    @Override
    public Object next() 
    {
        // check they is another item
        if (hasNext())
        {
            // get iterator off the stack and get its next item
            Iterator iterator = (Iterator) stack.peek();
            MediaItem component = (MediaItem) iterator.next();
            
            // if the item is a parent object it needs to be included in the iteration, so push it onto the stack
            if (component instanceof MediaElement)
            {
                stack.push(component.createIterator());
            }
            
            // return item
            return component;
        }
        else
        {
            // return nothing
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
