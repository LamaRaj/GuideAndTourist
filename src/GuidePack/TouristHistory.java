package GuidePack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TouristHistory extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private int touristId;

    public TouristHistory(int touristId) {
        this.touristId = touristId; // Store the tourist ID
        setLayout(new BorderLayout());

        // Create a table model with columns
        String[] columns = {"Tour ID", "Destination", "Date", "Guide Name", "Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load history data
        loadTouristHistory();
    }

    // Method to load history data
    private void loadTouristHistory() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Database connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String query = "SELECT b.booking_id, b.destination_name, b.booking_date, g.name AS guide_name, b.price, b.status " +
                           "FROM bookings b " +
                           "JOIN guide_info g ON b.guide_id = g.guide_id " +
                           "WHERE b.tourist_id = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, touristId); // Set the tourist ID

            // Debugging statement
            System.out.println("Executing query: " + pst.toString());

            rs = pst.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            // Check if result set is empty
            if (!rs.isBeforeFirst()) {
                System.out.println("No data found for tourist ID: " + touristId);
                JOptionPane.showMessageDialog(this, "No booking history found for this tourist.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            }

            // Populate table with data
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("destination_name"),
                    rs.getTimestamp("booking_date"),
                    rs.getString("guide_name"),
                    rs.getInt("price"),
                    rs.getString("status")
                };
                tableModel.addRow(row);

                // Debugging statement
                System.out.println("Added row: " + row[0] + ", " + row[1] + ", " + row[2] + ", " + row[3] + ", " + row[4] + ", " + row[5]);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading tourist history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Public method to refresh data
    public void refreshData() {
        loadTouristHistory();
    }
}
