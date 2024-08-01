package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class TouristSignup extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField contactField;
    private JButton signupButton;
    private JButton backButton;
    private JLabel statusLabel;

    public TouristSignup() {
        // Setting up the JFrame
        setTitle("Tourist Signup");
        setSize(300, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creating components
        nameField = new JTextField(15);
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        emailField = new JTextField(15);
        contactField = new JTextField(15);
        signupButton = new JButton("Signup");
        backButton = new JButton("Back");
        statusLabel = new JLabel();

        // Adding action listeners to the buttons
        signupButton.addActionListener(this);
        backButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose(); // Close the signup window
        });

        // Setting up the panel and adding components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Contact:"));
        panel.add(contactField);
        panel.add(backButton); // Added Back button on the left side
        panel.add(signupButton); // Signup button on the right side
        panel.add(statusLabel);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // SQL query to insert a new tourist record
            String sql = "INSERT INTO tourist_info (name, username, password, email, contact) VALUES (?, ?, ?, ?, ?)";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, nameField.getText());
            stm.setString(2, usernameField.getText());
            stm.setString(3, new String(passwordField.getPassword()));
            stm.setString(4, emailField.getText());
            stm.setString(5, contactField.getText());

            int rowsInserted = stm.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Signup successful!");
                new Login().setVisible(true);
                dispose(); // Close the signup window
            } else {
                statusLabel.setText("Signup failed. Please try again.");
                statusLabel.setForeground(Color.RED);
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error during signup. Please try again.");
            statusLabel.setForeground(Color.RED);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TouristSignup().setVisible(true));
    }
}
