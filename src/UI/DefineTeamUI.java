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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 *
 * @author James Staite
 */
public class DefineTeamUI extends javax.swing.JInternalFrame {

    private ArrayList<Worker> allWorkers = new ArrayList();
    
    private EventList avaliableWorkers = new BasicEventList();
    
    private EventList currentTeam = new BasicEventList();
    
    private WorkerRoleMatcherEditor matcherEditor = new WorkerRoleMatcherEditor();
    private FilterList filteredAvaliableWorkers = new FilterList(avaliableWorkers, matcherEditor);
    
    private ArrayList<Worker> addToTeamList = new ArrayList();
    private ArrayList<Worker> removeFromTeamList = new ArrayList();
        
    private ListSelectionListener tabTeamListner = new ListSelectionListener()
    {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            // check if a worker with task has been selected for removal
            int[] selection = tabTeam.getSelectedRows();
            for(int item: selection)
            {
                WorkerWrapper worker = (WorkerWrapper)currentTeam.get(tabTeam.convertRowIndexToModel(item));
                
                // check number of tasks
                if(worker.getNumberOfTasks() > 0 )
                {
                    // deselect individual
                    tabTeam.getSelectionModel().removeSelectionInterval(item, item);
                    
                    // display message explaining why individual cannot be selected.
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
     * Creates new DefineTeamUI form
     */
    public DefineTeamUI(Project project) {
        super("Define Project Team",false,true,false,false);
        initComponents();
        loadFormData();
        if (project != null) cmbProject.setSelectedItem(project);
    }
    
    private void loadFormData()
    {
        // check that there is at least one project defined
        ProjectRegister proReg = ProjectRegister.getInstance();
        if (proReg.getProjectList().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "There are no projects to allocate staff to.\nCreate a project first","Information",JOptionPane.ERROR_MESSAGE);
            try {
                this.setClosed(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

    private void cmbProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProjectActionPerformed
        loadTeamTable();
    }//GEN-LAST:event_cmbProjectActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnAddWorkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddWorkersActionPerformed
        // get array of selected items
        int[] selection = tabAvaliableWorkers.getSelectedRows();
        
        // temp storage for the selected objects
        ArrayList<Worker> tempList = new ArrayList();
        
        // clear selection from table
        tabAvaliableWorkers.clearSelection();
        
        // convert index to a collection of objects
        for(int item: selection)
        {
            tempList.add((Worker) avaliableWorkers.get(tabAvaliableWorkers.convertRowIndexToModel(item)));
        }
        
        // add objects
        Project project = (Project)cmbProject.getSelectedItem();
        for(Worker item: tempList)
        {
            currentTeam.add(new WorkerWrapper(project,item));
        }
        
        // remove objects
        avaliableWorkers.removeAll(tempList);
        
        // add workers to the add list
        addToTeamList.addAll(tempList);
        
        updateTeamCompleteBtnStatus();
    }//GEN-LAST:event_btnAddWorkersActionPerformed

    private void btnRemoveWorkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveWorkersActionPerformed
        // get array of selected items
        int[] selection = tabTeam.getSelectedRows();
        
        // temp storage for the selected objects
        ArrayList<WorkerWrapper> tempList = new ArrayList();
        
        // clear selection from table
        tabTeam.clearSelection();
        
        // convert index to a collection of objects
        for(int item: selection)
        {
            tempList.add((WorkerWrapper) currentTeam.get(tabTeam.convertRowIndexToModel(item)));
        }
        
        // add and remove objects + update the overall remove list
        for(WorkerWrapper item: tempList)
        {
            currentTeam.remove(item);
            avaliableWorkers.add(item.getWorker());
            
            // add worker to the remove list
            removeFromTeamList.add(item.getWorker());
        }
        
        // check that the overall list do not contain duplicates
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
        
        updateTeamCompleteBtnStatus();
    }//GEN-LAST:event_btnRemoveWorkersActionPerformed

    private void updateTeamCompleteBtnStatus() 
    {
         if(addToTeamList.isEmpty() && removeFromTeamList.isEmpty())
         {
             btnTeamComplete.setEnabled(false);
         }
         else
         {
             btnTeamComplete.setEnabled(true);
         }
    }
       
    private void btnTeamCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTeamCompleteActionPerformed
        Project project = (Project)cmbProject.getSelectedItem();
        
        project.setQC_TeamLeader((Staff) cmbQC_TeamLeader.getSelectedItem());
        
        if (project.updateWorkers(addToTeamList,removeFromTeamList))
        {
            try {
                this.setClosed(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            // reload status of project team after attempted update
            loadTeamTable();
            
            // display message explaining that an individual could not be removed because he had tasks.
            JOptionPane.showMessageDialog(DefineTeamUI.this, "Some Indivdual(s) now have tasks\nand could not be removed","Information",JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_btnTeamCompleteActionPerformed

    private void cmbFilterByRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterByRoleActionPerformed
        tabTeam.clearSelection();
        WorkerRoles role = (WorkerRoles)cmbFilterByRole.getSelectedItem();
        matcherEditor.updateList(role);
    }//GEN-LAST:event_cmbFilterByRoleActionPerformed

    private void loadTeamTable()
    {
        Project project = (Project)cmbProject.getSelectedItem();
        currentTeam.clear();
        
        for(Worker worker: project.getWorkers())
        {
            currentTeam.add(new WorkerWrapper(project, worker));
        }
        
        if(currentTeam.isEmpty())
        {
            btnTeamComplete.setText("Create Team");
        }
        else
        {
            btnTeamComplete.setText("Update Team");
        }
                
        // reset the avaliable team members
        avaliableWorkers.clear();
        // load all avaliable worker
        avaliableWorkers.addAll(allWorkers);
        
        // remove worker that are already part of the team
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

    private void loadProjectCombo() {
        // load projects combo box
        ProjectRegister proReg = ProjectRegister.getInstance();
        DefaultComboBoxModel projectComboModel = new DefaultComboBoxModel();
               
        for(Project project: proReg.getProjectList())
        {
            projectComboModel.addElement(project);
        }
        cmbProject.setModel(projectComboModel);
    }
    
    private void loadQCTeamLeaderCombo(WorkerRegister workerReg)
    {
        // load QC TeamLeaders 
        DefaultComboBoxModel qCTeamLeadersModel = new DefaultComboBoxModel();
               
        // find and add all QC Team leaders
        for(Worker worker: workerReg.findByRole(WorkerRoles.QC_TEAM_LEADER, WorkerType.STAFF))
        {
            qCTeamLeadersModel.addElement(worker);
        }
        // set combo box model to loaded data
        cmbQC_TeamLeader.setModel(qCTeamLeadersModel);
    }
    
    private void loadFilterByRoleCombo()
    {
        // load staff roles
        DefaultComboBoxModel workerRolesModel = new DefaultComboBoxModel();
        
        // find all staff that are not managers or QC Team Leaders
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
    
    private void getAllWorkers(WorkerRegister workerReg)
    {
        // find all staff that are not managers or QC Team Leaders
        for(WorkerRoles role :WorkerRoles.values())
        {
            if(role != WorkerRoles.QC_TEAM_LEADER && role != WorkerRoles.PROJECT_MANAGER)
            {
                // Add all the elements
                allWorkers.addAll(workerReg.findByRole(role, WorkerType.STAFF));
            }
        }
        // find and add freelancers
        allWorkers.addAll(workerReg.findByRole(null, WorkerType.FREELANCER));
    }
    
    private void setupAvaliableTable()
    {
        // set table format for the avaliable staff table
        TableFormat tableFormat = GlazedLists.tableFormat(Worker.class,
            // Names of the properties to fetch
            new String[] {"Name","Role","NumProjects","WorkerType"},
            // Names for the columns
            new String[] {"Name", "Work Role","No. Projects","Employment Type"});
        
        // set table up
        EventTableModel tableModel = new EventTableModel(filteredAvaliableWorkers, tableFormat);
        tabAvaliableWorkers.setModel(tableModel);
        tabAvaliableWorkers.getSelectionModel().addListSelectionListener(tabAvaliableListner);
    }
    
    private void setupCurrentTeamTable()
    {
        // set table format for the avaliable staff table
        TableFormat tableFormat = GlazedLists.tableFormat(WorkerWrapper.class,
            // Names of the properties to fetch
            new String[] {"Name","Role","NumProjects","NumberOfTasks","WorkerType"},
            // Names for the columns
            new String[] {"Name", "Work Role","No. Projects","No. of Tasks","Employment Type"});
        
        // set table up
        EventTableModel tableModel = new EventTableModel(currentTeam, tableFormat);
        tabTeam.setModel(tableModel);
        tabTeam.getSelectionModel().addListSelectionListener(tabTeamListner);
    }
    
    private static class WorkerRoleMatcherEditor extends AbstractMatcherEditor 
    {
        public void updateList(WorkerRoles filter)
        {
            //final String nationality = (String) this.nationalityChooser.getSelectedItem();
            if (filter == WorkerRoles.ALL)
                this.fireMatchAll();
            else
                this.fireChanged(new RoleMatcher(filter));
        }

        private static class RoleMatcher implements Matcher 
        {
            private WorkerRoles role;

            public RoleMatcher(WorkerRoles filter) 
            {
                this.role = filter;
            }

            public boolean matches(Object item) 
            {
                final Worker worker = (Worker) item;
                return this.role.toString().equals(worker.getRole());
            }
        }
    }
}
