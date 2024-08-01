package LogIn;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Home extends JFrame {
    private JTabbedPane tabpane;
    private JButton logoutButton;

    public Home() {
        // Initialize components
        tabpane = new JTabbedPane(JTabbedPane.LEFT); // Set tab placement to the left
        JPanel homePanel = new HomePanel();
        JPanel guidePanel = new GuideDetail();
        JPanel userPanel = new UserDetail(); // Ensure this class is correct
        logoutButton = new JButton("Logout");

        // Set up the tabbed pane
        tabpane.addTab("Home", homePanel);
        tabpane.addTab("Guide Detail", guidePanel);
        tabpane.addTab("User Detail", userPanel);

        // Create a panel for the button and add it to the bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(logoutButton, BorderLayout.EAST);

        // Set up the main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabpane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        this.setContentPane(mainPanel);

        // Set frame properties
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Add action listener to the logout button
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current window
                dispose();
                // Open the LoginPan window
                new LoginPan().setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        new Home();
    }
}
