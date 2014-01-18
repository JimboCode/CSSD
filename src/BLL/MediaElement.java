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
    
    @Override
    public boolean setStatus(MediaStatus status, Worker updatingUser, String Description, WorkerRoles roleType, Worker allocatedTo, int priority)
    {
        if (status == MediaStatus.SCRUBBED_FROM_DISC)
        {
            // TODO check that a task is not in progress & clear all task assoicated with it
            this.status = status;
            // notify update
            raiseUpdateEvent();
            
            // noitfy parent
            parent.childStatusChanged();
        }
        
        MediaStatus[] validChanges = getValidStatusOptions(false, updatingUser);
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
                            parent.childStatusChanged();
                            break;
                        }    
                    }

                    // update the status
                    this.status = thisStatus;

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
     * @param status New status of the Media Item - enumeration MediaStatus
     */
    @Override
    public boolean addFile(String filename, MediaStatus status, Worker worker)
    {
        MediaStatus[] validChanges = getValidStatusOptions(true, worker);
        if (validChanges.length == 0) return false;
        for(MediaStatus thisStatus: validChanges)
        {
            if (status == thisStatus)
            {
                switch (status)
                {
                    case QUICK_REQUESTED:
                    {
                        currentFile = new File(filename, FileStatus.QUICK, 0);
                        filesStored.add(currentFile);
                    }
                    case COMPRESSION_COMPLETED:
                    {
                        int version = currentFile.getVersion();
                        File file = new File(filename, FileStatus.COMPRESSED, version++);
                        filesStored.add(file);
                    }
                }
                this.status = status;
                
                // notify update
                raiseUpdateEvent();
                
                return true;
            }
        }
        return false;
    }
    
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
     * Allows notification from children nodes that they status has been updated
     */
    @Override
    public void childStatusChanged()
    {
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

            // if all child objects are ready of scrubbed update own status
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
     * 
     * @param mediaSource
     * @return 
     */
    @Override
    public boolean setMediaSource(MediaSource mediaSource) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean canMediaSourceBeChanged()
    {
       return false;
    }
}
