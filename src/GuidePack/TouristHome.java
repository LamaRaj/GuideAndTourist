package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TouristHome extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;
    private JButton logoutButton;

    public TouristHome() {
        // Setting up the JFrame
        setTitle("Tourist Home");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabs
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.add(new JLabel("Welcome to Tourist Home!", SwingConstants.CENTER), BorderLayout.CENTER);
        JPanel guideSearchPanel = new GuideSearch();
        JPanel destinationsPanel = new JPanel();
        destinationsPanel.add(new JLabel("Destinations"));

        // Add tabs to tabbed pane
        tabbedPane.addTab("Home", homePanel);
        tabbedPane.addTab("Guide Search", guideSearchPanel);
        tabbedPane.addTab("Destinations", destinationsPanel);

        // Create a panel for the logout button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        logoutButton = new JButton("Logout");
        bottomPanel.add(logoutButton, BorderLayout.EAST);

        // Set up the main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        this.setContentPane(mainPanel);

        // Set frame properties
        setVisible(true);

        // Add action listener to the logout button
        logoutButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            new Login().setVisible(true);
            dispose(); // Close the home window
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TouristHome().setVisible(true));
    }
}
