package BLL;

/**
 * Defined types for all workers
 * @author James Staite
 * @version 1.0.0
 */
public enum WorkerType {
    STAFF,
    CONTRACTOR,
    FREELANCER,
    CLIENT;
    
    /**
     * Provides a displayable string of the enum
     * @return displayable string
     */
    @Override
    public String toString() 
    {
        //only capitalize the first letter
        String s = super.toString();
        return s.substring(0, 1) + s.substring(1).toLowerCase();
    }
}
