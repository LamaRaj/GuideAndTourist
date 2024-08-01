package LogIn;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.JOptionPane;

public class GuideDetail extends JPanel {

    private JTable table;
    private GuideTableModel tableModel;
    private JPanel resultPanel = new JPanel(new BorderLayout());

    public GuideDetail() {
        setLayout(new BorderLayout());
        loadGuideData();
        add(resultPanel, BorderLayout.CENTER);
    }

    private void loadGuideData() {
        try {
            String sql = "SELECT * FROM guide_info";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement stm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet result = stm.executeQuery();

            result.last();
            int rowCount = result.getRow();
            result.beforeFirst();

            // Updated to 9 columns to include action buttons
            String[][] data = new String[rowCount][9];

            int row = 0;
            while (result.next()) {
                data[row][0] = result.getString("guide_id");
                data[row][1] = result.getString("username");
                data[row][2] = result.getString("name");
                data[row][3] = result.getString("contact_info");
                data[row][4] = result.getString("age");
                data[row][5] = result.getString("address");
                data[row][6] = result.getString("email");
                data[row][7] = "Edit";
                data[row][8] = "Delete";
                row++;
            }

            tableModel = new GuideTableModel(data);
            table = new JTable(tableModel);
            addButtonToTable();

            JScrollPane scrollPane = new JScrollPane(table);

            resultPanel.removeAll();
            resultPanel.add(scrollPane, BorderLayout.CENTER);
            resultPanel.revalidate();
            resultPanel.repaint();

            con.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void addButtonToTable() {
        TableColumn editColumn = table.getColumnModel().getColumn(7);
        TableColumn deleteColumn = table.getColumnModel().getColumn(8);

        editColumn.setCellRenderer(new ButtonRenderer());
        editColumn.setCellEditor(new ButtonEditor(new JButton("Edit")));

        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setCellEditor(new ButtonEditor(new JButton("Delete")));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Guide Detail");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new GuideDetail());
        frame.setVisible(true);
    }

    class GuideTableModel extends AbstractTableModel {
        private String[][] data;
        private String[] columnNames = { "Guide ID", "Username", "Name", "Contact Info", "Age", "Address", "Email", "", "" };

        public GuideTableModel(String[][] data) {
            this.data = data;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public boolean isCellEditable(int row, int col) {
            return col >= 7; // Buttons are in columns 7 and 8
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = (String) value;
            fireTableCellUpdated(row, col);
        }

        public void removeRow(int row) {
            String[][] newData = new String[getRowCount() - 1][getColumnCount()];
            for (int i = 0, k = 0; i < getRowCount(); i++) {
                if (i == row) {
                    continue;
                }
                newData[k++] = data[i];
            }
            data = newData;
            fireTableDataChanged();
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private String label;
        private JButton button;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JButton button) {
            super(new JTextField());
            this.button = button;
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int col) {
            this.label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            this.row = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                if ("Edit".equals(label)) {
                    editAction(row);
                } else if ("Delete".equals(label)) {
                    deleteAction(row);
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        private void editAction(int row) {
            String guideId = (String) table.getValueAt(row, 0);
            String newUsername = JOptionPane.showInputDialog("Enter new username:");
            String newName = JOptionPane.showInputDialog("Enter new name:");
            String newContactInfo = JOptionPane.showInputDialog("Enter new contact info:");
            String newAge = JOptionPane.showInputDialog("Enter new age:");
            String newAddress = JOptionPane.showInputDialog("Enter new address:");
            String newEmail = JOptionPane.showInputDialog("Enter new email:");

            if (newUsername != null && newName != null && newContactInfo != null && newAge != null && newAddress != null && newEmail != null) {
                try {
                    String sql = "UPDATE guide_info SET username = ?, name = ?, contact_info = ?, age = ?, address = ?, email = ? WHERE guide_id = ?";
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
                    PreparedStatement stm = con.prepareStatement(sql);
                    stm.setString(1, newUsername);
                    stm.setString(2, newName);
                    stm.setString(3, newContactInfo);
                    stm.setString(4, newAge);
                    stm.setString(5, newAddress);
                    stm.setString(6, newEmail);
                    stm.setString(7, guideId);
                    stm.executeUpdate();
                    con.close();

                    table.setValueAt(newUsername, row, 1);
                    table.setValueAt(newName, row, 2);
                    table.setValueAt(newContactInfo, row, 3);
                    table.setValueAt(newAge, row, 4);
                    table.setValueAt(newAddress, row, 5);
                    table.setValueAt(newEmail, row, 6);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void deleteAction(int row) {
            String guideId = (String) table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?", "Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM guide_info WHERE guide_id = ?";
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
                    PreparedStatement stm = con.prepareStatement(sql);
                    stm.setString(1, guideId);
                    stm.executeUpdate();
                    con.close();

                    ((GuideTableModel) table.getModel()).removeRow(row);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
