package LogIn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;

public class LoginPan extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginPan() {
        // Setting up the JFrame
        setTitle("Admin Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creating components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        statusLabel = new JLabel();

        // Adding action listener to the login button
        loginButton.addActionListener(this);

        // Setting up the panel and adding components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(loginButton);
        panel.add(statusLabel);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String sql = "SELECT * FROM admin WHERE username=? AND password=?";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, usernameField.getText());
            stm.setString(2, new String(passwordField.getPassword()));

            ResultSet result = stm.executeQuery();
            if (result.next()) {
                Home home = new Home();
                home.setVisible(true);
                this.dispose(); // Close the login window
            } else {
                // If credentials are incorrect, show error message
                statusLabel.setText("Invalid username or password.");
                statusLabel.setForeground(Color.RED);
            }

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginPan().setVisible(true);
            }
        });
    }
}
