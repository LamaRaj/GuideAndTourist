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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.JOptionPane;

public class UserDetail extends JPanel {

    private JTable table;
    private UserTableModel tableModel;
    private JPanel resultPanel = new JPanel(new BorderLayout());

    public UserDetail() {
        setLayout(new BorderLayout());

        loadUserData();

        // Add resultPanel to UserDetail panel
        add(resultPanel, BorderLayout.CENTER);
    }

    private void loadUserData() {
        try {
            String sql = "SELECT * FROM tourist_info";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/project", "root", "root");
            PreparedStatement stm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet result = stm.executeQuery();

            result.last();
            int rowCount = result.getRow();
            result.beforeFirst();

            String[][] data = new String[rowCount][7];

            int row = 0;
            while (result.next()) {
                data[row][0] = result.getString("user_id");
                data[row][1] = result.getString("username");
                data[row][2] = result.getString("password");
                data[row][3] = result.getString("email");
                data[row][4] = result.getString("contact");
                data[row][5] = "Edit";
                data[row][6] = "Delete";
                row++;
            }

            tableModel = new UserTableModel(data);
            table = new JTable(tableModel);
            addButtonToTable();

            JScrollPane scrollPane = new JScrollPane(table);

            // Clear previous results and add new results
            resultPanel.removeAll();
            resultPanel.add(scrollPane, BorderLayout.CENTER);
            resultPanel.revalidate();
            resultPanel.repaint();

        } catch (Exception e1) {
            System.err.println(e1);
        }
    }

    private void addButtonToTable() {
        TableColumn editColumn = table.getColumnModel().getColumn(5);
        TableColumn deleteColumn = table.getColumnModel().getColumn(6);

        editColumn.setCellRenderer(new ButtonRenderer());
        editColumn.setCellEditor(new ButtonEditor(new JButton("Edit")));

        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setCellEditor(new ButtonEditor(new JButton("Delete")));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("User Detail");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new UserDetail());
        frame.setVisible(true);
    }

    class UserTableModel extends AbstractTableModel {
        private String[][] data;
        private String[] columnNames = { "User ID", "Username", "Password", "Email", "Contact", "", "" };

        public UserTableModel(String[][] data) {
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
            return col >= 5;
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
            // Implement edit functionality here
            String userId = (String) table.getValueAt(row, 0);
            String newUsername = JOptionPane.showInputDialog("Enter new username:");
            String newPassword = JOptionPane.showInputDialog("Enter new password:");
            String newEmail = JOptionPane.showInputDialog("Enter new email:");
            String newContact = JOptionPane.showInputDialog("Enter new contact:");
            if (newUsername != null && newPassword != null && newEmail != null && newContact != null) {
                try {
                    String sql = "UPDATE tourist_info SET username = ?, password = ?, email = ?, contact = ? WHERE user_id = ?";
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/test", "root", "root");
                    PreparedStatement stm = con.prepareStatement(sql);
                    stm.setString(1, newUsername);
                    stm.setString(2, newPassword);
                    stm.setString(3, newEmail);
                    stm.setString(4, newContact);
                    stm.setString(5, userId);
                    stm.executeUpdate();
                    con.close();

                    // Update the table
                    table.setValueAt(newUsername, row, 1);
                    table.setValueAt(newPassword, row, 2);
                    table.setValueAt(newEmail, row, 3);
                    table.setValueAt(newContact, row, 4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void deleteAction(int row) {
            // Implement delete functionality here
            String userId = (String) table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?", "Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM tourist_info WHERE user_id = ?";
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3307/test", "root", "root");
                    PreparedStatement stm = con.prepareStatement(sql);
                    stm.setString(1, userId);
                    stm.executeUpdate();
                    con.close();

                    // Remove the row from the table model
                    ((UserTableModel) table.getModel()).removeRow(row);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
