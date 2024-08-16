package LogIn;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Home extends JFrame {
    private JTabbedPane tabPane;
    private JButton logoutButton;

    public Home() {
        // Initialize components
        tabPane = new JTabbedPane(JTabbedPane.LEFT); // Set tab placement to the left
        JPanel homePanel = new HomePanel();
        JPanel guidePanel = new GuideDetail();
        JPanel userPanel = new UserDetail();
        JPanel destinationPanel = new Destination();
        JPanel bookingPanel = new Bookings(); // Ensure this class is correct
        logoutButton = new JButton("Logout");

        // Set up the tabbed pane
        tabPane.addTab("Home", homePanel);
        tabPane.addTab("Guide Detail", guidePanel);
        tabPane.addTab("User Detail", userPanel);
        tabPane.addTab("Destinations", destinationPanel);
        tabPane.addTab("Bookings", bookingPanel); // Add the bookings tab

        // Create a panel for the button and add it to the bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(logoutButton, BorderLayout.EAST);

        // Set up the main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        this.setContentPane(mainPanel);

        // Set frame properties
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the window full-screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
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
        // Invoke the creation of the Home frame on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> new Home());
    }
}
