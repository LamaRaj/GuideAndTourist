package GuidePack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GuideSearch extends JPanel {
    private JPanel guidePanel;
    private JCheckBox[] filterCheckBoxes;
    private JComboBox<String> sortComboBox;
    private List<GuideDetails> guideDetailsList = new ArrayList<>();
    private Destination.DestinationDetails destinationDetails;
    private int touristId;
    private String touristName;
    
    private TouristHistory touristHistory;
    private TouristMessage touristMessage;

    public GuideSearch(Destination.DestinationDetails destinationDetails,int touristId, String touristName, TouristHistory touristHistory, TouristMessage touristMessage) {
        this.destinationDetails = destinationDetails;
        this.touristName = touristName;
        this.touristHistory = touristHistory;
        this.touristMessage = touristMessage;
        this.touristId=touristId;

        setLayout(new BorderLayout());

        // Create filter panel with checkboxes
        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        filterPanel.setLayout(new GridLayout(0, 1));

        String[] attributes = {"Number of Tours", "Ratings", "Number of Tourists"};
        filterCheckBoxes = new JCheckBox[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            filterCheckBoxes[i] = new JCheckBox(attributes[i]);
            filterPanel.add(filterCheckBoxes[i]);
        }

        // Create sorting panel with dropdown
        JPanel sortPanel = new JPanel();
        sortPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        sortPanel.setLayout(new FlowLayout());

        sortComboBox = new JComboBox<>(new String[]{"Sort by", "High to Low", "Low to High"});
        sortPanel.add(sortComboBox);

        // Create search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(filterPanel, BorderLayout.WEST);
        searchPanel.add(sortPanel, BorderLayout.EAST);

        add(searchPanel, BorderLayout.NORTH);

        // Create guide panel to hold guide details and buttons
        guidePanel = new JPanel();
        guidePanel.setLayout(new GridLayout(0, 1)); // Adjust the grid layout to hold multiple rows

        add(new JScrollPane(guidePanel), BorderLayout.CENTER);

        // Add action listeners for filters and sorting
        ActionListener updateListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchGuides();
            }
        };

        for (JCheckBox checkBox : filterCheckBoxes) {
            checkBox.addActionListener(updateListener);
        }
        sortComboBox.addActionListener(updateListener);

        // Fetch all guides initially
        fetchGuides();
    }

    private void fetchGuides() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");

            // Build query with filters and sorting
            StringBuilder sql = new StringBuilder("SELECT g.guide_id, g.name, g.username, g.email, p.no_of_tours, p.ratings, p.no_of_tourist FROM guide_info g LEFT JOIN guide_previous_detail p ON g.guide_id = p.guide_id");

            List<String> filters = new ArrayList<>();
            for (int i = 0; i < filterCheckBoxes.length; i++) {
                if (filterCheckBoxes[i].isSelected()) {
                    switch (i) {
                        case 0:
                            filters.add("p.no_of_tours IS NOT NULL");
                            break;
                        case 1:
                            filters.add("p.ratings IS NOT NULL");
                            break;
                        case 2:
                            filters.add("p.no_of_tourist IS NOT NULL");
                            break;
                    }
                }
            }

            if (!filters.isEmpty()) {
                sql.append(" WHERE ").append(String.join(" OR ", filters));
            }

            // Sorting
            String sortOption = (String) sortComboBox.getSelectedItem();
            if ("High to Low".equals(sortOption)) {
                sql.append(" ORDER BY p.ratings DESC");
            } else if ("Low to High".equals(sortOption)) {
                sql.append(" ORDER BY p.ratings ASC");
            } else {
                sql.append(" ORDER BY g.name ASC"); // Default sorting
            }

            PreparedStatement stm = con.prepareStatement(sql.toString());
            ResultSet result = stm.executeQuery();

            // Clear the existing panel
            guidePanel.removeAll();

            // Clear guide details list
            guideDetailsList.clear();

            while (result.next()) {
                int guideId = result.getInt("guide_id");
                 String guideName = result.getString("name");
                String guideUsername = result.getString("username");
                String guideEmail = result.getString("email");
                int noOfTours = result.getInt("no_of_tours");
                double ratings = result.getDouble("ratings");
                int noOfTourist = result.getInt("no_of_tourist");

                GuideDetails details = new GuideDetails(guideId, guideUsername, guideName, guideEmail, noOfTours, ratings, noOfTourist);
                guideDetailsList.add(details);

                // Create a panel for each guide detail
                JPanel detailPanel = new JPanel(new BorderLayout());
                JLabel nameLabel = new JLabel("Name: " + guideName);
                JLabel emailLabel = new JLabel("Email: " + guideEmail);
                JLabel noOfToursLabel = new JLabel("Number of Tours: " + noOfTours);
                JLabel ratingsLabel = new JLabel("Ratings: " + ratings);
                JLabel noOfTouristLabel = new JLabel("Number of Tourists: " + noOfTourist);
                JButton bookButton = new JButton("Book");

                // Add action listener for book button
                bookButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        bookGuide(details);
                    }
                });

                JPanel infoPanel = new JPanel(new GridLayout(5, 1)); // Panel for guide info
                infoPanel.add(nameLabel);
                infoPanel.add(emailLabel);
                infoPanel.add(noOfToursLabel);
                infoPanel.add(ratingsLabel);
                infoPanel.add(noOfTouristLabel);

                detailPanel.add(infoPanel, BorderLayout.CENTER);
                detailPanel.add(bookButton, BorderLayout.SOUTH);
                guidePanel.add(detailPanel);
            }

            // Revalidate and repaint to update the UI
            guidePanel.revalidate();
            guidePanel.repaint();

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching guides: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void bookGuide(GuideDetails guideDetails) {
        // Retrieve the guide's name from the GuideDetails object
        String guideName = guideDetails.getGuideName(); // Ensure getGuideName() method exists in GuideDetails

        // Create an instance of BookingConfirmationPanel
        BookingConfirmationPanel bookingPanel = new BookingConfirmationPanel(touristId, guideDetails, destinationDetails, guideName);
        
        // Get the parent frame and cast to TouristHome
        if (SwingUtilities.getWindowAncestor(this) instanceof TouristHome) {
            TouristHome touristHome = (TouristHome) SwingUtilities.getWindowAncestor(this);

            // Add BookingConfirmationPanel to the tabbed pane
            JTabbedPane tabbedPane = touristHome.getTabbedPane();
            if (tabbedPane != null) {
                tabbedPane.addTab("Booking Confirmation", bookingPanel);
                tabbedPane.setSelectedComponent(bookingPanel);
            } else {
                JOptionPane.showMessageDialog(this, "Error: Tabbed pane not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error: Cannot find the parent TouristHome frame.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }





    public static class GuideDetails {
        private int guideId;
        private String username;
        private String name;
        private String email;
        private int noOfTours;
        private double ratings;
        private int noOfTourist;

        public GuideDetails(int guideId, String username, String name, String email, int noOfTours, double ratings, int noOfTourist) {
            this.guideId = guideId;
            this.username = username;
            this.name = name;
            this.email = email;
            this.noOfTours = noOfTours;
            this.ratings = ratings;
            this.noOfTourist = noOfTourist;
        }

        
        public String getGuideName() {
        	return this.name;
        }
        public int getGuideId() {
            return this.guideId;
            
        }

        // Other getters and setters
    }

}
