package BLL;

/**
 * Client Record
 * @author James Staite
 */
public class Client 
{
    // Client information
    private String name;
    private String tel;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String postCode;
    
    /**
     * Creates a new client instance
     * @param name Client name
     * @param addLine1 Address
     * @param addLine2 Address
     * @param addLine3 Address
     * @param addLine4 Address
     * @param postCode Postcode
     * @param tel Telephone number
     */
    Client(String name, String addLine1, String addLine2, String addLine3, String addLine4, String postCode, String tel)
    {
        this.name = name;
        this.addressLine1 = addLine1;
        this.addressLine2 = addLine2;
        this.addressLine3 = addLine3;
        this.addressLine4 = addLine4;
        this.postCode = postCode;
        this.tel = tel;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
}
