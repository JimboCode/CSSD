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
    
    /**
     * Use to update the status and create a new task if required
     * 
     * @param status the new status - this is validated against the WorkFlow object (valid options can be obtained from getValidStatusOptions(Worker worker))
     * @param updatingUser the Worker object of the user wishing to make the change - not all users can carry out all operations
     * @param description the description to be including in the new task associated with this new status
     * @param roleType the worker role types that are required to carry out the operations of the new task
     * @param allocatedTo a specific worker if this task is allocated to them
     * @param priority the priority of the new task
     * @return confirmation if the status change has happened
     */
    @Override
    public boolean setStatus(MediaStatus status, Worker updatingUser, String description, WorkerRoles roleType, Worker allocatedTo, int priority)
    {
        // scrubbing an item is premitted at any time.
        if (status == MediaStatus.SCRUBBED_FROM_DISC)
        {
            this.status = status;
            // notify update
            
            raiseUpdateEvent();
            // nofity parent
            parent.childStatusChanged();
        }
        
        // only permit status changes while the parents status is awaiting assets
        // otherwise the build and testing of the assets has already begun
        MediaStatus parentStatus = parent.getStatus();
        if(parentStatus != MediaStatus.AWAITING_ASSETS && parentStatus != MediaStatus.AWAITING_ASSETS_DELAYED) return false;
        
        // Get valid status changes without a file submission based up the current status
        MediaStatus[] validChanges = getValidStatusOptions(updatingUser);
        
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
                        // update the status for parent benefit
                        this.status = thisStatus;
                        
                        // notify parent node the asset is ready
                        parent.childStatusChanged();
                        break;
                    }    
                }
                               
                // update the status
                this.status = thisStatus;
                
                // set if task will require a file
                boolean fileRequired = getFileRequiredWithStatus(thisStatus);
                
                // create a new task
                TaskItem task = addTask(roleType, priority, TaskStatus.AWAITING_ACTION, description, fileRequired);
                
                // if allocated to an individual set the assignment
                if (allocatedTo != null) task.setWorker(allocatedTo);
                
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
                description = "Asset provided by " + currentTask.getWorker() +"; Ready for inward QC";
                break;
            }
            case FIXES_COMPLETED:
            {
                int version = currentFile.getVersion();
                File file = new File(filename, FileStatus.FIXED, version++);
                filesStored.add(file);
                
                // set the status to update to
                newStatus = MediaStatus.AWAITING_QC;
                description = "Fixes carried out by " + currentTask.getWorker() +"; Ready for QC (Comment : " + comments + " )";
                break;
            }
            case COMPRESSION_COMPLETED:
            {
                int version = currentFile.getVersion();
                File file = new File(filename, FileStatus.COMPRESSED, version++);
                filesStored.add(file);
                
                // set the status to update to
                newStatus = MediaStatus.AWAITING_QC;
                description = "Compression carried out by " + currentTask.getWorker() +"; Ready for QC (Comment : " + comments + " )";
                break;
            }
        }
        
        // if status changing raise a new task
        if (newStatus != null) 
        {
            this.status = newStatus;
            
            if (!comments.isEmpty()) description += " (Comment : " + comments + " )";            
            // create a new task
            addTask(WorkerRoles.QC_TEAM_LEADER, 4, TaskStatus.AWAITING_ACTION, description, false);
            
            // notify update
            raiseUpdateEvent();
        }                
    }
    
    /**
     * Used by the UI layer to confirm that the current task has been completed
     * 
     * @param comments any comments to added to the next task that will be created by the completion
     * of this task
     */
    @Override
    public void currentTaskCompleted(String comments)
    {
        // check if the completion of the current task requires a file
        // if so call the addfile method that handles these operations
        if (currentTask.isFileRequired())
        {
            addFile(currentTask.getFilename(), comments);
        }
        
        // Alternatively if the operation is the submission of a QC Report
        else if (currentTask.getQCReport()!= null &&
                status == MediaStatus.INWARD_QC ||
                status == MediaStatus.AWAITING_QC)
        {
            // update the status
            this.status = MediaStatus.QC_REPORT_AVALIABLE;
            
            // create message for the next task
            String description = "QC Report avaliable";
            if (!comments.isEmpty()) description += " (Comment : " + comments + " )";  
            
            // get QC Report to add to the new task 
            QCReport report = currentTask.getQCReport();
            
            // create a new task 
            TaskItem task = addTask(WorkerRoles.QC_TEAM_LEADER, 4, TaskStatus.AWAITING_ACTION, description, false);
            
            // add QC Report
            task.setQCReport(report);
            
            // notify update
            raiseUpdateEvent();
        }
    }
    
    /**
     * Returns the current file name
     * @return filename including path
     */
    @Override
    public File getFile()
    {
        return currentFile;
    }
    
    /**
     * Confirms the mediaItem can be deleted
     * @return boolean answer
     */
    @Override
    public boolean canBeDeleted()
    {
        // can only be deleted if the task list is empty
        return mediaItemTasks.isEmpty();
    }
    
    /**
     * Used by parent node when an child is being deleted in order to clean up
     */
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
     * Set the source of the media e.g. client, contractor, etc.
     * Can only be set before any task to create media are created
     * 
     * @param mediaSource a media source
     * @return confirms if updated
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
    
    /**
     * Confirms if the media source can still be changed
     * @return boolean answer
     */
    @Override
    public boolean canMediaSourceBeChanged()
    {
       return mediaItemTasks.isEmpty();
    }
}
