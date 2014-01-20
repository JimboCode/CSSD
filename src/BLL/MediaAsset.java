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
     * instantiation of object 
     * @param name
     * @param description 
     */
    public MediaAsset(String name, String description, ComponentType mediaType, MediaItem parent, MediaItemWorkFlow workFlow)
    {
        super(name, description, mediaType, parent, workFlow);
        
        // default status for a new item
        status = MediaStatus.AWAITING_ACTION;
        
        // default media source
        mediaSource = MediaSource.SUBCONTRACTOR;
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
    
    @Override
    public boolean setStatus(MediaStatus status, Worker updatingUser, String description, WorkerRoles roleType, Worker allocatedTo, int priority)
    {
        // scrubbing an item is premitted at any time.
        if (status == MediaStatus.SCRUBBED_FROM_DISC)
        {
            // TODO check that a task is not in progress & clear all task assoicated with it
            this.status = status;
            // notify update
            
            raiseUpdateEvent();
            // nofity parent
            parent.childStatusChanged();
        }
        
        // only permit status changes while the parents status is awaiting assets
        // otherwise the build and testing of the assets has already began
        MediaStatus parentStatus = parent.getStatus();
        if(parentStatus != MediaStatus.AWAITING_ASSETS && parentStatus != MediaStatus.AWAITING_ASSETS_DELAYED) return false;
        
        // Get valid status changes without a file submission based up the current status
        MediaStatus[] validChanges = getValidStatusOptions(false, updatingUser);
        
        // if they are not any valid status changes return false
        if (validChanges.length == 0) return false;
        
        //check if the status change requested is permitted
        for(MediaStatus thisStatus: validChanges)
        {
            if (status == thisStatus)
            {
                // status change requested found
                // check if the status requires a change to the current assoicated file
                switch (status)
                {
                    case REPLACEMENT_REQUESTED_FROM_CLIENT:
                    {
                        currentFile.setStatus(FileStatus.OBSOLETE);
                        currentFile = null;
                        break;
                    }
                    case REORDERED_FROM_CONTRACTOR:
                    {
                        currentFile.setStatus(FileStatus.OBSOLETE);
                        currentFile = null;
                        break;
                    }
                    case FIXES_ORDERED_DELAYED:
                    {
                        currentFile.setStatus(FileStatus.DEFECTIVE);
                        break;
                    }
                    case ASSET_READY:
                    {
                        currentFile.setStatus(FileStatus.ACCEPTED);
                        parent.childStatusChanged();
                        break;
                    }    
                }
                
                // set if task will require a file
                boolean fileRequired = getFileRequiredWithStatus(thisStatus);
                
                // create a new task
                TaskItem task = new TaskItem(this, roleType, priority, TaskStatus.AWAITING_ACTION, description, fileRequired);
                
                // if allocated to an individual set the assignment
                if (allocatedTo != null) task.setWorker(allocatedTo);
                
                // addTask
                addTask(task);
                
                // update the status
                this.status = thisStatus;
                
                // notify update
                raiseUpdateEvent();
                
                // confirm the update
                return true;
            }
        }
        
        // status change requested not permitted - confirm back
        return false;
    }
    
    /**
     * Add media files to the Media Item and set the status of the Media Item
     * @param filename Filename
     */
    private void addFile(String filename, String comments)
    {
        MediaStatus newStatus = null;
        String description = "";
        Worker allocateTo = null;
        switch (status)
        {
            case REQUESTED_FROM_CLIENT: case ORDERED_FROM_CONTRACTOR: 
            case REORDERED_FROM_CONTRACTOR: case REPLACEMENT_REQUESTED_FROM_CLIENT:
            case ORDERED_IN_HOUSE:
            {
                // store file provided
                currentFile = new File(filename, FileStatus.NEW_NOT_QC_CHECKED, 0);
                filesStored.add(currentFile);
                
                // set the status to update to
                newStatus = MediaStatus.ARRIVED_IN_VAULT;
                description = "Asset provided; Ready for inward QC (Comment : " + comments + ")";
                // allocateTo = ;
                break;
            }
            case FIXES_COMPLETED:
            {
                int version = currentFile.getVersion();
                File file = new File(filename, FileStatus.FIXED, version++);
                filesStored.add(file);
                
                // set the status to update to
                newStatus = MediaStatus.AWAITING_QC;
                description = "Fixes completed; Ready for QC (Comment : " + comments + ")";
                break;
            }
            case COMPRESSION_COMPLETED:
            {
                int version = currentFile.getVersion();
                File file = new File(filename, FileStatus.COMPRESSED, version++);
                filesStored.add(file);
                
                // set the status to update to
                newStatus = MediaStatus.AWAITING_QC;
                description = "Fixes completed; Ready for QC (Comment : " + comments + ")";
                break;
            }
        }
        
        // if status changing raise a new task
        if (newStatus != null) 
        {
            // create a new task MediaItem mediaItem, WorkerRoles workRoleType, int priority, TaskStatus status, String description, boolean fileRequired
            TaskItem task = new TaskItem(this, WorkerRoles.QC_TEAM_LEADER, 4, TaskStatus.AWAITING_ACTION, description, false);

            // if allocated to an individual set the assignment
            if (allocateTo != null) task.setWorker(allocateTo);

            // addTask
            addTask(task);
            
            this.status = newStatus;
            
            // notify update
            raiseUpdateEvent();
        }                
    }
    
    @Override
    public void currentTaskCompleted(String comments)
    {
        if (currentTask.isFileRequired())
        {
            addFile(currentTask.getFilename(), comments);
        }
    }
    
    @Override
    public boolean canBeDeleted()
    {
        // can only be deleted if the task list is empty
        return mediaItemTasks.isEmpty();
    }
    
    @Override
    protected void beingRemovedCleanUp()
    {
        // remove all Observers
        this.deleteObservers();
        
        // raise event to tasklist remove all TaskItems assoicated with this object
        // flag change
        setChanged();
        // send notification of new child to add to tree
        notifyObservers(new TaskListEvent(null, this, TaskListEvent.DELETE_ALL_CONTENTS_TASKS));
    }
    
    /**
     * 
     * @param mediaSource
     * @return 
     */
    @Override
    public boolean setMediaSource(MediaSource mediaSource) {
        // can only be changed if no tasks have been set for this item
        if (mediaItemTasks.isEmpty())
        {
            this.mediaSource = mediaSource;
            // notify update
            raiseUpdateEvent();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canMediaSourceBeChanged()
    {
       return mediaItemTasks.isEmpty();
    }
}
