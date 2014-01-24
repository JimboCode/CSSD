
package BLL;

/**
 * Defines Staff
 * @author James Staite
 * @version 1.0.1
 */
public class Staff extends Worker
{
    private String firstName;
    private String lastName;
    
    /**
     * Creates new Staff member
     * @param role Role that the staff member carries out
     * @param name Name of the staff member
     * @param userName System username for the staff member
     * @param password System Password for the staff member
     */
    public Staff(WorkerRoles role, String[] name, String userName, String password)
    {
        super(role,userName, password);
        firstName = name[0];
        lastName = name[1];
    }
    
    /**
     * Get name
     * @return Name
     */
    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    /**
     * Provide the type of Worker
     * @return WorkerType e.g. STAFF, FREELANCER, etc.
     */
    @Override
    public WorkerType getWorkerType() {
        return WorkerType.STAFF;
    }
}
