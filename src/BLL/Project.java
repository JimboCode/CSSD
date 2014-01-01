package BLL;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author James Staite
 * @version 1.0.0
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
    
    // Task associated with this project
    private TaskList tasklist;
    
    // list of project team    
    ArrayList<Worker> team = new ArrayList();
    
    // QC Team leader for the project    
    private Staff qC_TeamLeader;
    
    public Project(String projectName, String discTitle, Client client, Region region, Worker projectManager, Date dueDate)
    {
        this.projectName = projectName;
        this.discTitle = discTitle;
        this.client = client;
        this.region = region;
        manager = (Staff) projectManager;
        this.dueDate = dueDate;
    }
    
    public String getName()
    {
        return projectName;
    }
    
    public Staff getManager()
    {
        return manager;
    }
    
    public Staff getQC_TeamLeader()
    {
        return qC_TeamLeader;
    }
    
    public void setQC_TeamLeader(Staff qC_TeamLeader)
    {
        this.qC_TeamLeader = qC_TeamLeader;
    }
    
    /**
     *
     * @return
     */
    public ArrayList<Worker> getWorkers()
    {
        ArrayList<Worker> temp = new ArrayList();
        temp.addAll(team);
        return temp;
    }
    
    public boolean updateWorkers(ArrayList<Worker> addWorkers, ArrayList<Worker> removeWorkers)
    {
        // add new workers to project team
        for(Worker worker: addWorkers)
        {
            team.add(worker);
            worker.assignProject(this);
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
                team.remove(worker);
                worker.removeProject(this);
            }
        }
        
        // flag problem removing workers from team
        return ok;
    }
    
    public TaskList getTaskList()
    {
        return tasklist;
    }
    
    @Override
    public String toString()
    {
        return projectName;
    }
}
