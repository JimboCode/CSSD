package BLL;

import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

/**
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
     * @param Description Longer description of the item
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
                // TODO pass in reference to media player for the component type (Stragery Pattern)
                child = new MediaElement(name, description, type, parent, elementController);
                break;
            case ASSET:
                // TODO pass in reference to media player for the component type (Stragery Pattern)
                child = new MediaAsset(name, description, type, parent, assetController);
                break;
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
    
    public void moveItem(MediaItem newParent, MediaItem child)
    {
        // reassign parent
        child.setParent(newParent);
    }
    
    /**
     * Add file to a MediaItem
     * @param item MediaItem receiving the file
     * @param filename Filename including directory structure
     */
    public void addFile(MediaItem item, String filename)
    {
        
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
     * Reload any child nodes when a node is moved 
     * This is because the node is deleted and then added in it new position
     * The adding does not add any child nodes.
     * @param node The node for which any children nodes will be added.
     */
    private void addChildChildren(MediaItem node)
    {
        // define temp varaiables
        MediaItem parentNode;
        // define temp storage for search
        Stack<MediaItem> stack = new Stack();
        
        // push the top of the tree onto the stack to start search from it
        stack.push(node);
        
        // loop until the stack has been emptied
        while(!stack.empty())
        {
            // pull the next parent node off the stack
            parentNode =  stack.pop();
            
            // add all children
            for(MediaItem child : parentNode.getChildren())
            {
                // raise an update event to add this node to any tree
                // flag change
                setChanged();
                // send notification of new child to add to tree
                notifyObservers(new ContentEvent(parentNode, child, ContentEvent.ADD));
                
                // if the child node has children push it onto the stack to check its children later
                if (child instanceof MediaElement)
                    stack.push(child);
            }
        }
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
