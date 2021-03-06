package BLL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;

/**
 *  A singleton register for all projects
 * 
 * @author James Staite
 * @version 1.0.1
 */
public class ProjectRegister extends Observable
{
    // To improve multithreading and because this class is always required
    // the instance is initialised immediately
    // unique instance reference
    private static ProjectRegister uniqueInstance = new ProjectRegister();
    
    // collection of project records
    private ArrayList<Project> projectReg = new ArrayList();
    
    // private constructor - singleton
    private ProjectRegister()
    {
    }
    
   /**
    * Provide instance of the ProjectRegister singleton object
    * @return Singleton instance of client register
    */
    public static synchronized ProjectRegister getInstance()
    {
        return uniqueInstance;
    }
    
    /**
     * Creates and add project to register (projectName must be unique)
     * @param projectName Name of project
     * @param discTitle Title of the disc to be produced
     * @param client Customer of the project
     * @param region Region disc is for
     * @param projectManager Manager of project
     * @param dueDate Date for project completion
     * @return Confirmation record has been created
     */
    public Project addProject(String projectName, String discTitle, Client client, Region region, Worker projectManager, Date dueDate)
    {
        // check project already exists
        Project project = validProjectName(projectName);
        if (project == null)
        {
            // create new project record
            project = new Project(projectName, discTitle, client, region, projectManager, dueDate);
            
            // add new client record
            projectReg.add(project);
            
            // notify project added
            raiseChangedEvent();
            
            return project;
        }
        return null;        
    }
   
    /**
     * remove exiting project from register
     * @param project Project object to be removed
     * @return Confirmation record found and removed
     */
    public boolean removeProject(Project project)
    {
        // check that the object existing
        if (projectReg.contains(project))
        {
            // remove worker object and confirm action
            projectReg.remove(project);
            
            // notify project removed
            raiseChangedEvent();
            
            return true;
        }
        // item not found
        return false;
    }
    
    /**
     * Raises events for the observers that projects have been added or removed
     */
    private void raiseChangedEvent()
    {
        // flag changes
        setChanged();
        
        // not sending any data object to observers - operating a pull model
        notifyObservers();
    }
    
    public void raiseProjectChangedEvent(Project project)
    {
        if (projectReg.contains(project))
        {
            raiseChangedEvent();
        }
    }
    
    /**
     * Locates a project record by it name (used to identify projects - must be unique) 
     * @param name name (used to identify projects - must be unique)
     * @return Project record object
     */
    public Project findbyName(String name, Worker user)
    {
        // iterate over collection to find matching records
        for(Project project: projectReg)
        {
            // check if the name matches (not case sensitive)
            if(project.getName().compareToIgnoreCase(name) == 0)
            {
                if (validUser(project, user)) return project;
            }
        }
        return null;
    }
    
    /**
     * Provides a list of all the project records
     * @return ArrayList of Project records
     */
    public List<Project> getProjectList(Worker user)
    {
        ArrayList<Project> foundProjects = new ArrayList();
        for(Project project: projectReg)
        {
            // check if user on team
            if (validUser(project, user) ||
                    user.getWorkerType() == WorkerType.CONTRACTOR ||
                    user == project.getClient())
                foundProjects.add(project);
        }
        return Collections.unmodifiableList(foundProjects);
    }
    
    /**
     * Confirms if a client has projects assigned to them
     * @param client Client to be checked
     * @return boolean true - client does have projects; else false
     */
    public boolean doesClientHaveProjects(Client client)
    {
        for(Project project: projectReg)
        {
            if (project.getClient().equals(client)) return true;
        }
        return false;
    }
    
    /**
     * Provides the total number of current projects 
     * @return number of projects
     */
    public int getNumberOfProjects()
    {
        return projectReg.size();
    }
    
    /**
     * Confirms if a user can view a project
     * @param project the project to check
     * @param user the user to check
     * @return boolean answer
     */
    private boolean validUser(Project project, Worker user)
    {
        // Project managers can view all projects
        if (user.getRole() == WorkerRoles.PROJECT_MANAGER ||
                        project.isWorkerOnTeam(user) ||
                        project.getClient() == user)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Check if the project name already exists
     * @param name the name to check
     * @return a boolean answer
     */
    private Project validProjectName(String name)
    {
        // iterate over collection to find matching records
        for(Project project: projectReg)
        {
            // check if the name matches (not case sensitive)
            if(project.getName().compareToIgnoreCase(name) == 0)
            {
                return project;
            }
        }
        return null;
    }
}
