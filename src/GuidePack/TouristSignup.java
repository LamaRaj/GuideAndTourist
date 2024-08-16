package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TouristSignup extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField contactField;
    private JTextField ageField;
    private JButton signupButton;
    private JButton backButton;
    private JLabel statusLabel;

    public TouristSignup() {
        setTitle("Tourist Signup");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creating components
        nameField = new JTextField(15);
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        emailField = new JTextField(15);
        contactField = new JTextField(15);
        ageField = new JTextField(15);
        signupButton = new JButton("Signup");
        backButton = new JButton("Back");
        statusLabel = new JLabel();

        signupButton.addActionListener(this);
        backButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2));
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
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(backButton);
        panel.add(signupButton);
        panel.add(statusLabel);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String email = emailField.getText();
        String contact = contactField.getText();

        if (!isValidEmail(email)) {
            statusLabel.setText("Invalid email format.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (!isValidContact(contact)) {
            statusLabel.setText("Invalid contact number.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            String sql = "INSERT INTO tourist_info (name, username, password, email, contact, age) VALUES (?, ?, ?, ?, ?, ?)";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, nameField.getText());
            stm.setString(2, usernameField.getText());
            stm.setString(3, new String(passwordField.getPassword()));
            stm.setString(4, email);
            stm.setString(5, contact);
            stm.setInt(6, Integer.parseInt(ageField.getText()));

            int rowsInserted = stm.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Signup successful!");
                new Login().setVisible(true);
                dispose();
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

    private boolean isValidEmail(String email) {
        // Simple email validation regex
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidContact(String contact) {
        // Regex to validate that the contact number starts with 98 or 97 and is 10 digits long
        String contactRegex = "^(98|97)\\d{8}$";
        Pattern contactPattern = Pattern.compile(contactRegex);
        Matcher matcher = contactPattern.matcher(contact);
        return matcher.matches();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TouristSignup().setVisible(true));
    }
}
