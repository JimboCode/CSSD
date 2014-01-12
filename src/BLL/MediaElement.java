package BLL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Parent node in the BLL tree structure of project content
 * 
 * @author James Staite
 */
public class MediaElement extends MediaItem
{
    // list of child objects held by this object
    private ArrayList<MediaItem> mediaChildren = new ArrayList();
        
    /**
     * instantiation of the object 
     * @param name the name to be displayed in trees and taskLists
     * @param description the longer description of the node
     */
    public MediaElement(String name, String description)
    {
        super(name, description);
    }
    
    /**
     * Adds a MediaItem as a child to this node
     * @param child MediaItem to be added as child
     */
    @Override
    public void addMediaItem(MediaItem child)
    {
        // only add if a MediaItem
        if (child != null) 
        {
            // if a branch append to the end of the list
            if (child instanceof MediaElement)
            {
                mediaChildren.add(child);
            }
            else
            {
                // insert leafs before the branches in list
                // assume to insert at the begining
                int insertIndex = 0;
                
                // iterate over list under a branch is found or the list ends
                for(MediaItem item: mediaChildren)
                {
                    // if a branch is found spot looking
                    if(item instanceof MediaElement) break;
                    
                    // increment to positon inline with loop
                    insertIndex++;
                }
                
                // add child at the index set
                mediaChildren.add(insertIndex, child);
            }
        }
    }
    
    /**
     * Removes a MediaItem child from this node
     * @param child MediaItem to be removed
     */
    @Override
    public void removeMediaItem(MediaItem child)
    {
        // remove child if in list
        if(mediaChildren.contains(child)) mediaChildren.remove(child);
    }
    
    /**
     * Return a child object at the index 
     * @param index the index of the object to be obtained
     * @return child object at the index
     */
    @Override
    public MediaItem getChild(int index)
    {
        return mediaChildren.get(index);
    }
    
    /**
     * returns the iterator for children objects
     * @return iterator
     */
    @Override
    public Iterator createIterator() {
        return new MediaIterator(mediaChildren.iterator());
    }

    /**
     * returns a list of the child objects held by this node
     * @return list of child objects
     */
    @Override
    public List<MediaItem> getChildren() {
        return Collections.unmodifiableList(mediaChildren);
    }
}
