package esfe.presentacion.users;

import esfe.dominio.User;
import esfe.persistencia.UserDAO;

import javax.swing.*;

public class ChangePasswordForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnChangePassword;

    // Instancias necesarias
    private UserDAO userDAO;
    private MainForm mainForm;

    // Constructor del formulario de cambio de contraseña
    public ChangePasswordForm(MainForm mainForm) {
        this.mainForm = mainForm; // Se guarda una referencia al formulario principal
        userDAO = new UserDAO(); // Se instancia el DAO de usuarios

        // Se muestra en el campo txtEmail el correo del usuario autenticado
        txtEmail.setText(mainForm.getUserAutenticate().getEmail());

        // Configuración del formulario
        setContentPane(mainPanel);
        setModal(true); // Bloquea otros formularios hasta cerrar este
        setTitle("Cambiar password");
        pack(); // Ajusta el tamaño de la ventana a los componentes
        setLocationRelativeTo(mainForm); // Centra el formulario respecto al formulario principal

        // Acción del botón para cambiar la contraseña
        btnChangePassword.addActionListener(e -> changePassword());
    }

    // Método que realiza el proceso de cambio de contraseña
    private void changePassword() {
        try {
            // Obtiene el usuario actualmente autenticado
            User userAut = mainForm.getUserAutenticate();

            // Se crea un nuevo objeto User solo con el ID y la nueva contraseña
            User user = new User();
            user.setId(userAut.getId());
            user.setPasswordHash(new String(txtPassword.getPassword())); // Se obtiene la contraseña del campo

            // Validación: la contraseña no puede estar vacía
            if (user.getPasswordHash().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "La contraseña es obligatoria",
                        "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Llama al DAO para actualizar la contraseña
            boolean res = userDAO.updatePassword(user);

            if (res) {
                // Si fue exitosa, se cierra este formulario y se muestra el de login
                this.dispose(); // Cierra el formulario actual
                LoginForm loginForm = new LoginForm(this.mainForm); // Muestra nuevamente el login
                loginForm.setVisible(true);
            } else {
                // Si no se logró actualizar la contraseña
                JOptionPane.showMessageDialog(null,
                        "No se logró cambiar la contraseña",
                        "Cambiar contraseña", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            // Captura y muestra cualquier excepción inesperada
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}
