package UI;

import BLL.Client;
import BLL.ClientRegister;
import BLL.ComponentType;
import BLL.ContentManager;
import BLL.MediaItem;
import BLL.MediaSource;
import BLL.NodeType;
import BLL.Project;
import BLL.ProjectRegister;
import BLL.Region;
import BLL.Worker;
import BLL.WorkerRegister;
import BLL.WorkerRoles;
import BLL.WorkerType;
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
        
        Worker Manager = register.addWorker(WorkerRoles.PROJECT_MANAGER, new String[]{"Manager",""}, "Manager", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.PROJECT_MANAGER, new String[]{"Sam","Pickstone"}, "S.Pickstone", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.PROJECT_MANAGER, new String[]{"Sarah","Murfet"}, "S.Murfet", "Password",WorkerType.STAFF); 

        register.addWorker(WorkerRoles.INTERRUPTER, new String[]{"Rachel","Jones"}, "R.Jones", "Password",WorkerType.STAFF);
                
        register.addWorker(WorkerRoles.QC, new String[]{"John","Smith"}, "J.Smith", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.QC, new String[]{"Richard","Brown"}, "R.Brown", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.QC, new String[]{"QC",""}, "QC", "Password",WorkerType.STAFF);

        register.addWorker(WorkerRoles.QC_TEAM_LEADER, new String[]{"Lynne","Featherstone"}, "L.Featherstone", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.QC_TEAM_LEADER, new String[]{"Richard","Brown"}, "R.Brown", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.QC_TEAM_LEADER, new String[]{"QC Leader",""}, "QC Leader", "Password",WorkerType.STAFF);
        
        register.addWorker(WorkerRoles.AUTHOR, new String[]{"Jill","Martin"}, "J.Martin", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.AUTHOR, new String[]{"Jackie","Brown"}, "J.Brown", "Password",WorkerType.STAFF);
        register.addWorker(WorkerRoles.AUTHOR, new String[]{"Author",""}, "Author", "Password",WorkerType.STAFF);
        
        register.addWorker(WorkerRoles.QC, new String[]{"Terry","Richardson"}, "T.Richardson", "Password", WorkerType.FREELANCER);
        register.addWorker(WorkerRoles.QC, new String[]{"Mark","Johnson"}, "M.Johnson", "Password", WorkerType.FREELANCER);
        register.addWorker(WorkerRoles.AUTHOR, new String[]{"Richard","Featherstone"}, "R.Feathersstone", "Password", WorkerType.FREELANCER);
        register.addWorker(WorkerRoles.AUTHOR, new String[]{"John","Hawke"}, "J.Hawke", "Password", WorkerType.FREELANCER);
        
        register.addWorker(WorkerRoles.CONTRACTOR, new String[]{"Video Productions"}, "Video Productions", "Password", WorkerType.CONTRACTOR);
        register.addWorker(WorkerRoles.CONTRACTOR, new String[]{"Audio Specialist"}, "Audio Specialist", "Password", WorkerType.CONTRACTOR);
        register.addWorker(WorkerRoles.CONTRACTOR, new String[]{"3D Animations"}, "3D Animations", "Password", WorkerType.CONTRACTOR);
        register.addWorker(WorkerRoles.CONTRACTOR, new String[]{"Digital Audio Services"}, "Digital Audio Services", "Password", WorkerType.CONTRACTOR);
        register.addWorker(WorkerRoles.CONTRACTOR, new String[]{"Video Mastering"}, "Video Mastering", "Password", WorkerType.CONTRACTOR);
        register.addWorker(WorkerRoles.CONTRACTOR, new String[]{"Supplier"}, "Supplier", "Password", WorkerType.CONTRACTOR);
        
        // Create some clients
        ClientRegister clientReg = ClientRegister.getInstance();
        
        clientReg.addClient("DreamWorks Animation", "1, The Road", "Westend", "Sheffield","", "S1 5GT","0114 2726758");
        clientReg.addClient("20th Century Fox", "Century City", "California", "USA","", "","01 6514 2726758");
        clientReg.addClient("Metro-Goldwyn-Mayer", "Golden Towers", "Darnell", "Sheffield","", "S9 6GT","0114 245986");
        clientReg.addClient("Client", "Golden Towers", "Darnell", "Sheffield","", "S9 6GT","0114 245986");
        
        List<Client> client = clientReg.getClientList();
        Client Cleint = (Client) client.get(3);
        
        // Create some Projects
        ProjectRegister projectReg = ProjectRegister.getInstance();

        // create new client
        Project created = projectReg.addProject("TV Series", "Disc 1", Cleint, Region.EUROPE_2, Manager, new Date());
        
        // create content tree
        ContentManager contentManager = created.getContentManger();
        MediaItem root = contentManager.getTree();
                
        MediaItem Menus = contentManager.addItem("Disc Menus", "Menus System", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, root);
        MediaItem Asset1 = contentManager.addItem("Menu sequence", "Menu art work", ComponentType.VIDEO, NodeType.ASSET, Menus);
        Asset1 = contentManager.addItem("Menu backing track", "Backing track to menu system", ComponentType.AUDIO, NodeType.ASSET, Menus);
        Asset1 = contentManager.addItem("Menu logic", "Java for menu system", ComponentType.JAVA, NodeType.ASSET, Menus);
        Asset1 = contentManager.addItem("Menu Subtitles", "Subtitles for menus", ComponentType.TEXTFILE, NodeType.ASSET, Menus);
        
        MediaItem temp = contentManager.addItem("Temp", "Temp", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, Menus);
        Asset1 = contentManager.addItem("Menu Subtitles", "Subtitles for menus", ComponentType.TEXTFILE, NodeType.ASSET, temp);
        
        MediaItem episodeContent = contentManager.addItem("Episode Content", "Content for episodes", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, root);
        
        MediaItem episode1 = contentManager.addItem("Episode 1", "Episode 1", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, episodeContent);
        
        MediaItem intro = contentManager.addItem("Episode Introduction", "Introduction titles", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, episode1);
        Asset1 = contentManager.addItem("Episode Introduction", "Introduction video", ComponentType.VIDEO, NodeType.ASSET, intro);
        Asset1 = contentManager.addItem("Audio", "English audio track", ComponentType.AUDIO, NodeType.ASSET, intro);
        
        MediaItem Chapter1 = contentManager.addItem("Chapter 1", "Chapter 1 of Episode 1", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, episode1);
        Asset1 = contentManager.addItem("Freature video", "Video content", ComponentType.VIDEO, NodeType.ASSET, Chapter1);
        Asset1 = contentManager.addItem("Audio", "English audio track", ComponentType.AUDIO, NodeType.ASSET, Chapter1);
        Asset1 = contentManager.addItem("French Subtitles", "French Translation", ComponentType.SUBTITLES, NodeType.ASSET, Chapter1);
         Asset1.setMediaSource(MediaSource.IN_HOUSE);
        Asset1 = contentManager.addItem("German Subtitles", "German Translation", ComponentType.SUBTITLES, NodeType.ASSET, Chapter1);
         Asset1.setMediaSource(MediaSource.IN_HOUSE);
        
        MediaItem Chapter2 = contentManager.addItem("Chapter 2", "Chapter 2 of Episode 1", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, episode1);
        Asset1 = contentManager.addItem("Freature video", "Video content", ComponentType.VIDEO, NodeType.ASSET, Chapter2);
        Asset1 = contentManager.addItem("Audio", "English audio track", ComponentType.AUDIO, NodeType.ASSET, Chapter2);
        Asset1 = contentManager.addItem("French Subtitles", "French Translation", ComponentType.SUBTITLES, NodeType.ASSET, Chapter2);
        Asset1.setMediaSource(MediaSource.IN_HOUSE);
        Asset1 = contentManager.addItem("German Subtitles", "German Translation", ComponentType.SUBTITLES, NodeType.ASSET, Chapter2);
         Asset1.setMediaSource(MediaSource.IN_HOUSE);
        
        MediaItem episode2 = contentManager.addItem("Episode 2", "Episode 2", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, episodeContent);
        
        intro = contentManager.addItem("Episode Introduction", "Introduction titles", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, episode2);
        Asset1 = contentManager.addItem("Episode Introduction", "Introduction video", ComponentType.VIDEO, NodeType.ASSET, intro);
        Asset1 = contentManager.addItem("Audio", "English audio track", ComponentType.AUDIO, NodeType.ASSET, intro);
        
        Chapter1 = contentManager.addItem("Chapter 1", "Chapter 1 of Episode 2", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, episode2);
        Asset1 = contentManager.addItem("Freature video", "Video content", ComponentType.VIDEO, NodeType.ASSET, Chapter1);
        Asset1 = contentManager.addItem("Audio", "English audio track", ComponentType.AUDIO, NodeType.ASSET, Chapter1);
        Asset1 = contentManager.addItem("French Subtitles", "French Translation", ComponentType.SUBTITLES, NodeType.ASSET, Chapter1);
        Asset1.setMediaSource(MediaSource.IN_HOUSE);
        Asset1 = contentManager.addItem("German Subtitles", "German Translation", ComponentType.SUBTITLES, NodeType.ASSET, Chapter1);
        Asset1.setMediaSource(MediaSource.IN_HOUSE);
        
        Chapter2 = contentManager.addItem("Chapter 2", "Chapter 2 of Episode 2", ComponentType.COMPRESSED_ELEMENT, NodeType.ELEMENT, episode2);
        Asset1 = contentManager.addItem("Freature video", "Video content", ComponentType.VIDEO, NodeType.ASSET, Chapter2);
        Asset1 = contentManager.addItem("Audio", "English audio track", ComponentType.AUDIO, NodeType.ASSET, Chapter2);
        Asset1 = contentManager.addItem("French Subtitles", "French Translation", ComponentType.SUBTITLES, NodeType.ASSET, Chapter2);
        Asset1.setMediaSource(MediaSource.IN_HOUSE);
        Asset1 = contentManager.addItem("German Subtitles", "German Translation", ComponentType.SUBTITLES, NodeType.ASSET, Chapter2); 
        Asset1.setMediaSource(MediaSource.IN_HOUSE);
    }
}
