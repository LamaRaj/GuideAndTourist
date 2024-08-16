package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel statusLabel;
    private JButton touristLoginButton;
    private JButton guideLoginButton;

    private boolean isTouristLogin = true; // Default to tourist login

    public Login() {
        // Setting up the JFrame
        setTitle("Tourist Login");
        setSize(300, 250); // Increased height to accommodate new buttons
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creating components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        signupButton = new JButton("Create New Account");
        statusLabel = new JLabel();
        touristLoginButton = new JButton("Tourist Login");
        guideLoginButton = new JButton("Guide Login");

        // Adding action listeners to the buttons
        loginButton.addActionListener(this);
        signupButton.addActionListener(e -> {
            if (isTouristLogin) {
                new TouristSignup().setVisible(true);
            } else {
                new GuideSignup().setVisible(true);
            }
            dispose(); // Close the login window
        });

        // Adding action listeners to the login mode buttons
        touristLoginButton.addActionListener(e -> switchToTouristLogin());
        guideLoginButton.addActionListener(e -> switchToGuideLogin());

        // Setting up the top panel with the tourist and guide login buttons
        JPanel topPanel = new JPanel();
        topPanel.add(touristLoginButton);
        topPanel.add(guideLoginButton);

        // Setting up the main panel and adding components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2));
        mainPanel.add(new JLabel("Username:"));
        mainPanel.add(usernameField);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passwordField);
        mainPanel.add(loginButton);
        mainPanel.add(signupButton);
        mainPanel.add(statusLabel);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Set the initial state
        switchToTouristLogin();
    }

    private void switchToTouristLogin() {
        isTouristLogin = true;
        touristLoginButton.setEnabled(false);
        guideLoginButton.setEnabled(true);
        setTitle("Tourist Login");
        clearFields();
    }

    private void switchToGuideLogin() {
        isTouristLogin = false;
        guideLoginButton.setEnabled(false);
        touristLoginButton.setEnabled(true);
        setTitle("Guide Login");
        clearFields();
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String sql;
            if (isTouristLogin) {
                sql = "SELECT tourist_id, username, name, email FROM tourist_info WHERE username=? AND password=?";
            } else {
                sql = "SELECT guide_id FROM guide_info WHERE username=? AND password=?";
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, usernameField.getText());
            stm.setString(2, new String(passwordField.getPassword()));

            ResultSet result = stm.executeQuery();
            if (result.next()) {
                // Login successful, redirect to the appropriate dashboard or another page
                if (isTouristLogin) {
                    int userId = result.getInt("tourist_id");
                    String username = result.getString("username");
                    String name = result.getString("name");
                    String email = result.getString("email");
                    new TouristHome(userId, username, name, email).setVisible(true);
                } else {
                    int guideId = result.getInt("guide_id");
                    new GuideHome(guideId).setVisible(true);
                }
                dispose(); // Close the login window
            } else {
                // If credentials are incorrect, show error message
                statusLabel.setText("Invalid username or password.");
                statusLabel.setForeground(Color.RED);
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during login. Please try again.");
        }
    }
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
