package BLL;

import java.util.Iterator;
import java.util.List;

/**
 * Media Tree interface - part of composite pattern
 * 
 * @author James Staite
 */
public abstract class MediaItem 
{
    // Name of item to be displayed in tasklists and content trees
    private String name;
    
    // longer detailed description of user in tasklists
    private String description;
        
    /*
     * Instanciates object
     */
    public MediaItem(String name, String description)
    {
        this.name = name;
        this.description = description;
    }
    
    /*
     * adds mediaitem to element
     */
    public void addMediaItem(MediaItem child)
    {
        throw new UnsupportedOperationException();
    }
    
    /*
     * removes mediaItem from element
     */
    public void removeMediaItem(MediaItem child)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Gets the MediaItem at the index passed in
     * @param index index of child object to be returned
     * @return child object at that index
     */
    public MediaItem getChild(int index)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of the children for this object
     * @return 
     */
    public abstract List<MediaItem> getChildren();
    
    /**
     * returns a iterator for the child objects of this object
     * @return iterator
     */
    public abstract Iterator createIterator();
    
    /**
     * Gets the name of the MediaItem
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the MediaItem
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the longer description of the MediaItem
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the longer description of the MediaItem
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Used to display the name of the object in Trees
     * @return 
     */
    @Override
    public String toString()
    {
        return name;
    }
}
