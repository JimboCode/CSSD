package BLL;

import java.util.Observable;
import java.util.Stack;

/**
 *
 * @author James Staite
 */
public class ContentManager extends Observable
{
    // root node to managed tree
    MediaItem rootNode;
    
    // reference to the parent project
    Project project;
    
    /**
     * Sets up the content manager
     * @param project reference to the parent project of the new ContentManager
     */
    ContentManager(Project project)
    {
        // hold reference to the project this belongs to
        this.project = project;
        
        // create the root node
        rootNode = new MediaElement(project.getName(), "root node");
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
                child = new MediaElement(name, description);
                break;
            case ASSET:
                // TODO pass in reference to media player for the component type (Stragery Pattern)
                child = new MediaAsset(name, description);
                break;
        }
        
        // add new object to parent
        parent.addMediaItem(child);
        
        // fire an event notifing any listenering trees of the addition
        
        // flag change
        setChanged();
        // send notification of new child to add to tree
        notifyObservers(new ContentEvent(parent, child, ContentEvent.ADD));
        
        // return new MediaItem
        return child;
    }
    
    /**
     * Removes MediaItem passed in from tree
     * @param item MediaItem to be removed
     */
    public void removeItem(MediaItem item)
    {
        // TODO check if this or any sub node have tasks - if so do not remove
        
        // find parent node
        MediaItem parent = findParentNode(item);
        
        // remove item from parents child
        parent.removeMediaItem(item);
        
        // fire an event notifing any listenering trees of the removal
        
        // flag change
        setChanged();
        // send notification of new child to add to tree
        notifyObservers(new ContentEvent(null, item, ContentEvent.DELETE));
    }
    
    /**
     * Notifies of updates to the MediaItem passed in
     * @param item MediaItem that has been updated
     */
    public void updateItem(MediaItem item)
    {
        // fire an event notifing any listenering trees of the update
        
        // flag change
        setChanged();
        
        // send notification of new child to add to tree
        notifyObservers(new ContentEvent(null, item, ContentEvent.UPDATE));
    }
    
    public void moveItem(MediaItem newParent, MediaItem child)
    {
        // find parent node
        MediaItem oldParent = findParentNode(child);
                
        // remove child from old parent
        oldParent.removeMediaItem(child);
        
        // add child to new parent
        newParent.addMediaItem(child);
        
        // fire an event notifing any listenering trees of the addition
        
        // flag change
        setChanged();
        // send notification of new child to add to tree
        notifyObservers(new ContentEvent(newParent, child, ContentEvent.MOVE));
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
     * Locates the parent node of the node passed in
     * @param node child of parent node to find
     * @return parent node of node passed in
     */
    private MediaItem findParentNode(MediaItem node)
    {
        // define temp varaiables
        MediaItem parentNode;
        // define temp storage for search
        Stack<MediaItem> stack = new Stack();
        
        // push the top of the tree onto the stack to start search from it
        stack.push(rootNode);
        
        // loop until the stack has been emptied
        while(!stack.empty())
        {
            // pull the next parent node off the stack
            parentNode =  stack.pop();
            
            // check to see if its child match the node required
            for(MediaItem child : parentNode.getChildren())
            {
                // if a child note matches the required node return the parent node
                if(child.equals(node))
                {
                    return parentNode;
                }
                
                // if the child node has children push it onto the stack to check its children later
                if (child instanceof MediaElement)
                    stack.push(child);
            }
        }
        // return null if parent was not found
        return null;
    }
}
