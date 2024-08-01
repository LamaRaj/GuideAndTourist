package GuidePack;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class GuideSearch extends JPanel implements ActionListener {

    private static final String[] CRITERIA = { "Number of Tours", "Number of Tourists", "Ratings", "Availability" };
    private static final String[] SORT_OPTIONS = { "High to Low", "Low to High" };

    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private JComboBox<String> sortDropdown = new JComboBox<>(SORT_OPTIONS);
    private JButton findButton = new JButton("Find");
    private JPanel resultPanel = new JPanel();
    private TitledBorder resultPanelBorder = new TitledBorder(new LineBorder(java.awt.Color.BLACK), "Search Results");

    public GuideSearch() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        for (String criterion : CRITERIA) {
            JCheckBox checkBox = new JCheckBox(criterion);
            checkBoxes.add(checkBox);
            inputPanel.add(checkBox);
        }

        inputPanel.add(new JLabel("Sort By:"));
        inputPanel.add(sortDropdown);
        inputPanel.add(findButton);

        add(inputPanel, BorderLayout.NORTH);

        resultPanel.setLayout(new BorderLayout());
        resultPanel.setBorder(resultPanelBorder);
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        add(scrollPane, BorderLayout.CENTER);

        findButton.addActionListener(this);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Guide Search");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.add(new GuideSearch());
            frame.pack();
            frame.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            List<String> selectedCriteria = getSelectedCriteria();
            String sortOrder = (String) sortDropdown.getSelectedItem();
            if (selectedCriteria.isEmpty() || sortOrder == null) {
                return;
            }
            String sql = getSQLQuery(selectedCriteria, sortOrder);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement stm = con.prepareStatement(sql);
            ResultSet result = stm.executeQuery();

            List<Object[]> resultsList = new ArrayList<>();
            while (result.next()) {
                resultsList.add(new Object[]{
                        result.getString("name"),
                        result.getString("contact_info"),
                        result.getInt("age"),
                        result.getString("address"),
                        result.getString("email"),
                        result.getInt("no_of_tours"),
                        result.getBigDecimal("ratings"),
                        result.getInt("no_of_tourist")
                });
            }

            resultPanel.removeAll();
            resultPanel.setLayout(new BorderLayout());
            JPanel resultsGrid = new JPanel();
            resultsGrid.setLayout(new GridLayout(0, 1, 5, 5));

            for (Object[] rowData : resultsList) {
                JPanel resultBox = new JPanel();
                resultBox.setLayout(new BorderLayout());
                resultBox.setBorder(new LineBorder(java.awt.Color.BLACK));
                resultBox.setPreferredSize(new Dimension(150, 100));

                String content = String.format("<html>Name: %s<br>Contact: %s<br>Age: %d<br>Address: %s<br>Email: %s<br>Number of Tours: %d<br>Ratings: %s<br>Number of Tourists: %d</html>",
                        rowData[0], rowData[1], rowData[2], rowData[3], rowData[4], rowData[5], rowData[6], rowData[7]);

                JLabel label = new JLabel(content);
                resultBox.add(label, BorderLayout.CENTER);

                JButton bookButton = new JButton("Book");
                bookButton.setPreferredSize(new Dimension(70, 30));
                resultBox.add(bookButton, BorderLayout.EAST);

                resultsGrid.add(resultBox);
            }

            resultPanel.add(resultsGrid, BorderLayout.CENTER);
            resultPanel.revalidate();
            resultPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<String> getSelectedCriteria() {
        List<String> selectedCriteria = new ArrayList<>();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedCriteria.add(checkBox.getText());
            }
        }
        return selectedCriteria;
    }

    private String getSQLQuery(List<String> criteria, String sortOrder) {
        StringBuilder sql = new StringBuilder("SELECT g.name, g.contact_info, g.age, g.address, g.email, d.no_of_tours, d.ratings, d.no_of_tourist FROM guide_info g JOIN guide_previous_detail d ON g.guide_id = d.guide_id WHERE ");
        for (int i = 0; i < criteria.size(); i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            switch (criteria.get(i)) {
                case "Number of Tours":
                    sql.append("d.no_of_tours IS NOT NULL");
                    break;
                case "Ratings":
                    sql.append("d.ratings IS NOT NULL");
                    break;
                case "Number of Tourists":
                    sql.append("d.no_of_tourist IS NOT NULL");
                    break;
                case "Availability":
                    sql.append("g.availability IS NOT NULL");
                    break;
            }
        }
        sql.append(" ORDER BY ");
        switch (sortOrder) {
            case "High to Low":
                sql.append("d.ratings DESC");
                break;
            case "Low to High":
                sql.append("d.ratings ASC");
                break;
        }
        return sql.toString();
    }
}
