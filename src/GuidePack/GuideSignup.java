package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
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
    private JButton uploadCVButton;
    private File selectedCVFile;

    public GuideSignup() {
        // Setting up the JFrame
        setTitle("Guide Signup");
        setSize(400, 350);
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
        uploadCVButton = new JButton("Upload CV");

        // Adding action listeners
        signupButton.addActionListener(this);
        uploadCVButton.addActionListener(this);

        // Setting up the panel and adding components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2)); // Updated to 9 rows for better layout
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
        panel.add(uploadCVButton);
        panel.add(new JLabel());
        panel.add(signupButton);
        panel.add(statusLabel);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadCVButton) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedCVFile = fileChooser.getSelectedFile();
                JOptionPane.showMessageDialog(this, "CV selected: " + selectedCVFile.getName());
            }
        } else if (e.getSource() == signupButton) {
            // Validate user input
            if (!validateInput()) {
                return;
            }

            try {
                // SQL statement to insert guide info
                String sql = "INSERT INTO guide_info (username, password, name, contact_info, age, address, email, cv_file) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

                if (selectedCVFile != null) {
                    FileInputStream inputStream = new FileInputStream(selectedCVFile);
                    stmt.setBinaryStream(8, inputStream, (int) selectedCVFile.length());
                } else {
                    stmt.setNull(8, java.sql.Types.BLOB);
                }

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
    }

    private boolean validateInput() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText();
        String contact = contactField.getText();
        String ageStr = ageField.getText();
        String address = addressField.getText();
        String email = emailField.getText();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty() || ageStr.isEmpty() || address.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
            return false;
        }

        if (!contact.matches("^(98|97)\\d{8}$")) {
            JOptionPane.showMessageDialog(this, "Contact number must be a valid Nepal number (starting with 98 or 97 and followed by 8 digits).");
            return false;
        }

        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 17) {
                JOptionPane.showMessageDialog(this, "Age must be above 18.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Email must be a valid email address.");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuideSignup().setVisible(true));
    }
}
