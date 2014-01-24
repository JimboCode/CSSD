package BLL;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author James
 */
public class ClientRegisterTest {
    
    public ClientRegisterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class ClientRegister.
     */
    @Test
    public void test1GetInstance() {
        System.out.println("getInstance test 1");
        ClientRegister result = ClientRegister.getInstance();
        assert(result instanceof ClientRegister);
    }
    
    /**
     * Test of getInstance method, returns singleton (same instance).
     */
    @Test
    public void test2GetInstance() {
        System.out.println("getInstance test 2");
        ClientRegister result = ClientRegister.getInstance();
        ClientRegister expResult = ClientRegister.getInstance();
        assertEquals(expResult, result);
    }

    /**
     * Test of addClient method, of class ClientRegister.
     */
    @Test
    public void test1AddClient() {
        System.out.println("addClient");
        String name = "Company";
        String addLine1 = "AddressLine1";
        String addLine2 = "AddressLine2";
        String addLine3 = "AddressLine3";
        String addLine4 = "AddressLine4";
        String postCode = "PostCode";
        String tel = "0114 276897";
        ClientRegister instance = ClientRegister.getInstance();
        boolean expResult = true;
        boolean result = instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of addClient method, of class ClientRegister.
     */
    @Test
    public void test2AddClient() {
        System.out.println("addClient");
        String name = "Company";
        String addLine1 = "AddressLine1";
        String addLine2 = "AddressLine2";
        String addLine3 = "AddressLine3";
        String addLine4 = "AddressLine4";
        String postCode = "PostCode";
        String tel = "0114 276897";
        ClientRegister instance = ClientRegister.getInstance();
        instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        int expResult = 1;
        int result = instance.getNumberOfClients();
        assertEquals(expResult, result);
    }

    /**
     * Test of removeClient method, of class ClientRegister.
     */
    @Test
    public void test1RemoveClient() {
        System.out.println("removeClient");
        String name = "Company";
        String addLine1 = "AddressLine1";
        String addLine2 = "AddressLine2";
        String addLine3 = "AddressLine3";
        String addLine4 = "AddressLine4";
        String postCode = "PostCode";
        String tel = "0114 276897";
        ClientRegister instance = ClientRegister.getInstance();
        instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        Client client = instance.getClientList().get(0);
        boolean expResult = true;
        boolean result = instance.removeClient(client);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of removeClient method, of class ClientRegister.
     */
    @Test
    public void test2RemoveClient() {
        System.out.println("removeClient");
        String name = "Company2";
        String addLine1 = "AddressLine1";
        String addLine2 = "AddressLine2";
        String addLine3 = "AddressLine3";
        String addLine4 = "AddressLine4";
        String postCode = "PostCode";
        String tel = "0114 276897";
        ClientRegister instance = ClientRegister.getInstance();
        instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        Client client = instance.getClientList().get(0);
        instance.removeClient(client);
        int expResult = 0;
        int result = instance.getNumberOfClients();
        assertEquals(expResult, result);
    }

    /**
     * Test of findbyName method, of class ClientRegister.
     */
    @Test
    public void testFindbyName() {
        System.out.println("findbyName");
        String name = "Company3";
        String addLine1 = "AddressLine1";
        String addLine2 = "AddressLine2";
        String addLine3 = "AddressLine3";
        String addLine4 = "AddressLine4";
        String postCode = "PostCode";
        String tel = "0114 276897";
        ClientRegister instance = ClientRegister.getInstance();
        instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        Client client = instance.getClientList().get(0);
        Client expResult = client;
        Client result = instance.findbyName(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getClientList method, of class ClientRegister.
     */
    @Test
    public void testGetClientList() {
        String name = "Company3";
        String addLine1 = "AddressLine1";
        String addLine2 = "AddressLine2";
        String addLine3 = "AddressLine3";
        String addLine4 = "AddressLine4";
        String postCode = "PostCode";
        String tel = "0114 276897";
        ClientRegister instance = ClientRegister.getInstance();
        instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        name = "Company4";
        instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        List clientList = instance.getClientList();
        int result = clientList.size();
        int expResult = 2;
        assertEquals(expResult, result);
    }

     /**
     * Test of getClientList method, of class ClientRegister.
     */
    @Test
    public void test2GetClientList() {
        String name = "Company5";
        String addLine1 = "AddressLine1";
        String addLine2 = "AddressLine2";
        String addLine3 = "AddressLine3";
        String addLine4 = "AddressLine4";
        String postCode = "PostCode";
        String tel = "0114 276897";
        ClientRegister instance = ClientRegister.getInstance();
        instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        name = "Company6";
        instance.addClient(name, addLine1, addLine2, addLine3, addLine4, postCode, tel);
        List clientList = instance.getClientList();
        Client result = instance.findbyName(name);
        Client expResult = (Client) clientList.get(clientList.indexOf(result));
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getNumberOfClients method, of class ClientRegister.
     */
    @Test
    public void testGetNumberOfClients() {
        System.out.println("getNumberOfClients");
        ClientRegister instance = ClientRegister.getInstance();
        List clientList = instance.getClientList();
        int expResult = clientList.size();
        int result = instance.getNumberOfClients();
        assertEquals(expResult, result);
    }
}