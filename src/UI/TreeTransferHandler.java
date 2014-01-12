package UI;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Stack;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Handles drag and drop actions for the ContentTree class then enabled
 * 
 * @author James Staite
 */
class TreeTransferHandler extends TransferHandler {  
    
    // supported mineType for this drag and drop
    DataFlavor nodeFlavour;  
    
    // array of supported types required by the interface
    DataFlavor[] flavours = new DataFlavor[1]; 
  
    /**
     * Setup transfer handler
     */
    public TreeTransferHandler() 
    {  
        try {  
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType +  ";class=\"" + javax.swing.tree.DefaultMutableTreeNode.class.getName() + "\"";
                nodeFlavour = new DataFlavor(mimeType);  
                flavours[0] = nodeFlavour;
            } 
        catch(ClassNotFoundException e) 
        {
            System.out.println("ClassNotFound: " + e.getMessage());  
        }  
    }  

    /**
     * Confirms if the current location permits the import of the transfer object 
     * @param support import information
     * @return true / false if import is possible
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) 
    {  
        // check drop is enabled
        if(!support.isDrop()) return false;  

        // show drop 
        support.setShowDropLocation(true);  
        
        // check correct object type (DefaultMutableTreeNode)
        if(!support.isDataFlavorSupported(nodeFlavour)) 
        {
            return false;  
        }  
        
        // Get reference to the tree  
        JTree.DropLocation dl =  (JTree.DropLocation)support.getDropLocation();  
        JTree tree = (JTree)support.getComponent();  

        // check they is a target
        TreePath dest = dl.getPath();  
        if (dest == null) return false;
        DefaultMutableTreeNode target = (DefaultMutableTreeNode)dest.getLastPathComponent();
        if (target == null)
            return false;
        
        // Do not allow a drop on a leaf
        if (!target.getAllowsChildren())
        {
            return false;
        }
        
        // Do not allow a drop on the drag source 
        int[] selRows = tree.getSelectionRows();  
        TreePath path = tree.getPathForRow(selRows[0]);  
        DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode)path.getLastPathComponent();
        if (target.equals(sourceNode)) return false;
        
        // Do not allow a drop on parent - pointless
        if(sourceNode.getParent().equals(target))
        {
            return false;
        }
        
        // check that the node is not being dropped lower in its own path
        // define temp varaiables
        DefaultMutableTreeNode parentNode;
        DefaultMutableTreeNode child;
        // define temp storage for search
        Stack<DefaultMutableTreeNode> stack = new Stack();
        
        // push the top of the tree onto the stack to start search from it
        stack.push(sourceNode);
        
        // loop until the stack has been emptied
        while(!stack.empty())
        {
            // pull the next parent node off the stack
            parentNode =  stack.pop();
            
            if (parentNode.getChildCount() > 0)
            {
                for(int i = 0; i < parentNode.getChildCount(); i++)
                {
                    child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                    // if a child note matches the source node then source node lower in tree path
                    if(child.equals(target))
                    {
                        return false;
                    }
                    if (child.getAllowsChildren()) stack.push(child);
                }
            }
        }
        // past all tests ok to import
        return true;
    }  

    /**
     * Creates a transfer object based upon the start of the drag
     * @param comp reference to the tree component
     * @return a transfer object hold the data to be transferred
     */
    @Override
    protected Transferable createTransferable(JComponent comp) 
    {  
        // get reference to the tree
        JTree tree = (JTree)comp;  
        TreePath[] paths = tree.getSelectionPaths();  
        
        // check that there is something to package
        if(paths != null) 
        {  
            // package the selected node for transfer
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();  
            return new NodesTransferable(node);  
        }  
        
        // return nothing because there was nothing to package
        return null;  
    }  

    /**
     * Provides the trees source actions its supports
     * @param comp reference to component passed in
     * @return supports actions flag
     */
    @Override
    public int getSourceActions(JComponent comp) 
    {  
        return MOVE;  
    }  
   
    /**
     * Handles the import of data into the tree
     * 
     * Note that the export of data method is NOT used as the tree only permits
     * the movement of data within the tree using a customer transfer type and
     * therefore no export is possible.  This method handles the movement
     * 
     * @param support The transfer information required for the movement
     * @return confirmation that the import has been handled successfully
     */
    @Override
    public boolean importData(TransferHandler.TransferSupport support) 
    {  
        // check can import offered type
        if(!canImport(support)) 
        {  
            return false;  
        }  
        // Extract transfer data.  
        DefaultMutableTreeNode node = null;  
        try {  
            Transferable t = support.getTransferable();  
            node = (DefaultMutableTreeNode)t.getTransferData(nodeFlavour);  
            } 
        catch(UnsupportedFlavorException ufe) 
        {  
            System.out.println("UnsupportedFlavor: " + ufe.getMessage());  
        }
        catch(java.io.IOException ioe) 
        {  
            System.out.println("I/O error: " + ioe.getMessage());  
        }  
        // Get drop location info.  
        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();  
        TreePath dest = dl.getPath();  
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)dest.getLastPathComponent();  
        ContentTree tree = (ContentTree)support.getComponent();
        tree.moveItem(parent, node);
        return true;  
    }
    
    /**
     * Custom transfer data class for the movement of a single tree node
     */
    public class NodesTransferable implements Transferable 
    {  
        // The tree node to be moved
        DefaultMutableTreeNode node;  

        /**
         * Stores the node to be transferred on creation of the object
         * @param node DefaultMutableTreeNode object to be moved
         */
        public NodesTransferable(DefaultMutableTreeNode node) 
        {  
            this.node = node;  
        }  

        /**
         * Provide the data being transferred in the requested flavour if supported
         * @param flavour The flavour for the information to be provided in
         * @return requested information
         * @throws UnsupportedFlavorException 
         */
        @Override
        public Object getTransferData(DataFlavor flavour) throws UnsupportedFlavorException {  
            // check flavour requested is supported
            if(!isDataFlavorSupported(flavour)) throw new UnsupportedFlavorException(flavour);  
            
            // return data
            return node;  
        }  

        /**
         * Provides a list of the supported information flavours that the information can be provided
         * @return list of flavours
         */
        @Override
        public DataFlavor[] getTransferDataFlavors() 
        {  
            return flavours;  
        }  

        /**
         * Confirms if a flavour is supported
         * @param flavour flavour type to be checked
         * @return confirmation if supported
         */
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavour) 
        {  
            return nodeFlavour.equals(flavour);  
        }
    }
}  

