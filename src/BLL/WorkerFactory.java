package BLL;

import java.util.ArrayList;

/**
 *
 * @author James Staite
 * @version 1.0.0
 */
public class WorkerFactory 
{
    /**
     * Factory Method for the creation of worker classes
     * @param roles the work roles that the class carries out - see WorkerRoles Enum
     * @param name Name of worker
     * @param userName Login username
     * @param password Login password
     * @param type Type of worker - see WorkerType Enum
     * @return The Worker class
     */
    public static Worker createWorker(ArrayList<WorkerRoles> roles, String name, String userName, String password, WorkerType type)
    {
        Worker worker = null;
        switch (type)
        {
            case STAFF:
                worker = new Staff(roles, name, userName, password);
                break;
            case CONTRACTOR:
                worker = new Contractor(roles, name, userName, password);
                break;
            case FREELANCER:
                worker = new Freelancer(roles, name, userName, password);
                break;    
        }
        return worker;
    }
}
