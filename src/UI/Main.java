package UI;

import BLL.Client;
import BLL.ClientRegister;
import BLL.ComponentType;
import BLL.ContentManager;
import BLL.MediaItem;
import BLL.NodeType;
import BLL.Project;
import BLL.ProjectRegister;
import BLL.Region;
import BLL.Worker;
import BLL.WorkerRegister;
import BLL.WorkerRoles;
import BLL.WorkerType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main entry point into the application
 * @author James Staite
 */
public class Main {
    /**
     * Main enter point to the application
     * 
     * @param args the command line arguments not used.
     */
    public static void main(String[] args) {
        // Set up some test data objects
        createTestData();
 
        //<editor-fold defaultstate="collapsed" desc="Set the Nimbus look and feel">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
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
    
    /**
     * Creates some temporary test data
     */
    private static void createTestData()
    {
        // Create some system users
        WorkerRegister register = WorkerRegister.getInstance();
        
        register.addWorker(WorkerRoles.PROJECT_MANAGER, new String[]{"James","Staite"}, "James.Staite", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.PROJECT_MANAGER, new String[]{"Sam","Pickstone"}, "S.Pickstone", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.PROJECT_MANAGER, new String[]{"Sarah","Murfet"}, "S.Murfet", "Password",WorkerType.STAFF); 

        register.addWorker(WorkerRoles.QC, new String[]{"John","Smith"}, "J.Smith", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.QC, new String[]{"Richard","Brown"}, "R.Brown", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.QC, new String[]{"David","Hudson"}, "D.Hudson", "Password",WorkerType.STAFF);

        register.addWorker(WorkerRoles.QC_TEAM_LEADER, new String[]{"Lynne","Featherstone"}, "L.Featherstone", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.QC_TEAM_LEADER, new String[]{"Richard","Brown"}, "R.Brown", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.QC_TEAM_LEADER, new String[]{"Dan","Roddis"}, "R.Roddis", "Password",WorkerType.STAFF);
        
        register.addWorker(WorkerRoles.AUTHOR, new String[]{"Jill","Martin"}, "J.Martin", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.AUTHOR, new String[]{"Jackie","Brown"}, "J.Brown", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.AUTHOR, new String[]{"Richard","Goodson"}, "R.Goodson", "Password",WorkerType.STAFF);
        
        register.addWorker(WorkerRoles.QC, new String[]{"Terry","Richardson"}, "T.Richardson", "Password", WorkerType.FREELANCER);
        register.addWorker(WorkerRoles.QC, new String[]{"Mark","Johnson"}, "M.Johnson", "Password", WorkerType.FREELANCER);
        
         ArrayList<Worker> managers = register.findByName(WorkerRoles.PROJECT_MANAGER, WorkerType.STAFF, "James Staite");
         Worker JamesStaite = managers.get(0);
        
        // Create some clients
        ClientRegister clientReg = ClientRegister.getInstance();
        
        clientReg.addClient("DreamWorks Animation", "1, The Road", "Westend", "Sheffield","", "S1 5GT","0114 2726758");
        clientReg.addClient("20th Century Fox", "Century City", "California", "USA","", "","01 6514 2726758");
        clientReg.addClient("Metro-Goldwyn-Mayer", "Golden Towers", "Darnell", "Sheffield","", "S9 6GT","0114 245986");
        
        List<Client> client = clientReg.getClientList();
        Client DreamWorks = (Client) client.get(0);
        
        // Create some Projects
        ProjectRegister projectReg = ProjectRegister.getInstance();

        // create new client
        Project created = projectReg.addProject("Film", "Disc 1", DreamWorks, Region.EUROPE_2, JamesStaite, new Date());
        
        // create content tree
        ContentManager contentManager = created.getContentManger();
        MediaItem root = contentManager.getTree();
        MediaItem element1 = contentManager.addItem("Element 1", "", ComponentType.NONE, NodeType.ELEMENT, root);
        MediaItem element2 = contentManager.addItem("Element 2", "", ComponentType.NONE, NodeType.ELEMENT, element1);
        MediaItem element3 = contentManager.addItem("Element 3", "", ComponentType.NONE, NodeType.ELEMENT, element2);
        MediaItem element4 = contentManager.addItem("Element 4", "", ComponentType.NONE, NodeType.ELEMENT, element3);
        MediaItem Asset1 = contentManager.addItem("Asset1", "", ComponentType.NONE, NodeType.ASSET, element4);
        MediaItem element5 = contentManager.addItem("Element 5", "", ComponentType.NONE, NodeType.ELEMENT, root);
        MediaItem element6 = contentManager.addItem("Element 6", "", ComponentType.NONE, NodeType.ELEMENT, element5);
        MediaItem Asset2 = contentManager.addItem("Asset2", "", ComponentType.NONE, NodeType.ASSET, element6);
    }
}
