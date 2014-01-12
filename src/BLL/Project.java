package BLL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Defines a project
 * @author James Staite
 * @version 1.0.2
 */
public class Project 
{
    // Project record information
    private String projectName;
    private String discTitle;
    private Client client;
    private Region region;
    private Staff manager;
    private Date dueDate;
    
    // Disc content structure
    private ContentManager content;
    
    // Task associated with this project
    private TaskList tasklist;
    
    // list of project team    
    ArrayList<Worker> team = new ArrayList();
    
    // QC Team leader for the project    
    private Staff qC_TeamLeader;
    
    /**
     * Creates a project
     * @param projectName Name of Project
     * @param discTitle Name of the disc
     * @param client The client of the project
     * @param region The region of the disc to be produced
     * @param projectManager The project manager
     * @param dueDate The due date of the project
     */    
    public Project(String projectName, String discTitle, Client client, Region region, Worker projectManager, Date dueDate)
    {
        this.projectName = projectName;
        this.discTitle = discTitle;
        this.client = client;
        this.region = region;
        manager = (Staff) projectManager;
        this.dueDate = dueDate;
        content = new ContentManager(this);
    }
    
    /**
     * Returns the name of the project
     * @return project name
     */
    public String getName()
    {
        return projectName;
    }
    
    /**
     * Returns the project manager
     * @return project manager
     */
    public Staff getManager()
    {
        return manager;
    }
    
    /**
     * Returns the project client
     * @return project client
     */
    public Client getClient()
    {
        return client;
    }
    
    /**
     * Provides QC Team Leader
     * @return QC Team Leader
     */
    public Staff getQC_TeamLeader()
    {
        return qC_TeamLeader;
    }
    
    /**
     * Set the project QC Team Leader
     * @param qC_TeamLeader QC Team Leader
     */
    public void setQC_TeamLeader(Staff qC_TeamLeader)
    {
        this.qC_TeamLeader = qC_TeamLeader;
    }
    
    /**
     * Provides a ArrayList of Workers assigned to the Project
     * @return ArrayList of Workers
     */
    public List<Worker> getWorkers()
    {
        return Collections.unmodifiableList(team);
    }
    
    /**
     * Adds and Removes Workers from the current project team
     * @param addWorkers ArrayList of Worker to add
     * @param removeWorkers ArrayList of Worker to remove
     * @return boolean confirming if all Workers where removed and did not have any tasks assigned to them
     */
    public boolean updateWorkers(ArrayList<Worker> addWorkers, ArrayList<Worker> removeWorkers)
    {
        // add new workers to project team
        for(Worker worker: addWorkers)
        {
            // check worker is not already apart of the team
            if (!team.contains(worker))
            {
                team.add(worker);
                worker.assignProject(this);
            }
        }
                
        // only remove workers from the team that have no tasks
        boolean ok = true;
        for(Worker worker: removeWorkers)
        {
            if (worker.getNumberOfTasks(this) > 0)
            {
                ok = false;
            }
            else
            {
                // check worker is apart of the existing team
                if (team.contains(worker))
                {
                    team.remove(worker);
                    worker.removeProject(this);
                }   
            }
        }
        
        // flag problem removing workers from team
        return ok;
    }
    
    /**
     * Provides the TaskList for this Project
     * @return TaskList object
     */
    public TaskList getTaskList()
    {
        return tasklist;
    }
    
    /**
     * Displays the project name for the Project
     * @return project name
     */
    @Override
    public String toString()
    {
        return projectName;
    }
    
    public ContentManager getContentManger()
    {
        return content;
    }
}
