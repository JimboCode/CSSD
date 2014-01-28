/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

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
public class StaffTest {
    
    public StaffTest() {
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
     * Test of getName method, of class Staff.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Staff instance = new Staff(WorkerRoles.INTERPRETER, new String[]{"Rachel","Jones"}, "R.Jones", "Password");
        String expResult = "Rachel Jones";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWorkerType method, of class Staff.
     */
    @Test
    public void testGetWorkerType() {
        System.out.println("getWorkerType");
        Staff instance = new Staff(WorkerRoles.INTERPRETER, new String[]{"Rachel","Jones"}, "R.Jones", "Password");
        WorkerType expResult = WorkerType.STAFF;
        WorkerType result = instance.getWorkerType();
        assertEquals(expResult, result);
    }
}