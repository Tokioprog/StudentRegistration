package esfe.presentacion.users;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import esfe.dominio.User;
import esfe.persistencia.UserDAO;

public class LoginForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtEmail;
    private JButton btnLogin;
    private JButton btnSalir;
    private JPasswordField txtPassword;

    // Instancias necesarias
    private UserDAO userDAO;
    private MainForm mainForm;

    // Constructor del formulario de login
    public LoginForm(MainForm mainForm){
        this.mainForm = mainForm; // Se guarda referencia al formulario principal
        userDAO = new UserDAO(); // Se instancia el DAO de usuarios

        // Configura el formulario
        setContentPane(mainPanel); // Establece el panel principal como contenido
        setModal(true); // Bloquea otros formularios hasta cerrar este
        setTitle("Login"); // Título del formulario
        pack(); // Ajusta tamaño automáticamente
        setLocationRelativeTo(mainForm); // Centra respecto al formulario principal

        // Acción del botón "Salir"
        btnSalir.addActionListener(e -> System.exit(0)); // Cierra completamente el programa

        // Acción del botón "Login"
        btnLogin.addActionListener(e -> login()); // Llama al método login

        // Detecta si el usuario cierra la ventana desde la X
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // Finaliza el programa si se cierra la ventana
            }
        });
    }

    // Método que valida las credenciales del usuario
    private void login() {
        try {
            User user = new User(); // Se crea un nuevo objeto de tipo User
            user.setEmail(txtEmail.getText()); // Se obtiene el email del campo
            user.setPasswordHash(new String(txtPassword.getPassword())); // Se obtiene la contraseña

            // Se intenta autenticar al usuario
            User userAut = userDAO.authenticate(user);

            // Si el usuario es válido y coincide su email con el ingresado
            if(userAut != null && userAut.getId() > 0 && userAut.getEmail().equals(user.getEmail())){
                this.mainForm.setUserAutenticate(userAut); // Se guarda el usuario autenticado en el formulario principal
                this.dispose(); // Se cierra el formulario de login
            }
            else {
                // Si las credenciales son incorrectas, muestra advertencia
                JOptionPane.showMessageDialog(null,
                        "Email y password incorrecto",
                        "Login",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            // Muestra cualquier error inesperado
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Sistem", // Nota: hay un error tipográfico aquí, debería decir "Sistema"
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
