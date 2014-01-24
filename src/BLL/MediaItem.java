package BLL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * Media Tree interface - part of composite pattern
 * 
 * @author James Staite
 */
public abstract class MediaItem extends Observable
{
    // Name of item to be displayed in tasklists and content trees
    private String name;
    
    // longer detailed description of user in tasklists
    private String description;
     
    // Origin of Media
    protected MediaSource mediaSource;
    
    // Media type of this media item
    protected ComponentType mediaType;
    
    // Files stored
    protected ArrayList<File> filesStored = new ArrayList();
    
    // current valid file is filesStored
    protected File currentFile;
    
    // Tasks of this media item
    protected ArrayList<TaskItem> mediaItemTasks = new ArrayList();
    
    // Current Task
    protected TaskItem currentTask = null;
    
    // Media Items current status
    protected MediaStatus status;
    
    // reference to parent node
    protected MediaItem parent;
    
    // The controlling workflow for this object
    protected MediaItemWorkFlow workFlow;
    
    // Flag for update events
    // Used when a child object moves parent to avoid adding and removing events
    protected boolean notificationFlag = true;
    
    /**
     * creates & initialises object
     * 
     * @param name Sort name displayed as the name of the node
     * @param description Description of the content item
     * @param mediaType The component yielded by the item
     * @param parent The mediaItem that this child belongs to
     * @param workFlow A object that implements MediaItemWorkFlow interface to provide control of work flow (Strategy Pattern)
     */
    public MediaItem(String name, String description, ComponentType mediaType, MediaItem parent, MediaItemWorkFlow workFlow)
    {
        this.name = name;
        this.description = description;
        this.mediaType = mediaType;
        this.parent = parent;
        this.workFlow = workFlow;
    }
    
    /*
     * adds mediaitem to element
     */
    public void addMediaItem(MediaItem child)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * removes mediaItem child from a media element
     * @param child child to be removed
     */
    public void deleteMediaItem(MediaItem child)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Call by parent to allow item to clean up when being deleted
     */
    protected abstract void beingRemovedCleanUp();
    
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
        // notify update
        raiseUpdateEvent();
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
        // notify update
        raiseUpdateEvent();
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

    /**
     * @return the mediaSource
     */
    public MediaSource getMediaSource() {
        return mediaSource;
    }
    
    /**
     * 
     * @return 
     */
    public abstract boolean canMediaSourceBeChanged();

    /**
     * 
     * @param mediaSource
     * @return 
     */
    public abstract boolean setMediaSource(MediaSource mediaSource);

    /**
     * @return the mediaType
     */
    public ComponentType getMediaType() {
        return mediaType;
    }

    /**
     * @param mediaType the mediaType to set
     */
    public boolean setMediaType(ComponentType mediaType) {
        if(mediaItemTasks.isEmpty())
        {
            this.mediaType = mediaType;
            // notify update
            raiseUpdateEvent();
            return true;
        }
        return false;
    }
    
    public boolean canSetMediaType()
    {
        if(mediaItemTasks.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @return the status
     */
    public MediaStatus getStatus() {
        return status;
    }
    
    /**
     * Return a valid list of MediaStatus options based upon the Media Items current state and status
     * that the status could be changed to
     * @param withAFile boolean indicating if the status change includes providing a file
     * @return MediaStatus[] of valid status option that the current status can be updated to
     */
    public MediaStatus[] getValidStatusOptions(Worker worker)
    {
        return workFlow.getHandledValidStatusOptions(status, mediaSource, worker);
    }
    
    public WorkerRoles[] getValidAllocateWorkRoles(MediaStatus status)
    {
        return workFlow.getHandledValidAllocateToWorkerRoles(status);
    }
    
    public boolean getFileRequiredWithStatus(MediaStatus status)
    {
        return workFlow.getFileRequiredWithStatus(status);
    }
    
    protected TaskItem addTask(WorkerRoles workerRole, int priority, TaskStatus taskStatus, String description, boolean fileRequired)
    {
        TaskItem newTask = new TaskItem(this, workerRole, priority, taskStatus, description, fileRequired);
        
        // add the new task
        mediaItemTasks.add(newTask);
        
        // update the current task
        currentTask = newTask;
        
        // raise event to tasklist to add this event to the master list
        // flag change
        setChanged();
        // send notification of new child to add to tree
        notifyObservers(new TaskListEvent(newTask, null, TaskListEvent.NEW));
        
        return newTask;
    }
    
    /**
     * Update the media items status and provide the details for the user to create the next task if required
     * @param status The new status requested - use getHandledValidStatusOptions to check which options are currently avaliable (based upon it current state)
     * @param updatingUser The user requesting the status change
     * @param Description Description used for the creation of any new task in connection with the new status
     * @param roleType The role of the individual that any new task should be allocated to - use getHandledValidAllocateToWorkerRoles for avaliable options
     * @param allocatedTo An individual that any task created by the status change will be allocated to (null if only allocated to a worker role)
     * @return confirmation if the update was successful
     */
    public abstract boolean setStatus(MediaStatus status, Worker updatingUser, String Description, WorkerRoles roleType, Worker allocatedTo, int priority);
    
    /**
     * Notifies the mediaItem thats it current task has been completed.
     * Used to get mediaItem to complete the next task if appropriate
     */
    public abstract void currentTaskCompleted(String Comments);
    
    /**
     * Check if the node or any of its child nodes have tasks allocated to them
     * If they are tasks allocated the node cannot be removed.
     * @return 
     */
    public abstract boolean canBeDeleted();
    
    /**
     * Allows notification from children nodes that they status has been updated
     */
    protected void childStatusChanged()
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Provide reference to the parent object.  It will be null if this is the root object
     * @return Parent MediaItem
     */
    public MediaItem getParent()
    {
        return parent;
    }
    
    /**
     * Moves this object to a new parent by setting its parent
     * @param newParent target node for this mediaItem to be moved to
     */
    public boolean setParent(MediaItem newParent)
    {
        // check new parents status will allow additions 
        if (parent.getStatus() == MediaStatus.AWAITING_ASSETS || parent.getStatus() == MediaStatus.AWAITING_ASSETS_DELAYED)
        {
            // temporary disable addition and removal notifications
            parent.setAddRemoveNotifications(false);
            
            // remove from existing parent
            parent.deleteMediaItem(this);
            
            // enable addition and removal notifications
            parent.setAddRemoveNotifications(true);
            
            // disable addition and removal notifications
            newParent.setAddRemoveNotifications(false);
            
            // temporary disable addition and removal notifications
            newParent.addMediaItem(this);
            
            // enable addition and removal notifications
            newParent.setAddRemoveNotifications(true);
            
            // update the parent reference
            this.parent = newParent;
            
            // notify any tree listeners of the move
            notifyObserversOfChange(new ContentEvent(newParent, this, ContentEvent.MOVE));
            
            // move succeeded
            return true;
        }
        
        // move failed
        return false;
    }
    
    protected void setAddRemoveNotifications(boolean value)
    {
        notificationFlag = value;
    }
    
    /**
     * Notifies Observing objects of a state change
     */
    public void notifyObserversOfChange(ContentEvent eventInfo)
    {
        // flag change
        setChanged();
        // send notification of new child to add to tree
        notifyObservers(eventInfo);
    }
    
    /**
     * Raise an update event on the MediaItem used for property updates etc.
     */
    protected void raiseUpdateEvent()
    {
        notifyObserversOfChange(new ContentEvent(null, this, ContentEvent.UPDATE));
    }
}
