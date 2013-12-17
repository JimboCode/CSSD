
package BLL;

import java.util.ArrayList;

/**
 *
 * @author James
 * @version 1.0.0
 */
public class Staff extends Worker
{
    /**
     * 
     * @param roles Roles that the staff member carries out
     * @param name Name of the staff member
     * @param userName System username for the staff member
     * @param password System Password for the staff member
     */
    public Staff(ArrayList<WorkerRoles> roles, String name, String userName, String password)
    {
        this.roles = roles;
        this.name = name;
        this.userName = userName;
        this.password = password;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public WorkerType getWorkerType() {
        return WorkerType.STAFF;
    }
}