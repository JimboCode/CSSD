package UI;

import BLL.MediaItem;
import BLL.MediaStatus;
import BLL.Project;
import BLL.ProjectRegister;
import BLL.TaskItem;
import BLL.TaskList;
import BLL.TaskStatus;
import BLL.Worker;
import BLL.WorkerRegister;
import BLL.WorkerRoles;
import BLL.WorkerType;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.EventJXTableModel;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * QC Team Leaders task management UI form
 * @author James Staite
 */
public class QCTeamLeaderUI extends javax.swing.JInternalFrame implements Observer
{
    // reference to the user of this form
    private Worker user;
    
    // reference to the Observered Task List
    private EventList<TaskItem> observedTasks;
    
    // Table model
    private EventJXTableModel AllTableModel;
    
    // selected task items
    private ArrayList<TaskItem> selectedTasks = new ArrayList();
    
    // Allocate to list of workers
    private ArrayList<Worker> allocateToList = new ArrayList();
    
    // The standard Media Item that all other selectedTask item match
    private MediaItem mediaItemStandard;
    
    // current QC Report
    private TaskItem task;
    
    // matcher editor for filtering the task list
    private QCTeamLeaderUI.StatusMatcherEditor matcherEditor;
    
    // flags that that the selected tasks from the table are being updated
    // and to enough the value changed event
    boolean updatingFlag = false;
    
