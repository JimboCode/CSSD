package UI;

import BLL.Worker;
import BLL.WorkerRegister;
import java.awt.*;
import javax.swing.*;

/**
 * modal dialog to get username and Password
 * @author James Staite
 */
public class LoginUI 
{
    private String username;
    private String password;

    // dialog options
    private String[] ConnectOptionNames = { "Login", "Cancel" };
    
    // Display elements
    private JPanel messagePanel;
    private JLabel lblDisclosure1 = new JLabel("The use of this system is restricted.  Enter");
    private JLabel lblDisclosure2 = new JLabel("your use credenticals to access the system.");
    private JLabel lblInvalidID = new JLabel("");
    private JLabel userNameLabel = new JLabel("Username:   ", JLabel.LEFT);
    private JLabel passwordLabel = new JLabel("Password:   ", JLabel.LEFT);
    private JTextField txtUsername = new JTextField("");
    private JTextField txtPassword = new JPasswordField("");
    
    private int max_attempts = 3;
    
    /**
     * sets up the UI elements
     */
    LoginUI() 
    {
        //<editor-fold defaultstate="collapsed" desc="Setup UI elements of the form">
        txtUsername.setToolTipText("Enter your allocated username");
        txtPassword.setToolTipText("Enter your allocated password");
        
        txtUsername.setPreferredSize(new Dimension(150,24));
        txtPassword.setPreferredSize(new Dimension(150,24));
        
	messagePanel = new JPanel(false);
        messagePanel.setLayout(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();
	
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;

        messagePanel.add(lblDisclosure1, constraints);
        
        constraints.gridy = 1;
        messagePanel.add(lblDisclosure2, constraints);
        
        constraints.gridy = 2;
        messagePanel.add(new JLabel("   "), constraints);
        
        constraints.gridy = 3;
        messagePanel.add(lblInvalidID, constraints);
        
        constraints.gridy = 4;
        messagePanel.add(new JLabel("   "), constraints);
        
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        messagePanel.add(userNameLabel, constraints);
        
        constraints.gridx = 1;
        messagePanel.add(txtUsername, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 6;
        messagePanel.add(passwordLabel, constraints);
        
        constraints.gridx= 1;
        messagePanel.add(txtPassword, constraints);
        //</editor-fold>
    }
    
    /**
     * Provides the username entered into the form
     * @return  Username
     */
    public String getUsername()
    {
        return username;
    }
    
    /**
     * Provides the password entered into the from
     * @return Password
     */
    public String getPassword()
    {
        return password;
    }
    
    /**
     * Set the maximum number of attempts for logging in
     * @param max_attempts number of attempts
     */
    public void setMaxAttempts(int max_attempts)
    {
        this.max_attempts = max_attempts;
    }

    /**
     * Called to prompt the user for their username and password modally
     * @return a Worker if username and password valid and null if not
     */
    public Worker getIDandPassword() 
    {
        // get reference to Worker Register
        WorkerRegister register = WorkerRegister.getInstance();
        
        // set up variables
        Worker worker = null;
        int attempts = 0;
        int messageType = JOptionPane.PLAIN_MESSAGE;
        
        // loop for the maximum number of attempts permitted if the details entered are not valid
        while (attempts < max_attempts && worker == null)
        {
            if(attempts > 0) 
            {
                messageType = JOptionPane.ERROR_MESSAGE;
                lblInvalidID.setText("Invalid username or password - check and try again");
                lblInvalidID.setForeground(Color.red);
                txtPassword.setText("");
            }
            // Get User input
            if(JOptionPane.showOptionDialog(null, messagePanel, 
                                            "Login",
                                            JOptionPane.OK_CANCEL_OPTION, 
                                            messageType,
                                            null, ConnectOptionNames, 
                                            ConnectOptionNames[0]) != 0) 
            {
                // return null if Cancel button pressed
                return null;
            }
            
            // get the Worker object if user details are valid
            worker = register.checkPassword(txtUsername.getText(), txtPassword.getText());
            
            // increment the number of attempts
            attempts++;
        }
        
        // return the result
        return worker;
    }

}
