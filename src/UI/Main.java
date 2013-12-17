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
                mainform.start();
                System.out.println("Main Called Start on MainMDIUI");
            }
        });
    }
    
    private static void createTestData()
    {
        // Create some system users
        WorkerRegister register = WorkerRegister.getInstance();
        
        ArrayList<WorkerRoles> roles = new ArrayList();
        //roles.add(WorkerRoles.QC);
        roles.add(WorkerRoles.PROJECT_MANAGER);
        
        register.addWorker(roles, "James Staite", "James.Staite", "Password",WorkerType.STAFF);
        register.addWorker(roles, "Sam Pickstone", "S.Pickstone", "Password",WorkerType.STAFF);
        register.addWorker(roles, "Sarah Murfet", "S.Murfet", "Password",WorkerType.STAFF);   
        
        // Create some clients
        ClientRegister clientReg = ClientRegister.getInstance();
        
        clientReg.addClient("DreamWorks Animation", "1, The Road", "Westend", "Sheffield","", "S1 5GT","0114 2726758");
        clientReg.addClient("20th Century Fox", "Century City", "California", "USA","", "","01 6514 2726758");
        clientReg.addClient("Metro-Goldwyn-Mayer", "Golden Towers", "Darnell", "Sheffield","", "S9 6GT","0114 245986");
    }
}
