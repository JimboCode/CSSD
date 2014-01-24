package BLL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * File Class test cases
 * @author James Staite
 */
public class FileTest {
    
    public FileTest() {
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
     * Test of getName method, of class File.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String name = "Filename";
        FileStatus status = FileStatus.NEW_NOT_QC_CHECKED;
        int version = 0;
        File instance = new File(name, status, version);
        String expResult = name;
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStatus method, of class File.
     */
    @Test
    public void testGetStatus() {
        System.out.println("getStatus");
        String name = "Filename";
        FileStatus status = FileStatus.NEW_NOT_QC_CHECKED;
        int version = 0;
        File instance = new File(name, status, version);
        FileStatus expResult = status;
        FileStatus result = instance.getStatus();
        assertEquals(expResult, result);
    }

    /**
     * Test of setStatus method, of class File.
     */
    @Test
    public void testSetStatus() {
        System.out.println("setStatus");
        String name = "Filename";
        FileStatus status = FileStatus.NEW_NOT_QC_CHECKED;
        int version = 0;
        File instance = new File(name, status, version);
        instance.setStatus(status);
        FileStatus result = status;
        FileStatus expResult = instance.getStatus();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVersion method, of class File.
     */
    @Test
    public void testGetVersion() {
        System.out.println("getVersion");
        String name = "Filename";
        FileStatus status = FileStatus.NEW_NOT_QC_CHECKED;
        int version = 0;
        File instance = new File(name, status, version);
        int expResult = 0;
        int result = instance.getVersion();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getVersion method, of class File.
     */
    @Test
    public void tes2tGetVersion() {
        System.out.println("getVersion");
        String name = "Filename";
        FileStatus status = FileStatus.NEW_NOT_QC_CHECKED;
        int version = 10;
        File instance = new File(name, status, version);
        int expResult = 10;
        int result = instance.getVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of setQCReport method, of class File.
     */
    @Test
    public void testSetQCReport() {
        System.out.println("setQCReport");
        String name = "Filename";
        FileStatus status = FileStatus.NEW_NOT_QC_CHECKED;
        int version = 10;
        File instance = new File(name, status, version);
        QCReport report = new QCReport();
        instance.setQCReport(report);
        QCReport expResult = instance.getQCReport();
        assertEquals(expResult, report);
    }

    /**
     * Test of getQCReport method, of class File.
     */
    @Test
    public void testGetQCReport() {
        System.out.println("getQCReport");
        String name = "Filename";
        FileStatus status = FileStatus.NEW_NOT_QC_CHECKED;
        int version = 10;
        File instance = new File(name, status, version);
        QCReport report = new QCReport();
        instance.setQCReport(report);
        QCReport expResult = instance.getQCReport();
        assertEquals(expResult, report);
    }
}