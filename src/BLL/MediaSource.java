/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

/**
 * Origin of media
 * 
 * @author James Staite
 */
public enum MediaSource 
{
    CLIENT,
    SUBCONTRACTOR,
    ASSETS,
    IN_HOUSE;
    
    
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
