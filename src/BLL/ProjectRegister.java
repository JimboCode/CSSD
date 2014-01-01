package BLL;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author James Staite
 * @version 1.0.0
 */
public class ProjectRegister 
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
        Project project = findbyName(projectName);
        if (project == null)
        {
            // create new project record
            project = new Project(projectName, discTitle, client, region, projectManager, dueDate);
            
            // add new client record
            projectReg.add(project);
            return project;
        }
        return null;        
    }
   
    /**
     * remove exiting project from register
     * @param project Project object to be removed
     * @return Confirmation record found and removed
     */
    public boolean removeClient(Project project)
    {
        // check that the object existing
        if (projectReg.contains(project))
        {
            // remove worker object and confirm action
            projectReg.remove(project);
            return true;
        }
        // item not found
        return false;
    }
    
    /**
     * Locates a project record by it name (used to identify projects - must be unique) 
     * @param name name (used to identify projects - must be unique)
     * @return Project record object
     */
    public Project findbyName(String name)
    {
        // iterate over collection to find matching record
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
    
    /**
     * Provides a list of all the project records
     * @return ArrayList of Project records
     */
    public ArrayList<Project> getProjectList()
    {
        return projectReg;
    }    
}
