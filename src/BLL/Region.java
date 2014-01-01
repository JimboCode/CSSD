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
    
    @Override public String toString() {
        String value = super.toString();
        String words[] = value.split("_");
        int region = Integer.parseInt(words[1]);
        switch (region)
        {
            case 0:
                return "Region 0 - Worldwide";
            case 1:
                return "Region 1 - North American";
            case 2:
                return "Region 2 - Europe";
            case 3:
                return "Region 3 - Asia";
            case 4:
                return "Region 4 - South American";
            case 5:
                return "Region 5 - Afica";
            case 6:
                return "Region 6 - China";
            case 8:
                return "Region 8 - International";
        }
        return "Invalid Region";
    }    
}
