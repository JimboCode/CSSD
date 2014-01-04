package BLL;

/**
 * Creates all worker objects
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
    public static Worker createWorker(WorkerRoles role, String[] name, String userName, String password, WorkerType type)
    {
        Worker worker = null;
        
        // create the appropiate Worker type
        switch (type)
        {
            case STAFF:
                worker = new Staff(role, name, userName, password);
                break;
            case CONTRACTOR:
                worker = new Contractor(role, name, userName, password);
                break;
            case FREELANCER:
                worker = new Freelancer(role, name, userName, password);
                break;    
        }
        return worker;
    }
}
