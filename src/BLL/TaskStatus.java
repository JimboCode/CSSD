package BLL;

/**
 * Status of individual tasks
 * 
 * @author James Staite
 */
public enum TaskStatus 
{
    AWAITING_ACTION,
    IN_PROGRESS,
    COMPLETE;
    
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
        return value.trim();
    }
}
