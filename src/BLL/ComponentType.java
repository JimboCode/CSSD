package BLL;

/**
 * Enumeration of the different types of components used and created by the 
 * project content tree
 * 
 * @author James Staite
 */
public enum ComponentType 
{
    VIDEO,
    AUDIO,
    JAVA,
    SUBTITLES,
    TRANSLATION,
    TEXTFILE,
    COMPRESSED_ELEMENT;
    
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
