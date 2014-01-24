package BLL;

import java.util.Observable;
import java.util.Observer;

/**
 * This class managers is used for additions and removals of MediaItems from the tree structure
 * It provides all events to the TaskList and ContentTree objects from activity from MediaItems
 * 
 * @author James Staite
 */
public class ContentManager extends Observable implements Observer
{
    // root node to managed tree
    MediaItem rootNode;
    
    // Elements workflow controller
    MediaItemWorkFlow elementController = new MediaElementWorkFlow();
    
    // Assets workflow controller
    MediaItemWorkFlow assetController = new MediaAssetWorkFlow();
    
    // Assets workflow controller
    MediaAssetSubtitlesWorkFlow assetSubtitlesController = new MediaAssetSubtitlesWorkFlow();
    
    TaskList tasklist;
    
    /**
     * Sets up the content manager
     * @param project reference to the parent project of the new ContentManager
     */
    ContentManager(Project project)
    {
        // create the root node
        rootNode = new MediaElement(project.getName(), "", ComponentType.COMPRESSED_ELEMENT, null, elementController);
        rootNode.addObserver(this);
    }
    
    /**
     * Adds a new MediaItem to the tree
     * @param name Short name of the MediaItem to be displayed in the Tree and TaskLists
     * @param description Longer description of the item
     * @param type The component type to be created by the completion of this portion of the tree e.g. video, audio etc.
     * @param nodeType Type of node to be created e.g. Element or Asset
     * @param parent The parent node that this is to be attached to
     */
    public MediaItem addItem(String name, String description, ComponentType type, NodeType nodeType, MediaItem parent)
    {        
        // setup reference
        MediaItem child = null;
        
        // create the appropriate object type
        switch (nodeType)
        {
            case ELEMENT:
            {
                // inserts the appropiate controller class - Strategy pattern
                child = new MediaElement(name, description, type, parent, elementController);
                break;
            }
            
            case ASSET:
            {
                switch(type)
                {
                    // inserts the appropiate controller class - Strategy pattern
                    case SUBTITLES:
                    {
                        child = new MediaAsset(name, description, type, parent, assetSubtitlesController);
                        break;
                    }
                    default:
                    {
                        child = new MediaAsset(name, description, type, parent, assetController);
                    }
                }
                break;
            }   
            
            default:
            {
                return child;
            }
        }
        // add event observation to the new object
        // subscribe to notification events for changes to tree structure
        child.addObserver(this);
        
        // add new object to parent
        parent.addMediaItem(child);
        
        return child;
    }
    
    /**
     * Removes MediaItem passed in from tree
     * @param item MediaItem to be removed
     */
    public void removeItem(MediaItem item)
    {
        // check if this or any sub node have tasks - if so do not remove
        if (item.canBeDeleted())
        {
            // find parent node
            MediaItem parent = item.getParent();
            item.deleteObserver(this);
            // remove item from parents child
            parent.deleteMediaItem(item);
        }
    }
    
    /**
     * Move a MediaItem node from one parent to another
     * 
     * @param newParent the new parent MediaItem to have the child node attached
     * @param child the child node to be moved to the parent node
     */
    public void moveItem(MediaItem newParent, MediaItem child)
    {
        // reassign parent
        child.setParent(newParent);
    }
    
    /**
     * Returns location of a file associated with a MediaItem
     * @param item MediaItem
     * @return Filename including directory structure of the file
     */
    public String getFile(MediaItem item)
    {
        return null;
    }
    
    /**
     * Return the tree structure
     * @return Root MediaItem node
     */
    public MediaItem getTree()
    {
        return rootNode;
    }
    
    /**
     * Receives change updates from MediaItems in the tree
     * @param object the MediaItem raising the event
     * @param arg argument parameters passed with the event
     */
    @Override
    public void update(Observable object, Object arg) 
    {
        // relay events notifing any listenering trees or the task list
        // Assumes that all calls to this will be from MediaItems, but check that
        // the event argument is vaild
        
        // if a tree event then relay it
        if(arg instanceof ContentEvent)
        {
            // flag change
            setChanged();
            // send notification of new child to add to tree
            notifyObservers(arg);
        }
        
        // if a task list event then relay it
        if (arg instanceof TaskListEvent)
        {
            // flag change
            setChanged();
            // send notification of new child to add to tree
            notifyObservers(arg);
        }
    }
}
