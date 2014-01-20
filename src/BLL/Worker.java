package BLL;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import java.util.ArrayList;

/**
 * Base class for all workers e.g. staff, sub-contractors, etc.
 * 
 * @author James Staite
 * @version 1.0.0
 */
public abstract class Worker 
{
    // list of roles that this worker can carry out
    protected WorkerRoles role;
    
    // User credenticals
    protected String userName;
    protected String password;
    
    // Assigned Tasks
    protected ArrayList<AssignedTasks> projectTaskLists = new ArrayList();
    
    /**
     * Creates a Worker
     * @param role WorkerRole of the Worker
     * @param userName login username
     * @param password login password
     */
    Worker(WorkerRoles role, String userName, String password)
    {
        this.role = role;
        this.userName = userName;
        this.password = Encrypter.encrypt(password);
    }
    
    /**
     * Worker Name
     * @return The name of the worker e.g. James Smith or Video Films Inc. 
     */
    public abstract String getName();
    
    /**
     * Provides the worker type
     * @return The type of worker this is e.g. Staff, SubContractor, etc.
     */
    public abstract WorkerType getWorkerType();
    
    /**
     * Confirms if the worker does this role
     * @param role A role to check if this worker does
     * @return Confirmation if this worker does this role
     */
    public boolean confirmDoesRole(WorkerRoles role)
    {
        if (this.role.equals(role)) return true;
        return false;
    }
    
    /**
     * Provides a text description of the worker role
     * @return A string array of the worker roles
     */
    public String getRoleDescription()
    {
        return role.toString();
    }
    
    /**
     * Provides a enum of the worker role
     * @return enum WorkerRoles of workers job role
     */
    public WorkerRoles getRole()
    {
        return role;
    }
    
    /**
     * Checks the users password against the password provided
     * @param password The password to check if correct for this worker
     * @return Confirmation if password is correct
     */
    public boolean checkPassword(String password)
    {
        if (this.password.equals(Encrypter.encrypt(password))) return true;
        return false;
    }
    
    /**
     * assigned worker to the project and creates a task list
     * @param project The project to assign this employee to
     */
    public void assignProject(Project project)
    {
        // check if already assigned to this project
        AssignedTasks taskList = getTaskList(project);
        if (taskList == null)
        {
            // create a new task list because one does not exist
            projectTaskLists.add(new AssignedTasks(project));
        }
    }
    
    /**
     * removes a workers task list for a project
     * @param project The project for which the worker is to be removed
     */
    public void removeProject(Project project)
    {
        // check if assigned to this project
        AssignedTasks taskList = getTaskList(project);
        
        if (taskList != null)
        {
            // check that they are no tasks assigned for this project
            if (taskList.getNumberOfTasks() == 0)
            {
                projectTaskLists.remove(taskList);
            }
        }
    }
    
    /**
     * Add a task to the workers task list for the project provided
     * @param project The project the task belongs to
     * @param task The task to be assigned to the worker
     */
    public void assignTask(Project project, TaskItem task)
    {
        // check there is a task list for this project
        AssignedTasks taskList = getTaskList(project);
        if (taskList != null)
        {
            // add item to task list
            taskList.addTask(task);
        }
    }
    
    public void removeTask(Project project, TaskItem task)
    {
        // check there is a task list for this project
        AssignedTasks taskList = getTaskList(project);
        if (taskList != null)
        {
            // remove item to task list
            if(taskList.contains(task)) taskList.remove(task);
        }
    }
    
    /**
     * Provide the number of tasks assigned to the worker for the given project
     * @param project project for which the number of tasks are to be provided
     * @return number of tasks assigned
     */
    public int getNumberOfTasks(Project project)
    {
        // check there is a task list for this project
        AssignedTasks taskList = getTaskList(project);
        if (taskList != null)
        {
            // add item to task list
            return taskList.getNumberOfTasks();
        }
        return 0;
    }
    
    /**
     * Provides the TaskList for the give project
     * @param project project to get task list for
     * @return TaskList
     */
    private AssignedTasks getTaskList(Project project)
    {
        for(AssignedTasks listItem: projectTaskLists)
        {
            if (listItem.getProject().equals(project))
            {
                return listItem;
            }
        }
        return null;
    }
    
    /**
     * Provides the Worker name
     * @return name of worker
     */
    @Override
    public String toString()
    {
        return getName();
    }
    
    /**
     * Provides the number of projects that the Worker has been assigned to
     * @return Number of assigned project
     */
    public int getNumProjects()
    {
        return projectTaskLists.size();
    }  
    
    /**
     * Class used to TaskLists for each project
     */
    private class AssignedTasks
    {
        private Project project;
        //private ArrayList<TaskItem> taskItems = new ArrayList();
        
        private EventList<TaskItem> taskItems = GlazedLists.threadSafeList(new BasicEventList<TaskItem>());
        private ObservableElementList.Connector<TaskItem> taskListConnector = GlazedLists.beanConnector(TaskItem.class);
        private EventList<TaskItem> observedTasks = new ObservableElementList<>(taskItems,taskListConnector);
        
        /**
         * Creates a new TaskList for the given project
         * @param project The project
         */
        public AssignedTasks(Project project)
        {
            this.project = project;
        }
        
        /**
         * Adds a task to the worker project TaskList
         * @param task Task to add
         */
        public void addTask(TaskItem task)
        {
            taskItems.add(task);
        }
        
        /**
         * confirm if the task list contains the task
         * @param task
         * @return 
         */
        public boolean contains(TaskItem task)
        {
            return taskItems.contains(task);
        }
        
        /**
         * remove a task item from the task list
         * @param task 
         */
        public void remove(TaskItem task)
        {
            taskItems.remove(task);
        }
        
        /**
         * Provides the number of Tasks assigned
         * @return number of tasks
         */
        public int getNumberOfTasks()
        {
            return taskItems.size();
        }
        
        /**
         * Provide the project for which this TaskList is for
         * @return project
         */
        public Project getProject()
        {
            return project;
        }
        
        public EventList<TaskItem> getTaskList()
        {
            return observedTasks;
        }
        
    }
}
