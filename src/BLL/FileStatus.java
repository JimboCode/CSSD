package BLL;

/**
 * Status of files within the vault
 * 
 * @author James Staite
 */
public enum FileStatus 
{
    NEW_NOT_QC_CHECKED,
    DEFECTIVE,
    FIXED,
    OBSOLETE,
    QUICK,
    ACCEPTED,
    COMPRESSED,
    FINAL_COMPRESSION;
    
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
