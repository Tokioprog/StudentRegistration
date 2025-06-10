package esfe.presentacion.users;
import esfe.dominio.User;
import esfe.persistencia.UserDAO;

import javax.swing.*;

public class ChangePasswordForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnChangePassword;

    private UserDAO userDAO;
    private MainForm mainForm;

    public ChangePasswordForm(MainForm mainForm) {
        this.mainForm = mainForm;
        userDAO = new UserDAO();
        txtEmail.setText(mainForm.getUserAutenticate().getEmail());
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Cambiar password");
        pack();
        setLocationRelativeTo(mainForm);

        btnChangePassword.addActionListener(e-> changePassword());

    }
    private void changePassword() {

        try {
            User userAut = mainForm.getUserAutenticate();
            User user = new User();
            user.setId(userAut.getId());
            user.setPasswordHash(new String(txtPassword.getPassword()));

            if (user.getPasswordHash().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "La contraseña es obligatoria",
                        "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean res = userDAO.updatePassword(user);

            if (res) {
                this.dispose();
                LoginForm loginForm = new LoginForm(this.mainForm);
                loginForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                        "No se logro cambiar la contraseña",
                        "Cambiar contraseña", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Sistema", JOptionPane.ERROR_MESSAGE);
        }

    }
}
