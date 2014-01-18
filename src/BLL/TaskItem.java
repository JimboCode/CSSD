package BLL;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * An individual task on a MediaItem
 * 
 * @author James Staite
 */
public class TaskItem 
{
    // the mediaItem that this task belongs to
    private MediaItem mediaItem;
    
    // The type of worker required to carry out this task
    private WorkerRoles workRoleType;
    
    // Individual worker this task has been allocated to
    private Worker worker;
    
    // Priority of the Task
    private int priority;
    
    // Task current status
    private TaskStatus status;
    
    // Task description
    private String description;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    /**
     * Creates a new MediaItem TaskItem
     * @param mediaItem The MediaItem the task belongs to
     * @param workRoleType The type of work role required to carry out the task
     * @param priority The priority of the task
     * @param status The status of this task
     * @param description The description of what the task is
     */
    public TaskItem(MediaItem mediaItem, WorkerRoles workRoleType, int priority, TaskStatus status, String description)
    {
        this.mediaItem = mediaItem;
        this.workRoleType = workRoleType;
        this.priority = priority;
        this.status = status;
        this.description = description;
    }
    
    public void addPropertyChangeListner(PropertyChangeListener pcl)
    {
        support.addPropertyChangeListener(pcl);
    }
    
    public void removePropertyChangeListner(PropertyChangeListener pcl)
    {
        support.removePropertyChangeListener(pcl);
    }
    
    /**
     * 
     * @return 
     */
    public MediaItem getMediaItem() {
        return mediaItem;
    }
    
    public MediaStatus getMediaItemStatus()
    {
        return mediaItem.getStatus();
    }

    /**
     * @return the workRoleType
     */
    public WorkerRoles getWorkRoleType() {
        return workRoleType;
    }

    /**
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }
    
     /**
     * @param worker the worker to set
     */
    public void setWorker(Worker worker) {
        final Worker oldWorker = this.worker;
        this.worker = worker;
        support.firePropertyChange("Worker", oldWorker, worker);
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        final int oldPriority = this.priority;
        this.priority = priority;
        support.firePropertyChange("Priority", oldPriority, priority);
    }

    /**
     * @return the status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(TaskStatus status) {
        final TaskStatus oldStatus = this.status;
        this.status = status;
        support.firePropertyChange("Status", oldStatus, status);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        final String oldDescription = this.description;
        this.description = description;
        support.firePropertyChange("Description", oldDescription, description);
    }
}
