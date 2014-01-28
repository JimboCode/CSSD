package UI;

import BLL.MediaAsset;
import BLL.MediaElement;
import BLL.MediaItem;
import BLL.MediaStatus;
import BLL.Project;
import BLL.ProjectRegister;
import BLL.Worker;
import BLL.WorkerRegister;
import BLL.WorkerRoles;
import BLL.WorkerType;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Defines initial tasks for obtaining the initial media in the content tree
 * 
 * @author James Staite
 */
public class OrderContentTasksUI extends javax.swing.JInternalFrame implements Observer
{
    // User details
    Worker user;
    
    // The currently selected project
    Project selectedProject;
    
    // selected mediaItems
    ArrayList<MediaItem> selectedNodes = new ArrayList();
    
    // reference to the current selected tree node
    MediaItem commonSelectedNode;
    
    // Allocate to list of workers
    ArrayList<Worker> allocateToList = new ArrayList();
    
    // Anonymous listner for tree selection
    private TreeSelectionListener nodeselection = new TreeSelectionListener(){
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            nodesSelected();
        }
    };
    
    /**
     * Creates new form OrderContentTasksUI
     */
    public OrderContentTasksUI(Worker user) 
    {
        // set the form name and enable the close button
        super("Order Project Content",false,true,false,false);
        initComponents();
        this.user = user;
        
        // setup the tree
        setupTree();
        
        // load the project combo box
        loadProjectCombo();
        
        // initial load the controls state and data
        updatecontrols();
        
        setControlsEnabled(false);
        btnCreateTask.setEnabled(false);
        
        // register for project update events
        registerEvents();
        
        txtTaskDescription.addCaretListener(new CaretListener() 
        {
            @Override
            public void caretUpdate(CaretEvent e) 
            {
                if(txtTaskDescription.getText().length() > 0 && txtTaskDescription.isEnabled())
                {
                    btnCreateTask.setEnabled(true);
                }
                else
                {
                    btnCreateTask.setEnabled(false);
                }
            }
        });
    }
    
    /**
     * setup the tree for use
     */
    private void setupTree() 
    {
        // create a content tree
        contentTree = new ContentTree();
        
        // set the selection of nodes to single
        contentTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        
        // connect the selection listner
        contentTree.addTreeSelectionListener(nodeselection);
        
        // add a border and attach to the form
        contentTree.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        spnlPanel.setViewportView(contentTree);   
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
        for(Project eachProject: proReg.getProjectList(user))
        {
            projectComboModel.addElement(eachProject);
        }
        cmbProject.setModel(projectComboModel);
    }
    
    /**
     *  updates the forms controls based upon the project in the project combo box
     */
    private void updatecontrols()
    {
        // get project
        selectedProject = (Project)cmbProject.getSelectedItem();
        
        // set the project on the content tree
        contentTree.setProject(selectedProject);
    }
    
    /**
     * processes the selected nodes in the content tree 
     * Detects if the selection is valid and tracks the selected nodes
     */
    private void nodesSelected()
    {
        // the paths of the selected nodes in the tree
        TreePath[] paths = contentTree.getSelectionPaths();
        
        // flags if all the nodes meet the criteria
        boolean nodesInvalid = false;
        
        // flags if all the node have the same media type
        boolean mediaTypeSameFlag = true;
        
        // temp pointer for the current node
        MediaItem thisItem;
        
        // clear the previously stored list of nodes
        selectedNodes.clear();
        
        if(paths != null)
        {
            // get the first node and use it as the stand to judge all over nodes by
            MediaItem standardNode = getUserObject(paths[0]);
            
            // iterate over the selected nodes
            for (TreePath path : paths) 
            {
                // convert node to it user object
                thisItem = getUserObject(path);
                
                // check that the node is not an element or media source is different or status is different
                if (thisItem instanceof MediaElement || standardNode.getMediaSource() != thisItem.getMediaSource() || standardNode.getStatus() != thisItem.getStatus())
                {
                        // if failed test then flag the selection of nodes invalid & end loop
                        nodesInvalid = true;
                        break;
                }
                
                // monitor if all the nodes share the same media types
                if (standardNode.getMediaType() != thisItem.getMediaType()) mediaTypeSameFlag = false;
                
                // add the valid node the selected list
                selectedNodes.add(thisItem);
            }
            
            // if the selection of nodes is invalid
            if (nodesInvalid) 
            {
                // clear the selection and blank the text fields
                selectedNodes.clear();
                txtName.setText("");
                txtDescription.setText("");
                txtMediaSource.setText("");
                txtMediaType.setText("");
                txtStatus.setText("");
                
                // disable the controls
                setControlsEnabled(false);
                clearControls();
            }
            else
            {
                // store the standard to which all the node have been judged
                commonSelectedNode = standardNode;
                
                // if the total number of selected nodes is 1
                if (selectedNodes.size() == 1)
                {
                    // display the singles nodes name and description
                    txtName.setText(standardNode.getName());
                    txtDescription.setText(standardNode.getDescription());
                }
                else
                {
                    // blank out the name and description because these will be different across
                    // mulitple nodes
                    txtName.setText("");
                    txtDescription.setText("");
                }
                
                // if the media type is the same for all nodes display it
                if (mediaTypeSameFlag == true) 
                {
                    txtMediaType.setText(standardNode.getMediaType().toString());
                }
                else
                {
                    txtMediaType.setText("");
                }
                
                // display the shared media source and status
                txtMediaSource.setText(standardNode.getMediaSource().toString());
                txtStatus.setText(standardNode.getStatus().toString());
                
                // only enable the creation of tasks for Asset nodes
                if(standardNode.getStatus() == MediaStatus.AWAITING_ACTION)
                {
                    // enable the controls
                    setControlsEnabled(true);
                    clearControls();
                    
                    // load avaliable status changes
                    loadNewStatus();
                    
                    // load the possible worker roles
                    loadWorkerRoles();
                    
                    // load possible allocate to list
                    loadAllocatedTo();
                }
                else
                {
                    // disable the controls
                    setControlsEnabled(false);
                    clearControls();
                }
            }
        }
    }
    
    /**
     * Converts and tree path into a user object
     * @param path path to convert
     * @return user object at the end of the path
     */
    private MediaItem getUserObject(TreePath path)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        return (MediaItem) node.getUserObject();
    }
    
    /**
     * Loads the valid status changes for the selected nodes based upon it current state and the users abilities
     */
    private void loadNewStatus()
    {
        // create a new model
        DefaultComboBoxModel NewStatusComboModel = new DefaultComboBoxModel();
        
        // get valid actions for the new task and load into the combo box
        MediaStatus[] nextActions = commonSelectedNode.getValidStatusOptions(user);
        for(MediaStatus action: nextActions)
        {
            NewStatusComboModel.addElement(action);
            
        }
        cmbTaskNewStatus.setModel(NewStatusComboModel);
        
        // if only a sign option disable further selection
        if (nextActions.length == 1)
        {
            cmbTaskNewStatus.setEnabled(false);
        }
    }
    
    /**
     * Loads the valid worker roles for the selected status proposed for the task
     */
    private void loadWorkerRoles()
    {
        // create a new model
        DefaultComboBoxModel workerRoleComboModel = new DefaultComboBoxModel();
        MediaStatus action = (MediaStatus) cmbTaskNewStatus.getSelectedItem();
        
        // get valid work roles for the new task and load into the combo box
        WorkerRoles[] workerRoles = commonSelectedNode.getValidAllocateWorkRoles(action);
        for(WorkerRoles workerRole: workerRoles)
        {
            workerRoleComboModel.addElement(workerRole);
        }
        cmbTaskWorkerRole.setModel(workerRoleComboModel);
        
        // if only a sign option disable further selection
        if (workerRoles.length == 1)
        {
            cmbTaskWorkerRole.setEnabled(false);
        }
    }
    
    /**
     * load workers that match the Work role selected
     */
    private void loadAllocatedTo()
    {
        // create a new model
        DefaultComboBoxModel workerRoleComboModel = new DefaultComboBoxModel();
        WorkerRoles workerRole = (WorkerRoles) cmbTaskWorkerRole.getSelectedItem();
        allocateToList.clear();
        
        switch (workerRole)
        {
            case CONTRACTOR:
            {
                // get a list of contractors
                WorkerRegister workReg = WorkerRegister.getInstance();
                allocateToList.addAll(workReg.findByRole(WorkerRoles.CONTRACTOR, WorkerType.CONTRACTOR));
                break;
            }
            case CLIENT:
            {
                // make this the only option as the client is the only choice
                allocateToList.add((Worker)selectedProject.getClient());
                break;
            }
            default:
            {
                // Team members
                // Add a blank option for if not allocated to any idividual
                allocateToList.add(null);
                
                // add to the list worker that match the work role
                allocateToList.addAll(selectedProject.findWorkersByRole(workerRole));
            }
        }
        
        // load into the combo box
        for(Worker worker: allocateToList)
        {
            workerRoleComboModel.addElement(worker);
        }
        cmbTaskAllocatedTo.setModel(workerRoleComboModel);
    }
    
    /**
     * Enable and disable the form controls
     * @param value boolean value
     */
    private void setControlsEnabled(boolean value)
    {
        txtTaskDescription.setEnabled(value);
        cmbTaskNewStatus.setEnabled(value);
        cmbTaskWorkerRole.setEnabled(value);
        cmbTaskAllocatedTo.setEnabled(value);
        cmbPriority.setEnabled(value);
    }
    
    /**
     * Reset / clear the form controls
     */
    private void clearControls()
    {
        txtTaskDescription.setText("");
        cmbTaskNewStatus.setModel(new DefaultComboBoxModel());
        cmbTaskWorkerRole.setModel(new DefaultComboBoxModel());
        cmbTaskAllocatedTo.setModel(new DefaultComboBoxModel());
        cmbPriority.setSelectedIndex(0);
    }
   
    /**
     * Handle the create button clicks
     */
    private void btnCreateTask()
    {
        // collect new information
        String taskDescription = txtTaskDescription.getText();
        MediaStatus newstatus = (MediaStatus) cmbTaskNewStatus.getSelectedItem();
        WorkerRoles workerRole = (WorkerRoles) cmbTaskWorkerRole.getSelectedItem();
        Worker allocatedTo = (Worker) cmbTaskAllocatedTo.getSelectedItem();
        int priority = Integer.parseInt((String)cmbPriority.getSelectedItem());
        
        // update all the selected nodes status with collected information
        for(MediaItem item: selectedNodes)
        {
            item.setStatus(newstatus, user, taskDescription, workerRole, allocatedTo, priority);
        }
                
        // Clear selection 
        contentTree.clearSelection();
        // disable & clear the controls
        setControlsEnabled(false);
        clearControls();
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

        spnlPanel = new javax.swing.JScrollPane();
        lblSelectProject = new javax.swing.JLabel();
        cmbProject = new javax.swing.JComboBox();
        pnlContentDetails = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        txtMediaSource = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtMediaType = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        pnlTaskCreation = new javax.swing.JPanel();
        lblTaskDescription = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtTaskDescription = new javax.swing.JTextArea();
        lblWorkRole = new javax.swing.JLabel();
        lblAllocatedTo = new javax.swing.JLabel();
        cmbTaskAllocatedTo = new javax.swing.JComboBox();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox();
        cmbTaskWorkerRole = new javax.swing.JComboBox();
        btnCreateTask = new javax.swing.JButton();
        lblNewStatus = new javax.swing.JLabel();
        cmbTaskNewStatus = new javax.swing.JComboBox();
        btnCloseForm = new javax.swing.JButton();

        spnlPanel.setBorder(null);

        lblSelectProject.setText("Select Project");

        cmbProject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProjectActionPerformed(evt);
            }
        });

        pnlContentDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Content Details"));

        jLabel1.setText("Name");

        jLabel2.setText("Description");

        txtName.setEnabled(false);
        txtName.setFocusable(false);

        jScrollPane1.setEnabled(false);

        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setEnabled(false);
        txtDescription.setFocusable(false);
        jScrollPane1.setViewportView(txtDescription);

        jLabel3.setText("Media Source");

        txtMediaSource.setEnabled(false);
        txtMediaSource.setFocusable(false);

        jLabel4.setText("Media Type");

        txtMediaType.setEnabled(false);
        txtMediaType.setFocusable(false);

        lblStatus.setText("Status");

        txtStatus.setEnabled(false);

        javax.swing.GroupLayout pnlContentDetailsLayout = new javax.swing.GroupLayout(pnlContentDetails);
        pnlContentDetails.setLayout(pnlContentDetailsLayout);
        pnlContentDetailsLayout.setHorizontalGroup(
            pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblStatus)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName)
                    .addComponent(jScrollPane1)
                    .addComponent(txtMediaSource)
                    .addComponent(txtMediaType)
                    .addComponent(txtStatus))
                .addContainerGap())
        );
        pnlContentDetailsLayout.setVerticalGroup(
            pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentDetailsLayout.createSequentialGroup()
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlContentDetailsLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 64, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtMediaSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMediaType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus))
                .addGap(0, 0, 0))
        );

        pnlTaskCreation.setBorder(javax.swing.BorderFactory.createTitledBorder("Define Task for Content"));

        lblTaskDescription.setText("Description");

        txtTaskDescription.setColumns(20);
        txtTaskDescription.setLineWrap(true);
        txtTaskDescription.setRows(5);
        txtTaskDescription.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtTaskDescription);

        lblWorkRole.setText("For Work Role");

        lblAllocatedTo.setText("Allocated To");

        cmbTaskAllocatedTo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblPriority.setText("Priority");

        cmbPriority.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        cmbTaskWorkerRole.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTaskWorkerRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTaskWorkerRoleActionPerformed(evt);
            }
        });

        btnCreateTask.setText("Create Task");
        btnCreateTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateTaskActionPerformed(evt);
            }
        });

        lblNewStatus.setText("Action");

        cmbTaskNewStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTaskNewStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTaskNewStatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlTaskCreationLayout = new javax.swing.GroupLayout(pnlTaskCreation);
        pnlTaskCreation.setLayout(pnlTaskCreationLayout);
        pnlTaskCreationLayout.setHorizontalGroup(
            pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTaskCreationLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPriority)
                    .addComponent(lblWorkRole)
                    .addComponent(lblTaskDescription)
                    .addComponent(lblNewStatus)
                    .addComponent(lblAllocatedTo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                    .addComponent(cmbTaskAllocatedTo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlTaskCreationLayout.createSequentialGroup()
                        .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCreateTask))
                    .addComponent(cmbTaskWorkerRole, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbTaskNewStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlTaskCreationLayout.setVerticalGroup(
            pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTaskCreationLayout.createSequentialGroup()
                .addGroup(pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTaskDescription)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNewStatus)
                    .addComponent(cmbTaskNewStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblWorkRole)
                    .addComponent(cmbTaskWorkerRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbTaskAllocatedTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAllocatedTo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTaskCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPriority)
                    .addComponent(btnCreateTask))
                .addGap(9, 9, 9))
        );

        btnCloseForm.setText("Close");
        btnCloseForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseFormActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spnlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(pnlTaskCreation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(lblSelectProject)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cmbProject, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(pnlContentDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnCloseForm))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSelectProject)
                            .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlContentDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlTaskCreation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCloseForm)
                        .addGap(0, 28, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(spnlPanel)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProjectActionPerformed
        updatecontrols();
    }//GEN-LAST:event_cmbProjectActionPerformed

    private void cmbTaskNewStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTaskNewStatusActionPerformed
        loadWorkerRoles();
    }//GEN-LAST:event_cmbTaskNewStatusActionPerformed

    private void cmbTaskWorkerRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTaskWorkerRoleActionPerformed
        loadAllocatedTo();
    }//GEN-LAST:event_cmbTaskWorkerRoleActionPerformed

    private void btnCloseFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseFormActionPerformed
        // de-registers events for this form and close it
        deregisterEvents();
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCloseFormActionPerformed

    private void btnCreateTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateTaskActionPerformed
        btnCreateTask();
    }//GEN-LAST:event_btnCreateTaskActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseForm;
    private javax.swing.JButton btnCreateTask;
    private javax.swing.JComboBox cmbPriority;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JComboBox cmbTaskAllocatedTo;
    private javax.swing.JComboBox cmbTaskNewStatus;
    private javax.swing.JComboBox cmbTaskWorkerRole;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAllocatedTo;
    private javax.swing.JLabel lblNewStatus;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblSelectProject;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTaskDescription;
    private javax.swing.JLabel lblWorkRole;
    private javax.swing.JPanel pnlContentDetails;
    private javax.swing.JPanel pnlTaskCreation;
    private javax.swing.JScrollPane spnlPanel;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtMediaSource;
    private javax.swing.JTextField txtMediaType;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextArea txtTaskDescription;
    // End of variables declaration//GEN-END:variables
    // Tree variable
    private ContentTree contentTree;
}
