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
    EventList<TaskItem> mediaItemTasks = new BasicEventList<>();
    ObservableElementList.Connector<TaskItem> taskListConnector = GlazedLists.beanConnector(TaskItem.class);
    EventList<TaskItem> observedTasks = new ObservableElementList<>(mediaItemTasks,taskListConnector);
    
    // reference to the content manager
    ContentManager contentManager;
    
    // reference to the project
    Project project;
    
    public TaskList(ContentManager contentManager, Project project)
    {
        // hold references required
        this.contentManager = contentManager;
        this.project = project;
        
        // subscribe to new task notifications
        contentManager.addObserver(this);
    }
    
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
    
    private void addTaskToList(TaskItem newTask)
    {
        // add task to list
        mediaItemTasks.add(newTask);
        
        // add task to users own tasklist if included in task details
        Worker worker = newTask.getWorker();
        if (worker != null) worker.assignTask(project, newTask);
    }
    
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
    
    public EventList<TaskItem> getTaskList()
    {
        return observedTasks;
    }
    
}
