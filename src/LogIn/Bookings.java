package LogIn;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Bookings extends JPanel {

    private JTable bookingsTable;
    private DefaultTableModel tableModel;

    public Bookings() {
        setLayout(new BorderLayout());

        // Create a table model with columns
        String[] columns = {"Booking ID", "Guide Name", "Tourist Name", "Destination Name", "Price", "Status", "Booking Date", "Arrival Day"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingsTable = new JTable(tableModel);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load bookings data
        loadBookings();
    }

    private void loadBookings() {
        try {
            // Database connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            // Query to fetch bookings with guide_name and booking_date
            PreparedStatement pst = con.prepareStatement(
                "SELECT b.booking_id, g.name AS guide_name, b.tourist_name, b.destination_name, b.price, b.status, b.booking_date, b.arrival_day " +
                "FROM bookings b " +
                "JOIN guide_info g ON b.guide_id = g.guide_id"
            );
            ResultSet rs = pst.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            // Populate table with data
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("guide_name"),
                    rs.getString("tourist_name"),
                    rs.getString("destination_name"),
                    rs.getInt("price"),
                    rs.getString("status"),
                    rs.getTimestamp("booking_date"),
                    rs.getInt("arrival_day")
                };
                tableModel.addRow(row);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Admin Bookings Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Bookings());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
