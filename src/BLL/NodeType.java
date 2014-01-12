package BLL;

/**
 * Enumeration used within the code for the node types supported
 * Also provides text to display for each type in UI
 * 
 * @author James Staite
 */
public enum NodeType 
{
    ASSET,
    ELEMENT;
    
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
