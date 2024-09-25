package GuidePack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuideHome extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;
    private JPanel touristsGuidedPanel;
    private JPanel toursDonePanel;
    private JPanel totalAmountEarnedPanel;
    private JLabel touristsGuidedLabel;
    private JLabel toursDoneLabel;
    private JLabel totalAmountEarnedLabel;
    private JButton logoutButton;

    private int guideId;

    public GuideHome(int guideId) {
        this.guideId = guideId;
        setTitle("Guide Home");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the window full-screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        // Home tab
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.add(createStatsPanel(), BorderLayout.CENTER);
        tabbedPane.addTab("Home", homePanel);

        // GuideMessage tab
        JPanel guideMessagePanel = new GuideMessage(guideId, null); // Pass TouristMessage if required
        tabbedPane.addTab("Messages", guideMessagePanel);

        // History tab
        JPanel guideHistoryPanel = new GuideHistory(guideId);
        tabbedPane.addTab("History", guideHistoryPanel);

        // Logout button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        logoutButton = new JButton("Logout");
        bottomPanel.add(logoutButton, BorderLayout.EAST);
        logoutButton.addActionListener(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);

        loadData();
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        touristsGuidedPanel = new JPanel();
        toursDonePanel = new JPanel();
        totalAmountEarnedPanel = new JPanel();

        touristsGuidedLabel = new JLabel("Number of Tourists Guided: ");
        toursDoneLabel = new JLabel("Number of Tours Done: ");
        totalAmountEarnedLabel = new JLabel("Total Amount Earned: ");

        addPanelToHome(touristsGuidedPanel, touristsGuidedLabel, "Tourists Guided");
        addPanelToHome(toursDonePanel, toursDoneLabel, "Tours Done");
        addPanelToHome(totalAmountEarnedPanel, totalAmountEarnedLabel, "Amount Earned");

        statsPanel.add(touristsGuidedPanel);
        statsPanel.add(toursDonePanel);
        statsPanel.add(totalAmountEarnedPanel);

        return statsPanel;
    }

    private void addPanelToHome(JPanel panel, JLabel label, String title) {
        panel.setLayout(new GridLayout(1, 1));
        panel.setPreferredSize(new Dimension(200, 100));
        panel.setBackground(Color.WHITE);

        Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
        Border titledBorder = BorderFactory.createTitledBorder(border, title);
        panel.setBorder(titledBorder);

        panel.add(label);
    }

    private void loadData() {
        try {
            String touristsGuidedQuery = "SELECT SUM(no_of_tourist) FROM guide_previous_detail WHERE guide_id = ?";
            String toursDoneQuery = "SELECT COUNT(*) FROM guide_previous_detail WHERE guide_id = ?";
            String totalAmountEarnedQuery = "SELECT SUM(earning) FROM guide_previous_detail WHERE guide_id = ?";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");

            // Load number of tourists guided
            PreparedStatement touristsGuidedStmt = con.prepareStatement(touristsGuidedQuery);
            touristsGuidedStmt.setInt(1, guideId);
            ResultSet touristsGuidedRs = touristsGuidedStmt.executeQuery();
            if (touristsGuidedRs.next()) {
                touristsGuidedLabel.setText("Number of Tourists Guided: " + touristsGuidedRs.getInt(1));
            }

            // Load number of tours done
            PreparedStatement toursDoneStmt = con.prepareStatement(toursDoneQuery);
            toursDoneStmt.setInt(1, guideId);
            ResultSet toursDoneRs = toursDoneStmt.executeQuery();
            if (toursDoneRs.next()) {
                toursDoneLabel.setText("Number of Tours Done: " + toursDoneRs.getInt(1));
            }

            // Load total amount earned
            PreparedStatement totalAmountEarnedStmt = con.prepareStatement(totalAmountEarnedQuery);
            totalAmountEarnedStmt.setInt(1, guideId);
            ResultSet totalAmountEarnedRs = totalAmountEarnedStmt.executeQuery();
            if (totalAmountEarnedRs.next()) {
                totalAmountEarnedLabel.setText("Total Amount Earned: " + totalAmountEarnedRs.getInt(1));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            new Login().setVisible(true);
            dispose(); // Close the home window
        }
    }

    public static void main(String[] args) {
        int guideId = 1; // Replace with the actual guide_id after login
        SwingUtilities.invokeLater(() -> new GuideHome(guideId).setVisible(true));
    }
}
