package UI;

import BLL.ComponentType;
import BLL.ContentEvent;
import BLL.ContentManager;
import BLL.MediaElement;
import BLL.MediaItem;
import BLL.NodeType;
import BLL.Project;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Custom Jtree for displaying Project Media Content
 * Incorporates automatic addition, updating and deletion of nodes form the
 * underlying BLL structure using events.
 * 
 * @author James Staite
 */
public class ContentTree extends JTree implements Observer
{
    // reference to the project tree being displayed
    Project project;
    
    // reference to the ContentManager for the tree data of the project
    ContentManager contentManager;
    
    // referemce to the tree model for this tree
    private DefaultTreeModel internalTreeModel;
    
    /**
     * Initiates the tree settings
     */
    public ContentTree()
    {
        // create new tree model
        internalTreeModel = new DefaultTreeModel(null); 
        
        // set the tree model to use the AllowsChildren setting for the images
        internalTreeModel.setAsksAllowsChildren(true);
        
        // assist the tree model to the tree
        this.setModel(internalTreeModel);
        
        //Add Angled lineStyle to Nimbus Style tree
        NimbusLookAndFeel laf = new NimbusLookAndFeel();
        try {
            UIManager.setLookAndFeel(laf);
            UIDefaults nimbUID = laf.getDefaults();
            nimbUID.put("Tree.drawHorizontalLines", true);
            nimbUID.put("Tree.drawVerticalLines", true);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(DefineContentUI.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * Enables drag and drop on the tree and creates the handler to support it
     * @param enable state of drag and drop
     */
    @Override
    public void setDragEnabled(boolean enable)
    {
        if (enable == true)
        {
            // set drag and dropMode plus creates the handler
            super.setDragEnabled(enable);
            this.setDropMode(DropMode.ON);
            this.setTransferHandler(new TreeTransferHandler());
        }
        else
        {
            this.setDragEnabled(false);
        }
    }
    
    /**
     * Assigns the project to be used for the tree
     * @param selectedProject Project to be used for the tree to display
     */
    public void setProject(Project selectedProject)
    {
        // check if this project is already displayed
        if (!selectedProject.equals(project))
        {
            // if the reference to the ContentManager for the project is not null
            // then remove this tree object from event updates
            if(contentManager != null) contentManager.deleteObserver(this);
            
            // store the new project reference
            this.project = selectedProject;
            // get the contentManager from the project
            contentManager = project.getContentManger();

            // load the tree data for the new ContentManager into the tree
            internalTreeModel.setRoot(loadTreeData());
           
            // subscribe to notification events for changes to tree structure
            contentManager.addObserver(this);
        }
    }
    
    /**
     * Convert the BLL tree structure into a Jtree treeModel
     * @return Root node of the BLL tree structure as for a treeModel
     */
    private DefaultMutableTreeNode loadTreeData()
    {
        // temporary storage for parent nodes
        Stack<DefaultMutableTreeNode> stack = new Stack();
        
        // temporary node references
        DefaultMutableTreeNode parentNode;
        DefaultMutableTreeNode rootNode;
        
        // get the root node from BLL
        MediaItem topnode = contentManager.getTree();
        
        // create a new Jtree root node
        rootNode = new DefaultMutableTreeNode(topnode);
        
        // push the new node onto the stack to start from this point
        stack.push(rootNode);
        
        // repeat until all parent nodes found have been used
        while(!stack.empty())
        {
            // get the next parent node from the stack
            parentNode =  stack.pop();
            
            // convert to MediaElement and interate over children nodes
            MediaElement element = (MediaElement) parentNode.getUserObject();
            for(MediaItem child : element.getChildren())
            {
                // create and add a new children nodes for each one
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                parentNode.add(childNode);
                
                // check if the child node is also parent node
                if(child instanceof MediaElement)
                {
                    // push it onto the stack for later processing 
                    stack.add(childNode);
                    // flag its properties as a parent node
                    childNode.setAllowsChildren(true);
                }
                else
                {
                    // flag its properties as leaf node
                    childNode.setAllowsChildren(false);
                }
            }
        }
        // return root node of the completed tree structure
        return rootNode;
    }
        
    /**
     * Courses the tree to made visible to the user the node that contains the
     * UserObject passed in
     * @param newNode The UserOject to be make visible
     */
    public void displayUserObject(MediaItem newNode)
    {
        // finds the tree node containing the MediaItem
        DefaultMutableTreeNode newTreeNode = findUserObject(newNode);
        
        // gets the path of the nodes to the found tree node
        TreeNode[] nodes = internalTreeModel.getPathToRoot(newTreeNode);
        
        // converts the nodes into a path
        TreePath path = new TreePath(nodes);
        
        // make the nodes in the path visible
        this.makeVisible(path);
    }
    
    /**
     * Make the tree node passed in selected
     * @param node Tree node to be highlighted as selected
     */
    public void selectTreeNode(DefaultMutableTreeNode node)
    {
        // gets the nodes in the path to the tree node
        TreeNode[] nodes = internalTreeModel.getPathToRoot(node);
        
        // converts the nodes into a path
        TreePath path = new TreePath(nodes);
        
        // sets the selection to the path
        this.setSelectionPath(path);
    }
    
    /**
     *  Creates a new node in the BLL tree
     * @param name The name of node; displayed in the tree and tasklists
     * @param description The longer description of the asset or element used as an explanation to the user
     * @param type The component type to be created by this assets or element
     * @param nodeType The type of node e.g. Asset or Element
     * @param parent The parent node that this is to a child of
     * @return A copy of the newly created object
     */
    public MediaItem addItem(String name, String description, ComponentType type, NodeType nodeType, MediaItem parent)
    {
        return contentManager.addItem(name, description, type, nodeType, parent);
    }
    
    /**
     * The BLL tree node to be removed
     * @param item The MediaItem to be removed
     */
    public void removeItem(MediaItem item)
    {
        contentManager.removeItem(item);
    }
    
    /**
     * The BLL tree node to be removed
     * @param item The MediaItem to be removed
     */
    public void moveItem(DefaultMutableTreeNode newTreeParent, DefaultMutableTreeNode treeChild)
    {
        MediaItem parent = (MediaItem) newTreeParent.getUserObject();
        MediaItem child = (MediaItem) treeChild.getUserObject();
        contentManager.moveItem(parent, child);
    }
    
    /**
     * Receives updates from the ContentManager of modifications the underlying BLL tree structure
     * @param object The object sending the event
     * @param arg The Argument passed with the event
     */
    @Override
    public void update(Observable object, Object arg) 
    {
        // check if event from the ContentManager 
        if (object.equals(contentManager) && arg instanceof ContentEvent)
        {
            // unpack the argument object
            ContentEvent eventArg = (ContentEvent) arg;
            MediaItem parent = eventArg.getParent();
            MediaItem child = eventArg.getChild();
            
            // process the argument payload based upon the Action passed
            switch(eventArg.getAction())
            {
                case ContentEvent.ADD:
                    // add a new node
                    addNode(parent, child);
                    break;
                case ContentEvent.DELETE:
                    // remove a node
                    removeNode(child);
                    break;
                case ContentEvent.UPDATE:
                    // update a node
                    updateNode(child);
                    break;
                case ContentEvent.MOVE:
                    // move a node
                    moveNodes(parent, child);
                    break;
            }            
        }
    }
    
    /**
     * Adds a new node received by an update event to the tree
     * @param parentObj The parent node to have the child added
     * @param child The child node to be added
     */
    private void addNode(MediaItem parentObj, MediaItem child)
    {
        // find the parent tree node that contains the parent UserObject
        DefaultMutableTreeNode parentNode = findUserObject(parentObj);
        
        // update the tree
        // Create a new node for insertion
        DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(child);
        
        // set the appropiate property for the type of new child node
        if (child instanceof MediaElement)
        {
            newTreeNode.setAllowsChildren(true);
        }
        else
        {
            newTreeNode.setAllowsChildren(false);
         }
  
        // add the new node to the tree model
        insertNodeInto(newTreeNode, parentNode);
    }
    
    /**
     * Removes a node from the tree as details received from an update event
     * @param child the node to be removed
     */
    private void removeNode(MediaItem child)
    {
        // find the parent tree node that contains the parent UserObject
        DefaultMutableTreeNode deleteNode = findUserObject(child);
        
        // remove the node from the model
        internalTreeModel.removeNodeFromParent(deleteNode);
    }
    
    /**
     * Updates a node from the tree as details received from an update event
     * @param child the node to be updated
     */
    private void updateNode(MediaItem child)
    {
        // find the parent tree node that contains the parent UserObject
        DefaultMutableTreeNode updateNode = findUserObject(child);
        
        // update the node in the model
        internalTreeModel.nodeChanged(updateNode);
    }
    
    /**
     * Moves Tree node to new parent
     * @param parent new parent to receive tree node
     * @param child tree node to be moved to new parent
     */
    private void moveNodes(MediaItem parent, MediaItem child)
    {
        // find the tree node that contains the child UserObject
        DefaultMutableTreeNode moveNode = findUserObject(child);
        
        // find the tree node that contains the child UserObject
        DefaultMutableTreeNode newparent = findUserObject(parent);
        
        // remove the node from the model
        internalTreeModel.removeNodeFromParent(moveNode);
        
        // add the new node to the tree model
        insertNodeInto(moveNode, newparent);
    }
    
    /**
     * Adds child node to parent node
     * Inserts all leaf nodes before branches
     * Append branches to the end of the branches
     * @param child node to be added
     * @param parent node to have child added
     */
    private void insertNodeInto(DefaultMutableTreeNode child, DefaultMutableTreeNode parent)
    {
        int insertIndex = 0;
        // set the appropiate property for the type of new child node
        if (child.getAllowsChildren())
        {
            insertIndex = parent.getChildCount();
        }
        else
        {
             // set position for insert at the end of the leafs but before the branches
            for(int i = 0; i < parent.getChildCount(); i++)
            {
                if (parent.getChildAt(i).getAllowsChildren())
                {
                    insertIndex = i;
                    break;
                }
            }
        }
        internalTreeModel.insertNodeInto(child, parent, insertIndex);
    }
    
    /**
     * finds the UserObject with the tree model and returns the tree node that contains it
     * @param obj UserObject to be found
     * @return The tree node that contains the object
     */
    private DefaultMutableTreeNode findUserObject(Object obj)
    {
        // get the root node for the tree model
        DefaultMutableTreeNode rootnode = (DefaultMutableTreeNode) internalTreeModel.getRoot();
        
        // create an iterator
        Enumeration e = rootnode.breadthFirstEnumeration();
        
        // iterator over the tree
        while (e.hasMoreElements())
        {
            // get each object and check if it contains the UserObject
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject().equals(obj)) return node;
        }
        // return null if not found
        return null;
    }
}
