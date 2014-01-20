/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import BLL.Project;
import BLL.ProjectRegister;
import BLL.Worker;
import BLL.TaskList;
import BLL.TaskItem;
import BLL.TaskStatus;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.EventJXTableModel;
import java.beans.PropertyVetoException;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;

/**
 * Show overall view of project tasks
 * 
 * @author James Staite
 */
public class ProjectProgressUI extends javax.swing.JInternalFrame implements Observer
{
    // reference to the user using the form
    private Worker user;
    
    // reference to the Observered Task List
    private EventList<TaskItem> observedTasks;
    
    /**
     * Creates new form ProjectProgressUI
     */
    public ProjectProgressUI(Worker user) 
    {
        super("Project Progress ",false,true,false,false);
        initComponents();
        
        this.user = user;
        
        // load project into combo box
        loadProjectCombo();
        
        // setup table
        setupTable((Project)cmbProject.getSelectedItem());
        
        // register for project update events
        registerEvents();
    }
    
    private void setupTable(Project project)
    {
        // get the tasklist object from the project
        TaskList tasklist = project.getTaskList();
        
        // get the list of tasks from tasklist
        observedTasks = tasklist.getTaskList();
        
        // define properties and column labels
        String[] propertyNames = {"MediaItem", "MediaItemStatus", "Description", "WorkRoleType", "Worker", "Status", "Priority"};
        String[] columnLabels = {"Content Item", "Content Status", "Description", "Work Role", "Allocated To", "Task Status", "Priority"};
        int[] columnSizes = {80,100,230,40,80,45,5};
        
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
        UnallocatedMatcher unallocatedMatcher = new UnallocatedMatcher();
        FilterList unallocatedTasks = new FilterList(observedTasks, unallocatedMatcher);
        
        // sort the filtered unallocated list by priority
        SortedList sortedUnallocatedTasks = new SortedList(unallocatedTasks, sortByPriority);
        
        // setup Unallocated filter on overall task list
        AllocatedMatcher allocatedMatcher = new AllocatedMatcher();
        FilterList allocatedTasks = new FilterList(observedTasks, allocatedMatcher);
        
        // sort the filtered allocated list by priority
        SortedList sortedAllocatedTasks = new SortedList(allocatedTasks, sortByPriority);
        
        // setup in-progress filter on overall task list
        InProgressMatcher inProgress = new InProgressMatcher();
        FilterList inProgressTasks = new FilterList(observedTasks, inProgress);
        
        // setup completed filter on overall task list
        CompletedMatcher completed = new CompletedMatcher();
        FilterList completedTasks = new FilterList(observedTasks, completed);
        
        // set table format for unallocated task items
        TableFormat tableFormat = GlazedLists.tableFormat(TaskItem.class, propertyNames, columnLabels);
        EventJXTableModel tableModel = new EventJXTableModel(sortedUnallocatedTasks, tableFormat);
        tblUnallocatedTasks.setModel(tableModel);
        
        // set table format for unallocated task items
        EventJXTableModel unallTableModel = new EventJXTableModel(sortedAllocatedTasks, tableFormat);
        tblAllocatedTasks.setModel(unallTableModel);
        
        // set table format for unallocated task items
        EventJXTableModel inProTableModel = new EventJXTableModel(inProgressTasks, tableFormat);
        tblTasksInProgress.setModel(inProTableModel);
        
        // set table format for unallocated task items
        EventJXTableModel CompTableModel = new EventJXTableModel(completedTasks, tableFormat);
        tblCompletedTasks.setModel(CompTableModel);
        
        setColumnWidths(tblUnallocatedTasks,columnSizes);
        setColumnWidths(tblAllocatedTasks,columnSizes);
        setColumnWidths(tblTasksInProgress,columnSizes);
        setColumnWidths(tblCompletedTasks,columnSizes);
    }
    
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

        lblProject = new javax.swing.JLabel();
        cmbProject = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUnallocatedTasks = new javax.swing.JTable();
        lblAllocatedTask = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAllocatedTasks = new javax.swing.JTable();
        lblTasksInProgress = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTasksInProgress = new javax.swing.JTable();
        lblCompletedTasks = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCompletedTasks = new javax.swing.JTable();
        btnClose = new javax.swing.JButton();

        lblProject.setText("Project");

        cmbProject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProjectActionPerformed(evt);
            }
        });

        jLabel1.setText("Unallocated Task List");

        tblUnallocatedTasks.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblUnallocatedTasks);

        lblAllocatedTask.setText("Allocated Task List");

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
        jScrollPane2.setViewportView(tblAllocatedTasks);

        lblTasksInProgress.setText("Tasks In Progress");

        tblTasksInProgress.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblTasksInProgress);

        lblCompletedTasks.setText("Completed Tasks");

        tblCompletedTasks.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(tblCompletedTasks);

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblProject)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1)
                            .addComponent(lblAllocatedTask)
                            .addComponent(lblTasksInProgress)
                            .addComponent(lblCompletedTasks))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAllocatedTask)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTasksInProgress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCompletedTasks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addContainerGap())
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblAllocatedTask;
    private javax.swing.JLabel lblCompletedTasks;
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblTasksInProgress;
    private javax.swing.JTable tblAllocatedTasks;
    private javax.swing.JTable tblCompletedTasks;
    private javax.swing.JTable tblTasksInProgress;
    private javax.swing.JTable tblUnallocatedTasks;
    // End of variables declaration//GEN-END:variables
        /**
         * Class used to carry out filter matching by GlazedLists
         */
        private static class UnallocatedMatcher implements Matcher 
        {
            public UnallocatedMatcher() 
            {
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
                
                // returns true if the worker is null and therefore unallocated
                return (taskItem.getWorker() == null);
            }
        }
        
        /**
         * Class used to carry out filter matching by GlazedLists
         */
        private static class AllocatedMatcher implements Matcher 
        {
            public AllocatedMatcher() 
            {
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
                
                // returns true if the worker is null and therefore unallocated
                return (taskItem.getWorker() != null && taskItem.getStatus() == TaskStatus.AWAITING_ACTION);
            }
        }
        
        /**
         * Class used to carry out filter matching by GlazedLists
         */
        private static class InProgressMatcher implements Matcher 
        {
            public InProgressMatcher() 
            {
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
                
                // returns true if the worker is null and therefore unallocated
                return (taskItem.getStatus() == TaskStatus.IN_PROGRESS);
            }
        }
        
        /**
         * Class used to carry out filter matching by GlazedLists
         */
        private static class CompletedMatcher implements Matcher 
        {
            public CompletedMatcher() 
            {
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
                
                // returns true if the worker is null and therefore unallocated
                return (taskItem.getStatus() == TaskStatus.COMPLETE);
            }
        }
}
