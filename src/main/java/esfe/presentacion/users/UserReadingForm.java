package esfe.presentacion.users;
import esfe.persistencia.UserDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import esfe.dominio.User;
import esfe.utils.CUD;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class UserReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JButton btnCreate;
    private JTable tableUsers;
    private JButton btnUpdate;
    private JButton btnDelete;

    private UserDAO userDAO;
    private MainForm mainForm;

    public UserReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        userDAO = new UserDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Usuario");
        pack();
        setLocationRelativeTo(mainForm);
        txtName.addKeyListener(new KeyAdapter() {
            // Sobrescribe el método keyReleased, que se llama cuando se suelta una tecla.
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableUsers.setModel(emptyModel);
                }
            }
        });
        btnCreate.addActionListener(s -> {
            UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.CREATE, new User());
            userWriteForm.setVisible(true);
            DefaultTableModel emptyModel = new DefaultTableModel();
            tableUsers.setModel(emptyModel);
        });
        btnUpdate.addActionListener(s -> {
            User user = getUserFromTableRow();
            if (user != null) {
                UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.UPDATE, user);
                userWriteForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableUsers.setModel(emptyModel);
            }
        });
        btnDelete.addActionListener(s -> {
            User user = getUserFromTableRow();
            if (user != null) {
                UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.DELETE, user);
                userWriteForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableUsers.setModel(emptyModel);
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<User> users = userDAO.search(query);
            createTable(users);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public void createTable(ArrayList<User> users) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Id");
        model.addColumn("Nombre");
        model.addColumn("Email");
        model.addColumn("Estatus");

        this.tableUsers.setModel(model);
        Object row[] = null;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            model.addRow(row);
            model.setValueAt(user.getId(), i, 0);
            model.setValueAt(user.getName(), i, 1);
            model.setValueAt(user.getEmail(), i, 2);
            model.setValueAt(user.getStrEstatus(), i, 3);
        }
        hideCol(0);
    }

    private void hideCol(int pColumna) {
        this.tableUsers.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableUsers.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tableUsers.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableUsers.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }

    private User getUserFromTableRow() {
        User user = null;
        try {
            int filaSelect = this.tableUsers.getSelectedRow();
            int id = 0; // Inicializa la variable id a 0.
            if (filaSelect != -1) {
                id = (int) this.tableUsers.getValueAt(filaSelect, 0);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            user = userDAO.getById(id);
            if (user.getId() == 0) {
                JOptionPane.showMessageDialog(null,
                        "No se encontró ningún usuario.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return user;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
