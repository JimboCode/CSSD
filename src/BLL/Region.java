package BLL;

/**
 * Defines the DVD & Blu-ray disc regions
 * @author James Staite
 */
public enum Region 
{
    WORLDWIDE_0,
    USA_1,
    EUROPE_2,
    ASIA_3,
    SOUTHAMERICA_4,
    AFRICA_5,
    CHINA_6,
    INTERNATIONAL_8;
    
    /**
     * Takes description and turns it into a displayable string
     * @return description
     */
    @Override 
    public String toString() 
    {
        // use regional value to provide string
        switch (this)
        {
            case WORLDWIDE_0:
                return "Region 0 - Worldwide";
            case USA_1:
                return "Region 1 - North American";
            case EUROPE_2:
                return "Region 2 - Europe";
            case ASIA_3:
                return "Region 3 - Asia";
            case SOUTHAMERICA_4:
                return "Region 4 - South American";
            case AFRICA_5:
                return "Region 5 - Afica";
            case CHINA_6:
                return "Region 6 - China";
            case INTERNATIONAL_8:
                return "Region 8 - International";
        }
        return "Invalid Region";
    }    
}
