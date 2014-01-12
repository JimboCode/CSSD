/*
 * Client Register - holds all the client records
 */
package BLL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

/**
 * Lazy loading singleton
 * @author James Staite
 * @version 1.0.0
 */
public class ClientRegister extends Observable
{
    // single instance of this class
    private static ClientRegister uniqueInstance;
    
    // collection of client records
    private ArrayList<Client> clientReg = new ArrayList();
    
    // private constructor - singleton
    private ClientRegister()
    {
    }
    
   /**
    * Provide instance of the singleton object
    * @return Singleton instance of client register
    */
    public static synchronized ClientRegister getInstance()
    {
        if (uniqueInstance == null)
        {
            uniqueInstance = new ClientRegister();
        }
        return uniqueInstance;
    }
    
    /**
     * creates and adds a client record
     * @param name client name (used to identify clients - must be unique)
     * @param addLine1 address
     * @param addLine2 address
     * @param addLine3 address
     * @param addLine4 address
     * @param postCode postcode
     * @param tel telephone number
     * @return confirmation of creation and addition to the register
     */
    public boolean addClient(String name, String addLine1, String addLine2, String addLine3, String addLine4, String postCode, String tel)
    {
        // check client already exists
        Client client = findbyName(name);
        if (client == null)
        {
            // create new client record
            client = new Client(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
            
            // add new client record
            clientReg.add(client);
            
            // notify client added
            raiseChangedEvent();
            
            return true;
        }
        return false;        
    }
   
    /**
     * remove exiting clients from register
     * @param client Client object to be removed
     * @return Confirmation record found and removed
     */
    public boolean removeClient(Client client)
    {
        // check that the object existing
        if (clientReg.contains(client))
        {
            // check that the client has no projects before removing them
            ProjectRegister proReg = ProjectRegister.getInstance();
            if (proReg.doesClientHaveProjects(client))
            {
                return false;
            }
            else
            {
                // remove worker object and confirm action
                clientReg.remove(client);

                // notify client removed
                raiseChangedEvent();

                return true;
            }
        }
        // item not found
        return false;
    }
    
    /**
     * Raises event to notify observers that the contents of the register have changed
     */
    private void raiseChangedEvent()
    {
        // flag change
        setChanged();
        
        // send notification - not sending any data object to observers - operating a pull model
        notifyObservers();
    }
    
    /**
     * Locates a client record by their name (used to identify clients - must be unique) 
     * @param name name (used to identify clients - must be unique)
     * @return Client record object
     */
    public Client findbyName(String name)
    {
        // iterate over collection to find matching record
        for(Client client: clientReg)
        {
            // check if the name matches (not case sensitive)
            if(client.getName().compareToIgnoreCase(name) == 0)
            {
                return client;
            }
        }
        return null;
    }
    
    /**
     *  Returns a collection of clients
     * @return ArrayList of clients
     */    
    public List<Client> getClientList()
    {
        return Collections.unmodifiableList(clientReg);
    }

    /**
     * provides the number of clients
     * @return number of clients
     */
    public int getNumberOfClients() {
        return clientReg.size();
    }
}
