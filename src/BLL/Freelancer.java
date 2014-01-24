package BLL;

/**
 * Freelancer is a type of worker used
 * @author James Staite
 * @version 1.0.0
 */
public class Freelancer extends Worker
{
    private String firstName;
    private String lastName;
    
    /**
     * Setups Freelancer
     * @param roles Work role of the freelancer
     * @param name Individuals name
     * @param userName Individuals username for authentication
     * @param password Individuals password for authentication
     */
    public Freelancer(WorkerRoles role, String[] name, String userName, String password)
    {
        super(role,userName, password);
        firstName = name[0];
        lastName = name[1];
    }
    
    /**
     * Provides freelancers name
     * @return Name
     */
    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    /**
     * Provides the type of worker (quicker than reflection)
     * @return Worker type
     */
    @Override
    public WorkerType getWorkerType() {
        return WorkerType.FREELANCER;
    }
}
