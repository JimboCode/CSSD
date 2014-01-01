package BLL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Register for all worker type classes e.g. Staff, Contractor, etc.
 * This is a singleton class
 * 
 * @author James Staite
 * @version 1.0.0
 */
public class WorkerRegister 
{
    // To improve multithreading and because this class is always required
    // the instance is initialised immediately
    // unique instance reference
    private static WorkerRegister uniqueInstance = new WorkerRegister();
    
    // list of worker objects referenced by username as key
    // Only a small amount of data expected and therefore a hashmap used
    // may be necessary to change if volume increases 
    private Map<String, Worker> workerReg = new HashMap();
    
    /**
     * Return a unique class ref (Singleton)
     * @return Singleton reference to WorkerRegister
     */
    public static WorkerRegister getInstance()
    {
        return uniqueInstance;
    }
    
    /**
     * Adds Worker objects to the Worker Register
     * N.B. will not replace an instance of a worker object with the same username
     * remove the instance and then add it to re-use the username.
     * 
     * @param worker object to be added to register
     * @return Confirmation that the object has been added
     */
    public boolean addWorker(WorkerRoles[] roles, String[] name, String userName, String password, WorkerType type)
    {
        // check that a worker with this username does not already exist and that its username is unique
        if (!workerReg.containsKey(userName))
        {
            // create worker
            Worker worker = WorkerFactory.createWorker(roles, name, userName, password, type);
            
            // add new worker object
            workerReg.put(worker.userName, worker);
            return true;
        }
        return false;        
    }
    
    /**
     * Removes Worker object from the register
     * @param worker object to be removed
     * @return Confirmation that the object has been found and removed
     */
    public boolean removeWorker(Worker worker)
    {
        // check that the object existing
        if (workerReg.containsValue(worker))
        {
            // remove worker object and confirm action
            workerReg.remove(worker.userName);
            return true;
        }
        // item not found
        return false;
    }
    
    /**
     * Checks user credentials for validation
     * @param userName Username
     * @param password Password
     * @return Worker object that match the credentials if found
     */
    public Worker checkPassword(String userName, String password)
    {
        // use username as key for hasmap lookup
        Worker worker = workerReg.get(userName);
        
        // check if a worker object found
        if (worker != null)
        {
            // check if password is valid
            if (worker.checkPassword(password))
            {
                // reutrn worker object
                return worker;
            }
        }
        // return null if no object found
        return null;
    }
    
    /**
     * Finds workers by role and worker type
     * @param role a worker role what is required e.g. QC
     * @param workType a worker type e.g. Staff, Sub-contractor OR null for all
     * @return ArrayList of Worker objects that match the search
     */
    public synchronized ArrayList<Worker> findByRole(WorkerRoles role, WorkerType workType)
    {
        // create list to pass back
        ArrayList workersFound = new ArrayList();
        
        // Iterate over collection of Workers
        for(Map.Entry<String, Worker> entry : workerReg.entrySet())
        {
            // get current Worker
            Worker worker = entry.getValue();
            
            // check if all worker types are required or if it matches the type to search for
            if(workType != null && worker.getWorkerType() == workType)
            {
                // check if the worker does the role looking for
                if(role == null || worker.confirmDoesRole(role))
                {
                    // if so add to list
                    workersFound.add(worker);
                }
            }
        }
        // return list of found workers
        return workersFound;
    }
    
    /**
     * Finds Workers by role, worker type and name
     * @param role a worker role what is required e.g. QC
     * @param workType a worker type e.g. Staff, Sub-contractor OR null for all
     * @param name The name of the worker (Case insensitive & matches parts of name e.g. JAM = James)
     * @return ArrayList of Worker objects that match the search
     */
    public synchronized ArrayList<Worker> findByName(WorkerRoles role, WorkerType workType, String name)
    {
        // create list to pass back
        ArrayList workersFound = new ArrayList();
        
        // Iterate over collection of Workers
        for(Map.Entry<String, Worker> entry : workerReg.entrySet())
        {
            // get current Worker
            Worker worker = entry.getValue();
            
            // check if all worker types are required or if it matches the type to search for
            if(workType == null || worker.getWorkerType() == workType)
            {
                // check if all roles are required or if only matches for a type are required
                if(role == null || worker.confirmDoesRole(role))
                {
                    // check if name matches the object name
                    if(worker.getName().toUpperCase().indexOf(name.toUpperCase())!= 0)
                    {
                        // add to list
                        workersFound.add(worker);
                    }
                }
            }
        }
        // return list of found workers
        return workersFound;
    }
}
