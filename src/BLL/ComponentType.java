package BLL;

/**
 * Enumeration of the different types of components used and created by the 
 * project content tree
 * 
 * @author James Staite
 */
public enum ComponentType 
{
    NONE,
    VIDEO,
    AUDIO,
    JAVA,
    SUBTITLES,
    TRANSLATION,
    TEXTFILE;
    
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
