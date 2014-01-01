/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import BLL.Project;
import BLL.Worker;
import BLL.WorkerType;

/**
 *
 * @author James Staite
 */
public class WorkerWrapper 
{
    private Worker worker;
    private Project project;

    public WorkerWrapper (Project project, Worker worker)
    {
        this.project = project;
        this.worker = worker;
    }

    public String getName()
    {
        return worker.getName();
    }

    public String getRole()
    {
        return worker.getRole();
    }

    public int getNumProjects()
    {
        return worker.getNumProjects();
    }

    public WorkerType getWorkerType()
    {
        return worker.getWorkerType();
    }

    public int getNumberOfTasks()
    {
        return worker.getNumberOfTasks(project);
    }

    public Worker getWorker()
    {
        return worker;
    }
}
