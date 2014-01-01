package UI;

import BLL.ClientRegister;
import BLL.WorkerRegister;
import BLL.WorkerRoles;
import BLL.WorkerType;
import java.util.ArrayList;

/**
 *
 * @author James
 */
public class Main {

    /**
     * @param args the command line arguments not used.
     */
    public static void main(String[] args) {
        // Set up objects
        createTestData();
 
        //<editor-fold defaultstate="collapsed" desc="Set the Nimbus look and feel">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainMDIUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMDIUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMDIUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMDIUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        // Create and setup the main UI form
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                // create and display main form UI
                MainMDIUI mainform = new MainMDIUI();
                mainform.setVisible(true);
                
                // start login dialog
                mainform.start(true);
            }
        });
    }
    
    private static void createTestData()
    {
        // Create some system users
        WorkerRegister register = WorkerRegister.getInstance();
        
        register.addWorker(new WorkerRoles[]{WorkerRoles.PROJECT_MANAGER}, new String[]{"James","Staite"}, "James.Staite", "Password",WorkerType.STAFF);
        register.addWorker(new WorkerRoles[]{WorkerRoles.PROJECT_MANAGER}, new String[]{"Sam","Pickstone"}, "S.Pickstone", "Password",WorkerType.STAFF);
        register.addWorker(new WorkerRoles[]{WorkerRoles.PROJECT_MANAGER}, new String[]{"Sarah","Murfet"}, "S.Murfet", "Password",WorkerType.STAFF); 

        register.addWorker(new WorkerRoles[]{WorkerRoles.QC}, new String[]{"John","Smith"}, "J.Smith", "Password",WorkerType.STAFF);
        register.addWorker(new WorkerRoles[]{WorkerRoles.QC}, new String[]{"Richard","Brown"}, "R.Brown", "Password",WorkerType.STAFF);
        register.addWorker(new WorkerRoles[]{WorkerRoles.QC}, new String[]{"David","Hudson"}, "D.Hudson", "Password",WorkerType.STAFF);

        register.addWorker(new WorkerRoles[]{WorkerRoles.QC_TEAM_LEADER}, new String[]{"Lynne","Featherstone"}, "L.Featherstone", "Password",WorkerType.STAFF);
        register.addWorker(new WorkerRoles[]{WorkerRoles.QC_TEAM_LEADER}, new String[]{"Richard","Brown"}, "R.Brown", "Password",WorkerType.STAFF);
        register.addWorker(new WorkerRoles[]{WorkerRoles.QC_TEAM_LEADER}, new String[]{"Dan","Roddis"}, "R.Roddis", "Password",WorkerType.STAFF);
        
        register.addWorker(new WorkerRoles[]{WorkerRoles.AUTHOR}, new String[]{"Jill","Martin"}, "J.Martin", "Password",WorkerType.STAFF);
        register.addWorker(new WorkerRoles[]{WorkerRoles.AUTHOR}, new String[]{"Jackie","Brown"}, "J.Brown", "Password",WorkerType.STAFF);
        register.addWorker(new WorkerRoles[]{WorkerRoles.AUTHOR}, new String[]{"Richard","Goodson"}, "R.Goodson", "Password",WorkerType.STAFF);
        
        register.addWorker(new WorkerRoles[]{WorkerRoles.QC}, new String[]{"Terry","Richardson"}, "T.Richardson", "Password", WorkerType.FREELANCER);
        register.addWorker(new WorkerRoles[]{WorkerRoles.QC}, new String[]{"Mark","Johnson"}, "M.Johnson", "Password", WorkerType.FREELANCER);
        
        // Create some clients
        ClientRegister clientReg = ClientRegister.getInstance();
        
        clientReg.addClient("DreamWorks Animation", "1, The Road", "Westend", "Sheffield","", "S1 5GT","0114 2726758");
        clientReg.addClient("20th Century Fox", "Century City", "California", "USA","", "","01 6514 2726758");
        clientReg.addClient("Metro-Goldwyn-Mayer", "Golden Towers", "Darnell", "Sheffield","", "S9 6GT","0114 245986");
        
        // Create some Projects
    }
}
