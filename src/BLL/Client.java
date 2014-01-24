package BLL;

/**
 * Client Record
 * @author James Staite
 */
public class Client extends Worker
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
    Client(WorkerRoles role, String[] name, String userName, String password) 
    {
        super(role,userName, password);
        this.name = name[0];
    }

    /**
     * Provides the clients name
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * displays the client name when the object is used
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Provides the type of worker (quicker than reflection)
     * @return Name
     */
    @Override
    public WorkerType getWorkerType() {
        return WorkerType.CLIENT;
    }    

    /**
     * Gets part of the address
     * @return the addressLine1
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets part of the address
     * @param addressLine1 the addressLine1 to set
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Gets part of the address
     * @return the addressLine2
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets part of the address
     * @param addressLine2 the addressLine2 to set
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Gets part of the address
     * @return the addressLine3
     */
    public String getAddressLine3() {
        return addressLine3;
    }

    /**
     * Gets part of the address
     * @param addressLine3 the addressLine3 to set
     */
    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    /**
     * Gets part of the address
     * @return the addressLine4
     */
    public String getAddressLine4() {
        return addressLine4;
    }

    /**
     * Sets part of the address
     * @param addressLine4 the addressLine4 to set
     */
    public void setAddressLine4(String addressLine4) {
        this.addressLine4 = addressLine4;
    }

    /**
     * Gets part of the address
     * @return the postCode
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * The postcode to set
     * @param postCode the postCode to set
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * Gets the telephone number
     * @return the tel
     */
    public String getTel() {
        return tel;
    }

    /**
     * set the telephone number
     * @param tel the telephone number to set
     */
    public void setTel(String tel) {
        this.tel = tel;
    }
}
