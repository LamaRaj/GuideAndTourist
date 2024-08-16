package GuidePack;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TouristHome extends JFrame {
    private JTabbedPane tabbedPane;
    private int touristId;
    private String username;
    private String name;
    private String email;
    private JPanel topGuidePanel;  // Panel to display top guide

    public TouristHome(int touristId, String username, String name, String email) {
        this.touristId = touristId;
        this.username = username;
        this.name = name;
        this.email = email;

        setTitle("Tourist Home");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the window full-screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        JPanel homePanel = createHomePanel();
        JPanel destinationPanel = new Destination(this);
        JPanel touristMessagePanel = new TouristMessage(touristId);
        JPanel touristHistoryPanel = new TouristHistory(touristId);

        tabbedPane.addTab("Home", homePanel);
        tabbedPane.addTab("Destinations", destinationPanel);
        tabbedPane.addTab("Messages", touristMessagePanel);
        tabbedPane.addTab("History", touristHistoryPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        bottomPanel.add(logoutButton, BorderLayout.EAST);
        logoutButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.add(new JLabel("Welcome, " + name + "!", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1)); // Two rows for Top Destination and Top Guide

        // Top Destination of the Month
        JPanel topDestinationPanel = new JPanel(new BorderLayout());
        topDestinationPanel.add(new JLabel("Top Destination of the Month:", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea topDestinationDetails = new JTextArea(5, 30); // Increased height for more details
        topDestinationDetails.setEditable(false);
        topDestinationPanel.add(new JScrollPane(topDestinationDetails), BorderLayout.CENTER);
        infoPanel.add(topDestinationPanel);

        // Top Guide of the Month
        topGuidePanel = new JPanel(new BorderLayout());  // Use an instance variable to update later
        topGuidePanel.add(new JLabel("Top Guide of the Month:", SwingConstants.CENTER), BorderLayout.NORTH);
        JLabel topGuideDetails = new JLabel();  // Use JLabel for easier updates
        topGuidePanel.add(topGuideDetails, BorderLayout.CENTER);
        infoPanel.add(topGuidePanel);

        homePanel.add(infoPanel, BorderLayout.CENTER);

        // Fetch and set the top destination and guide
        SwingUtilities.invokeLater(() -> {
            try {
                String topDestination = getTopDestination();
                topDestinationDetails.setText(topDestination != null ? topDestination : "No data available");
                
                // Update the top guide panel
                updateTopGuidePanel();
            } catch (Exception e) {
                e.printStackTrace();
                topDestinationDetails.setText("Error fetching data");
            }
        });

        return homePanel;
    }

    private String getTopDestination() throws Exception {
        StringBuilder details = new StringBuilder();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String sql = "SELECT b.destination_name, d.price, d.time, d.difficulty_level, COUNT(*) AS count " +
                         "FROM bookings b " +
                         "JOIN destinations d ON b.destination_name = d.name " +
                         "WHERE b.booking_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
                         "GROUP BY b.destination_name, d.price, d.time, d.difficulty_level " +
                         "ORDER BY count DESC " +
                         "LIMIT 1";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                String destinationName = rs.getString("destination_name");
                int price = rs.getInt("price");
                int days = rs.getInt("time");
                int difficultyLevel = rs.getInt("difficulty_level");

                details.append("Destination Name: ").append(destinationName).append("\n");
                details.append("Price: Nrs").append(price).append("\n");
                details.append("Days: ").append(days).append("\n");
                details.append("Difficulty Level (Out of 10): ").append(difficultyLevel).append("\n");
                details.append("Number of Bookings: ").append(rs.getInt("count")).append("\n");
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (con != null) con.close();
        }

        return details.toString();
    }

    private void updateTopGuidePanel() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            
            // Define the SQL query
            String sql = "SELECT gi.name, gpd.ratings, gpd.no_of_tours, gpd.no_of_tourist, gpd.earning " +
                         "FROM guide_info gi " +
                         "INNER JOIN guide_previous_detail gpd ON gi.guide_id = gpd.guide_id " +
                         "ORDER BY gpd.ratings DESC, gpd.no_of_tours DESC " +
                         "LIMIT 1";
            // Prepare the statement
            pst = con.prepareStatement(sql);
            // Execute the query
            rs = pst.executeQuery();

            // Check if results exist
            if (rs.next()) {
                String guideName = rs.getString("name");
                float ratings = rs.getFloat("ratings");
                int noOfTours = rs.getInt("no_of_tours");
                int noOfTourists = rs.getInt("no_of_tourist");
                int earnings = rs.getInt("earning");

                // Update the top guide panel with these details
                JLabel guideLabel = new JLabel("<html><b>Top Guide of the Month:</b><br>" +
                                                "Name: " + guideName + "<br>" +
                                                "Ratings: " + ratings + "<br>" +
                                                "No. of Tours: " + noOfTours + "<br>" +
                                                "No. of Tourists: " + noOfTourists + "<br>" +
                                                "Earnings: Nrs" + earnings + "</html>");
                topGuidePanel.removeAll();
                topGuidePanel.add(new JLabel("Top Guide of the Month:", SwingConstants.CENTER), BorderLayout.NORTH);
                topGuidePanel.add(guideLabel, BorderLayout.CENTER);
                topGuidePanel.revalidate();
                topGuidePanel.repaint();
            } else {
                // Handle case where no result is returned
                JLabel noGuideLabel = new JLabel("No guide data available.");
                topGuidePanel.removeAll();
                topGuidePanel.add(noGuideLabel, BorderLayout.CENTER);
                topGuidePanel.revalidate();
                topGuidePanel.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print detailed stack trace for debugging
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace(); // Print detailed stack trace for debugging
            }
        }
    }

    public void switchToGuideSearch(Destination.DestinationDetails destinationDetails) {
        // Remove existing "Guide Search" tab if it exists
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals("Guide Search")) {
                tabbedPane.remove(i);
                break;
            }
        }

        // Create and add the new "Guide Search" panel
        GuideSearch guideSearchPanel = new GuideSearch(destinationDetails, touristId, name,
                (TouristHistory) tabbedPane.getComponentAt(tabbedPane.indexOfTab("History")),
                (TouristMessage) tabbedPane.getComponentAt(tabbedPane.indexOfTab("Messages")));
        tabbedPane.addTab("Guide Search", guideSearchPanel);
        tabbedPane.setSelectedComponent(guideSearchPanel);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
