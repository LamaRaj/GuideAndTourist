package GuidePack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GuideHistory extends JPanel {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private int guideId;

    public GuideHistory(int guideId) {
        this.guideId = guideId;
        setLayout(new BorderLayout());

        // Create a table model with columns
        String[] columns = {"Tour ID","Tourist Name", "Destination", "Price", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load history data
        loadHistory();
    }

    private void loadHistory() {
        try {
            // Database connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            String query = "SELECT * FROM bookings WHERE guide_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, guideId);
            ResultSet rs = pst.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            // Populate table with data
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("tourist_name"),
                    rs.getString("destination_name"),
                    rs.getInt("price"),
                    rs.getTimestamp("booking_date"),
                    rs.getString("status")
                };
                tableModel.addRow(row);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
