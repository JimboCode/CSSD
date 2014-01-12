package UI;

import BLL.ProjectRegister;
import BLL.Staff;
import BLL.Worker;
import BLL.WorkerRegister;
import BLL.WorkerType;
import BLL.Project;
import BLL.WorkerRoles;
import ca.odell.glazedlists.*;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.EventTableModel;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * UI for defining and updating project team
 * 
 * @author James Staite
 */
public class DefineTeamUI extends javax.swing.JInternalFrame implements Observer
{
    // master list of avaliable workers on the system
    private ArrayList<Worker> allWorkers = new ArrayList();
    
    // list of avaliable workers that are not currently assigned to the project
    private EventList avaliableWorkers = new BasicEventList();
    
    // list of current workers assigend to the project
    private EventList currentTeam = new BasicEventList();
    
    // used to filter the view of the avaliableWorkers list by work role for display on the form
    private WorkerRoleMatcherEditor matcherEditor = new WorkerRoleMatcherEditor();
    private FilterList filteredAvaliableWorkers = new FilterList(avaliableWorkers, matcherEditor);
    
    // overall list of additions and omission from the currentTeam
    private ArrayList<Worker> addToTeamList = new ArrayList();
    private ArrayList<Worker> removeFromTeamList = new ArrayList();
    