    /**
     * Creates new form QCTeamLeaderUI
     */
    public QCTeamLeaderUI(Worker user) 
    {
        super("Tasks Management",false,true,false,false);
        initComponents();
        
        // store user details
        this.user = user;
        
        // load filter task list combo box
        loadFilterTaskListCombo();
                
        // load project into combo box
        loadProjectCombo();
        
        // setup table
        setupTable((Project)cmbProject.getSelectedItem());
        
        // setup the selection setting for the table
        tblTaskList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblTaskList.setRowSelectionAllowed(true);
        tblTaskList.setColumnSelectionAllowed(false);
        
        // register for project update events
        registerEvents();
        
        // initialise form
        projectSelected();
        
        tblTaskList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // possible row selection
                if (updatingFlag == false)
                {
                    if (!event.getValueIsAdjusting()) rowselection();
                }                
            }
        });
        
        txtNTaskDescription.addCaretListener(new CaretListener() 
        {
            @Override
            public void caretUpdate(CaretEvent e) 
            {
                validateNTask();
            }
        });
    }
    
    /**
     * validates new task controls
     */
    private void validateNTask()
    {
        boolean valid = true;
        
        // check each of the controls state
        if(!txtNTaskDescription.isEnabled() || txtNTaskDescription.getText().length() == 0) valid = false;
                
        if (cmbNTaskAction.getSelectedItem() == null) valid = false;
        
        if (cmbNtaskWorkerRole.getSelectedItem() == null) valid = false;
        
        // enable or disable the create task button based upon validation
        if (valid) 
        {
            btnCreateTask.setEnabled(true);
        }
        else
        {
            btnCreateTask.setEnabled(false);
        }
    }
    
    /**
     * handle the row selection process and store a list of the rows selected in
     * selectedTasks. Also toggles the forms controls state
     */
    private void rowselection()
    {
        // flags if all the tasks selected have the same status
        boolean contentNameSame = true;
        boolean contentDescriptionSame = true;
        boolean mediaTypeSame = true;
        boolean taskDescriptionSame = true;
        
        // temporary reference to task being processed
        TaskItem thisItem;
        
        // get all rows selected
        int[] selectedRows= tblTaskList.getSelectedRows();
        
        // clear the previous list of rows
        selectedTasks.clear();
        
        //check if any items are selected
        if (selectedRows.length > 0)
        {
            // hold the first item to use for comparison
            TaskItem taskStandard = (TaskItem) AllTableModel.getElementAt(selectedRows[0]);
            mediaItemStandard = taskStandard.getMediaItem();
            
            // multiple item can only be selected if their status is Awaiting action
            for (int index : selectedRows)
            {
                thisItem = (TaskItem) AllTableModel.getElementAt(index);
                
                // get the status of the item is AWAITING_ACTION if multiple items selected
                if (thisItem.getStatus() != taskStandard.getStatus())
                {
                    // then empty list and stop checking as the items with different status cannot be processed together
                    selectedTasks.clear();
                    break;
                }
                
                // check which fields are common amongst the selected rows
                MediaItem thisMediaItem = thisItem.getMediaItem();
                
                // flag items that are not shared
                if(!mediaItemStandard.getName().equals(thisMediaItem.getName())) contentNameSame = false;
                if(!mediaItemStandard.getDescription().equals(thisMediaItem.getDescription())) contentDescriptionSame = false;
                if(mediaItemStandard.getMediaType() != thisMediaItem.getMediaType()) mediaTypeSame = false;
                if(taskStandard.getDescription() != thisItem.getDescription()) taskDescriptionSame = false;
                selectedTasks.add(thisItem);
            }
            
            // update the controls
            if (selectedTasks.isEmpty())
            {
                // either not rows selected or not a usable group selected
                // clear form controls
                clearOTaskControls();
                setCreateTaskControlsEnabled(false);
            } else
            {
                // only display items that are common to all selected rows
                if (contentNameSame) txtOTaskContentName.setText(mediaItemStandard.getName()); else txtOTaskContentName.setText("");
                if (contentDescriptionSame) txtOTaskContentDescription.setText(mediaItemStandard.getDescription()); else txtOTaskContentDescription.setText("");
                if (mediaTypeSame) txtOTaskMediaRequired.setText(mediaItemStandard.getMediaType().toString()); else txtOTaskMediaRequired.setText("");
                if (taskDescriptionSame) txtOTaskDescription.setText(taskStandard.getDescription()); else txtOTaskDescription.setText("");
                
                txtOTaskContentStatus.setText(mediaItemStandard.getStatus().toString());
                
                if(selectedTasks.size() == 1)
                {
                    if (taskStandard.getQCReport() != null)
                    {
                        btnViewQCReport.setEnabled(true);
                        task = taskStandard;
                    }
                }
                else
                {
                    btnViewQCReport.setEnabled(false);
                    task = null;
                }
                
                // get the premitted actions for the selected task(s)
                // enable the controls
                setCreateTaskControlsEnabled(true);
                clearNTaskControls();
                    
                // load avaliable status changes
                loadNewStatus();
            }
        }
        else
        {
            // no rows selected
            clearOTaskControls();
            setCreateTaskControlsEnabled(false);
            clearNTaskControls();
        }
    }
    
    /**
     * Loads the valid status changes for the selected nodes based upon it current state and the users abilities
     */
    private void loadNewStatus()
    {
        // create a new model
        DefaultComboBoxModel NewStatusComboModel = new DefaultComboBoxModel();
        MediaStatus[] nextActions = mediaItemStandard.getValidStatusOptions(user);
        for(MediaStatus action: nextActions)
        {
            NewStatusComboModel.addElement(action);
            
        }
        cmbNTaskAction.setModel(NewStatusComboModel);
        if (nextActions.length == 1)
        {
            cmbNTaskAction.setEnabled(false);
        }
        loadWorkerRoles();
    }
    
    /**
     * Load valid worker roles for the given action
     */
    private void loadWorkerRoles()
    {
        // create a new model
        DefaultComboBoxModel workerRoleComboModel = new DefaultComboBoxModel();
        MediaStatus action = (MediaStatus) cmbNTaskAction.getSelectedItem();
        WorkerRoles[] workerRoles = mediaItemStandard.getValidAllocateWorkRoles(action);
        for(WorkerRoles workerRole: workerRoles)
        {
            workerRoleComboModel.addElement(workerRole);
        }
        cmbNtaskWorkerRole.setModel(workerRoleComboModel);
        if (workerRoles.length == 1)
        {
            cmbNtaskWorkerRole.setEnabled(false);
        }
        loadAllocatedTo();
    }
    
    /**
     * Loads workers that meet the worker role selected
     */
    private void loadAllocatedTo()
    {
        // create a new model
        DefaultComboBoxModel workerRoleComboModel = new DefaultComboBoxModel();
        WorkerRoles workerRole = (WorkerRoles) cmbNtaskWorkerRole.getSelectedItem();
        allocateToList.clear();
        
        switch (workerRole)
        {
            case CONTRACTOR:
            {
                WorkerRegister workReg = WorkerRegister.getInstance();
                allocateToList.addAll(workReg.findByRole(WorkerRoles.CONTRACTOR, WorkerType.CONTRACTOR));
                break;
            }
            default:
            {
                // Team members
                // Add a blank option for if not allocated to any idividual
                allocateToList.add(null);
                Project selectedProject = (Project)cmbProject.getSelectedItem();
                allocateToList.addAll(selectedProject.findWorkersByRole(workerRole));
            }
        }
        for(Worker worker: allocateToList)
        {
            workerRoleComboModel.addElement(worker);
        }
        cmbNTaskAllocateTo.setModel(workerRoleComboModel);
    }
    
    /**
     * Clears the existing tasks controls
     */
    private void clearOTaskControls()
    {
        txtOTaskContentName.setText("");
        txtOTaskContentDescription.setText("");
        txtOTaskContentStatus.setText("");
        txtOTaskMediaRequired.setText("");
        txtOTaskDescription.setText("");
    }
    
    /**
     * clears the new tasks controls
     */
    private void clearNTaskControls()
    {
        txtNTaskDescription.setText("");
        cmbNTaskAction.setModel(new DefaultComboBoxModel());
        cmbNtaskWorkerRole.setModel(new DefaultComboBoxModel());
        cmbNTaskAllocateTo.setModel(new DefaultComboBoxModel());
        cmbNTaskPriority.setSelectedIndex(0);
    }
    
    /**
     * Set the enabled state of the new task controls to the passed value
     * @param value boolean enabled state
     */
    private void setCreateTaskControlsEnabled(boolean value)
    {
        txtNTaskDescription.setEnabled(value);
        cmbNTaskAction.setEnabled(value);
        cmbNtaskWorkerRole.setEnabled(value);
        cmbNTaskAllocateTo.setEnabled(value);
        cmbNTaskPriority.setEnabled(value);
    }
    
    /**
     * Setup the table and format the information for the given project
     * @param project the project that the information is to be displayed about
     */
    private void setupTable(Project project)
    {
        
        // check that their are projects to load information from
        if (project == null)
        {
            observedTasks = new BasicEventList();
        }
        else
        {
            // get the tasklist object from the project
            TaskList tasklist = project.getTaskList();

            // get the list of tasks from tasklist
            observedTasks = tasklist.getTaskList();
        }
        
        // define properties and column labels
        String[] propertyNames = {"MediaItem", "MediaDescription","MediaItemStatus", "Description", "Status", "Priority"};
        String[] columnLabels = {"Content Item", "Media Description","Content Status", "Task Description", "Task Status", "Priority"};
        int[] columnSizes = {80,230,100,230,45,5};
        
        // sort for filtered lists by prioroty
        Comparator sortByPriority = new Comparator() {
            @Override
            public int compare(Object o1, Object o2)
            {
                TaskItem task1 = (TaskItem) o1;
                TaskItem task2 = (TaskItem) o2;
                
                return Integer.compare(task1.getPriority(),task2.getPriority());
            }
        };
        
        // setup Unallocated filter on overall task list
        matcherEditor = new QCTeamLeaderUI.StatusMatcherEditor();
        FilterList allocatedTasks = new FilterList(observedTasks, matcherEditor);
        
        // sort the filtered allocated list by priority
        SortedList sortedAllocatedTasks = new SortedList(allocatedTasks, sortByPriority);
        
        // set table format for unallocated task items
        TableFormat tableFormat = GlazedLists.tableFormat(TaskItem.class, propertyNames, columnLabels);
        AllTableModel = new EventJXTableModel(sortedAllocatedTasks, tableFormat);
        tblTaskList.setModel(AllTableModel);
        
        // set the preferred column widths
        int col = 0;
        for(int width: columnSizes)
        {
            tblTaskList.getColumnModel().getColumn(col).setPreferredWidth(width);
            col++;
        }
    }
    
    /**
     * Loads project combo box with the avaliable projects
     */
    private void loadProjectCombo() {
        boolean notCurrentProjects = false;
        
        if (cmbProject.getSelectedItem() == null) notCurrentProjects = true;
        
        // get ref to project register
        ProjectRegister proReg = ProjectRegister.getInstance();
        
        // create a new model
        DefaultComboBoxModel projectComboModel = new DefaultComboBoxModel();
        
        // load the model and set the combo box to the new model
        for(Project project: proReg.getProjectList(user))
        {
            projectComboModel.addElement(project);
        }
        cmbProject.setModel(projectComboModel);
        if (notCurrentProjects == true) projectSelected();
    }
    
    /**
     * Loads the MediaState filters.  Used by user to filter the viewed tasks
     */
    private void loadFilterTaskListCombo()
    {
        // create a new model
        DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
        
        // valid choices
        comboModel.addElement(MediaStatus.NONE);
        comboModel.addElement(MediaStatus.ARRIVED_IN_VAULT);
        comboModel.addElement(MediaStatus.FIXES_COMPLETED);
        comboModel.addElement(MediaStatus.COMPRESSION_COMPLETED);
        
        cmbFilterTaskList.setModel(comboModel);
    }
    
    /**
     * Handles project combo selection
     */
    private void projectSelected()
    {
        // clear any task list selection
        tblTaskList.clearSelection();
        
        // setup table
        setupTable((Project)cmbProject.getSelectedItem());
        
        // reset the task filter
        cmbFilterTaskList.setSelectedItem(MediaStatus.NONE);
        
        // raise the update list event with the new status to filter by
        matcherEditor.updateList(MediaStatus.NONE);
    }
    
    
    /**
     * Handles the create new task button
     */
    private void createNewTask()
    {
        // collect new information
        String taskDescription = txtNTaskDescription.getText();
        MediaStatus newstatus = (MediaStatus) cmbNTaskAction.getSelectedItem();
        WorkerRoles workerRole = (WorkerRoles) cmbNtaskWorkerRole.getSelectedItem();
        Worker allocatedTo = (Worker) cmbNTaskAllocateTo.getSelectedItem();
        int priority = Integer.parseInt((String)cmbNTaskPriority.getSelectedItem());
        
        // flag to prevent table updates and prevent changes to selectedTasks until the update has
        // been completed
        updatingFlag = true;
        
        // assign task status from the combo box to selected task
        for (int i = 0; i < selectedTasks.size(); i++)
        {
            TaskItem task = selectedTasks.get(i);
            if (task.getWorker() == null) task.setWorker(user);
            task.setStatus(TaskStatus.COMPLETE, "");
            MediaItem mediaItem = task.getMediaItem();
            mediaItem.setStatus(newstatus, user, taskDescription, workerRole, allocatedTo, priority);
        }
        
        updatingFlag = false;
                
        // Clear selection 
        tblTaskList.clearSelection();
        // disable the controls
        clearOTaskControls();
        setCreateTaskControlsEnabled(false);
        clearNTaskControls();
        btnCreateTask.setEnabled(false);
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

        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        lblTaskList = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTaskList = new javax.swing.JTable();
        lblContentName = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtOTaskContentName = new javax.swing.JTextField();
        lblContentDescription = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtOTaskContentDescription = new javax.swing.JTextArea();
        lblMediaRequired = new javax.swing.JLabel();
        txtOTaskMediaRequired = new javax.swing.JTextField();
        lblOTaskDescription = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtOTaskDescription = new javax.swing.JTextArea();
        btnViewQCReport = new javax.swing.JButton();
        lblContentStatus = new javax.swing.JLabel();
        txtOTaskContentStatus = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblNewTaskDescription = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtNTaskDescription = new javax.swing.JTextArea();
        lblAction = new javax.swing.JLabel();
        cmbNTaskAction = new javax.swing.JComboBox();
        lblWorkerRole = new javax.swing.JLabel();
        cmbNtaskWorkerRole = new javax.swing.JComboBox();
        lblAllocateTo = new javax.swing.JLabel();
        cmbNTaskAllocateTo = new javax.swing.JComboBox();
        lblPriority = new javax.swing.JLabel();
        cmbNTaskPriority = new javax.swing.JComboBox();
        btnCreateTask = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblProject = new javax.swing.JLabel();
        cmbProject = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        lblFilterTaskList = new javax.swing.JLabel();
        cmbFilterTaskList = new javax.swing.JComboBox();

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        lblTaskList.setText("Task List");

        tblTaskList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblTaskList);

        lblContentName.setBorder(javax.swing.BorderFactory.createTitledBorder("Task Details"));

        jLabel1.setText("Content Name");

        txtOTaskContentName.setEnabled(false);

        lblContentDescription.setText("Content Description");

        txtOTaskContentDescription.setColumns(20);
        txtOTaskContentDescription.setLineWrap(true);
        txtOTaskContentDescription.setRows(5);
        txtOTaskContentDescription.setWrapStyleWord(true);
        txtOTaskContentDescription.setEnabled(false);
        jScrollPane2.setViewportView(txtOTaskContentDescription);

        lblMediaRequired.setText("Media Required");

        txtOTaskMediaRequired.setEnabled(false);

        lblOTaskDescription.setText("Task Description");

        txtOTaskDescription.setColumns(20);
        txtOTaskDescription.setLineWrap(true);
        txtOTaskDescription.setRows(5);
        txtOTaskDescription.setWrapStyleWord(true);
        txtOTaskDescription.setEnabled(false);
        jScrollPane4.setViewportView(txtOTaskDescription);

        btnViewQCReport.setText("View QC Report");
        btnViewQCReport.setEnabled(false);
        btnViewQCReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewQCReportActionPerformed(evt);
            }
        });

        lblContentStatus.setText("Content Status");

        txtOTaskContentStatus.setEnabled(false);

        javax.swing.GroupLayout lblContentNameLayout = new javax.swing.GroupLayout(lblContentName);
        lblContentName.setLayout(lblContentNameLayout);
        lblContentNameLayout.setHorizontalGroup(
            lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblContentNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lblContentNameLayout.createSequentialGroup()
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblOTaskDescription)
                            .addComponent(lblMediaRequired)
                            .addComponent(lblContentDescription)
                            .addComponent(jLabel1)
                            .addComponent(lblContentStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOTaskContentName)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                            .addComponent(txtOTaskMediaRequired)
                            .addComponent(jScrollPane4)
                            .addComponent(txtOTaskContentStatus)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblContentNameLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnViewQCReport)))
                .addContainerGap())
        );
        lblContentNameLayout.setVerticalGroup(
            lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblContentNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtOTaskContentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContentDescription))
                .addGap(9, 9, 9)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMediaRequired)
                    .addComponent(txtOTaskMediaRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblContentStatus)
                    .addComponent(txtOTaskContentStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOTaskDescription))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnViewQCReport)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Define a Task"));

        lblNewTaskDescription.setText("Description");

        txtNTaskDescription.setColumns(20);
        txtNTaskDescription.setLineWrap(true);
        txtNTaskDescription.setRows(5);
        txtNTaskDescription.setWrapStyleWord(true);
        txtNTaskDescription.setEnabled(false);
        jScrollPane5.setViewportView(txtNTaskDescription);

        lblAction.setText("Action");

        cmbNTaskAction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbNTaskAction.setEnabled(false);
        cmbNTaskAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbNTaskActionActionPerformed(evt);
            }
        });

        lblWorkerRole.setText("For Work Role");

        cmbNtaskWorkerRole.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbNtaskWorkerRole.setEnabled(false);
        cmbNtaskWorkerRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbNtaskWorkerRoleActionPerformed(evt);
            }
        });

        lblAllocateTo.setText("Allocate to");

        cmbNTaskAllocateTo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbNTaskAllocateTo.setEnabled(false);

        lblPriority.setText("Priority");

        cmbNTaskPriority.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        cmbNTaskPriority.setEnabled(false);

        btnCreateTask.setText("Create Task");
        btnCreateTask.setEnabled(false);
        btnCreateTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateTaskActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPriority)
                    .addComponent(lblAllocateTo)
                    .addComponent(lblWorkerRole)
                    .addComponent(lblAction)
                    .addComponent(lblNewTaskDescription))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addComponent(cmbNTaskAction, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbNtaskWorkerRole, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbNTaskAllocateTo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmbNTaskPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCreateTask)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNewTaskDescription))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAction)
                    .addComponent(cmbNTaskAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblWorkerRole)
                    .addComponent(cmbNtaskWorkerRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAllocateTo)
                    .addComponent(cmbNTaskAllocateTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPriority)
                            .addComponent(cmbNTaskPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(btnCreateTask)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        lblProject.setText("Project");

        cmbProject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProjectActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter Tasks by Current Status"));

        lblFilterTaskList.setText("Current Status");

        cmbFilterTaskList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterTaskListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFilterTaskList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbFilterTaskList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFilterTaskList)
                    .addComponent(cmbFilterTaskList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTaskList)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblProject)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblContentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnClose))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProject)
                    .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTaskList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblContentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProjectActionPerformed
        projectSelected();
    }//GEN-LAST:event_cmbProjectActionPerformed

    private void btnCreateTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateTaskActionPerformed
        createNewTask();
        btnViewQCReport.setEnabled(false);
    }//GEN-LAST:event_btnCreateTaskActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // de-registers events for this form and close it
        deregisterEvents();
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCloseActionPerformed

    private void cmbFilterTaskListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterTaskListActionPerformed
        // clear tasklist selection
        tblTaskList.clearSelection();
        
        // get the status to filter the table by
        MediaStatus filterStatus = (MediaStatus)cmbFilterTaskList.getSelectedItem();
        
        // raise the update list event with the new status to filter by
        matcherEditor.updateList(filterStatus);
    }//GEN-LAST:event_cmbFilterTaskListActionPerformed

    private void cmbNtaskWorkerRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbNtaskWorkerRoleActionPerformed
        // load the possible worker allocations
        loadAllocatedTo();
        validateNTask();
    }//GEN-LAST:event_cmbNtaskWorkerRoleActionPerformed

    private void cmbNTaskActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbNTaskActionActionPerformed
        // load the possible worker roles
        loadWorkerRoles();
        validateNTask();
    }//GEN-LAST:event_cmbNTaskActionActionPerformed

    private void btnViewQCReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewQCReportActionPerformed
        task.setStatus(TaskStatus.IN_PROGRESS, "");
        QCReportViewerUI frm = new QCReportViewerUI(task);
        frm.modal = true;
        this.getDesktopPane().add(frm);
        frm.setVisible(true);
    }//GEN-LAST:event_btnViewQCReportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCreateTask;
    private javax.swing.JButton btnViewQCReport;
    private javax.swing.JComboBox cmbFilterTaskList;
    private javax.swing.JComboBox cmbNTaskAction;
    private javax.swing.JComboBox cmbNTaskAllocateTo;
    private javax.swing.JComboBox cmbNTaskPriority;
    private javax.swing.JComboBox cmbNtaskWorkerRole;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel lblAction;
    private javax.swing.JLabel lblAllocateTo;
    private javax.swing.JLabel lblContentDescription;
    private javax.swing.JPanel lblContentName;
    private javax.swing.JLabel lblContentStatus;
    private javax.swing.JLabel lblFilterTaskList;
    private javax.swing.JLabel lblMediaRequired;
    private javax.swing.JLabel lblNewTaskDescription;
    private javax.swing.JLabel lblOTaskDescription;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblTaskList;
    private javax.swing.JLabel lblWorkerRole;
    private javax.swing.JTable tblTaskList;
    private javax.swing.JTextArea txtNTaskDescription;
    private javax.swing.JTextArea txtOTaskContentDescription;
    private javax.swing.JTextField txtOTaskContentName;
    private javax.swing.JTextField txtOTaskContentStatus;
    private javax.swing.JTextArea txtOTaskDescription;
    private javax.swing.JTextField txtOTaskMediaRequired;
    // End of variables declaration//GEN-END:variables

    /**
     * Class used for the filtering of the avaliable workers by role within this form
     */
    private class StatusMatcherEditor extends AbstractMatcherEditor 
    {
        /**
         * Raise the event to update the filtered list of avaliable workers by the filter
         * @param filter A WorkersRoles as a filter criteria
         */
        public void updateList(MediaStatus filter)
        {
            // check if no filter required
            if (filter == MediaStatus.NONE)
                // filter by the filter and create a RolerMatcher for the job
                this.fireChanged(new AllocatedMatcher(filter));
            else
                // filter by the filter and create a RolerMatcher for the job
                this.fireChanged(new AllocatedMatcher(filter));
        }

        /**
         * Class used to carry out filter matching by GlazedLists
         */
        private class AllocatedMatcher implements Matcher 
        {
            // role to filter by
            private MediaStatus filterStatus;

            public AllocatedMatcher(MediaStatus filter) 
            {
                this.filterStatus = filter;
            }
            
            /**
             * provides matching functionality for each object
             * @param item the object to be matched
             * @return boolean of the match
             */
            @Override
            public boolean matches(Object item) 
            {
                // convert the object to its true type
                final TaskItem taskItem = (TaskItem) item;

                if (filterStatus == MediaStatus.NONE)
                {
                    // returns true if the worker matches the user workrole;
                    // task is not complete and therefore open;
                    return (taskItem.getWorkRoleType() == user.getRole() &&
                            taskItem.getStatus() != TaskStatus.COMPLETE);
                }
                else
                {
                    // returns true if the worker matches the user workrole;
                    // task is not complete and therfore open;
                    // the mediaItem status matcers the filter;
                    return (taskItem.getWorkRoleType() == user.getRole() &&
                            taskItem.getStatus() != TaskStatus.COMPLETE &&
                            taskItem.getMediaItemStatus() == filterStatus);
                }             
            }
        }
    }
}
