package BLL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Client class test case
 * @author James Staite
 */
public class ClientTest {
    
    public ClientTest() {
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
     * Test of getName method, of class Client.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "Company";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Client.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "Company";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWorkerType method, of class Client.
     */
    @Test
    public void testGetWorkerType() {
        System.out.println("getWorkerType");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        WorkerType expResult = WorkerType.CLIENT;
        WorkerType result = instance.getWorkerType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressLine1 method, of class Client.
     */
    @Test
    public void testGetAddressLine1() {
        System.out.println("getAddressLine1");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "setAddressLine1";
        instance.setAddressLine1(expResult);
        String result = instance.getAddressLine1();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAddressLine1 method, of class Client.
     */
    @Test
    public void testSetAddressLine1() {
        System.out.println("setAddressLine1");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "setAddressLine1";
        instance.setAddressLine1(expResult);
        String result = instance.getAddressLine1();

        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressLine2 method, of class Client.
     */
    @Test
    public void testGetAddressLine2() {
        System.out.println("getAddressLine2");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "getAddressLine2";
        instance.setAddressLine2(expResult);
        String result = instance.getAddressLine2();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAddressLine2 method, of class Client.
     */
    @Test
    public void testSetAddressLine2() {
        System.out.println("setAddressLine2");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "setAddressLine2";
        instance.setAddressLine2(expResult);
        String result = instance.getAddressLine2();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressLine3 method, of class Client.
     */
    @Test
    public void testGetAddressLine3() {
        System.out.println("getAddressLine3");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "getAddressLine3";
        instance.setAddressLine3(expResult);
        String result = instance.getAddressLine3();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAddressLine3 method, of class Client.
     */
    @Test
    public void testSetAddressLine3() {
        System.out.println("setAddressLine3");
         String expResult = "setAddressLine3";
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        instance.setAddressLine3(expResult);
        String result = instance.getAddressLine3();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressLine4 method, of class Client.
     */
    @Test
    public void testGetAddressLine4() {
        System.out.println("getAddressLine4");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "getAddressLine4";
        instance.setAddressLine4(expResult);
        String result = instance.getAddressLine4();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAddressLine4 method, of class Client.
     */
    @Test
    public void testSetAddressLine4() {
        System.out.println("setAddressLine4");
        String expResult = "setAddressLine4";
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        instance.setAddressLine4(expResult);
        String result = instance.getAddressLine4();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPostCode method, of class Client.
     */
    @Test
    public void testGetPostCode() {
        System.out.println("getPostCode");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "getPostCode";
        instance.setPostCode(expResult);
        String result = instance.getPostCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPostCode method, of class Client.
     */
    @Test
    public void testSetPostCode() {
        System.out.println("setPostCode");
        String expResult = "S1 3TY";
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        instance.setPostCode(expResult);
        String result = instance.getPostCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTel method, of class Client.
     */
    @Test
    public void testGetTel() {
        System.out.println("getTel");
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        String expResult = "getTel";
        instance.setPostCode(expResult);
        String result = instance.getPostCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTel method, of class Client.
     */
    @Test
    public void testSetTel() {
        System.out.println("setTel");
        String expResult = "0114 2725698";
        Client instance = new Client(WorkerRoles.CLIENT, new String[]{"Company"}, "John.Smith", "Password");
        instance.setTel(expResult);
        String result = instance.getTel();
        assertEquals(expResult, result);
    }
}