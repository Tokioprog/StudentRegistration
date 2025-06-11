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

    // DAO para acceder a datos de usuario
    private UserDAO userDAO;
    private MainForm mainForm;

    // Constructor del formulario de lectura de usuarios
    public UserReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        userDAO = new UserDAO(); // Inicializa el DAO

        // Configura el formulario
        setContentPane(mainPanel); // Establece el panel como contenido principal
        setModal(true); // Hace el formulario modal
        setTitle("Buscar Usuario"); // Título de la ventana
        pack(); // Ajusta el tamaño automáticamente
        setLocationRelativeTo(mainForm); // Centra el formulario respecto al principal

        // Evento de teclado en el campo de texto
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText()); // Realiza búsqueda si hay texto
                } else {
                    // Limpia la tabla si el campo está vacío
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableUsers.setModel(emptyModel);
                }
            }
        });

        // Acción del botón "Crear"
        btnCreate.addActionListener(s -> {
            UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.CREATE, new User());
            userWriteForm.setVisible(true); // Abre formulario de escritura en modo CREAR
            tableUsers.setModel(new DefaultTableModel()); // Limpia la tabla después de crear
        });

        // Acción del botón "Actualizar"
        btnUpdate.addActionListener(s -> {
            User user = getUserFromTableRow(); // Obtiene el usuario seleccionado
            if (user != null) {
                UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.UPDATE, user);
                userWriteForm.setVisible(true); // Abre formulario de escritura en modo ACTUALIZAR
                tableUsers.setModel(new DefaultTableModel()); // Limpia la tabla
            }
        });

        // Acción del botón "Eliminar"
        btnDelete.addActionListener(s -> {
            User user = getUserFromTableRow(); // Obtiene el usuario seleccionado
            if (user != null) {
                UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.DELETE, user);
                userWriteForm.setVisible(true); // Abre formulario de escritura en modo ELIMINAR
                tableUsers.setModel(new DefaultTableModel()); // Limpia la tabla
            }
        });
    }

    // Método que busca usuarios por nombre
    private void search(String query) {
        try {
            ArrayList<User> users = userDAO.search(query); // Busca en BD
            createTable(users); // Llena la tabla con los resultados
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método que genera el modelo de tabla y llena los datos
    public void createTable(ArrayList<User> users) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace todas las celdas no editables
            }
        };

        // Columnas de la tabla
        model.addColumn("Id");
        model.addColumn("Nombre");
        model.addColumn("Email");
        model.addColumn("Estatus");

        // Asigna el modelo a la tabla
        this.tableUsers.setModel(model);
        Object row[] = null;

        // Agrega cada usuario como fila
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            model.addRow(row);
            model.setValueAt(user.getId(), i, 0);
            model.setValueAt(user.getName(), i, 1);
            model.setValueAt(user.getEmail(), i, 2);
            model.setValueAt(user.getStrEstatus(), i, 3);
        }

        // Oculta la columna de Id por seguridad y estética
        hideCol(0);
    }

    // Método para ocultar una columna de la tabla
    private void hideCol(int pColumna) {
        this.tableUsers.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableUsers.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tableUsers.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableUsers.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }

    // Método que obtiene el usuario seleccionado en la tabla
    private User getUserFromTableRow() {
        User user = null;
        try {
            int filaSelect = this.tableUsers.getSelectedRow(); // Fila seleccionada
            int id = 0;

            if (filaSelect != -1) {
                id = (int) this.tableUsers.getValueAt(filaSelect, 0); // Obtiene el ID oculto
            } else {
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            user = userDAO.getById(id); // Busca el usuario por ID

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
