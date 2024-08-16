package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookingConfirmationPanel extends JPanel {
    private int touristId;
    private GuideSearch.GuideDetails guideDetails;
    private Destination.DestinationDetails destinationDetails;
    private String guideName;
    private JTextField nameField;
    private JTextField numOfTouristsField;
    private JTextField arrivalDayField;
    private JButton confirmButton;

    public BookingConfirmationPanel(int touristId, GuideSearch.GuideDetails guideDetails, Destination.DestinationDetails destinationDetails, String guideName) {
        this.touristId = touristId;
        this.guideDetails = guideDetails;
        this.destinationDetails = destinationDetails;
        this.guideName = guideName; // Initialize guideName

        setLayout(new GridLayout(6, 2)); // Update grid layout to add another row for guideName

        // Initialize components
        add(new JLabel("Tourist Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Number of Tourists:"));
        numOfTouristsField = new JTextField();
        add(numOfTouristsField);

        add(new JLabel("Arrival Day:"));
        arrivalDayField = new JTextField();
        add(arrivalDayField);

        add(new JLabel("Guide Name:"));
        JTextField guideNameField = new JTextField(guideName);
        guideNameField.setEditable(false);
        add(guideNameField);

        confirmButton = new JButton("Confirm Booking");
        add(confirmButton);

        // Add action listener for confirm button
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmBooking();
            }
        });
    }

    private void confirmBooking() {
        String name = nameField.getText();
        int numOfTourists;
        String arrivalDay = arrivalDayField.getText();

        try {
            numOfTourists = Integer.parseInt(numOfTouristsField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number of tourists", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save booking details to the database
        saveBookingToDatabase(name, numOfTourists, arrivalDay);
    }

    private void saveBookingToDatabase(String name, int numOfTourists, String arrivalDay) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            // Load the database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");

            // Debugging: print the tourist ID
            System.out.println("Checking existence for tourist ID: " + touristId);

            // Check if the tourist_id exists in tourist_info
            String checkTouristSql = "SELECT COUNT(*) FROM tourist_info WHERE tourist_id = ?";
            PreparedStatement checkTouristPst = con.prepareStatement(checkTouristSql);
            checkTouristPst.setInt(1, touristId);
            ResultSet rs = checkTouristPst.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Tourist ID does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // SQL query to insert booking details
            String sql = "INSERT INTO bookings (tourist_id, guide_id, guide_name, tourist_name, destination_name, price, status, no_of_tourist, arrival_day) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setInt(1, touristId);
            pst.setInt(2, guideDetails.getGuideId());
            pst.setString(3, guideName); // Add guideName
            pst.setString(4, name);
            pst.setString(5, destinationDetails.name);
            pst.setInt(6, destinationDetails.price);
            pst.setString(7, "Upcoming");
            pst.setInt(8, numOfTourists);
            pst.setString(9, arrivalDay);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Booking confirmed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Booking failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close resources
            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
