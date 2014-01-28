package BLL;

/**
 * Defined roles for all workers
 * @author James Staite
 * @version 1.0.0
 */
public enum WorkerRoles 
{
    ALL,
    AUTHOR,
    INTERPRETER,
    QC,
    PROJECT_MANAGER,
    QC_TEAM_LEADER,
    CONTRACTOR,
    CLIENT;
    
    /**
     * Provides a displayable string of the enum
     * @return displayable string
     */
    @Override
    public String toString()
    {
        // strips the _ and replaces with " "
        // Capitalises the first letter
        // Replaces QC with Quality Control
        String value = super.toString();
        String words[] = value.split("_");
        value = "";
        for(String word: words)
        {
            word = word.toUpperCase().replace(word.substring(1), word.substring(1).toLowerCase());
            value += word + " ";
        }
        value = value.replace("Qc", "Quality Control");
        return value.trim();
 }

}
