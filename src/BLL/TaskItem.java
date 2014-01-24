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
    
    // flags if a file is required to complete this task
    private boolean fileRequired;
    
    // supplied file if required
    private String filename;
    
    // task status for this task
    private MediaStatus taskStatus;
    
    // QCReport
    private QCReport qCReport;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    /**
     * Creates a new MediaItem TaskItem
     * @param mediaItem The MediaItem the task belongs to
     * @param workRoleType The type of work role required to carry out the task
     * @param priority The priority of the task
     * @param status The status of this task
     * @param description The description of what the task is
     */
    public TaskItem(MediaItem mediaItem, WorkerRoles workRoleType, int priority, TaskStatus status, String description, boolean fileRequired)
    {
        this.mediaItem = mediaItem;
        this.workRoleType = workRoleType;
        this.priority = priority;
        this.status = status;
        this.description = description;
        this.fileRequired = fileRequired;
        this.taskStatus = mediaItem.getStatus();
    }
    
    /**
     * Adds property change listener
     * @param pcl Listener
     */
    public void addPropertyChangeListner(PropertyChangeListener pcl)
    {
        support.addPropertyChangeListener(pcl);
    }
    
    /**
     * removes property change listener
     * @param pcl Listener
     */
    public void removePropertyChangeListner(PropertyChangeListener pcl)
    {
        support.removePropertyChangeListener(pcl);
    }
    
    /**
     * Provides the MediaItem this task is associated with
     * @return MediaItem the task was created about
     */
    public MediaItem getMediaItem() {
        return mediaItem;
    }
    
    /**
     * Provides the Status of the MediaItem this task is associated with
     * @return the MediaStatus
     */
    public MediaStatus getMediaItemStatus()
    {
        return this.taskStatus;
    }
    
    /**
     * Provides description of the MediaItem the task is associated with
     * @return String description
     */
    public String getMediaDescription()
    {
        return mediaItem.getDescription();
    }

    /**
     * Provides the worker roles this task is allocated to
     * @return workRoleType
     */
    public WorkerRoles getWorkRoleType() {
        return workRoleType;
    }

    /**
     * Provide the worker if any that this task has been assigned to
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }
    
     /**
      * Sets the worker who is carrying out this task
     * @param worker Worker to be assigned to this task
     */
    public void setWorker(Worker worker) {
        final Worker oldWorker = this.worker;
        this.worker = worker;
        
        // raise an event to nofity the property change
        support.firePropertyChange("Worker", oldWorker, worker);
    }

    /**
     * Provides the priority of this task
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Set the priority of the task
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        final int oldPriority = this.priority;
        this.priority = priority;
        
        // raise an event to nofity the property change
        support.firePropertyChange("Priority", oldPriority, priority);
    }

    /**
     * Provides the status of the task
     * @return the status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Updates the status of the task;  Used to update the MediaItem associated with the task
     * Setting the status to TaskStatus.COMPLETE finalised the task
     * @param status the status to set
     */
    public boolean setStatus(TaskStatus status, String comments) 
    {
        final TaskStatus oldStatus = this.status;
        if (status == TaskStatus.COMPLETE)
        {
            // check if file required and field has been set
            if (fileRequired == true && filename != null)
            {
                this.status = status;
                
                // update the MediaItem
                mediaItem.currentTaskCompleted(comments);
                
                // raise an event to nofity the property change
                support.firePropertyChange("Status", oldStatus, status);
                return true;
            }
            else if (fileRequired == false)
            {
                // if no file is required
                this.status = status;
                
                // update the MediaItem
                mediaItem.currentTaskCompleted(comments);
                
                // raise an event to nofity the property change
                support.firePropertyChange("Status", oldStatus, status);
                return true;
            }
            return false;
        }
        else
        {
            // update status
            this.status = status;        
            
            // raise an event to nofity the property change
            support.firePropertyChange("Status", oldStatus, status);
            return true;
        }
    }

    /**
     * Provides the task description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Task Description
     * @param description the description to set
     */
    public void setDescription(String description) {
        final String oldDescription = this.description;
        this.description = description;
        
        //raise an event to nofity the property change
        support.firePropertyChange("Description", oldDescription, description);
    }

    /**
     * Confirms if a file is required by this task
     * @return boolean answer
     */
    public boolean isFileRequired() {
        return fileRequired;
    }

    /**
     * Provides the filename set
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename associated with this task
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    /**
     * Provide the QC Report associated with this item
     * @return QCReport
     */
    public QCReport getQCReport()
    {
        return qCReport;
    }
    
    /**
     * Sets the QCReport associated with this item
     * @param report QCReport
     */
    public void setQCReport(QCReport report)
    {
        qCReport = report;
    }
}
