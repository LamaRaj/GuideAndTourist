package LogIn;

import java.awt.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HomePanel extends JPanel {
    private int guideCount = 0;
    private int userCount = 0;
    private int tourCount = 0;

    public HomePanel() {
        setLayout(new GridLayout(1, 1)); // A single layout for the chart

        // Load data from the database
        loadData();

        // Create the pie chart panel and add it
        PieChartPanel pieChartPanel = new PieChartPanel(guideCount, userCount, tourCount);
        add(pieChartPanel);
    }

    private void loadData() {
        try {
            String guideCountQuery = "SELECT COUNT(*) FROM guide_info";
            String userCountQuery = "SELECT COUNT(*) FROM tourist_info";
            String tourCountQuery = "SELECT COUNT(*) FROM bookings WHERE status='Completed'";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");

            // Load guide count
            PreparedStatement guideStmt = con.prepareStatement(guideCountQuery);
            ResultSet guideRs = guideStmt.executeQuery();
            if (guideRs.next()) {
                guideCount = guideRs.getInt(1);
            }

            // Load user count
            PreparedStatement userStmt = con.prepareStatement(userCountQuery);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                userCount = userRs.getInt(1);
            }

            // Load tour count
            PreparedStatement tourStmt = con.prepareStatement(tourCountQuery);
            ResultSet tourRs = tourStmt.executeQuery();
            if (tourRs.next()) {
                tourCount = tourRs.getInt(1);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class to draw the pie chart
    private class PieChartPanel extends JPanel {
        private int guideCount;
        private int userCount;
        private int tourCount;

        public PieChartPanel(int guideCount, int userCount, int tourCount) {
            this.guideCount = guideCount;
            this.userCount = userCount;
            this.tourCount = tourCount;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            drawPieChart(g2d);
        }

        private void drawPieChart(Graphics2D g2d) {
            int total = guideCount + userCount + tourCount;

            // If there are no counts, don't draw the pie chart
            if (total == 0) {
                g2d.drawString("No data to display", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            // Calculate angles for each section of the pie chart
            int guideAngle = (int) Math.round((guideCount * 360.0) / total);
            int userAngle = (int) Math.round((userCount * 360.0) / total);
            int tourAngle = 360 - (guideAngle + userAngle);  // Remaining angle for tours

            // Draw the pie chart sections
            int x = getWidth() / 4;
            int y = getHeight() / 4;
            int diameter = Math.min(getWidth() / 2, getHeight() / 2);

            // Draw guides section
            g2d.setColor(Color.RED);
            g2d.fillArc(x, y, diameter, diameter, 0, guideAngle);

            // Draw users section
            g2d.setColor(Color.GREEN);
            g2d.fillArc(x, y, diameter, diameter, guideAngle, userAngle);

            // Draw tours section
            g2d.setColor(Color.BLUE);
            g2d.fillArc(x, y, diameter, diameter, guideAngle + userAngle, tourAngle);

            // Add legend
            drawLegend(g2d, x + diameter + 20, y);
        }

        private void drawLegend(Graphics2D g2d, int x, int y) {
            int legendHeight = 20;

            // Guides legend
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Guides: " + guideCount, x + 30, y + 15);

            // Users legend
            g2d.setColor(Color.GREEN);
            g2d.fillRect(x, y + legendHeight + 10, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Tourists: " + userCount, x + 30, y + legendHeight + 25);

            // Tours legend
            g2d.setColor(Color.BLUE);
            g2d.fillRect(x, y + 2 * (legendHeight + 10), 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Tours: " + tourCount, x + 30, y + 2 * (legendHeight + 25));
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Home Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new HomePanel());
        frame.setVisible(true);
    }
}
