package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GuideMessage extends JPanel {
    private JPanel messagePanel;
    private int guideId;
    private TouristMessage touristMessage;

    public GuideMessage(int guideId, TouristMessage touristMessage) {
        this.guideId = guideId;
        this.touristMessage = touristMessage;

        setLayout(new BorderLayout());

        messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(0, 1)); // Adjust the grid layout to hold multiple rows
        JScrollPane scrollPane = new JScrollPane(messagePanel);

        add(scrollPane, BorderLayout.CENTER);

        fetchMessages();
    }

    private void fetchMessages() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String sql = "SELECT * FROM bookings WHERE guide_id = ? AND status = 'Upcoming'";
            pst = con.prepareStatement(sql);
            pst.setInt(1, guideId);
            rs = pst.executeQuery();

            // Clear existing messages
            messagePanel.removeAll();

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String touristName = rs.getString("tourist_name");
                String destinationName = rs.getString("destination_name");

                JPanel bookingPanel = new JPanel(new BorderLayout());
                JLabel messageLabel = new JLabel("<html>New booking from <b>" + touristName + "</b> for destination <b>" + destinationName + "</b></html>");
                JPanel buttonPanel = new JPanel();

                JButton acceptButton = new JButton("Accept");
                JButton rejectButton = new JButton("Reject");

                acceptButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateBookingStatus(bookingId, "Active");
                        notifyTourist(touristName, destinationName, "Active");
                        updatePanel();
                    }
                });

                rejectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateBookingStatus(bookingId, "Canceled");
                        notifyTourist(touristName, destinationName, "Canceled");
                        updatePanel();
                    }
                });

                buttonPanel.add(acceptButton);
                buttonPanel.add(rejectButton);

                bookingPanel.add(messageLabel, BorderLayout.CENTER);
                bookingPanel.add(buttonPanel, BorderLayout.SOUTH);

                messagePanel.add(bookingPanel);
            }

            messagePanel.revalidate();
            messagePanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateBookingStatus(int bookingId, String status) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, status); // Ensure the status fits the column constraints
            pst.setInt(2, bookingId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void notifyTourist(String touristName, String destinationName, String status) {
        if (touristMessage != null) {
            touristMessage.updateMessages(); // Refresh tourist messages
        }
    }

    private void updatePanel() {
        messagePanel.removeAll();
        fetchMessages(); // Refresh guide messages
    }
}
