package UI;

import BLL.Project;
import BLL.QCReport;
import BLL.TaskItem;
import BLL.Worker;
import BLL.QCReport.Fault;
import BLL.TaskStatus;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventJXTableModel;
import java.beans.PropertyVetoException;
import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Form for QC members to create QC Reports for the media viewed - UI
 * @author James Staite
 */
public class QCMemberReport extends javax.swing.JInternalFrame 
{
    // user using this form
    Worker user;
    
    // project that the task belongs to
    Project project;
    
    // the task the report will be about
    TaskItem task;
    
    // QCReport
    QCReport report;
    
    // forms current fault
    Fault fault;
    
    // fault panel flags
    final int newstate = 1;
    final int update = 2;
    final int noState = 3;
    
    // flags fault panel state
    int newUpdateFlag = noState;
    
    /**
     * Creates new form QCMemberReport
     */
    public QCMemberReport(TaskItem task, Project project, Worker user) 
    {
        super("Quality Control Report",false,true,false,false);
        initComponents();
        
        // store user details
        this.user = user;
        
        // store porject
        this.project = project;
        
        // store task
        this.task = task;
        
        // check if the task already has a QCReport
        if (task.getQCReport() == null) 
        {
            // if not create a new report
            QCReport newReport = new QCReport(task.getMediaItem().getFile());
            
            // assign to the task
            task.setQCReport(newReport);
            
            // set a field for use in form
            this.report = newReport;
        }
        else
        {
            // get the existing report
            report = task.getQCReport();
        }
        
        // display task details
        displayTaskDetails();
        
        // load the fault table with details from the QCReport
        loadFaultsTable();
        
        // set up table listener
        tblFlauts.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // possible row selection
                if (!event.getValueIsAdjusting()) rowselection();
            }
        });
        
        // set up listener events for the text files
        faultDescription.addCaretListener(new CaretListener() 
        {
            @Override
            public void caretUpdate(CaretEvent e) 
            {
                validateFault();
            }
        });
        
        faultPosition.addCaretListener(new CaretListener() 
        {
            @Override
            public void caretUpdate(CaretEvent e) 
            {
                validateFault();
            }
        });
    }
    
    /**
     * Displays the task details
     */
    private void displayTaskDetails()
    {
        txtProject.setText(project.getName());
        txtTaskContentName.setText(task.getMediaItem().getName());
        txtTaskContentDescription.setText(task.getMediaItem().getDescription());
        txtTaskMediaRequired.setText(task.getMediaDescription());
        txtTaskDescription.setText(task.getDescription());
        String path = report.getFilename();
        int index = path.lastIndexOf("\\");
        String fileName = path.substring(index + 1);
        txtTaskFilename.setText(fileName);
        btnOpenFile.setEnabled(true);
    }
    
    /**
     * Copies the associated media file to a location specified by the user
     */
    private void openFile()
    {
        File file = new File(report.getFilename());
        String path = report.getFilename();
        int index = path.lastIndexOf(".");
        String fileExt = path.substring(index);
        JFileChooser chooser = new JFileChooser();
        chooser.resetChoosableFileFilters();
        int retrival = chooser.showSaveDialog(this);
        if (retrival == JFileChooser.APPROVE_OPTION) 
        {
            try 
            {
                File target = new File(chooser.getSelectedFile().toString() + fileExt);
                Files.copy(file.toPath(),target.toPath());
            } catch (Exception ex) {
            }
        }
    }
    
    /**
     * Load the QCReport faults into the table
     */
    private void loadFaultsTable()
    {
        // define properties and column labels
        String[] propertyNames = {"Description", "Position", "Severity"};
        String[] columnLabels = {"Description", "Position", "Severity"};
        int[] columnSizes = {180,30,5};
        
        // set table format for unallocated task items
        TableFormat tableFormat = GlazedLists.tableFormat(Fault.class, propertyNames, columnLabels);
        EventJXTableModel tableModel = new EventJXTableModel(report.getReportList(), tableFormat);
        tblFlauts.setModel(tableModel);
        
        // set the preferred column widths
        int col = 0;
        for(int width: columnSizes)
        {
            tblFlauts.getColumnModel().getColumn(col).setPreferredWidth(width);
            col++;
        }
        
        // setup the selection setting for the table
        tblFlauts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFlauts.setRowSelectionAllowed(true);
        tblFlauts.setColumnSelectionAllowed(false);
    }
    
    /**
     * Processes the selected row
     */
    private void rowselection()
    {
        //check if any items are selected
        if (tblFlauts.getSelectedRow() > -1)
        {
            // hold the first item to use for comparison
            EventJXTableModel tableModel = (EventJXTableModel) tblFlauts.getModel();
            fault = (Fault) tableModel.getElementAt(tblFlauts.getSelectedRow());
            
            // display faults details
            faultDescription.setText(fault.getDescription());
            faultPosition.setText(fault.getPosition());
            cmbFaultSeveity.setSelectedIndex(fault.getSeverity()-1);
            
            // update controls status
            setFaultControlsEnabled(true);
            btnUpdate.setEnabled(false);
            btnUpdate.setText("Update");
            btnCancelRemove.setEnabled(true);
            btnAdd.setEnabled(false);
            btnCancelRemove.setText("Remove");
        }
        else
        {
            // clear controls if no row selected
            clearFaultControls();
            setFaultControlsEnabled(false);
            btnUpdate.setEnabled(false);
            btnCancelRemove.setEnabled(false);
            btnAdd.setEnabled(true);
            fault = null;
        }
    }
    
    /**
     * Handles the Add button
     */
    private void Addbutton()
    {
        // clear the fault controls
        clearFaultControls();
        
        // enable the fault controls
        setFaultControlsEnabled(true);
        
        // set the form controls state
        btnAdd.setEnabled(false);
        newUpdateFlag = newstate;
        btnCancelRemove.setText("Cancel");
        btnCancelRemove.setEnabled(true);
        faultDescription.requestFocus();
    }
    
    /**
     * Handles the update button action
     */
    private void updateButton()
    {
        // check state of the form
        if (newUpdateFlag == update)
        {
            // update the fault details
            fault.setDescription(faultDescription.getText());
            fault.setPosition(faultPosition.getText());
            fault.setSeverity(Integer.parseInt((String)cmbFaultSeveity.getSelectedItem()));
        }
        else
        {
            // create a new fault
            report.addFault(faultDescription.getText(), faultPosition.getText(), Integer.parseInt((String)cmbFaultSeveity.getSelectedItem())); 
        }
        // reset the form state
        newUpdateFlag = noState;
        
        // reset the form
        clearFaultControls();
        setFaultControlsEnabled(false);
        btnUpdate.setEnabled(false);
        btnCancelRemove.setEnabled(false);
        btnAdd.setEnabled(true);
        fault = null;
        tblFlauts.clearSelection();
        btnAdd.requestFocus();
    }
    
    /**
     * Handles the cancel button
     */
    private void cancelRemovebutton()
    {
        // check form state
        if (newUpdateFlag == noState)
        {
            // if not updating a fault - remove fault
            if (fault != null) report.removeFault(fault);
        }
        else
        {
            // cancel the current action and reset the form
            clearFaultControls();
            setFaultControlsEnabled(false);
            btnUpdate.setEnabled(false);
            btnCancelRemove.setEnabled(false);
            btnAdd.setEnabled(true);
            fault = null;
            tblFlauts.clearSelection();
            newUpdateFlag = noState;
        }
    }
    
    /**
     * Complete the QCReport and close the form
     */
    private void submitReport()
    {
        task.setStatus(TaskStatus.COMPLETE, "Report submitted by "+ user.getName());
        closeButton();
    }
    
    /**
     * Validate the form entry
     */
    private void validateFault()
    {
        boolean valid = true;
        
        // check the description and position field
        if(!faultDescription.isEnabled() || faultDescription.getText().length() == 0) valid = false;
                
        if(!faultPosition.isEnabled() || faultPosition.getText().length() == 0) valid = false;
        
        // change the form state based on validity
        if (valid) 
        {
            if (newUpdateFlag == newstate) 
            {
                btnUpdate.setText("Add");
            } 
            else 
            {
                btnUpdate.setText("Update");
                newUpdateFlag = update;
            }
            btnUpdate.setEnabled(true);
            btnCancelRemove.setEnabled(true);
        }
        else
        {
            btnUpdate.setEnabled(false);
            btnCancelRemove.setEnabled(false);
        }
    }
    
    /**
     * Handles the close button
     */
    private void closeButton()
    {
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    /**
     * clear fault entry controls
     */
    private void clearFaultControls()
    {
        faultDescription.setText("");
        faultPosition.setText("");
        cmbFaultSeveity.setSelectedIndex(0);
    }
    
    /**
     * sets the enabled state of fault entry controls
     * @param value value
     */
    private void setFaultControlsEnabled(boolean value)
    {
        faultDescription.setEnabled(value);
        faultPosition.setEnabled(value);
        cmbFaultSeveity.setEnabled(value);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        lblContentName = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTaskContentName = new javax.swing.JTextField();
        lblContentDescription = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtTaskContentDescription = new javax.swing.JTextArea();
        lblMediaRequired = new javax.swing.JLabel();
        txtTaskMediaRequired = new javax.swing.JTextField();
        lblOTaskDescription = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtTaskDescription = new javax.swing.JTextArea();
        lblProject = new javax.swing.JLabel();
        txtProject = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTaskFilename = new javax.swing.JTextField();
        btnOpenFile = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFlauts = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        faultDescription = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        faultPosition = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbFaultSeveity = new javax.swing.JComboBox();
        btnAdd = new javax.swing.JButton();
        btnCancelRemove = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnSubmit = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane5.setViewportView(jTextArea1);

        lblContentName.setBorder(javax.swing.BorderFactory.createTitledBorder("Task Details"));

        jLabel1.setText("Content Name");

        txtTaskContentName.setEnabled(false);

        lblContentDescription.setText("Content Description");

        txtTaskContentDescription.setColumns(20);
        txtTaskContentDescription.setLineWrap(true);
        txtTaskContentDescription.setRows(5);
        txtTaskContentDescription.setWrapStyleWord(true);
        txtTaskContentDescription.setEnabled(false);
        jScrollPane2.setViewportView(txtTaskContentDescription);

        lblMediaRequired.setText("Media Required");

        txtTaskMediaRequired.setEnabled(false);

        lblOTaskDescription.setText("Task Description");

        txtTaskDescription.setColumns(20);
        txtTaskDescription.setLineWrap(true);
        txtTaskDescription.setRows(5);
        txtTaskDescription.setWrapStyleWord(true);
        txtTaskDescription.setEnabled(false);
        jScrollPane4.setViewportView(txtTaskDescription);

        lblProject.setText("Project");

        txtProject.setEnabled(false);

        jLabel2.setText("File");

        txtTaskFilename.setEnabled(false);

        btnOpenFile.setText("Open File");
        btnOpenFile.setEnabled(false);
        btnOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lblContentNameLayout = new javax.swing.GroupLayout(lblContentName);
        lblContentName.setLayout(lblContentNameLayout);
        lblContentNameLayout.setHorizontalGroup(
            lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblContentNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(lblProject)
                    .addComponent(lblOTaskDescription)
                    .addComponent(lblMediaRequired)
                    .addComponent(lblContentDescription)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTaskContentName)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addComponent(txtTaskMediaRequired)
                    .addComponent(jScrollPane4)
                    .addComponent(txtProject)
                    .addGroup(lblContentNameLayout.createSequentialGroup()
                        .addComponent(txtTaskFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnOpenFile)))
                .addContainerGap())
        );
        lblContentNameLayout.setVerticalGroup(
            lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblContentNameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProject)
                    .addComponent(txtProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTaskContentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContentDescription))
                .addGap(9, 9, 9)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMediaRequired)
                    .addComponent(txtTaskMediaRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOTaskDescription))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lblContentNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTaskFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnOpenFile))
                .addGap(17, 17, 17))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Quality Control Report"));

        tblFlauts.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblFlauts);

        jLabel3.setText("Fault");

        jLabel4.setText("Description");

        faultDescription.setColumns(20);
        faultDescription.setLineWrap(true);
        faultDescription.setRows(5);
        faultDescription.setWrapStyleWord(true);
        faultDescription.setEnabled(false);
        jScrollPane3.setViewportView(faultDescription);

        jLabel5.setText("Time / Position");

        faultPosition.setEnabled(false);

        jLabel6.setText("Seveity");

        cmbFaultSeveity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        cmbFaultSeveity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFaultSeveityActionPerformed(evt);
            }
        });

        btnAdd.setText("New");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnCancelRemove.setText("Cancel");
        btnCancelRemove.setEnabled(false);
        btnCancelRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelRemoveActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnCancelRemove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdate)
                .addGap(5, 5, 5)
                .addComponent(btnAdd))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jLabel4))
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(faultPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbFaultSeveity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(faultPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(cmbFaultSeveity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnCancelRemove)
                    .addComponent(btnUpdate)))
        );

        btnSubmit.setText("Finialise & Submit Report");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnClose.setText("Suppend Report & Close");
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblContentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClose)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSubmit)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblContentName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSubmit)
                    .addComponent(btnClose))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelRemoveActionPerformed
        cancelRemovebutton();
    }//GEN-LAST:event_btnCancelRemoveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Addbutton();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        closeButton();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        submitReport();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateButton();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void cmbFaultSeveityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFaultSeveityActionPerformed
        validateFault();
    }//GEN-LAST:event_cmbFaultSeveityActionPerformed

    private void btnOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenFileActionPerformed
        openFile();
    }//GEN-LAST:event_btnOpenFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancelRemove;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOpenFile;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox cmbFaultSeveity;
    private javax.swing.JTextArea faultDescription;
    private javax.swing.JTextField faultPosition;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblContentDescription;
    private javax.swing.JPanel lblContentName;
    private javax.swing.JLabel lblMediaRequired;
    private javax.swing.JLabel lblOTaskDescription;
    private javax.swing.JLabel lblProject;
    private javax.swing.JTable tblFlauts;
    private javax.swing.JTextField txtProject;
    private javax.swing.JTextArea txtTaskContentDescription;
    private javax.swing.JTextField txtTaskContentName;
    private javax.swing.JTextArea txtTaskDescription;
    private javax.swing.JTextField txtTaskFilename;
    private javax.swing.JTextField txtTaskMediaRequired;
    // End of variables declaration//GEN-END:variables
}
