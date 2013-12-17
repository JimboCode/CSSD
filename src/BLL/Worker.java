package BLL;

import java.util.ArrayList;
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
    protected ArrayList<WorkerRoles> roles = new ArrayList<>();
    
    // User credenticals
    protected String userName;
    protected String password;
    
    // Worker Name
    protected String name;
    
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
     * 
     * @param project The project to assign this employee to
     */
    public void assignProject(Project project)
    {
        
    }
    
    /**
     * 
     * @param project The project the task belongs to
     * @param task The task to be assigned to the worker
     */
    public void assignTask(Project project, TaskItem task)
    {
        
        
    }
}
