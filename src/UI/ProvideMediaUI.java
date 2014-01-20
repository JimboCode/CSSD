/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import BLL.MediaItem;
import BLL.Project;
import BLL.ProjectRegister;
import BLL.TaskItem;
import BLL.TaskList;
import BLL.TaskStatus;
import BLL.Worker;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.EventJXTableModel;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Used by Client and Contractors to view their tasks, update tasks and submit media requested
 * 
 * @author James
 */
public class ProvideMediaUI extends javax.swing.JInternalFrame implements Observer
{
    // user using this form
    Worker user;
    
    // reference to the Observered Task List
    private EventList<TaskItem> observedTasks;
    
    // Table model
    EventJXTableModel AllTableModel;
    
    // selected task items
    ArrayList<TaskItem> selectedTasks = new ArrayList();
    
    // full file name selected for attaching
    String filename;
    
    /**
     * Creates new form ProvideMediaUI
     * 
     * @author James Staite
     */
    public ProvideMediaUI(Worker user) 
    {
        super("Tasks View",false,true,false,false);
        initComponents();
        
        // store user details
        this.user = user;
        
        // load project into combo box
        loadProjectCombo();
        
        // setup table
        setupTable((Project)cmbProject.getSelectedItem());
        
        // setup the selection setting for the table
        tblAllocatedTasks.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblAllocatedTasks.setRowSelectionAllowed(true);
        tblAllocatedTasks.setColumnSelectionAllowed(false);
        
        // register for project update events
        registerEvents();
        
        tblAllocatedTasks.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // possible row selection
                rowselection();                
            }
        });
    }
    
    /**
     * handle the row selection process and store a list of the rows selected in
     * selectedTasks. Also toggles the forms controls state
     */
    private void rowselection()
    {
        // flags if all the tasks selected have the same status
        boolean contentDescriptionSame = true;
        boolean mediaTypeSame = true;
        boolean taskDescriptionSame = true;
        
        // temporary reference to task being processed
        TaskItem thisItem;
        
        // get all rows selected
        int[] selectedRows= tblAllocatedTasks.getSelectedRows();
        
        // clear the previous list of rows
        selectedTasks.clear();
        
        //check if any items are selected
        if (selectedRows.length > 0)
        {
            // hold the first item to use for comparison
            TaskItem taskStandard = (TaskItem) AllTableModel.getElementAt(selectedRows[0]);
            MediaItem mediaItemStandard = taskStandard.getMediaItem();
            
            // multiple item can only be selected if their status is Awaiting action
            for (int index : selectedRows)
            {
                thisItem = (TaskItem) AllTableModel.getElementAt(index);
                
                // get the status of the item is AWAITING_ACTION if multiple items selected
                if (thisItem.getStatus() != taskStandard.getStatus() ||
                        thisItem.getStatus() != TaskStatus.AWAITING_ACTION &&
                        selectedTasks.size() > 0)
                {
                    // if not empty list and stop checking as the items cannot be processed
                    selectedTasks.clear();
                    break;
                }
                
                // check which fields are common amongst the selected rows
                MediaItem thisMediaItem = thisItem.getMediaItem();
                
                // flag items that are not shared
                if(mediaItemStandard.getDescription() != thisMediaItem.getDescription()) contentDescriptionSame = false;
                if(mediaItemStandard.getMediaType() != thisMediaItem.getMediaType()) mediaTypeSame = false;
                if(taskStandard.getDescription() != thisItem.getDescription()) taskDescriptionSame = false;
                selectedTasks.add(thisItem);
            }
            
            // update the controls
            if (selectedTasks.isEmpty())
            {
                // either not rows selected or not a usable group selected
                // clear form controls
                txtContentName.setText("");
                txtContentDescription.setText("");
                txtMediaType.setText("");
                txtTaskDescription.setText("");
                cmbTaskStatus.setModel(new DefaultComboBoxModel());
                cmbTaskStatus.setEnabled(false);
                txtTaskComments.setEnabled(false);
                btnLoadFile.setEnabled(false);
                btnUpdateTask.setEnabled(false);
            } else
            {
                // only display items that are common to all selected rows
                if (selectedTasks.size() == 1) txtContentName.setText(mediaItemStandard.getName()); else txtContentName.setText("");
                if (contentDescriptionSame) txtContentDescription.setText(mediaItemStandard.getDescription()); else txtContentDescription.setText("");
                if (mediaTypeSame) txtMediaType.setText(mediaItemStandard.getMediaType().toString()); else txtMediaType.setText("");
                if (taskDescriptionSame) txtTaskDescription.setText(taskStandard.getDescription()); else txtTaskDescription.setText("");
                
                // create a new model
                DefaultComboBoxModel taskStatusModel = new DefaultComboBoxModel();

                // update the state based upon the task status
                if (taskStandard.getStatus() == TaskStatus.AWAITING_ACTION)
                {
                    taskStatusModel.addElement(TaskStatus.IN_PROGRESS);
                    btnLoadFile.setEnabled(false);
                    btnUpdateTask.setEnabled(true);
                    txtTaskComments.setEnabled(false);
                }
                else
                {
                    taskStatusModel.addElement(TaskStatus.COMPLETE);
                    btnLoadFile.setEnabled(true);
                    btnUpdateTask.setEnabled(false);
                    txtTaskComments.setEnabled(true);
                }
                cmbTaskStatus.setModel(taskStatusModel);
                cmbTaskStatus.setEnabled(true);
            }
        }
    }
    
    /**
     * Handles the update button click and updates the selected tasks information
     */
    private void UpdateTask()
    {
        // if a file has been selected only 1 task is selected; update the task
        if (txtTaskFilename.getText().length() > 0 )
        {
            selectedTasks.get(0).setFilename(filename);
            filename = "";
            txtTaskFilename.setText("");            
        }
        
        // assign task status from the combo box to selected task
        for (int i = 0; i < selectedTasks.size(); i++)
        {
            selectedTasks.get(i).setStatus((TaskStatus) cmbTaskStatus.getSelectedItem(),txtTaskComments.getText());
        }
        
        // clear comments
        txtTaskComments.setText("");
        txtTaskComments.setEnabled(false);
        
        // if row is still displayed then reselect it to update the display
        int row = tblAllocatedTasks.getSelectedRow();
        if (row > -1) 
        {
            tblAllocatedTasks.clearSelection();
            tblAllocatedTasks.setRowSelectionInterval(row, row);
        }
        
    }
    
    /**
     * handles the loading of filename information of the file to attached to the task
     */
    private void loadFile()
    {
        // create a file chooser to attach a file
        JFileChooser chooser = new JFileChooser(); 
        chooser.setDialogTitle("Attach File");
        
        // get and set the file types based upon the component enumeration type selected
        String fileTypes = selectedTasks.get(0).getMediaItem().getMediaType().fileExtensions();
        MyFileFilter fileFilter = new MyFileFilter(fileTypes);
        
        // setup the filter
        chooser.setFileFilter(fileFilter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        
        // if a file was selected store its details for the update action
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
        {
            filename = chooser.getSelectedFile().toString();
            txtTaskFilename.setText(chooser.getSelectedFile().getName());
            btnUpdateTask.setEnabled(true);
        }
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
        String[] propertyNames = {"MediaItem", "Description", "Status", "Priority"};
        String[] columnLabels = {"Content Item", "Description", "Task Status", "Priority"};
        int[] columnSizes = {80,230,45,5};
        
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
        ProvideMediaUI.AllocatedMatcher allocatedMatcher = new ProvideMediaUI.AllocatedMatcher();
        FilterList allocatedTasks = new FilterList(observedTasks, allocatedMatcher);
        
        // sort the filtered allocated list by priority
        SortedList sortedAllocatedTasks = new SortedList(allocatedTasks, sortByPriority);
        
        // set table format for unallocated task items
        TableFormat tableFormat = GlazedLists.tableFormat(TaskItem.class, propertyNames, columnLabels);
        AllTableModel = new EventJXTableModel(sortedAllocatedTasks, tableFormat);
        tblAllocatedTasks.setModel(AllTableModel);
        
        // set the preferred column widths
        int col = 0;
        for(int width: columnSizes)
        {
            tblAllocatedTasks.getColumnModel().getColumn(col).setPreferredWidth(width);
            col++;
        }
    }
    
    /**
     * Loads project combo box with the avaliable projects
     */
    private void loadProjectCombo() {
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
        
        tblAllocatedTasks.clearSelection();
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

        lblAllocatedTasks = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAllocatedTasks = new javax.swing.JTable();
        lblProject = new javax.swing.JLabel();
        cmbProject = new javax.swing.JComboBox();
        pnlContentDetails = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtContentDescription = new javax.swing.JTextArea();
        txtContentName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtMediaType = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lblContentName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtTaskFilename = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtTaskDescription = new javax.swing.JTextArea();
        cmbTaskStatus = new javax.swing.JComboBox();
        btnLoadFile = new javax.swing.JButton();
        lblCurrentStatus = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnUpdateTask = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtTaskComments = new javax.swing.JTextArea();
        btnClose = new javax.swing.JButton();

        setRequestFocusEnabled(false);

        lblAllocatedTasks.setText("Allocated Tasks");

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
        jScrollPane1.setViewportView(tblAllocatedTasks);

        lblProject.setText("Project");

        cmbProject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProjectActionPerformed(evt);
            }
        });

        pnlContentDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Content Details"));

        txtContentDescription.setColumns(20);
        txtContentDescription.setRows(5);
        txtContentDescription.setEnabled(false);
        jScrollPane2.setViewportView(txtContentDescription);

        txtContentName.setEnabled(false);

        jLabel2.setText("Media Required");

        txtMediaType.setEnabled(false);

        jLabel1.setText("Content Description");

        lblContentName.setText("Name");

        javax.swing.GroupLayout pnlContentDetailsLayout = new javax.swing.GroupLayout(pnlContentDetails);
        pnlContentDetails.setLayout(pnlContentDetailsLayout);
        pnlContentDetailsLayout.setHorizontalGroup(
            pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(lblContentName)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtContentName)
                    .addComponent(jScrollPane2)
                    .addComponent(txtMediaType, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        pnlContentDetailsLayout.setVerticalGroup(
            pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblContentName)
                    .addComponent(txtContentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlContentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMediaType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Task Details"));

        txtTaskFilename.setEnabled(false);

        jLabel3.setText("File");

        txtTaskDescription.setColumns(20);
        txtTaskDescription.setRows(5);
        txtTaskDescription.setEnabled(false);
        jScrollPane3.setViewportView(txtTaskDescription);

        cmbTaskStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTaskStatus.setEnabled(false);

        btnLoadFile.setText("Attach File");
        btnLoadFile.setEnabled(false);
        btnLoadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadFileActionPerformed(evt);
            }
        });

        lblCurrentStatus.setText("Current Status");

        jLabel4.setText("Description");

        btnUpdateTask.setText("Update Task");
        btnUpdateTask.setEnabled(false);
        btnUpdateTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateTaskActionPerformed(evt);
            }
        });

        jLabel5.setText("Comments");

        txtTaskComments.setColumns(20);
        txtTaskComments.setRows(5);
        txtTaskComments.setEnabled(false);
        jScrollPane4.setViewportView(txtTaskComments);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(253, 253, 253)
                                .addComponent(btnUpdateTask))
                            .addComponent(jScrollPane4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblCurrentStatus)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbTaskStatus, 0, 259, Short.MAX_VALUE)
                            .addComponent(txtTaskFilename))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLoadFile)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrentStatus)
                    .addComponent(cmbTaskStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTaskFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoadFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(82, 82, 82))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUpdateTask)))
                .addContainerGap())
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
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblAllocatedTasks)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblProject)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlContentDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(7, 7, 7)
                .addComponent(lblAllocatedTasks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlContentDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProjectActionPerformed
        // setup table
        setupTable((Project)cmbProject.getSelectedItem());
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

    private void btnLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadFileActionPerformed
        loadFile();
    }//GEN-LAST:event_btnLoadFileActionPerformed

    private void btnUpdateTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateTaskActionPerformed
        UpdateTask();
    }//GEN-LAST:event_btnUpdateTaskActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnLoadFile;
    private javax.swing.JButton btnUpdateTask;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JComboBox cmbTaskStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblAllocatedTasks;
    private javax.swing.JLabel lblContentName;
    private javax.swing.JLabel lblCurrentStatus;
    private javax.swing.JLabel lblProject;
    private javax.swing.JPanel pnlContentDetails;
    private javax.swing.JTable tblAllocatedTasks;
    private javax.swing.JTextArea txtContentDescription;
    private javax.swing.JTextField txtContentName;
    private javax.swing.JTextField txtMediaType;
    private javax.swing.JTextArea txtTaskComments;
    private javax.swing.JTextArea txtTaskDescription;
    private javax.swing.JTextField txtTaskFilename;
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
            return (taskItem.getWorker() != null && taskItem.getStatus() != TaskStatus.COMPLETE && taskItem.getWorkRoleType() == user.getRole());
        }
    }
    
    /**
     * File filter for the file chooser used to attach a file
     */
    public class MyFileFilter extends javax.swing.filechooser.FileFilter
    {
        // The Criteria to filter the files on
        private String filter;
        
        /**
         * Sets up the class for use
         * @param filter The string used to filter the file type e.g. *.mp3, *.acc
         */
        public MyFileFilter(String filter)
        {
            this.filter = filter;
        }
        
        /**
         * Handles the file checking against the passed in criteria (extended interface)
         * @param pathname passed in file path
         * @return boolean match against the filtering criteria
         */
        @Override
        public boolean accept(File pathname) 
        {
            // if a directory accept it for navigation
            if (pathname.isDirectory()) 
            {
                return true;
            }
            
            // the the filename portion
            String s = pathname.getName();
            
            // get the file extension
            int i = s.lastIndexOf('.');
            if (i > 0 &&  i < s.length() - 1)
            {
                // get extension and convert to lower case
                String ext = s.substring(i+1).toLowerCase();
                
                // if in filter provided accept it
                if (filter.indexOf(ext) > 0) return true;
                
                return false;
            }
            return false;
        }

        /**
         * returns the description of the filter criteria
         * @return string filter criteria
         */
        @Override
        public String getDescription() {
            return filter;
        }
    }
}
