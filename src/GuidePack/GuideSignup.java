package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class GuideSignup extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField contactField;
    private JTextField ageField;
    private JTextField addressField;
    private JTextField emailField;
    private JButton signupButton;
    private JLabel statusLabel;

    public GuideSignup() {
        // Setting up the JFrame
        setTitle("Guide Signup");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creating components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        nameField = new JTextField(15);
        contactField = new JTextField(15);
        ageField = new JTextField(15);
        addressField = new JTextField(15);
        emailField = new JTextField(15);
        signupButton = new JButton("Sign Up");
        statusLabel = new JLabel();

        // Adding action listener
        signupButton.addActionListener(this);

        // Setting up the panel and adding components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2)); // Updated to 8 rows for better layout
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Contact Info:"));
        panel.add(contactField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(signupButton);
        panel.add(statusLabel);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // SQL statement to insert guide info
            String sql = "INSERT INTO guide_info (username, password, name, contact_info, age, address, email) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");

            // Insert guide data
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, usernameField.getText());
            stmt.setString(2, new String(passwordField.getPassword()));
            stmt.setString(3, nameField.getText());
            stmt.setString(4, contactField.getText());
            stmt.setInt(5, Integer.parseInt(ageField.getText()));
            stmt.setString(6, addressField.getText());
            stmt.setString(7, emailField.getText());
            stmt.executeUpdate();

            con.close();

            JOptionPane.showMessageDialog(this, "Signup successful! Please log in.");
            new Login().setVisible(true);
            dispose(); // Close the signup window
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during signup. Please try again.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuideSignup().setVisible(true));
    }
}
