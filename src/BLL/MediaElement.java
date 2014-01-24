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
    public MediaElement(String name, String description, ComponentType mediaType, MediaItem parent, MediaItemWorkFlow workFlow)
    {
        super(name, description, mediaType, parent, workFlow);
        
        // default status of a new element
        status = MediaStatus.AWAITING_ASSETS;
        
        // default source of media for elements
        mediaSource = MediaSource.ASSETS;
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
            // do not premit a child to added twice
            if (!mediaChildren.contains(child))
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

                if (notificationFlag) 
                {
                    // sent tree update events
                    notifyObserversOfChange(new ContentEvent(this, child, ContentEvent.ADD));
                }
                
                // fire a check of the child nodes incase it affects the parent node
                childStatusChanged();
            }
        }
    }
    
    /**
     * Removes a MediaItem child from this node
     * @param child MediaItem to be removed
     */
    @Override
    public void deleteMediaItem(MediaItem child)
    {
        // remove child if in list
        if(mediaChildren.contains(child)) 
        {
            mediaChildren.remove(child);
            
            if (notificationFlag) 
            {
                // sent tree update events
                notifyObserversOfChange(new ContentEvent(null, child, ContentEvent.DELETE));
                
                // if event is being notified then its a delete and not move as used by setParent method
                child.beingRemovedCleanUp();
            }
            
            // fire a check of the child nodes incase it affects the parent node
            childStatusChanged();
        }
    }
    
    /**
     * Used by parent node when an child is being deleted in order to clean up
     */
    @Override
    protected void beingRemovedCleanUp()
    {
        // remove all Observers
        this.deleteObservers();
        
        // notify all child objects
        for(MediaItem child: mediaChildren)
        {
            child.beingRemovedCleanUp();
        }
        
        // raise event to tasklist remove all TaskItems assoicated with this object
        // flag change
        setChanged();
        // send notification of new child to add to tree
        notifyObservers(new TaskListEvent(null, this, TaskListEvent.DELETE_ALL_CONTENTS_TASKS));
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
        if (status == MediaStatus.SCRUBBED_FROM_DISC)
        {
            this.status = status;
            // notify update
            raiseUpdateEvent();
            
            // noitfy parent
            parent.childStatusChanged();
        }
        
        MediaStatus[] validChanges = getValidStatusOptions(updatingUser);
        if (validChanges.length == 0) return false;
        for(MediaStatus thisStatus: validChanges)
        {
            if (this.status == thisStatus)
            {
                if (status == thisStatus)
                {
                    // status change requested found
                    // check if the status requires a change to the current assoicated file
                    switch (status)
                    {
                        case AWAITING_ASSETS_DELAYED:
                        {
                            currentFile.setStatus(FileStatus.OBSOLETE);
                            currentFile = null;
                            break;
                        }
                        case BUILD_COMPLETE:
                        {
                            currentFile.setStatus(FileStatus.FINAL_COMPRESSION);
                            currentFile = null;
                            break;
                        }
                        case ASSET_READY:
                        {
                            currentFile.setStatus(FileStatus.ACCEPTED);
                            
                            // update the status for parent benefit
                            this.status = thisStatus;
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
        }
        return false;
    }
    
    /**
     * Add media files to the Media Item and set the status of the Media Item
     * @param filename Filename
     */
    public void addFile(String filename, String comments)
    {
        MediaStatus newStatus = null;
        switch (status)
        {
            case QUICK_REQUESTED:
            {
                currentFile = new File(filename, FileStatus.QUICK, 0);
                filesStored.add(currentFile);
                newStatus = MediaStatus.AWAITING_QC;
            }
            case COMPRESSION_COMPLETED:
            {
                int version = currentFile.getVersion();
                File file = new File(filename, FileStatus.COMPRESSED, version++);
                filesStored.add(file);
                newStatus = MediaStatus.AWAITING_QC;
            }
        }
        if (newStatus != null) 
        {
            this.status = newStatus;
            
            // notify update
            raiseUpdateEvent();
        }      
    }
    
    /**
     * Confirms the mediaItem can be deleted
     * @return boolean answer
     */
    @Override
    public boolean canBeDeleted()
    {
        // can only be deleted if the task list is empty and all childrens tasklist are empty
        if (!mediaItemTasks.isEmpty())
        {
            return false;
        }
        
        // check child nodes if any
        for(MediaItem child : mediaChildren)
        {
            if (!child.canBeDeleted())
            {
                return false;
            }
        }
     
        // pass all checks
        return true;        
    }
    
    /**
     * handles notification from children nodes that they status has been updated
     */
    @Override
    protected void childStatusChanged()
    {
        // if no child are attached set the status to AWAITING_ASSETS to avoid child being
        // deleted and updating the status to Ready artificially
        if (mediaChildren.isEmpty())
        {
            if(status != MediaStatus.SCRUBBED_FROM_DISC) status = MediaStatus.AWAITING_ASSETS;
        }
        else
        {
            // check all child nodes to see if all assets are now ready or scrubbed
            boolean readyFlag = true;
            for(MediaItem child : mediaChildren)
            {
                if (child.getStatus() != MediaStatus.ASSET_READY && child.getStatus() != MediaStatus.SCRUBBED_FROM_DISC)
                {
                    readyFlag = false;
                    break;
                }
            }

            // if all child objects are ready or scrubbed update own status
            if(readyFlag == true)
            {
                status = MediaStatus.ALL_ASSETS_AVALIABLE;

                // notify update
                raiseUpdateEvent();

                // if this object has a parent then notify it
                if (parent != null) parent.childStatusChanged();
            }
        }            
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
        throw new UnsupportedOperationException();
    }
    
    /**
     * Confirms if the media source can still be changed
     * @return boolean answer
     */
    @Override
    public boolean canMediaSourceBeChanged()
    {
       return false;
    }
}
