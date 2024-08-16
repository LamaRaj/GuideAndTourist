package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class Destination extends JPanel {
    private JPanel destinationPanel;
    private JTextField searchField;
    private JButton searchButton;
    private List<DestinationDetails> destinationDetailsList = new ArrayList<>();
    private TouristHome parent;

    public Destination(TouristHome parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        // Create search panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Create destination panel to hold destination details and buttons
        destinationPanel = new JPanel();
        destinationPanel.setLayout(new GridLayout(0, 1)); // Adjust the grid layout to hold multiple rows

        add(new JScrollPane(destinationPanel), BorderLayout.CENTER);

        // Add action listener for search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                fetchDestinations(searchText);
            }
        });

        // Fetch all destinations initially
        fetchDestinations(null);
    }

    private void fetchDestinations(String searchText) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String sql = "SELECT * FROM destinations";
            if (searchText != null && !searchText.isEmpty()) {
                sql += " WHERE name LIKE ?";
            }
            sql += " ORDER BY name ASC";
            PreparedStatement stm = con.prepareStatement(sql);
            if (searchText != null && !searchText.isEmpty()) {
                stm.setString(1, "%" + searchText + "%");
            }
            ResultSet result = stm.executeQuery();

            // Clear the existing panel
            destinationPanel.removeAll();

            // Clear destination details list
            destinationDetailsList.clear();

            while (result.next()) {
                String name = result.getString("name");
                int price = result.getInt("price");
                int time = result.getInt("time");
                int difficultyLevel = result.getInt("difficulty_level");

                DestinationDetails details = new DestinationDetails(name, price, time, difficultyLevel);
                binaryInsert(destinationDetailsList, details);
            }

            // Add sorted destination details to the panel
            for (DestinationDetails details : destinationDetailsList) {
                JPanel detailPanel = new JPanel();
                detailPanel.setLayout(new BorderLayout());
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new GridLayout(0, 1));

                JLabel nameLabel = new JLabel("Name: " + details.name);
                JLabel priceLabel = new JLabel("Price: NRs" + details.price);
                JLabel timeLabel = new JLabel("Time: " + details.time + " hours");
                JLabel difficultyLevelLabel = new JLabel("Difficulty Level: " + details.difficultyLevel);

                JButton bookButton = new JButton("Book");

                // Add action listener for book button
                bookButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        parent.switchToGuideSearch(details);
                    }
                });

                infoPanel.add(nameLabel);
                infoPanel.add(priceLabel);
                infoPanel.add(timeLabel);
                infoPanel.add(difficultyLevelLabel);

                detailPanel.add(infoPanel, BorderLayout.CENTER);
                detailPanel.add(bookButton, BorderLayout.SOUTH);

                destinationPanel.add(detailPanel);
            }

            // Revalidate and repaint to update the UI
            destinationPanel.revalidate();
            destinationPanel.repaint();

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void binaryInsert(List<DestinationDetails> list, DestinationDetails newDetail) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (newDetail.name.compareTo(list.get(mid).name) < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        list.add(low, newDetail);
    }


    public static class DestinationDetails {
        String name;
        int price;
        int time;
        int difficultyLevel;

        public DestinationDetails(String name, int price, int time, int difficultyLevel) {
            this.name = name;
            this.price = price;
            this.time = time;
            this.difficultyLevel = difficultyLevel;
        }
    }
}
