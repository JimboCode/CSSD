package UI;

import BLL.QCReport;
import BLL.QCReport.Fault;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventJXTableModel;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author James
 */
public class QCReportViewerUI extends ModalBase 
{
    private QCReport report;
    
    // forms current fault
    Fault fault;
    
    /**
     * Creates new form QCReportViewerUI
     */
    public QCReportViewerUI(QCReport qCReport) 
    {
        super("Quality Control Report",false,true,false,false);
        initComponents();
        
        // store report
        report = qCReport;
        
        loadFaultsTable();
        
        if (report.isReportmoderated() == true) btnFinialise.setVisible(false);
        
        tblFlauts.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // possible row selection
                if (!event.getValueIsAdjusting()) rowselection();
            }
        });
        
        txtLeaderComments.addCaretListener(new CaretListener() 
        {
            @Override
            public void caretUpdate(CaretEvent e) 
            {
                validateForm();
            }
        });
    }
    
    private void loadFaultsTable()
    {
        // define properties and column labels
        String[] propertyNames = {"Description", "Position", "Severity","Moderated"};
        String[] columnLabels = {"Description", "Position", "Severity","Moderated"};
        int[] columnSizes = {180,30,5,5};
        
        // set table format for unallocated task items
        TableFormat tableFormat = GlazedLists.tableFormat(QCReport.Fault.class, propertyNames, columnLabels);
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
    
    private void rowselection()
    {
        //check if any items are selected
        if (tblFlauts.getSelectedRow() > -1)
        {
            // hold the first item to use for comparison
            EventJXTableModel tableModel = (EventJXTableModel) tblFlauts.getModel();
            fault = (QCReport.Fault) tableModel.getElementAt(tblFlauts.getSelectedRow());
            
            // display faults details
            faultDescription.setText(fault.getDescription());
            faultPosition.setText(fault.getPosition());
            cmbFaultSeveity.setSelectedIndex(fault.getSeverity()-1);
            
            // update controls status
            if (report.isReportmoderated() == false) 
            {
                btnUpdate.setEnabled(false);
                txtLeaderComments.setEnabled(true);
                cmbLeaderSeveity.setEnabled(true);
                txtLeaderComments.requestFocus();
            }
            else
            {
                btnCancel.setEnabled(false);
            }
            txtLeaderComments.setText(fault.getModeratedComments());
            cmbLeaderSeveity.setSelectedIndex(fault.getModeratedSeverity()-1);
        }
        else
        {
            clearFaultControls();
            btnUpdate.setEnabled(false);
            txtLeaderComments.setEnabled(false);
            fault = null;
        }
    }
    
    private void clearFaultControls()
    {
        faultDescription.setText("");
        faultPosition.setText("");
        txtLeaderComments.setText("");
        cmbLeaderSeveity.setSelectedIndex(0);
        cmbFaultSeveity.setSelectedIndex(0);
    }
    
    private void validateForm()
    {
        if (!txtLeaderComments.isEnabled() || txtLeaderComments.getText().length() == 0) 
        {
            btnUpdate.setEnabled(false);
            if (report.isReportmoderated() == true) btnCancel.setEnabled(true);
        }
        else
        {
            btnUpdate.setEnabled(true);
            if (report.isReportmoderated() == true) btnCancel.setEnabled(true);
        }
    }
    
    private void updateButton()
    {
        fault.setModeratedComments(txtLeaderComments.getText());
        fault.setModeratedSeverity(Integer.parseInt((String)cmbLeaderSeveity.getSelectedItem()));
        
        clearFaultControls();
        btnUpdate.setEnabled(false);
        btnCancel.setEnabled(false);
                
        fault = null;
        cmbLeaderSeveity.setEnabled(false);
        txtLeaderComments.setEnabled(false);
        int row = tblFlauts.getSelectedRow();
        if (row < (tblFlauts.getModel().getRowCount()-1) && row > -1)
        {
            row++;
            tblFlauts.setRowSelectionInterval(row,row);
        } 
        else
        {
            tblFlauts.setRowSelectionInterval(0,0);
        }
        txtLeaderComments.requestFocus();
    }
    
    private void cancelRemovebutton()
    {
        clearFaultControls();
        btnUpdate.setEnabled(false);
        btnCancel.setEnabled(false);
        fault = null;
        tblFlauts.clearSelection();
    }
    
    private void closeButton()
    {
        try {
            this.setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DefineTeamUI.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
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
        btnUpdate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtLeaderComments = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        cmbLeaderSeveity = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnFinialise = new javax.swing.JButton();

        jTextField1.setText("jTextField1");

        jButton2.setText("jButton2");

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
        cmbFaultSeveity.setEnabled(false);

        btnUpdate.setText("Accept");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jLabel1.setText("QC Leader's");

        jLabel2.setText("Comment");

        txtLeaderComments.setColumns(20);
        txtLeaderComments.setRows(5);
        txtLeaderComments.setEnabled(false);
        jScrollPane2.setViewportView(txtLeaderComments);

        jLabel7.setText("QC Leader's Moderated Seveity");

        cmbLeaderSeveity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        cmbLeaderSeveity.setEnabled(false);
        cmbLeaderSeveity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLeaderSeveityActionPerformed(evt);
            }
        });

        jLabel8.setText("File");

        jTextField2.setEnabled(false);

        jButton1.setText("Open File");
        jButton1.setEnabled(false);

        btnCancel.setText("Cancel");
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(faultPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmbFaultSeveity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane2)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbLeaderSeveity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUpdate)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jButton1)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(faultPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(cmbFaultSeveity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(cmbLeaderSeveity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnUpdate)
                        .addComponent(btnCancel))))
        );

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnFinialise.setText("Finialise & Close Review");
        btnFinialise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinialiseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFinialise)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnFinialise))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateButton();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelRemovebutton();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        closeButton();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnFinialiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinialiseActionPerformed
        report.setReportmoderated(true);
        closeButton();
    }//GEN-LAST:event_btnFinialiseActionPerformed

    private void cmbLeaderSeveityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLeaderSeveityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbLeaderSeveityActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFinialise;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox cmbFaultSeveity;
    private javax.swing.JComboBox cmbLeaderSeveity;
    private javax.swing.JTextArea faultDescription;
    private javax.swing.JTextField faultPosition;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTable tblFlauts;
    private javax.swing.JTextArea txtLeaderComments;
    // End of variables declaration//GEN-END:variables
}
