package LogIn;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Destination extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, timeField, difficultyField, priceField;
    private JButton addButton, updateButton;
    private int editRow = -1; // Row being edited, -1 means no row is currently being edited

    public Destination() {
        setLayout(new BorderLayout());

        // Table model and table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Time", "Difficulty", "Price", "Edit", "Delete"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6; // Only Edit and Delete buttons are editable
            }
        };
        table = new JTable(tableModel);
        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Edit").setCellEditor(new ButtonEditor(new JButton("Edit")));
        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JButton("Delete")));
        JScrollPane scrollPane = new JScrollPane(table);

        // Input fields
        JPanel inputPanel = new JPanel();
        idField = new JTextField(5);
        nameField = new JTextField(10);
        timeField = new JTextField(5);
        difficultyField = new JTextField(5);
        priceField = new JTextField(5);
        addButton = new JButton("Add");
        updateButton = new JButton("Update");

        inputPanel.add(idField);
        inputPanel.add(nameField);
        inputPanel.add(timeField);
        inputPanel.add(difficultyField);
        inputPanel.add(priceField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        loadData();

        // Button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDestination();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDestination();
            }
        });
    }

    private void loadData() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement pst = con.prepareStatement("SELECT * FROM destinations");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("destination_id"),
                    rs.getString("name"),
                    rs.getInt("time"),
                    rs.getInt("difficulty_level"),
                    rs.getInt("price"),
                    "Edit",
                    "Delete"
                });
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDestination() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement pst = con.prepareStatement("INSERT INTO destinations(destination_id, name, time, difficulty_level, price) VALUES (?, ?, ?, ?, ?)");
            pst.setInt(1, Integer.parseInt(idField.getText()));
            pst.setString(2, nameField.getText());
            pst.setInt(3, Integer.parseInt(timeField.getText()));
            pst.setInt(4, Integer.parseInt(difficultyField.getText()));
            pst.setInt(5, Integer.parseInt(priceField.getText()));
            pst.executeUpdate();

            tableModel.addRow(new Object[]{
                Integer.parseInt(idField.getText()),
                nameField.getText(),
                Integer.parseInt(timeField.getText()),
                Integer.parseInt(difficultyField.getText()),
                Integer.parseInt(priceField.getText()),
                "Edit",
                "Delete"
            });
            con.close();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startEditDestination(int row) {
        editRow = row;
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        timeField.setText(tableModel.getValueAt(row, 2).toString());
        difficultyField.setText(tableModel.getValueAt(row, 3).toString());
        priceField.setText(tableModel.getValueAt(row, 4).toString());
        addButton.setEnabled(false);
        updateButton.setEnabled(true);
    }

    private void updateDestination() {
        if (editRow != -1) {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                int time = Integer.parseInt(timeField.getText());
                int difficulty = Integer.parseInt(difficultyField.getText());
                int price = Integer.parseInt(priceField.getText());

                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
                PreparedStatement pst = con.prepareStatement("UPDATE destinations SET name=?, time=?, difficulty_level=?, price=? WHERE destination_id=?");
                pst.setString(1, name);
                pst.setInt(2, time);
                pst.setInt(3, difficulty);
                pst.setInt(4, price);
                pst.setInt(5, id);
                pst.executeUpdate();

                tableModel.setValueAt(name, editRow, 1);
                tableModel.setValueAt(time, editRow, 2);
                tableModel.setValueAt(difficulty, editRow, 3);
                tableModel.setValueAt(price, editRow, 4);

                con.close();
                clearFields();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteDestination(int row) {
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement pst = con.prepareStatement("DELETE FROM destinations WHERE destination_id=?");
            pst.setInt(1, id);
            pst.executeUpdate();

            tableModel.removeRow(row);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        idField.setText(" ");
        nameField.setText(" ");
        timeField.setText(" ");
        difficultyField.setText(" ");
        priceField.setText(" ");
        addButton.setEnabled(true);
        updateButton.setEnabled(true);
        editRow = -1;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Destination Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Destination());
        frame.pack();
        frame.setVisible(true);
    }

    // Custom button renderer and editor for table buttons
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor, ActionListener {
        private JButton button;
        private String label;
        private int row;

        public ButtonEditor(JButton button) {
            this.button = button;
            button.addActionListener(this);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.label = (value == null) ? "" : value.toString();
            this.row = row;
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (label.equals("Edit")) {
                startEditDestination(row);
            } else if (label.equals("Delete")) {
                deleteDestination(row);
            }
            fireEditingStopped();
        }
    }
}
