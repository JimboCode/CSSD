package BLL;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/**
 * TaskList register for the project
 * 
 * @author James Staite
 */
public class TaskList implements Observer
{
    // master list of all tasks for a project
    EventList<TaskItem> mediaItemTasks = GlazedLists.threadSafeList(new BasicEventList<TaskItem>());
    
    // event handler for observable list
    ObservableElementList.Connector<TaskItem> taskListConnector = GlazedLists.beanConnector(TaskItem.class);
    
    // Observerable version of the master task list
    EventList<TaskItem> observedTasks = new ObservableElementList<>(mediaItemTasks,taskListConnector);
    
    // reference to the content manager
    ContentManager contentManager;
    
    // reference to the project
    Project project;
    
    /**
     * Sets up the TaskList for use;  main purpose is to maintain master list;
     * add new tasks as notified by events from the ContentManager
     * 
     * @param contentManager reference to ContentManager used so TaskList can register for update events on TaskItems
     * @param project The project that is TaskList belongs to obtain QC Team Leader and Project Manager and add them to
     * task allocated to their role
     */
    public TaskList(ContentManager contentManager, Project project)
    {
        // hold references required
        this.contentManager = contentManager;
        this.project = project;
        
        // subscribe to new task notifications
        contentManager.addObserver(this);
    }
    
    /**
     * Receives update events from ContentManager about new task items to add to the master list
     * @param object The call object (in this case ContentManger)
     * @param arg Argument (in this case the TaskItem)
     */
    @Override
    public void update(Observable object, Object arg) {
        
        // check if event from the ContentManager and the argument is a TaskListEvent
        if (object.equals(contentManager) && arg instanceof TaskListEvent)
        {
            // unpack the argument
            TaskListEvent taskListEvent = (TaskListEvent) arg;
            
            // call the approach action
            switch(taskListEvent.getAction())
            {
                // add a new task to the project TaskList
                case TaskListEvent.NEW:
                {
                    addTaskToList(taskListEvent.getTaskItem());
                }
                    
                // Delete all the events for the content item
                case TaskListEvent.DELETE_ALL_CONTENTS_TASKS:
                {
                    removeAllTask(taskListEvent.getMediaItem());
                }
            }
        }
    }
    
    /**
     * Adds new TaskItems to the master list for the project
     * @param newTask the new TaskItem
     */
    private void addTaskToList(TaskItem newTask)
    {
        // check if the task has been allocated to the QC Team Leader
        if (newTask.getWorkRoleType() == WorkerRoles.QC_TEAM_LEADER)
        {
            newTask.setWorker(project.getQC_TeamLeader());
        }
        
        // check if the task has been allocated to the QC Team Leader
        if (newTask.getWorkRoleType() == WorkerRoles.PROJECT_MANAGER)
        {
            newTask.setWorker(project.getManager());
        }
        
        // add task to list
        mediaItemTasks.add(newTask);
        
        
        // add task to users own tasklist if included in task details
        Worker worker = newTask.getWorker();
        if (worker != null) worker.assignTask(project, newTask);
    }
    
    /**
     * Removes all tasks belonging to a MediaItem
     * @param mediaItem MediaItem which tasks belong to
     */
    private void removeAllTask(MediaItem mediaItem)
    {
        // iterate over the tasklist looking for task that belong to the mediaItem
        Iterator i = mediaItemTasks.iterator();
        TaskItem task;
        
        while (i.hasNext()) 
        {
            task = (TaskItem) i.next();
        
            // if task belongs to the mediaItem
            if(task.getMediaItem() == mediaItem)
            {
                // get the worker if assigned
                Worker worker = task.getWorker();
                
                // if the task has been assigned to worker then remove it from their list
                if (worker != null) worker.removeTask(project, task);
                
                // remove the task from the gobal list
                mediaItemTasks.remove(task);
            }
        }
    }
    
    /**
     * Provides a reference to the observable master task list for the project for use in
     * UI JTables
     * @return 
     */
    public EventList<TaskItem> getTaskList()
    {
        return observedTasks;
    }
    
}
