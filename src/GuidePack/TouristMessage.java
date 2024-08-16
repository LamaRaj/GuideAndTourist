package GuidePack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TouristMessage extends JPanel {
    private JPanel messagePanel;
    private int touristId;
    private GuideMessage guideMessage;

    public TouristMessage(int touristId) {
        this.touristId = touristId;
        setLayout(new BorderLayout());

        messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(0, 1)); // Adjust the grid layout to hold multiple rows
        JScrollPane scrollPane = new JScrollPane(messagePanel);

        add(scrollPane, BorderLayout.CENTER);

        fetchMessages();
    }

    public void updateMessages() {
        fetchMessages(); // Refresh the messages
    }

    private void fetchMessages() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String sql = "SELECT * FROM bookings WHERE tourist_id = ? AND status = 'Upcoming'";
            pst = con.prepareStatement(sql);
            pst.setInt(1, touristId);
            rs = pst.executeQuery();

            // Clear existing messages
            messagePanel.removeAll();

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String guideName = rs.getString("guide_name");
                String destinationName = rs.getString("destination_name");

                JPanel bookingPanel = new JPanel(new BorderLayout());
                JLabel messageLabel = new JLabel("<html>Upcoming booking with guide <b>" + guideName + "</b> for destination <b>" + destinationName + "</b></html>");
                JPanel buttonPanel = new JPanel();

                JButton completeButton = new JButton("Complete");

                completeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateBookingStatus(bookingId, "Completed");
                        showRatingPopup(bookingId);
                        updatePanel();
                    }
                });

                buttonPanel.add(completeButton);

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

    private void updateBookingStatus(int bookingId, String status) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, status);
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

    private void showRatingPopup(int bookingId) {
        SwingUtilities.invokeLater(() -> {
            String ratingStr = JOptionPane.showInputDialog(this, "Please rate your guide (0.0 to 5.0):", "Guide Rating", JOptionPane.QUESTION_MESSAGE);
            try {
                float rating = Float.parseFloat(ratingStr);
                if (rating < 0.0 || rating > 5.0) {
                    throw new NumberFormatException();
                }
                saveRatingToDatabase(bookingId, rating);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid rating. Please enter a number between 0.0 and 5.0.", "Invalid Rating", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void saveRatingToDatabase(int bookingId, float rating) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String sql = "UPDATE guide_previous_detail gpd "
                       + "INNER JOIN bookings b ON gpd.guide_id = b.guide_id "
                       + "SET gpd.ratings = ((gpd.ratings * gpd.no_of_tours) + ?) / (gpd.no_of_tours + 1), "
                       + "gpd.no_of_tours = gpd.no_of_tours + 1, "
                       + "gpd.no_of_tourist = gpd.no_of_tourist + b.no_of_tourist, "
                       + "gpd.earning = gpd.earning + b.price "
                       + "WHERE b.booking_id = ?";
            pst = con.prepareStatement(sql);
            pst.setFloat(1, rating);
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

    private void updatePanel() {
        messagePanel.removeAll();
        fetchMessages(); // Refresh tourist messages
    }
}
