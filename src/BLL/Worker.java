package BLL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Base class for all workers e.g. staff, sub-contractors, etc.
 * 
 * @author James Staite
 * @version 1.0.0
 */
public abstract class Worker 
{
    // list of roles that this worker can carry out
    protected ArrayList<WorkerRoles> roles;
    
    // User credenticals
    protected String userName;
    protected String password;
    
    // Assigned Tasks
    protected ArrayList<AssignedTasks> assignedTasks = new ArrayList();
    
   
    Worker(WorkerRoles[] roles, String userName, String password)
    {
        this.roles = new ArrayList<> (Arrays.asList(roles));
        this.userName = userName;
        this.password = password;
    }
    
    /**
     * 
     * @return The name of the worker e.g. James Smith or Video Films Inc. 
     */
    public abstract String getName();
    
    /**
     * 
     * @return The type of worker this is e.g. Staff, SubContractor, etc.
     */
    public abstract WorkerType getWorkerType();
    
    /**
     * 
     * @param role A role to check if this worker does
     * @return Confirmation if this worker does this role
     */
    public boolean confirmDoesRole(WorkerRoles role)
    {
        if (roles.contains(role)) return true;
        return false;
    }
    
    public String getRole()
    {
        String jobs = "";
        for(WorkerRoles role: roles)
        {
            jobs += role.toString() + " ";
        }
        return jobs.trim();
    }
    
    /**
     * 
     * @return A string array of the worker roles
     */
    public ArrayList<String> getRoles()
    {
        ArrayList<String> jobs = new ArrayList();
        for (Iterator<WorkerRoles> it = roles.iterator(); it.hasNext();) 
        {
            WorkerRoles role = it.next();
            jobs.add(role.toString());
        }
        return jobs;
    }
    
    /**
     * 
     * @param password The password to check if correct for this worker
     * @return Confirmation if password is correct
     */
    public boolean checkPassword(String password)
    {
        if (this.password.equals(password)) return true;
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
            assignedTasks.add(new AssignedTasks(project));
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
                assignedTasks.remove(taskList);
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
    
    public int getNumberOfTasks(Project project)
    {
        // check there is a task list for this project
        AssignedTasks taskList = getTaskList(project);
        if (taskList != null)
        {
            // add item to task list
            taskList.getNumberOfTasks();
        }
        return 0;
    }
    
    // finds tasklist for the give project
    private AssignedTasks getTaskList(Project project)
    {
        for(AssignedTasks listItem: assignedTasks)
        {
            if (listItem.getProject().equals(project))
            {
                return listItem;
            }
        }
        return null;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    public int getNumProjects()
    {
        return assignedTasks.size();
    }  
    
    private class AssignedTasks
    {
        private Project project;
        private ArrayList<TaskItem> taskItems = new ArrayList();
        
        public AssignedTasks(Project project)
        {
            this.project = project;
        }
        
        public void addTask(TaskItem task)
        {
            taskItems.add(task);
        }
        
        public int getNumberOfTasks()
        {
            return taskItems.size();
        }
        
        public Project getProject()
        {
            return project;
        }
        
    }
}
