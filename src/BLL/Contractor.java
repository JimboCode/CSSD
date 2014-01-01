package BLL;

import java.util.ArrayList;

/**
 * Contractor is a type of worker used
 * @author James Staite
 * @version 1.0.0
 */
public class Contractor extends Worker
{
    // contractors name
    private String name;
   
    /**
     * Setup Contractor
     * @param roles Arraylist of valid roles
     * @param name Name
     * @param userName username for authentication
     * @param password password for authentication
     */
    public Contractor(WorkerRoles[] roles, String[] name, String userName, String password)
    {
        super(roles,userName, password);
        this.name = name[0];
    }
    
    /**
     * Contractors Name
     * @return Name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Provides the type of worker (quicker than reflection)
     * @return Name
     */
    @Override
    public WorkerType getWorkerType() {
        return WorkerType.CONTRACTOR;
    }
}
