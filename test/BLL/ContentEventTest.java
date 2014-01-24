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
public class ContentEventTest {
    
    public ContentEventTest() {
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
     * Test of getParent method, of class ContentEvent.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");
        MediaItem parent = new MediaElement("Test", "Description", ComponentType.COMPRESSED_ELEMENT, null, new MediaElementWorkFlow());
        MediaItem child = new MediaAsset("Test", "Description", ComponentType.TEXTFILE, null, new MediaAssetWorkFlow());
        ContentEvent instance = new ContentEvent(parent, child, ContentEvent.ADD);
        MediaItem expResult = parent;
        MediaItem result = instance.getParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChild method, of class ContentEvent.
     */
    @Test
    public void testGetChild() {
        System.out.println("getChild");
        MediaItem parent = new MediaElement("Test", "Description", ComponentType.COMPRESSED_ELEMENT, null, new MediaElementWorkFlow());
        MediaItem child = new MediaAsset("Test", "Description", ComponentType.TEXTFILE, null, new MediaAssetWorkFlow());
        ContentEvent instance = new ContentEvent(parent, child, ContentEvent.ADD);
        MediaItem expResult = child;
        MediaItem result = instance.getChild();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAction method, of class ContentEvent.
     */
    @Test
    public void test1GetAction() {
        System.out.println("getAction");
        MediaItem parent = new MediaElement("Test", "Description", ComponentType.COMPRESSED_ELEMENT, null, new MediaElementWorkFlow());
        MediaItem child = new MediaAsset("Test", "Description", ComponentType.TEXTFILE, null, new MediaAssetWorkFlow());
        ContentEvent instance = new ContentEvent(parent, child, ContentEvent.ADD);
        int expResult = ContentEvent.ADD;
        int result = instance.getAction();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getAction method, of class ContentEvent.
     */
    @Test
    public void test2GetAction() {
        System.out.println("getAction");
        MediaItem parent = new MediaElement("Test", "Description", ComponentType.COMPRESSED_ELEMENT, null, new MediaElementWorkFlow());
        MediaItem child = new MediaAsset("Test", "Description", ComponentType.TEXTFILE, null, new MediaAssetWorkFlow());
        ContentEvent instance = new ContentEvent(parent, child, ContentEvent.DELETE);
        int expResult = ContentEvent.DELETE;
        int result = instance.getAction();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getAction method, of class ContentEvent.
     */
    @Test
    public void test3GetAction() {
        System.out.println("getAction");
        MediaItem parent = new MediaElement("Test", "Description", ComponentType.COMPRESSED_ELEMENT, null, new MediaElementWorkFlow());
        MediaItem child = new MediaAsset("Test", "Description", ComponentType.TEXTFILE, null, new MediaAssetWorkFlow());
        ContentEvent instance = new ContentEvent(parent, child, ContentEvent.MOVE);
        int expResult = ContentEvent.MOVE;
        int result = instance.getAction();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getAction method, of class ContentEvent.
     */
    @Test
    public void test4GetAction() {
        System.out.println("getAction");
        MediaItem parent = new MediaElement("Test", "Description", ComponentType.COMPRESSED_ELEMENT, null, new MediaElementWorkFlow());
        MediaItem child = new MediaAsset("Test", "Description", ComponentType.TEXTFILE, null, new MediaAssetWorkFlow());
        ContentEvent instance = new ContentEvent(parent, child, ContentEvent.UPDATE);
        int expResult = ContentEvent.UPDATE;
        int result = instance.getAction();
        assertEquals(expResult, result);
    }
}