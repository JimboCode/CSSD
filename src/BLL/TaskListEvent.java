package BLL;

/**
 * MediaItem TaskList update event argument object passed with details of the event
 * 
 * @author James Statie
 */
public class TaskListEvent 
{
    // Set of constant actions
    public static final int NEW = 0;
    public static final int DELETE_ALL_CONTENTS_TASKS = 1;
    
    // reference to the child object involved in the update
    private TaskItem taskItem;
    
    // reference to the child object involved in the update
    private MediaItem mediaItem;
    
    // the event action
    private int action;
    
    /**
     * Sets the event parameters at creation
     * @param taskItem TaskItem to be added to the TaskList or null if action = DELETE_ALL_CONTENTS_TASKS
     * @param mediaItem MediaItem for which all items are to be removed from the TaskList or null if action = NEW
     * @param action Action to be taken by this event as set by the constants NEW or DELETE_ALL_CONTENTS_TASKS
     */
    public TaskListEvent(TaskItem taskItem, MediaItem mediaItem, int action)
    {
        this.taskItem = taskItem;
        this.mediaItem = mediaItem;
        this.action = action;
    }

    /**
     * Provides the TaskItem passed with this event
     * 
     * @return the taskItem
     */
    public TaskItem getTaskItem() {
        return taskItem;
    }

    /**
     * Provides the action to taken with this event
     * 
     * @return the action
     */
    public int getAction() {
        return action;
    }

    /**
     * Provides the mediaItem that the DELETE_ALL_CONTENTS_TASKS constant refers to
     * 
     * @return the mediaItem The item for which all its tasks are to be deleted
     */
    public MediaItem getMediaItem() {
        return mediaItem;
    }
}
