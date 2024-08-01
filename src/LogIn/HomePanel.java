package LogIn;

import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HomePanel extends JPanel {
    private JPanel guidePanel;
    private JPanel userPanel;
    private JPanel tourPanel;
    private JLabel guideCountLabel;
    private JLabel userCountLabel;
    private JLabel tourCountLabel;

    public HomePanel() {
        setLayout(new GridLayout(3, 1, 10, 10)); // 10 pixel gaps between rows

        guidePanel = new JPanel();
        userPanel = new JPanel();
        tourPanel = new JPanel();

        guideCountLabel = new JLabel("Number of Guides: ");
        userCountLabel = new JLabel("Number of Users as Tourists: ");
        tourCountLabel = new JLabel("Number of Tours Planned: ");

        addPanelToHome(guidePanel, guideCountLabel, "Guides");
        addPanelToHome(userPanel, userCountLabel, "Users");
        addPanelToHome(tourPanel, tourCountLabel, "Tours");

        loadData();
    }

    private void addPanelToHome(JPanel panel, JLabel label, String title) {
        panel.setLayout(new GridLayout(1, 1));
        panel.setPreferredSize(new Dimension(200, 100));
        panel.setBackground(Color.WHITE);

        Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
        Border titledBorder = BorderFactory.createTitledBorder(border, title);
        panel.setBorder(titledBorder);

        panel.add(label);
        add(panel);
    }

    private void loadData() {
        try {
            String guideCountQuery = "SELECT COUNT(*) FROM guide_info";
            String userCountQuery = "SELECT COUNT(*) FROM tourist_info";
            String tourCountQuery = "SELECT COUNT(*) FROM guide_previous_detail";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");

            // Load guide count
            PreparedStatement guideStmt = con.prepareStatement(guideCountQuery);
            ResultSet guideRs = guideStmt.executeQuery();
            if (guideRs.next()) {
                guideCountLabel.setText("Number of Guides: " + guideRs.getInt(1));
            }

            // Load user count
            PreparedStatement userStmt = con.prepareStatement(userCountQuery);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                userCountLabel.setText("Number of Tourists: " + userRs.getInt(1));
            }

            // Load tour count
            PreparedStatement tourStmt = con.prepareStatement(tourCountQuery);
            ResultSet tourRs = tourStmt.executeQuery();
            if (tourRs.next()) {
                tourCountLabel.setText("Number of Tours Planned: " + tourRs.getInt(1));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
