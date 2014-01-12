package BLL;

/**
 * ContentManager tree update event argument object passed with details of the update
 *
 * @author James Staite
 */
public class ContentEvent 
{
    // Set of constant actions
    public static final int ADD = 0;
    public static final int DELETE = 1;
    public static final int UPDATE = 2;
    public static final int MOVE = 3;
    
    // reference to any parent involved in update (only used in ADD and MOVE)
    private MediaItem parent;
    
    // reference to the child object involved in the update
    private MediaItem child;
    
    // the event action
    private int action;
    
    /**
     * Sets the event parameters at creation
     * @param parent parent object - used in an ADD
     * @param child child object - used in ADD, DELETE, UPDATE & MOVE
     * @param action the action to be preformed
     */
    public ContentEvent(MediaItem parent, MediaItem child, int action)
    {
        this.parent = parent;
        this.child = child;
        this.action = action;
    }

    /**
     * returns the parent object
     * @return the parent
     */
    public MediaItem getParent() {
        return parent;
    }

    /**
     * returns the child object
     * @return the child
     */
    public MediaItem getChild() {
        return child;
    }

    /**
     * returns the action to be preformed
     * @return the action
     */
    public int getAction() {
        return action;
    }
}
