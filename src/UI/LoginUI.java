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
    
    public String getUsername()
    {
        return username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setMaxAttempts(int max_attempts)
    {
        this.max_attempts = max_attempts;
    }

    public Worker getIDandPassword() 
    {
        WorkerRegister register = WorkerRegister.getInstance();
        Worker worker = null;
        int attempts = 0;
        int messageType = JOptionPane.PLAIN_MESSAGE;
        
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
                System.exit(0);
            }
            
            worker = register.checkPassword(txtUsername.getText(), txtPassword.getText());
            attempts++;
        }
        
        return worker;
    }

}
