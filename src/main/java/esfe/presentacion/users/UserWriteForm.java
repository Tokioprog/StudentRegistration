package esfe.presentacion.users;
import esfe.persistencia.UserDAO;
import esfe.utils.CBOption;
import esfe.utils.CUD;
import esfe.dominio.User;
import javax.swing.*;

public class UserWriteForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox cbStatus;
    private JLabel lbPassword;
    private JButton btnOk;
    private JButton btnCancel;

    // Dependencias y datos
    private UserDAO userDAO;       // DAO para operaciones con base de datos
    private MainForm mainForm;     // Referencia al formulario principal
    private CUD cud;               // Tipo de operación: CREATE, UPDATE, DELETE
    private User en;               // Usuario que se va a manipular

    // Constructor
    public UserWriteForm(MainForm mainForm, CUD cud, User user) {
        this.cud = cud;             // Tipo de operación
        this.en = user;            // Usuario sobre el que se hará la operación
        this.mainForm = mainForm;
        userDAO = new UserDAO();   // Inicializa acceso a base de datos

        // Configura el formulario
        setContentPane(mainPanel);
        setModal(true);
        init();                    // Inicializa campos y controles según la operación
        pack();
        setLocationRelativeTo(mainForm); // Centra el formulario respecto al principal

        // Acción del botón Cancelar
        btnCancel.addActionListener(s -> this.dispose());

        // Acción del botón OK (Guardar/Eliminar)
        btnOk.addActionListener(s -> ok());
    }

    // Método que inicializa el formulario según el tipo de operación
    private void init() {
        initCBStatus(); // Carga los valores del combo box de estatus

        switch (this.cud) {
            case CREATE:
                setTitle("Crear Usuario");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Usuario");
                btnOk.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Usuario");
                btnOk.setText("Eliminar");
                break;
        }

        setValuesControls(this.en); // Llena los controles con los datos del usuario
    }

    // Inicializa las opciones del ComboBox de estatus
    private void initCBStatus() {
        DefaultComboBoxModel<CBOption> model = (DefaultComboBoxModel<CBOption>) cbStatus.getModel();
        model.addElement(new CBOption("ACTIVO", (byte)1));
        model.addElement(new CBOption("INACTIVO", (byte)2));
    }

    // Asigna los valores del objeto User a los controles visuales
    private void setValuesControls(User user) {
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        cbStatus.setSelectedItem(new CBOption(null, user.getStatus()));

        // En modo CREATE, el estatus por defecto será ACTIVO
        if (this.cud == CUD.CREATE) {
            cbStatus.setSelectedItem(new CBOption(null, 1));
        }

        // En modo DELETE, los campos no son editables
        if (this.cud == CUD.DELETE) {
            txtName.setEditable(false);
            txtEmail.setEditable(false);
            cbStatus.setEnabled(false);
        }

        // En modo UPDATE o DELETE no se permite cambiar la contraseña
        if (this.cud != CUD.CREATE) {
            txtPassword.setVisible(false);
            lbPassword.setVisible(false);
        }
    }

    // Obtiene y valida los valores ingresados en el formulario
    private boolean getValuesControls() {
        boolean res = false;

        // Obtiene el estatus seleccionado
        CBOption selectedOption = (CBOption) cbStatus.getSelectedItem();
        byte status = selectedOption != null ? (byte) selectedOption.getValue() : 0;

        // Validaciones básicas
        if (txtName.getText().trim().isEmpty()) {
            return res;
        } else if (txtEmail.getText().trim().isEmpty()) {
            return res;
        } else if (status == 0) {
            return res;
        } else if (this.cud != CUD.CREATE && this.en.getId() == 0) {
            return res;
        }

        // Si pasa validaciones, actualiza el objeto User con los nuevos valores
        res = true;
        this.en.setName(txtName.getText());
        this.en.setEmail(txtEmail.getText());
        this.en.setStatus(status);

        // Solo en CREATE se debe establecer la contraseña
        if (this.cud == CUD.CREATE) {
            this.en.setPasswordHash(new String(txtPassword.getPassword()));
            if (this.en.getPasswordHash().trim().isEmpty()) {
                return false;
            }
        }

        return res;
    }

    // Método que ejecuta la operación (Guardar o Eliminar)
    private void ok() {
        try {
            boolean res = getValuesControls(); // Valida y recupera datos
            if (res) {
                boolean r = false;

                // Según el tipo de operación ejecuta el método correspondiente en el DAO
                switch (this.cud) {
                    case CREATE:
                        User user = userDAO.create(this.en);
                        r = user.getId() > 0;
                        break;
                    case UPDATE:
                        r = userDAO.update(this.en);
                        break;
                    case DELETE:
                        r = userDAO.delete(this.en);
                        break;
                }

                // Muestra el resultado de la operación
                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose(); // Cierra el formulario
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Los campos con * son obligatorios",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}

