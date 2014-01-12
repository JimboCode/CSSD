package UI;

import BLL.ComponentType;
import BLL.MediaElement;
import BLL.MediaItem;
import BLL.NodeType;
import BLL.Project;
import BLL.ProjectRegister;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * UI form used for defining project content
 * 
 * @author James Staite
 */
public class DefineContentUI extends javax.swing.JInternalFrame implements Observer
{
    // reference to the protect current being displayed
    Project project;
    
    // reference to the current selected tree node
    DefaultMutableTreeNode selectedNode;
    
    // internal constants for the current state of the form
    private final int NOTSET = 0;
    private final int CREATING = 1;
    private final int UPDATING = 2;

    // current state of the form
    private int status = NOTSET;
    
    // Anonymous listner for tree selection
    private TreeSelectionListener nodeselection = new TreeSelectionListener(){
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            
            // get the selected node
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) contentTree.getLastSelectedPathComponent();
            
            // check if no node selected
            if (node == null)
            {
                // store no node selected
                selectedNode = null;
                
                // check if form is in the middle of creating or editing a node
                if(status == CREATING || status == UPDATING)
                {
                    // if so cancel the current operation
                    cancelCreation();
                }
                // update the forms display to no node selected
                noNodeSelected();
            }
            else
            {
                // check if form is in the middle of creating or editing a node
                if(status == CREATING || status == UPDATING)
                {
                    // check if the node is not the same node that the current operation is on
                    if (!selectedNode.equals(node))
                    {
                        // if so cancel the current operation
                        cancelCreation();
                    }
                }
                // store the node selected
                selectedNode = node;
                
                // update the forms display with the currently selected node
                nodeSelected(node);
            } 
        }
    };
    
    /**
     * Creates new form DefineContentUI
     */
    public DefineContentUI() 
    {
        // set the form name and enable the close button
        super("Define Project Content",false,true,false,false);
        initComponents();
        
        // setup the tree
        setupTree();
        
        // create the popup menu
        createPopupMenu();
        
        // load the project combo box
        loadProjectCombo();
        
        // load the node type combo box
        loadNodeTypeCombo();
        
        // load the media combo box
        loadMediaCombo();
        
        // initial load the controls state and data
        updatecontrols();
        
        // setup validation action listner events for the required fields used in editing and creation of nodes
        txtName.addCaretListener(new CaretListener() 
        {
            @Override
            public void caretUpdate(CaretEvent e) 
            {
                validiateNodeEntry();
            }
        });
        
        txtDescription.addCaretListener(new CaretListener() 
        {
            @Override
            public void caretUpdate(CaretEvent e) 
            {
                validiateNodeEntry();
            }
        });
        
        // register the form for project update events
        registerEvents();
    }

    /**
     * setup the tree for use
     */
    private void setupTree() 
    {
        // create a content tree
        contentTree = new ContentTree();
        
        // set the selection of nodes to single
        contentTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // connect the selection listner
        contentTree.addTreeSelectionListener(nodeselection);
        
        // add a border and attach to the form
        contentTree.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        spnlPanel.setViewportView(contentTree);
        
        // set up the mouse listener events for right clicking (popup menus)
        contentTree.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                
                // check for a right click
                if(e.getButton() == MouseEvent.BUTTON3)
                {
                    // get the node clicked on
                    TreePath pathForLocation = contentTree.getPathForLocation(e.getX(), e.getY());
                    
                    // check that is was a node
                    if (pathForLocation != null)
                    {
                        // convert the path to a node
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathForLocation.getLastPathComponent();
                        
                        // update the selected tree node
                        contentTree.selectTreeNode(node);
                        
                        // display popup menu
                        popupMenu.show(contentTree, e.getX(), e.getY());
                    }
                }
            }
        });
        
        // turn on drap and drop
        contentTree.setDragEnabled(true);
    }
    
    /**
     * Creates and sets up the popup menus
     */
    private void createPopupMenu()
    {
        // create new menu
        popupMenu = new JPopupMenu();
        
        // add delete option and set it action to call delete
        deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete();
            }
        });
        popupMenu.add(deleteMenuItem);
        
        // add add option and set it action to call add
        newMenuItem = new JMenuItem("Add");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startCreation();
            }
        });
        popupMenu.add(newMenuItem);
        
        // add edit option and set it action to call edit
        editMenuItem = new JMenuItem("Edit");
        editMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startEditing();
            }
        });
        popupMenu.add(editMenuItem);          
    }
    
    /**
     * Loads project combo box with projects from the ProjectRegister
     */
    private void loadProjectCombo() {
        // get ref to project register
        ProjectRegister proReg = ProjectRegister.getInstance();
        
        // create a new model
        DefaultComboBoxModel projectComboModel = new DefaultComboBoxModel();
        
        // load the model and set the combo box to the new model
        for(Project eachProject: proReg.getProjectList())
        {
            projectComboModel.addElement(eachProject);
        }
        cmbProject.setModel(projectComboModel);
    }
    
    /**
     * Loads node type combo box with types from the Enum
     */
    private void loadNodeTypeCombo()
    {
        // create a new model
        DefaultComboBoxModel nodeTypeComboModel = new DefaultComboBoxModel();
        
         // load the model and set the combo box to the new model
        for(NodeType type: NodeType.values())
        {
            nodeTypeComboModel.addElement(type);
        }
        cmbNodeType.setModel(nodeTypeComboModel);
    }
    
    /**
     * Loads media type combo box with types from the Enum
     */
    private void loadMediaCombo()
    {
        // create a new model
        DefaultComboBoxModel nodeTypeComboModel = new DefaultComboBoxModel();
        
         // load the model and set the combo box to the new model
        for(ComponentType type: ComponentType.values())
        {
            nodeTypeComboModel.addElement(type);
        }
        cmbMediaType.setModel(nodeTypeComboModel);
    }
    
    // updates the forms controls based upon the project in the project combo box
    private void updatecontrols()
    {
        // get project
        Project selectedProject = (Project)cmbProject.getSelectedItem();
        
        // set the project on the content tree
        contentTree.setProject(selectedProject);
    }


    /**
     * Function used to enable the edit menu and edit button to the same state
     * @param value true or false state
     */
    private void setEditControls(boolean value)
    {
        btnEdit.setEnabled(value);
        editMenuItem.setEnabled(value);
    }
    
    /**
     * Function used to enable the new menu and edit button to the same state
     * @param value true or false state
     */
    private void setNewControls(boolean value)
    {
        btnNew.setEnabled(value);
        newMenuItem.setEnabled(value);
    }
    
    /**
     * Function used to enable the delete menu and edit button to the same state
     * @param value true or false state
     */
    private void setDeleteControls(boolean value)
    {
        btnDelete.setEnabled(value);
        deleteMenuItem.setEnabled(value);
    }
    
    /**
     * Clear the node editing and creating controls to a blank state
     */
    private void resetNodeEntryControls()
    {
        txtName.setText("");
        txtDescription.setText("");
        cmbNodeType.setSelectedItem(NodeType.ASSET);
        cmbMediaType.setSelectedIndex(0);
    }
    
    /**
     * sets the form state when no node is selected
     */
    private void noNodeSelected()
    {
        btnCreate.setEnabled(false);
        setEditControls(false);
        btnCancel.setEnabled(false);
        setNewControls(false);
        setDeleteControls(false);
        resetNodeEntryControls();
    }
    
    /**
     * Sets the form state when a node is selected
     * @param node The node selected - this is passed in by the node selection listener
     */
    private void nodeSelected(DefaultMutableTreeNode node)
    {
        // check that a valid node has been passed in
        if (node != null)
        {
            // check is the node is the root node and set the form appropriately
            // root cannot be deleted or edited
            if (node.isRoot())
            {
                btnCreate.setEnabled(false);
                btnCancel.setEnabled(false);
                btnNew.setEnabled(true);
                newMenuItem.setEnabled(true);
                setDeleteControls(false);
                setEditControls(false);
                resetNodeEntryControls();
            }
            else
            {
                // if not the root node populate the field readly for editing
                MediaItem mediaItem = (MediaItem) node.getUserObject();
                txtName.setText(mediaItem.getName());
                txtDescription.setText(mediaItem.getDescription());
                btnCreate.setEnabled(false);
                btnCancel.setEnabled(false);
                setEditControls(true);
                
                // TODO only enable if the node and any child does not have any tasks
                setDeleteControls(true);
                
                // set the node type combo box based upon the node selected
                if(mediaItem instanceof MediaElement)
                {
                    cmbNodeType.setSelectedItem(NodeType.ELEMENT);
                    // set controls so a user could add child
                    setNewControls(true);
                }
                else
                {
                    cmbNodeType.setSelectedItem(NodeType.ASSET);
                    // set controls so a user could not add child
                    setNewControls(false);
                }
            }  
        }
        
    }
    
    /**
     * Initiates the creation of a new node
     */
    private void startCreation()
    {
        // check if in the middle of an exiting operation 
        if (status == CREATING || status == UPDATING)
        {
            // if so cancel the existing operation
            cancelCreation();
        }
        
        // set the status to the current operation
        status = CREATING;
        
        // update the finalise button text
        btnCreate.setText("Create");
        
        // clear the controls
        resetNodeEntryControls();
        
        // enabled controls
        cmbNodeType.setEnabled(true);
        txtName.setEnabled(true);
        txtDescription.setEnabled(true);
        cmbMediaType.setEnabled(true);
        
        // update buttons
        setNewControls(false);
        btnCancel.setEnabled(true);
        
        // set focus
        txtName.requestFocusInWindow();
    }
    
    /**
     * Cancel a creations operation and reset the controls
     */
    private void cancelCreation()
    {
        // disable controls
        cmbNodeType.setEnabled(false);
        txtName.setEnabled(false);
        txtDescription.setEnabled(false);
        cmbMediaType.setEnabled(false);
        
        // reset buttons
        btnCreate.setEnabled(false);
        setNewControls(true);
        btnCancel.setEnabled(false);
        
        // set status
        status = NOTSET;
        nodeSelected(selectedNode);
    }
    
    /**
     * finalise the creation or update operation
     */
    private void finaliseCreation()
    {
        // check if the current operation is creating
        if(status == CREATING)
        {
            // Get the information to create the new object
            NodeType nodeType = (NodeType)cmbNodeType.getSelectedItem();
            MediaItem parentNode = (MediaItem) selectedNode.getUserObject();
            
            // create the new node
            MediaItem newNode = contentTree.addItem(txtName.getText(), txtDescription.getText(), (ComponentType) cmbMediaType.getSelectedItem(), nodeType, parentNode);
            
            // get the display to display the new node
            contentTree.displayUserObject(newNode);
        }
        else
        {
            // get the user object from the selected node
            MediaItem node = (MediaItem) selectedNode.getUserObject();
            
            // update the user objects information
            node.setName(txtName.getText());
            node.setDescription(txtDescription.getText());
            
            // request the tree updates the node
            contentTree.updateItem(node);
        }
                
        // disable controls
        cmbNodeType.setEnabled(false);
        txtName.setEnabled(false);
        txtDescription.setEnabled(false);
        cmbMediaType.setEnabled(false);
        
        // Update Controls
        nodeSelected(selectedNode);
    }
    
    /**
     * Initiates the editing of a new node
     */
    private void startEditing()
    {
        // check if in the middle of an exiting operation
        if (status == CREATING || status == UPDATING)
        {
            // if so cancel the existing operation
            cancelCreation();
        }
        
        // set the status to the current operation
        status = UPDATING;
        
        // update the finalise button text
        btnCreate.setText("Update");
        
        // enabled controls
        txtName.setEnabled(true);
        txtDescription.setEnabled(true);
        
        //TODO only enable if the item does not have any tasks
        cmbMediaType.setEnabled(true);
        
        // update buttons
        setNewControls(false);
        setEditControls(false);
        btnCancel.setEnabled(true);
        setDeleteControls(false);
        
        // set focus
        txtName.requestFocusInWindow();
    }
    
    /**
     * Delete the currently selected node
     */
    private void delete()
    {
        // get the UserObject from the tree
        MediaItem deleteNode = (MediaItem) selectedNode.getUserObject();
        
        // request the node is removed
        contentTree.removeItem(deleteNode);
        
        // clear the currently selected node
        selectedNode = null;
        noNodeSelected();
    }
    
    /**
     * Validate the controls for the creation or editing of a node
     * fired by listener events
     */
    private void validiateNodeEntry()
    {
        // assume state is valid
        boolean invalid = false;
        
        // check project elementName entered
        if (txtName.getText().length() == 0)
        {
            // invalid form state 
            invalid = true;
        } 
        
        // check disc title entered
        if (txtDescription.getText().length() == 0)
        {
            // invlaid form state
            invalid = true;
        }
        
        // enable create button if form valid
        if (invalid)
        {
            btnCreate.setEnabled(false);
        }
        else
        {
            btnCreate.setEnabled(true);
        }
    }
    
    
    /**
     * Receives update events from ProjectRegister for the addition or removal of projects
     * @param object Object sending the event
     * @param arg arguments passed with the event
     */
    @Override
    public void update(Observable object, Object arg) 
    {
        // check to see if the object is the RrojectRegister
        if (object instanceof ProjectRegister)
        {
            // Events only rasied for additions of omission of projects
            // reload the avaliable projects
            loadProjectCombo();
        }
    }
    
    /**
     * Register this form to receive observer events when projects are added or removed
     */
    private void registerEvents()
    {
        // get reference to project register and register for events
        ProjectRegister proReg = ProjectRegister.getInstance();
        proReg.addObserver(this);
    }

    /**
     * Register this form to receive observer events when projects are added or removed
     */
    private void deregisterEvents()
    {
        // get reference to project register and register for events
        ProjectRegister proReg = ProjectRegister.getInstance();
        proReg.deleteObserver(this);
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCloseForm = new javax.swing.JButton();
        cmbProject = new javax.swing.JComboBox();
        lblSelectProject = new javax.swing.JLabel();
        spnlPanel = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        lblType = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        cmbNodeType = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cmbMediaType = new javax.swing.JComboBox();
        btnDelete = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();

        btnCloseForm.setText("Close");
        btnCloseForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseFormActionPerformed(evt);
            }
        });

        cmbProject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Element", "Assets" }));
        cmbProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProjectActionPerformed(evt);
            }
        });

        lblSelectProject.setText("Select Project");

        spnlPanel.setBorder(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Details"));
        jPanel1.setName(""); // NOI18N

        lblType.setText("Type");

        lblName.setText("Name");

        lblDescription.setText("Description");

        txtName.setEnabled(false);

        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setEnabled(false);
        jScrollPane1.setViewportView(txtDescription);

        cmbNodeType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Element", "Asset" }));
        cmbNodeType.setEnabled(false);

        jLabel4.setText("Media");

        cmbMediaType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbMediaType.setEnabled(false);

        btnDelete.setText("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnNew.setText("New");
        btnNew.setEnabled(false);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnCreate.setText("Create");
        btnCreate.setEnabled(false);
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCreate))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblName)
                                    .addComponent(lblType))
                                .addGap(31, 31, 31)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtName)
                                    .addComponent(cmbNodeType, 0, 258, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDescription)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                    .addComponent(cmbMediaType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblType)
                    .addComponent(cmbNodeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbMediaType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete)
                    .addComponent(btnNew)
                    .addComponent(btnCancel)
                    .addComponent(btnCreate)
                    .addComponent(btnEdit))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spnlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCloseForm)
                        .addGap(19, 19, 19))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblSelectProject)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnlPanel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSelectProject))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 188, Short.MAX_VALUE)
                        .addComponent(btnCloseForm)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseFormActionPerformed
        // de-registers events for this form and close it
        deregisterEvents();
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCloseFormActionPerformed

    private void cmbProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProjectActionPerformed
        updatecontrols();
    }//GEN-LAST:event_cmbProjectActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        finaliseCreation();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelCreation();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        startCreation();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        startEditing();
    }//GEN-LAST:event_btnEditActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCloseForm;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnNew;
    private javax.swing.JComboBox cmbMediaType;
    private javax.swing.JComboBox cmbNodeType;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSelectProject;
    private javax.swing.JLabel lblType;
    private javax.swing.JScrollPane spnlPanel;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
    // Menu variables
    private JPopupMenu popupMenu;
    private JMenuItem deleteMenuItem;
    private JMenuItem newMenuItem;
    private JMenuItem editMenuItem;
    // Tree variable
    private ContentTree contentTree;
}
