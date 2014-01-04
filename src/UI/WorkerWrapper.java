package UI;

import BLL.Project;
import BLL.Worker;
import BLL.WorkerType;

/**
 * Object only used by DefineTeamUI to provide GlazedLists with details of
 * the number of tasks allocated to a worker for the project set on this object 
 * @author James Staite
 */
public class WorkerWrapper 
{
    // worker objected wrapped
    private Worker worker;
    
    // the project context that the worker is being viewed in
    private Project project;

    public WorkerWrapper (Project project, Worker worker)
    {
        // wrap the worker
        this.project = project;
        this.worker = worker;
    }

    /**
     * Provides the worker name
     * @return Worker Name
     */
    public String getName()
    {
        return worker.getName();
    }

    /**
     * Provides the workers role as a text description
     * @return Description of the workers role
     */
    public String getRoleDescription()
    {
        return worker.getRoleDescription();
    }

    /**
     * Provides the number of projects that this work is currently assigned to
     * @return Number of project assigned to 
     */
    public int getNumProjects()
    {
        return worker.getNumProjects();
    }

    /**
     * Returns the type of Worker e.g. Staff, Contractor, etc.
     * @return WorkerType enum
     */
    public WorkerType getWorkerType()
    {
        return worker.getWorkerType();
    }

    /**
     * Provides the number of tasks assigned to this worker for the project
     * assigned at instantiation
     * @return number of assigned tasks
     */
    public int getNumberOfTasks()
    {
        return worker.getNumberOfTasks(project);
    }

    /**
     * Provides access to the wrapped worker
     * @return Worker instance that is wrapped
     */
    public Worker getWorker()
    {
        return worker;
    }
}
