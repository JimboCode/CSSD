package UI;

import BLL.MediaItem;
import BLL.Project;
import BLL.ProjectRegister;
import BLL.TaskItem;
import BLL.TaskList;
import BLL.TaskStatus;
import BLL.Worker;
import BLL.WorkerRoles;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
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
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Allows QC and Authors to view avaliable tasks; allocate task to themselves and 
 * open tasks to work on them
 * @author James Staite
 */
public class QCAuthorTasksUI extends javax.swing.JInternalFrame implements Observer
{
    // user using this form
    Worker user;
    
    // reference to the Observered Task List
    private EventList<TaskItem> observedTasks;
    
    // Table models
    EventJXTableModel unAllTableModel;
    EventJXTableModel allTableModel;
    
    // selected task items
    ArrayList<TaskItem> selectedTasks = new ArrayList();
    
    // table user is currently working with
    JTable currentTable;
    
    // the current project
    Project currentProject;
    
    // reference to main form
    MainMDIUI mainform;
    
    // flags that that the selected tasks from the table are being updated
    // and to enough the value changed event
    boolean updatingFlag = false;
    
    /**
     * Creates new form QCAuthorTasksUI
     */
    public QCAuthorTasksUI(Worker user, MainMDIUI mainform) 
    {
        super("Tasks View",false,true,false,false);
        initComponents();
        
        // store user details
        this.user = user;
        
        // store mainform
        this.mainform = mainform;
        
        // load project into combo box
        loadProjectCombo();
        
        // setup table
        setupTable((Project)cmbProject.getSelectedItem());
        
        // register for project update events
        registerEvents();
        
        // register listener for both tables
        tblAvaliableTasks.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // possible row selection
                // flag set to true when updating selected tasks and prevents table updates
                if (updatingFlag == false)
                {
                    if (!event.getValueIsAdjusting())
                    {
                        if (currentTable != tblAvaliableTasks)
                        {
                            tblAllocatedTasks.clearSelection();
                            currentTable = tblAvaliableTasks;
                        }
                        rowselection();
                    }
                }
            }
        });
        
        tblAllocatedTasks.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // possible row selection
                 // flag set to true when updating selected tasks and prevents table updates
                if (updatingFlag == false)
                {
                    if (!event.getValueIsAdjusting())
                    {
                        if (currentTable != tblAllocatedTasks)
                        {
                            tblAvaliableTasks.clearSelection();
                            currentTable = tblAllocatedTasks;
                        }
                        rowselection();
                    }
                }
            }
        });
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
     * Handles project combo box selection
     */
    private void projectSelected()
    {
        // clear any task list selections
        tblAvaliableTasks.clearSelection();
        tblAllocatedTasks.clearSelection();
        
        // setup table
        currentProject = (Project)cmbProject.getSelectedItem();
        setupTable(currentProject);
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
        int[] selectedRows= currentTable.getSelectedRows();
        
        // clear the previous list of rows
        selectedTasks.clear();
        
        //check if any items are selected
        if (selectedRows.length > 0)
        {
            // hold the first item to use for comparison
            EventJXTableModel tableModel = (EventJXTableModel) currentTable.getModel();
            TaskItem taskStandard = (TaskItem) tableModel.getElementAt(selectedRows[0]);
            MediaItem mediaItemStandard = taskStandard.getMediaItem();
            
            // multiple item can only be selected if their status is Awaiting action
            for (int index : selectedRows)
            {
                thisItem = (TaskItem) tableModel.getElementAt(index);
                
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
                clearTaskControls();
            } else
            {
                // only display items that are common to all selected rows
                if (contentNameSame) txtTaskContentName.setText(mediaItemStandard.getName()); else txtTaskContentName.setText("");
                if (contentDescriptionSame) txtTaskContentDescription.setText(mediaItemStandard.getDescription()); else txtTaskContentDescription.setText("");
                if (mediaTypeSame) txtTaskMediaRequired.setText(mediaItemStandard.getMediaType().toString()); else txtTaskMediaRequired.setText("");
                if (taskDescriptionSame) txtTaskDescription.setText(taskStandard.getDescription()); else txtTaskDescription.setText("");
                
                if (currentTable == tblAvaliableTasks) 
                {
                    btnAllocateTask.setEnabled(true);
                    btnOpenTask.setEnabled(false);
                } else 
                {
                    btnAllocateTask.setEnabled(false);
                    btnOpenTask.setEnabled(true);
                }
            }
        }
    }
    
    /**
     * Clears all the task controls
     */
    private void clearTaskControls()
    {
        // clear form controls
        txtTaskContentName.setText("");
        txtTaskContentDescription.setText("");
        txtTaskDescription.setText("");
        txtTaskDescription.setText("");
        btnAllocateTask.setEnabled(false);
        btnOpenTask.setEnabled(false);
        btnAllocateTask.setEnabled(false);
    }
   
    /**
     * Handles the updating of tasks items selected
     */
    private void allocateSelectedTasks()
    {
        // flag to prevent table updates and prevent changes to selectedTasks until the update has
        // been completed
        updatingFlag = true;
        
        // assign task status from the combo box to selected task
        for (int i = 0; i < selectedTasks.size(); i++)
        {
            TaskItem task = selectedTasks.get(i);
            task.setWorker(user);
        }
        
        updatingFlag = false;
                
        // Clear selection 
        currentTable.clearSelection();
        // disable the controls
        clearTaskControls();
    }
    
    /**
     * Handles the open task button to start a task
     */
    private void startTask()
    {
        TaskItem task = selectedTasks.get(0);
        task.setStatus(TaskStatus.IN_PROGRESS, "");
        
        // Clear selection 
        currentTable.clearSelection();
        
        // open form assoicated with user to process the task
        if(user.getRole() == WorkerRoles.QC)
        {
            QCMemberReport frm = new QCMemberReport(task, (Project)cmbProject.getSelectedItem(), user);
            mainform.addForm(frm);
        }
        if (user.getRole() == WorkerRoles.AUTHOR)
        {
            // TODO implement author specific form for opening a selected task
        }
        
        // Clear selection 
        currentTable.clearSelection();
        // disable the controls
        clearTaskControls();        
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
        QCAuthorTasksUI.UnallocatedMatcher unAllocatedMatcher = new QCAuthorTasksUI.UnallocatedMatcher();
        QCAuthorTasksUI.AllocatedMatcher allocatedMatcher = new QCAuthorTasksUI.AllocatedMatcher();
        
        FilterList unAllocateTasks = new FilterList(observedTasks, unAllocatedMatcher);
        FilterList allocatedTasks = new FilterList(observedTasks, allocatedMatcher);
        
        
        // sort the filtered allocated list by priority
        SortedList sortedUnallocatedTasks = new SortedList(unAllocateTasks, sortByPriority);
        SortedList sortedAllocatedTasks = new SortedList(allocatedTasks, sortByPriority);
        
        // set table format for unallocated task items
        TableFormat unAllTableFormat = GlazedLists.tableFormat(TaskItem.class, propertyNames, columnLabels);
        unAllTableModel = new EventJXTableModel(sortedUnallocatedTasks, unAllTableFormat);
        tblAvaliableTasks.setModel(unAllTableModel);
        
        // set table format for allocated task items
        TableFormat allTableFormat = GlazedLists.tableFormat(TaskItem.class, propertyNames, columnLabels);
        allTableModel = new EventJXTableModel(sortedAllocatedTasks, allTableFormat);
        tblAllocatedTasks.setModel(allTableModel);
        
        // set the preferred column widths
        setColumnWidths(tblAvaliableTasks,columnSizes);
        setColumnWidths(tblAllocatedTasks,columnSizes);
        
        // setup muliple selection settings for the avaliable table
        tblAvaliableTasks.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblAvaliableTasks.setRowSelectionAllowed(true);
        tblAvaliableTasks.setColumnSelectionAllowed(false);
        
        // setup single selection for the allocated table
        tblAllocatedTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAllocatedTasks.setRowSelectionAllowed(true);
        tblAllocatedTasks.setColumnSelectionAllowed(false);
    }
    
    /**
     * formats the preferred column width for a table passed in
     * @param table JTable to format
     * @param columnSizes Array of integer column widths
     */
    private void setColumnWidths(JTable table, int columnSizes[])
    {
        int col = 0;
        for(int width: columnSizes)
        {
            table.getColumnModel().getColumn(col).setPreferredWidth(width);
            col++;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        lblProject = new javax.swing.JLabel();
        cmbProject = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAvaliableTasks = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAllocatedTasks = new javax.swing.JTable();
        lblContentName = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtTaskContentName = new javax.swing.JTextField();
        lblContentDescription = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtTaskContentDescription = new javax.swing.JTextArea();
        lblMediaRequired = new javax.swing.JLabel();
        txtTaskMediaRequired = new javax.swing.JTextField();
        lblOTaskDescription = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtTaskDescription = new javax.swing.JTextArea();
        btnOpenTask = new javax.swing.JButton();
        btnAllocateTask = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        lblProject.setText("Project");

        cmbProject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProjectActionPerformed(evt);
            }
        });

        jLabel2.setText("Avaliable Tasks");

        tblAvaliableTasks.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblAvaliableTasks);

        jLabel3.setText("Allocated Tasks");

        tblAllocatedTasks.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblAllocatedTasks);

        lblContentName.setBorder(javax.swing.BorderFactory.createTitledBorder("Task Details"));

        jLabel4.setText("Content Name");

        txtTaskContentName.setEnabled(false);

        lblContentDescription.setText("Content Description");

        txtTaskContentDescription.setColumns(20);
        txtTaskContentDescription.setLineWrap(true);
        txtTaskContentDescription.setRows(5);
        txtTaskContentDescription.setWrapStyleWord(true);
        txtTaskContentDescription.setEnabled(false);
        jScrollPane4.setViewportView(txtTaskContentDescription);

        lblMediaRequired.setText("Media Type");

        txtTaskMediaRequired.setEnabled(false);

        lblOTaskDescription.setText("Task Description");

        txtTaskDescription.setColumns(20);
        txtTaskDescription.setLineWrap(true);
        txtTaskDescription.setRows(5);
        txtTaskDescription.setWrapStyleWord(true);
        txtTaskDescription.setEnabled(false);
        jScrollPane5.setViewportView(txtTaskDescription);

        btnOpenTask.setText("Open Task");
        btnOpenTask.setEnabled(false);
        btnOpenTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenTaskActionPerformed(evt);
            }
        });

        btnAllocateTask.setText("Allocate to Self");
        btnAllocateTask.setEnabled(false);
        btnAllocateTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllocateTaskActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lblContentNameLayout = new javax.swing.GroupLayout(lblContentName);
        lblContentName.setLayout(lblContentNameLayout);
        lblContentNameLayout.setHorizontalGroup(
            lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblContentNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lblContentNameLayout.createSequentialGroup()
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblContentDescription)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTaskContentName)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblOTaskDescription)
                            .addComponent(lblMediaRequired))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTaskMediaRequired)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblContentNameLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAllocateTask)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnOpenTask)))
                .addContainerGap())
        );
        lblContentNameLayout.setVerticalGroup(
            lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblContentNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lblContentNameLayout.createSequentialGroup()
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtTaskContentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblContentDescription)))
                    .addGroup(lblContentNameLayout.createSequentialGroup()
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMediaRequired)
                            .addComponent(txtTaskMediaRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblOTaskDescription)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOpenTask)
                    .addComponent(btnAllocateTask))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblProject)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblContentName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnClose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProject)
                    .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblContentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProjectActionPerformed
        projectSelected();
    }//GEN-LAST:event_cmbProjectActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // de-registers events for this form and close it
        deregisterEvents();
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnAllocateTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllocateTaskActionPerformed
        allocateSelectedTasks();
    }//GEN-LAST:event_btnAllocateTaskActionPerformed

    private void btnOpenTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenTaskActionPerformed
        startTask();
    }//GEN-LAST:event_btnOpenTaskActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAllocateTask;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOpenTask;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblContentDescription;
    private javax.swing.JPanel lblContentName;
    private javax.swing.JLabel lblMediaRequired;
    private javax.swing.JLabel lblOTaskDescription;
    private javax.swing.JLabel lblProject;
    private javax.swing.JTable tblAllocatedTasks;
    private javax.swing.JTable tblAvaliableTasks;
    private javax.swing.JTextArea txtTaskContentDescription;
    private javax.swing.JTextField txtTaskContentName;
    private javax.swing.JTextArea txtTaskDescription;
    private javax.swing.JTextField txtTaskMediaRequired;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Class used to carry out filter matching by GlazedLists
     */
    private class AllocatedMatcher implements Matcher 
    {
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

            // returns true if the worker is null and therefore unallocated
            return (taskItem.getWorker() == user && taskItem.getStatus() != TaskStatus.COMPLETE);
        }
    }
    
    /**
     * Class used to carry out filter matching by GlazedLists
     */
    private class UnallocatedMatcher implements Matcher 
    {
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

            // returns true if the worker is null and therefore unallocated
            return (taskItem.getWorkRoleType() == user.getRole() && taskItem.getWorker() == null && taskItem.getStatus() != TaskStatus.COMPLETE);
        }
    }
}
