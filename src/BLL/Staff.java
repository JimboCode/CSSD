
package BLL;

import java.util.ArrayList;

/**
 *
 * @author James
 * @version 1.0.0
 */
public class Staff extends Worker
{
    private String firstName;
    private String lastName;
    
    /**
     * 
     * @param roles Roles that the staff member carries out
     * @param name Name of the staff member
     * @param userName System username for the staff member
     * @param password System Password for the staff member
     */
    public Staff(WorkerRoles[] roles, String[] name, String userName, String password)
    {
        super(roles,userName, password);
        firstName = name[0];
        lastName = name[1];
    }
    
    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public WorkerType getWorkerType() {
        return WorkerType.STAFF;
    }
}
