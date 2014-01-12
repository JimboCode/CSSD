package UI;

import BLL.ClientRegister;
import BLL.ProjectRegister;
import BLL.Worker;
import BLL.WorkerRoles;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * Main window that initiates all other operations for the user
 * @author James Staite
 */
public class MainMDIUI extends javax.swing.JFrame implements Observer
{
    // hold a reference to the user that is logged in and authoristed.
    private Worker user;
    
    // flags this is the first and main instances - used for exiting / closing
    boolean mainInstance;
    
    // loads and holds application icon
    ImageIcon appIcon = new ImageIcon(getClass().getClassLoader().getResource("Resources/AppIcon.png"));
    
    /**
     * Creates new form MainMDIUI
     */
    public MainMDIUI() {
        initComponents();
        
        // set the application icon
        this.setIconImage(appIcon.getImage());
        
        // maximumise the window
        this.setExtendedState(Frame.MAXIMIZED_BOTH); 
        
        // hook up window closing event
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                checkClosingWindow();
            }
        });
    }
    
    /**
     * Carries out login and sets up the user interface
     * 
     * @param mainInstance true if is the first form created by Main entry point to the application
     * false is an instance created by the form its self.  Used to decide either to close the applications
     * or just to dispose of the form instance.
     */
    public void start(boolean mainInstance)
    {
        this.mainInstance = mainInstance;
        
        // create Login dialog
        LoginUI login = new LoginUI();
        
        // request user details and set the field in the main UI for reference
        user = login.getIDandPassword();

        // check if valid user details have been provided
        if (user == null)
        {
            // if not a valid user close the application or form
            // check if this is the first instance
            if (mainInstance == false)
            {
                // if not first instance close this form only
                this.dispose();
            }
            else
            {
                // close application because is the first instance of the form
                System.exit(0);
            }
        }

        // setup user interface based upon user login
        customiseUI();
        
        // set initial menu state based upon BLL objects
        setupMenuState();
        
        // register for update events from BLL objects state
        registerEvents();
    }
    
    /**
     * customises the main form interface based upon the user type that has logged in
     */
    private void customiseUI()
    {
        // get logged in users role
        WorkerRoles role = user.getRole();
        
        switch (role)
        {
            case AUTHOR:
            {
                setupUIAuthor(); 
                break;       
            }
            case QC:
            {
                setupUIQC_Member();
                break;
            }
            case PROJECT_MANAGER:
            {
                setupUIProjectManager();
                break;
            }
            case QC_TEAM_LEADER:
            {
                setupUIQC_TeamLeader();
                break;
            }
        }
    }
    
    /*
     * Sets up the UI for Author users
     */
    private void setupUIAuthor()
    // <editor-fold defaultstate="collapsed" desc="UI Code"> 
    {
        // create and setup Author users specific menus here
        
        // add common help menus
        setupUIhelpMenu();
        
        // repaint menus
        this.repaint();
    } // </editor-fold>
    
    /*
     * Sets up the UI for QC users
     */
    private void setupUIQC_Member()
    // <editor-fold defaultstate="collapsed" desc="UI Code"> 
    {
        // create new menu items
        viewMenu = new javax.swing.JMenu();
        qCWorkerWindowMenuItem = new javax.swing.JMenuItem();
        
        viewMenu.setMnemonic('v');
        viewMenu.setText("View");
        
        qCWorkerWindowMenuItem.setText("Work Window");
        
        // setup menu action listner
        qCWorkerWindowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qCWorkWindowMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(qCWorkerWindowMenuItem);
        menuBar.add(viewMenu);
        
        // add common help menus
        setupUIhelpMenu();
        
        // repaint menus        
        this.repaint();
    } // </editor-fold>
    
    /*
     * Sets up the UI for Project Manager users
     */
    private void setupUIProjectManager()
    // <editor-fold defaultstate="collapsed" desc="UI Code"> 
    {
        // create menu items
        createMenu = new javax.swing.JMenu();
        clientMenuItem = new javax.swing.JMenuItem();
        
        defineMenu = new javax.swing.JMenu();
        projectMenuItem = new javax.swing.JMenuItem();
        teamMenuItem = new javax.swing.JMenuItem();
        contentMenuItem = new javax.swing.JMenuItem();
        
        taskMenu = new javax.swing.JMenu();
        createTaskMenuItem = new javax.swing.JMenuItem();
        
        viewMenu = new javax.swing.JMenu();
        projectProgressMenuItem = new javax.swing.JMenuItem();
        
        // Setup menu items and action listners
        createMenu.setMnemonic('c');
        createMenu.setText("Create");

        clientMenuItem.setText("Client");
        clientMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createClientMenuItemActionPerformed(evt);
            }
        });
        createMenu.add(clientMenuItem);
        menuBar.add(createMenu);

        defineMenu.setMnemonic('d');
        defineMenu.setText("Define");
        
        projectMenuItem.setText("Project");
        projectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProjectMenuItemActionPerformed(evt);
            }
        });
        defineMenu.add(projectMenuItem);

        teamMenuItem.setText("Project Team");
        teamMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teamMenuItemActionPerformed(evt);
            }
        });
        
        defineMenu.add(teamMenuItem);

        contentMenuItem.setText("Project Content");
        contentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentMenuItemActionPerformed(evt);
            }
        });
        
        defineMenu.add(contentMenuItem);
        menuBar.add(defineMenu);
        
        taskMenu.setMnemonic('t');
        taskMenu.setText("Tasks");
        
        createTaskMenuItem.setText("Create");
        createTaskMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createTaskMenuItemActionPerformed(evt);
            }
        });
        taskMenu.add(createTaskMenuItem);
        menuBar.add(taskMenu);
        
        viewMenu.setMnemonic('v');
        viewMenu.setText("View");
        
        projectProgressMenuItem.setText("Project Progress");
        projectProgressMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectProgressMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(projectProgressMenuItem);
        menuBar.add(viewMenu);
        
        // add common menus items
        setupUIhelpMenu();
        
        // repaint menus
        this.repaint();
    } // </editor-fold>
    
    /*
     * Sets up the UI for QC Team Leaders users
     */
    private void setupUIQC_TeamLeader()
    // <editor-fold defaultstate="collapsed" desc="UI Code"> 
    {
        // create menu items
        taskMenu.setMnemonic('t');
        taskMenu.setText("Tasks");
        
        // setup menu items and action listners
        createTaskMenuItem.setText("Create");
        createTaskMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createTaskMenuItemActionPerformed(evt);
            }
        });
        taskMenu.add(createTaskMenuItem);
        menuBar.add(taskMenu);
        
        // add common menus
        setupUIhelpMenu();
        
        // repaint menus
        this.repaint();
    } // </editor-fold>
    
    /*
     * Used by all UI setups to add common help menu
     */
    private void setupUIhelpMenu()
    // <editor-fold defaultstate="collapsed" desc="UI Code"> 
    {
        // create menu items
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();
        
        // setup menu items and action listners
        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);
    } // </editor-fold>

    /*
     * checks with BLL objects and sets the initial state of the menus
     */
    private void setupMenuState()
    {
        // check if they are any clients
        ClientRegister clientReg = ClientRegister.getInstance();
        if (clientReg.getNumberOfClients() == 0)
        {
            // disable these menu options because they are no clients
            if(projectMenuItem != null) projectMenuItem.setEnabled(false);
        }
        
        // check if they are any projects
        ProjectRegister proReg = ProjectRegister.getInstance();
        if (proReg.getNumberOfProjects() == 0)
        {
            // disable these menu options because they are no projects
            if(teamMenuItem != null) teamMenuItem.setEnabled(false);
            if(contentMenuItem != null) contentMenuItem.setEnabled(false);
            if(createTaskMenuItem != null) createTaskMenuItem.setEnabled(false);
            if(projectProgressMenuItem != null) projectProgressMenuItem.setEnabled(false);
        }
    }
    
    /**
    * Receives events raised using observer pattern built into Java
    * @param object Object raising the event
    * @param arg Any object passed when the event was raised
    */
    @Override
    public void update(Observable object, Object arg) 
    {
        // check to see if the object is a change in the project register
        if (object instanceof ProjectRegister)
        {
            ProjectRegister proReg = (ProjectRegister) object;
            
            // check if there are ant projects in the system and alter the menu state accordingly
            if (proReg.getNumberOfProjects() == 0)
            {
                projectMenuItem.setEnabled(false);
            }
            else
            {
                if(teamMenuItem != null) teamMenuItem.setEnabled(true);
                if(contentMenuItem != null) contentMenuItem.setEnabled(true);
                if(createTaskMenuItem != null) createTaskMenuItem.setEnabled(true);
                if(projectProgressMenuItem != null) projectProgressMenuItem.setEnabled(true);
            }
        }
        
        // check to see if the object is a change in the client register
        else if (object instanceof ClientRegister)
        {
            ClientRegister clientReg = (ClientRegister) object;
            
            // check if there are ant projects in the system and alter the menu state accordingly
            if (clientReg.getNumberOfClients() == 0)
            {
                projectMenuItem.setEnabled(false);
            }
            else
            {
                projectMenuItem.setEnabled(true);
            }
        }
    }
    
    /**
     * Register for observer events
     */
    private void registerEvents()
    {
        // get reference to project register and register for events
        ProjectRegister proReg = ProjectRegister.getInstance();
        proReg.addObserver(this);
        
        // get reference to client register and register for events
        ClientRegister clientReg = ClientRegister.getInstance();
        clientReg.addObserver(this);
    }
    
    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPane = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newInstanceMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Post Production Media Management");
        setIconImage(getIconImage());

        menuBar.setName(""); // NOI18N

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        newInstanceMenuItem.setText("New Instance");
        newInstanceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newInstanceMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newInstanceMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1235, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 851, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Action handler for menu Exit
     * @param evt event arguments
     */
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        
        // close window after checking with the user
        checkClosingWindow();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    /**
     * Action handler for menu New Instance
     * @param evt event arguments
     */
    private void newInstanceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newInstanceMenuItemActionPerformed
        // Create and setup the main UI form in a new thread
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                // create and display main form UI
                MainMDIUI mainform = new MainMDIUI();
                mainform.setVisible(true);
                
                // start login dialog - false flags it as not the main instance
                mainform.start(false);
            }
        });
    }//GEN-LAST:event_newInstanceMenuItemActionPerformed

    // <editor-fold defaultstate="collapsed" desc="UI varaiables"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newInstanceMenuItem;
    // End of variables declaration//GEN-END:variables

    // Varaiables declaration for UI
    private JMenu createMenu;
    private JMenu defineMenu;
    private JMenu taskMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;
    private JMenuItem clientMenuItem;
    private JMenuItem teamMenuItem;
    private JMenuItem projectMenuItem;
    private JMenuItem contentMenuItem;
    private JMenuItem createTaskMenuItem;
    private JMenuItem projectProgressMenuItem;
    private JMenuItem qCWorkerWindowMenuItem;
    private JMenuItem aboutMenuItem;
    
    // position of the next Jframe will be displayed on the desktop
    private int nextFrameX = 0;
    private int nextFrameY = 0;
    // End of variables declaration
    // </editor-fold>
    
    /**
     * Handles requests to close the form and check it was the users intension
     */
    private void checkClosingWindow()
    {
        // display dialog and confirm it was the users intension
        int action = JOptionPane.showConfirmDialog(MainMDIUI.this, "Do you really want to exit the application?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.OK_OPTION)
        {
            // check if this is the main initial form
            if (mainInstance == false)
            {
                // if not close only the form
                this.dispose();
            }
            else
            {
                // alternatively close the application
                System.exit(0);
            }
        }
    }

    /**
     * Handles Create Client menu action
     * @param evt event arguments
     */
    private void createClientMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        // create and add create client form
        CreateClientUI frm = new CreateClientUI();
        addForm(frm);
    }  
    
    /**
     * Handles Create Project menu action
     * @param evt event arguments
     */
    private void createProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) 
    {                                                      
        // create and add create project form
        CreateProjectUI frm = new CreateProjectUI(user, this);
        addForm(frm);
    }   
    
    /**
     * Handles Define Team menu action
     * @param evt event arguments
     */
    private void teamMenuItemActionPerformed(java.awt.event.ActionEvent evt) 
    {                                                   
        // create and add create project team form
        DefineTeamUI frm = new DefineTeamUI(null);
        addForm(frm);
    }  
    
    /**
     * Handles Define Content menu action
     * @param evt event arguments
     */
    private void contentMenuItemActionPerformed(ActionEvent evt) 
    {
        DefineContentUI frm = new DefineContentUI();
        addForm(frm);
    }
    
    /**
     * Handles Task Create menu action
     * @param evt event arguments
     */
    private void createTaskMenuItemActionPerformed(ActionEvent evt) 
    {
    
    }
    
    /**
     * Handles View Project Progress menu action
     * @param evt event arguments
     */
    private void projectProgressMenuItemActionPerformed(ActionEvent evt) 
    {
    
    }
    
    /**
     * Handles Help About menu action
     * @param evt event arguments
     */
    private void aboutMenuItemActionPerformed(ActionEvent evt) 
    {
        AboutUI frm = new AboutUI(appIcon);
        addForm(frm);
    }
    
    /**
     * Handles View Work Window menu action
     * @param evt event arguments
     */
    private void qCWorkWindowMenuItemActionPerformed(ActionEvent evt) 
    {
        
    }
    
    /**
     * Add a internal form to the desktop
     * @param frm a JInternal form to be added to the desktop
     */
    public void addForm(JInternalFrame frm)
    {
        // set the icon of the form
        frm.setFrameIcon(appIcon);
        
        // add the form to the desktop pane
        desktopPane.add(frm);
        
        // calculate position of next form
        // get height of the title bar of the window
        int frameDistance = frm.getHeight() - frm.getContentPane().getHeight();
        
        // update next form position
        nextFrameX += frameDistance;
        nextFrameY += frameDistance;
        
        // check form size at the posotion is on the desktop
        if (nextFrameX + frm.getWidth() > desktopPane.getWidth())
        {
            nextFrameX = 0;
        }
        if (nextFrameY + frm.getHeight() > desktopPane.getHeight())
        {
            nextFrameY = 0;
        }
        // set form position
        frm.setLocation(nextFrameX,nextFrameY);
        
        // make form visible
        frm.setVisible(true);
        
        // give the new form focus
        try {
            frm.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(MainMDIUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
