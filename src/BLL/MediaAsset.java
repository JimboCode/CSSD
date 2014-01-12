package BLL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Leaf node in the BLL tree structure of project content
 * 
 * @author James Staite
 */
public class MediaAsset extends MediaItem
{
    /**
     * Instanciates object 
     * @param name
     * @param description 
     */
    public MediaAsset(String name, String description)
    {
        super(name, description);
    }
    
    
    /**
     * Pass back NullIterator to comply with the interface for iteration
     * @return NullIterator
     */
    @Override
    public Iterator createIterator() {
        return new NullIterator();
    }

    /**
     * Pass back an empty list to comply with the interface
     * @return Empty list
     */
    @Override
    public List<MediaItem> getChildren() {
        return new ArrayList();
    }
}