    // Actionlistner for selections of row in the current team table
    private ListSelectionListener tabTeamListner = new ListSelectionListener()
    {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            // check if a worker with task has been selected for removal
            
            // get list of selected items & iterate over them
            int[] selection = tabTeam.getSelectedRows();
            for(int item: selection)
            {
                //convert list ref to worker object
                WorkerWrapper worker = (WorkerWrapper)currentTeam.get(tabTeam.convertRowIndexToModel(item));
                
                // check number of tasks for worker
                if(worker.getNumberOfTasks() > 0 )
                {
                    // deselect workers with tasks assigned to them
                    tabTeam.getSelectionModel().removeSelectionInterval(item, item);
                    
                    // display message explaining why worker cannot be selected.
                    JOptionPane.showMessageDialog(DefineTeamUI.this, "Indivduals with tasks cannot be removed","Information",JOptionPane.ERROR_MESSAGE);
                }
            }
            
            // check if there any items still selected and enable / disable the remove button
            selection = tabTeam.getSelectedRows();
            if (selection.length > 0)
            {
                btnRemoveWorkers.setEnabled(true);
            }
            else
            {
                btnRemoveWorkers.setEnabled(false);
            }
        }
    };
    
    // Actionlistner for selections of row in the available workerstable
    private ListSelectionListener tabAvaliableListner = new ListSelectionListener()
    {
        @Override
        public void valueChanged(ListSelectionEvent e) 
        {
            // check if there any items still selected and enable / disable the remove button
            int[] selection = tabAvaliableWorkers.getSelectedRows();
            if (selection.length > 0)
            {
                btnAddWorkers.setEnabled(true);
            }
            else
            {
                btnAddWorkers.setEnabled(false);
            }
        }
    };

    
    /**
     * Instantiates a new DefineTeamUI form
     */
    public DefineTeamUI(Project project) {
        super("Define Project Team",false,true,false,false);
        initComponents();
        
        // load initial form data
        loadFormData();
        
        // set the project combo to the passed in project if avaliable
        if (project != null) cmbProject.setSelectedItem(project);
    }
    
    /**
     * Loads initial form data
     */
    private void loadFormData()
    {
        // check that there is at least one project defined
        ProjectRegister proReg = ProjectRegister.getInstance();
        
        // load avaliable projects
        loadProjectCombo();

        // get reference to worker register
        WorkerRegister workerReg = WorkerRegister.getInstance();

        // load QC team leaders
        loadQCTeamLeaderCombo(workerReg);

        // load roles to filter by
        loadFilterByRoleCombo();

        // load arraylist avaliableWorkers with all workers except managers and QC team leaders
        getAllWorkers(workerReg);

        // setup formatting etc. for avaliable workers table
        setupAvaliableTable();

        // setup formatting etc. for current project team table
        setupCurrentTeamTable();

        // initialise controls data
        loadTeamTable();

        // register for BLL events
        registerEvents();
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

        lblSelectProject = new javax.swing.JLabel();
        cmbProject = new javax.swing.JComboBox();
        lblQCLeader = new javax.swing.JLabel();
        cmbQC_TeamLeader = new javax.swing.JComboBox();
        lblFilterByRole = new javax.swing.JLabel();
        cmbFilterByRole = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabAvaliableWorkers = new javax.swing.JTable();
        btnAddWorkers = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabTeam = new javax.swing.JTable();
        lblCurrentTeam = new javax.swing.JLabel();
        btnRemoveWorkers = new javax.swing.JButton();
        btnTeamComplete = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        lblSelectProject.setText("Select Project");

        cmbProject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProjectActionPerformed(evt);
            }
        });

        lblQCLeader.setText("Quality Control Team Leader");

        cmbQC_TeamLeader.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblFilterByRole.setText("Filter by role");

        cmbFilterByRole.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbFilterByRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterByRoleActionPerformed(evt);
            }
        });

        tabAvaliableWorkers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Staff Member", "Work Role", "Current Nummber of Projects"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabAvaliableWorkers.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabAvaliableWorkers);

        btnAddWorkers.setText("Add Selected Members");
        btnAddWorkers.setEnabled(false);
        btnAddWorkers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddWorkersActionPerformed(evt);
            }
        });

        tabTeam.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Staff Member", "Work Role"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabTeam);

        lblCurrentTeam.setText("Current Team Members");

        btnRemoveWorkers.setText("Remove Selected Members");
        btnRemoveWorkers.setEnabled(false);
        btnRemoveWorkers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveWorkersActionPerformed(evt);
            }
        });

        btnTeamComplete.setText("Team Complete");
        btnTeamComplete.setEnabled(false);
        btnTeamComplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTeamCompleteActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
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
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblCurrentTeam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddWorkers))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRemoveWorkers, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnTeamComplete))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblQCLeader)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbQC_TeamLeader, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFilterByRole)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbFilterByRole, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblSelectProject)
                                .addGap(18, 18, 18)
                                .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 265, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelectProject)
                    .addComponent(cmbProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblQCLeader)
                    .addComponent(cmbQC_TeamLeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFilterByRole)
                    .addComponent(cmbFilterByRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAddWorkers)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblCurrentTeam)
                        .addGap(8, 8, 8)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoveWorkers)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTeamComplete)
                    .addComponent(btnCancel))
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Re-populates the other form controls when the project combo box selects a project to display
     * @param evt event arguments
     */
    private void cmbProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProjectActionPerformed
        // load form details for the selected team
        loadTeamTable();
    }//GEN-LAST:event_cmbProjectActionPerformed

    /**
     * Closes the form without updating any worker changes
     * @param evt event arguments
     */
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        deregisterEvents();
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    /**
     * Adds selected workers to the current team
     * @param evt event arguments
     */
    private void btnAddWorkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddWorkersActionPerformed
        // get array of selected items
        int[] selection = tabAvaliableWorkers.getSelectedRows();
        
        // temp storage for the selected objects
        ArrayList<Worker> tempList = new ArrayList();
        
        // clear selection from table
        tabAvaliableWorkers.clearSelection();
        
        // convert selection list into a collection of objects
        for(int item: selection)
        {
            tempList.add((Worker) avaliableWorkers.get(tabAvaliableWorkers.convertRowIndexToModel(item)));
        }
        
        // wrap the workers in WorkerWrapper from the templist and add to the currentTeam
        Project project = (Project)cmbProject.getSelectedItem();
        for(Worker item: tempList)
        {
            // WorkerWrapper associates the current project with the worker so that
            // current team table can display the number of tasks for this project
            currentTeam.add(new WorkerWrapper(project,item));
        }
        
        // remove workers from the avaliable list
        avaliableWorkers.removeAll(tempList);
        
        // add workers to the overall add list for updating the Project object later
        addToTeamList.addAll(tempList);
        
        // enable / disable the Team Complete btn based upon there being any updates
        updateTeamCompleteBtnStatus();
    }//GEN-LAST:event_btnAddWorkersActionPerformed

    /**
     * Removes selected workers to the current team
     * @param evt event arguments
     */
    private void btnRemoveWorkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveWorkersActionPerformed
        // get array of selected items
        int[] selection = tabTeam.getSelectedRows();
        
        // temp storage for the selected objects
        ArrayList<WorkerWrapper> tempList = new ArrayList();
        
        // clear selection from table
        tabTeam.clearSelection();
        
        // convert index of selections to a collection of objects
        for(int item: selection)
        {
            tempList.add((WorkerWrapper) currentTeam.get(tabTeam.convertRowIndexToModel(item)));
        }
        
        // add and remove workers + update the overall remove list
        for(WorkerWrapper item: tempList)
        {
            // remove form current team
            currentTeam.remove(item);
            
            // add the worker back the avaliable list as a Worker
            avaliableWorkers.add(item.getWorker());
            
            // add worker to the overallremove list for updating Project later
            removeFromTeamList.add(item.getWorker());
        }
        
        // check that the overall lists do not contain duplicates e.g add worker1 & remove worker1
        Iterator<Worker> iterator = removeFromTeamList.iterator();
        while(iterator.hasNext())
        {
            Worker worker = iterator.next();
            // check both list contain the same worker
            if(addToTeamList.contains(worker))
            {
                // remove both occurances of the worker
                addToTeamList.remove(worker);
                iterator.remove();
            }
        }
        
        // enable / disable the Team Complete btn based upon there being any updates
        updateTeamCompleteBtnStatus();
    }//GEN-LAST:event_btnRemoveWorkersActionPerformed

    /**
     *  toggles the enabled state of the update team button based upon there being any updates to the team
     */
    private void updateTeamCompleteBtnStatus() 
    {
        // if both lists are empty then disable the update team btn; otherwise enable it 
        if(addToTeamList.isEmpty() && removeFromTeamList.isEmpty())
        {
            btnTeamComplete.setEnabled(false);
        }
        else
        {
            btnTeamComplete.setEnabled(true);
        }
    }
    
    /**
     * Updates changes to the project team and closes the form
     * 
     * @param evt event arguments
     */
    private void btnTeamCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTeamCompleteActionPerformed
        // get reference to the project to update
        Project project = (Project)cmbProject.getSelectedItem();
        
        // set the QC team leader
        project.setQC_TeamLeader((Staff) cmbQC_TeamLeader.getSelectedItem());
        
        // pass in the overall lists of additions and omission of workers
        // returns false if the list of workers to be removed contained a 
        // worker that had assigned task - this can happen between being added
        // for removal and the user committing the update
        if (project.updateWorkers(addToTeamList,removeFromTeamList))
        {
            // if all updates succeeded the close the form
            deregisterEvents();
            try {
                this.setClosed(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            // because a worker was requested to be removed that had assigned tasks
            // reload status of project team after attempted update
            loadTeamTable();
            
            // display message explaining that an individual could not be removed because he had tasks.
            JOptionPane.showMessageDialog(DefineTeamUI.this, "Some Indivdual(s) now have tasks\nand could not be removed","Information",JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_btnTeamCompleteActionPerformed

    /**
     * Filter the avaliable workers table based upon the FilterByRole combo
     * @param evt event arguments
     */
    private void cmbFilterByRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterByRoleActionPerformed
        // clear any selection made from the avaliable workers table before filtering
        tabTeam.clearSelection();
        
        // get the role to filter the table by
        WorkerRoles role = (WorkerRoles)cmbFilterByRole.getSelectedItem();
        
        // raise the update list event with the new role to filter by
        matcherEditor.updateList(role);
    }//GEN-LAST:event_cmbFilterByRoleActionPerformed

    /**
     * Re-populates the form based upon the project selected in the project combo
     */
    private void loadTeamTable()
    {
        // get the current project
        Project project = (Project)cmbProject.getSelectedItem();
        
        // clear the current team list
        currentTeam.clear();
        
        // get the list of worker from the project & add to the current team
        // wrapped in WorkerWrapper, so that they display their number of tasks
        for(Worker worker: project.getWorkers())
        {
            currentTeam.add(new WorkerWrapper(project, worker));
        }
        
        // set the text on the update button
        // set to "create" if the current team list is empty else "update"
        if(currentTeam.isEmpty())
        {
            btnTeamComplete.setText("Create Team");
        }
        else
        {
            btnTeamComplete.setText("Update Team");
        }
                
        // reset the avaliable team members
        // clear current contents
        avaliableWorkers.clear();
        
        // load a full list of workers
        avaliableWorkers.addAll(allWorkers);
        
        // remove workers that are already part of the team
        for(Object worker: currentTeam)
        {
            WorkerWrapper item = (WorkerWrapper) worker;
            avaliableWorkers.remove(item.getWorker());
        }
        
        // set team leader for project if there is one
        if (project.getQC_TeamLeader() != null) cmbQC_TeamLeader.setSelectedItem(project.getQC_TeamLeader());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddWorkers;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnRemoveWorkers;
    private javax.swing.JButton btnTeamComplete;
    private javax.swing.JComboBox cmbFilterByRole;
    private javax.swing.JComboBox cmbProject;
    private javax.swing.JComboBox cmbQC_TeamLeader;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCurrentTeam;
    private javax.swing.JLabel lblFilterByRole;
    private javax.swing.JLabel lblQCLeader;
    private javax.swing.JLabel lblSelectProject;
    private javax.swing.JTable tabAvaliableWorkers;
    private javax.swing.JTable tabTeam;
    // End of variables declaration//GEN-END:variables

    /**
     * Loads project combo box with the avaliable projects
     */
    private void loadProjectCombo() {
        // get ref to project register
        ProjectRegister proReg = ProjectRegister.getInstance();
        
        // create a new model
        DefaultComboBoxModel projectComboModel = new DefaultComboBoxModel();
        
        // load the model and set the combo box to the new model
        for(Project project: proReg.getProjectList())
        {
            projectComboModel.addElement(project);
        }
        cmbProject.setModel(projectComboModel);
    }
    
    /**
     * Loads all of the avaliable QC Team Leaders into the QC Team Leader Combo
     * @param workerReg Reference to the WorkerRegister object
     */
    private void loadQCTeamLeaderCombo(WorkerRegister workerReg)
    {
        // create a new model
        DefaultComboBoxModel qCTeamLeadersModel = new DefaultComboBoxModel();
               
        // find and add all QC Team leaders using findByRole to filter result
        for(Worker worker: workerReg.findByRole(WorkerRoles.QC_TEAM_LEADER, WorkerType.STAFF))
        {
            qCTeamLeadersModel.addElement(worker);
        }
        
        // set combo box model to loaded data
        cmbQC_TeamLeader.setModel(qCTeamLeadersModel);
    }
    
    /**
     * Load the filterByRole combo box with the avaliable roles - but excluding 
     * QC Team Leaders and Project Managers
     */
    private void loadFilterByRoleCombo()
    {
        // Create a new model
        DefaultComboBoxModel workerRolesModel = new DefaultComboBoxModel();
        
        // find all staff that are not managers or QC Team Leaders
        // iterate over the enum add each except mangers and QC Team Leaders
        for(WorkerRoles role :WorkerRoles.values())
        {
            if(role != WorkerRoles.QC_TEAM_LEADER && role != WorkerRoles.PROJECT_MANAGER)
            {
                workerRolesModel.addElement(role);
            }
        }
        
        // set the filter by role combobox to roles less Manager and QC Team Leaders found
        cmbFilterByRole.setModel(workerRolesModel);
    }
    
    /**
     * Load all avaliable workers into allWorkers except Project Managers and QC Team Leaders
     * @param workerReg Reference to the WorkerRegister object
     */
    private void getAllWorkers(WorkerRegister workerReg)
    {
        // find all staff that are not managers or QC Team Leaders
        // iterate over all roles
        for(WorkerRoles role :WorkerRoles.values())
        {
            // only use the role if it is not Project Manager or QC Team Leader
            if(role != WorkerRoles.QC_TEAM_LEADER && role != WorkerRoles.PROJECT_MANAGER)
            {
                // Add all the workers to the list that match the search
                allWorkers.addAll(workerReg.findByRole(role, WorkerType.STAFF));
            }
        }
        
        // find and add all freelancers
        allWorkers.addAll(workerReg.findByRole(null, WorkerType.FREELANCER));
    }
    
    /**
     * Setup the GlazedList table objects for avaliable workers table
     */
    private void setupAvaliableTable()
    {
        // set table format for the avaliable staff table
        TableFormat tableFormat = GlazedLists.tableFormat(Worker.class,
            // Names of the properties to fetch
            new String[] {"Name","RoleDescription","NumProjects","WorkerType"},
            // Names for the columns
            new String[] {"Name", "Work Role","No. Projects","Employment Type"});
        
        // set table up
        EventTableModel tableModel = new EventTableModel(filteredAvaliableWorkers, tableFormat);
        tabAvaliableWorkers.setModel(tableModel);
        tabAvaliableWorkers.getSelectionModel().addListSelectionListener(tabAvaliableListner);
    }
    
    /**
     * Setup the GlazedList table objects for the current team table
     */
    private void setupCurrentTeamTable()
    {
        // set table format for the avaliable staff table
        TableFormat tableFormat = GlazedLists.tableFormat(WorkerWrapper.class,
            // Names of the properties to fetch
            new String[] {"Name","RoleDescription","NumProjects","NumberOfTasks","WorkerType"},
            // Names for the columns
            new String[] {"Name", "Work Role","No. Projects","No. of Tasks","Employment Type"});
        
        // set table up
        EventTableModel tableModel = new EventTableModel(currentTeam, tableFormat);
        tabTeam.setModel(tableModel);
        tabTeam.getSelectionModel().addListSelectionListener(tabTeamListner);
    }

    /**
     * Receives notifications of subscribed events
     * @param object the object raising the events
     * @param arg any parameters being passed by the object raising the events
     */
    @Override
    public void update(Observable object, Object arg) {
        // check to see if the object is the RrojectRegister
        if (object instanceof ProjectRegister)
        {
            // Events only rasied for additions of omission of projects
            // reload the avaliable projects
            loadProjectCombo();
        }
    }
    
    /**
     * Class used for the filtering of the avaliable workers by role within this form
     */
    private static class WorkerRoleMatcherEditor extends AbstractMatcherEditor 
    {
        /**
         * Raise the event to update the filtered list of avaliable workers by the filter
         * @param filter A WorkersRoles as a filter criteria
         */
        public void updateList(WorkerRoles filter)
        {
            // check if no filter required
            if (filter == WorkerRoles.ALL)
                // clear filter and match all
                this.fireMatchAll();
            else
                // filter by the filter and create a RolerMatcher for the job
                this.fireChanged(new RoleMatcher(filter));
        }

        /**
         * Class used to carry out filter matching by GlazedLists
         */
        private static class RoleMatcher implements Matcher 
        {
            // role to filter by
            private WorkerRoles role;

            public RoleMatcher(WorkerRoles filter) 
            {
                this.role = filter;
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
                final Worker worker = (Worker) item;
                
                // returns true if the worker matches the role passed into the object
                return this.role.equals(worker.getRole());
            }
        }
    }
}
