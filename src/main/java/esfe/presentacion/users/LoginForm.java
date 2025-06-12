package esfe.presentacion.users; // Mantenemos el paquete original

import javax.swing.*;
import java.awt.*; // Necesario para Color, Font, Dimension, Insets
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import esfe.dominio.User;
import esfe.persistencia.UserDAO;
import javax.swing.border.EmptyBorder; // Para añadir espacio alrededor de los componentes
import javax.swing.border.LineBorder; // Para bordes personalizados en campos de texto

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

        // Configura el formulario (Aquí aplicamos mejoras)
        setContentPane(mainPanel); // Establece el panel principal como contenido
        setModal(true); // Bloquea otros formularios hasta cerrar este
        setTitle("Acceso al Sistema"); // Título del formulario mejorado
        setSize(450, 300); // Establecemos un tamaño fijo y más grande
        setResizable(false); // Deshabilitamos el redimensionamiento
        setLocationRelativeTo(mainForm); // Centra respecto al formulario principal
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Controlamos el cierre manualmente

        // --- APLICACIÓN DE ESTILOS Y MEJORAS VISUALES ---
        applyVisualEnhancements(); // Nueva llamada para aplicar los estilos

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

    // Nuevo método para aplicar todas las mejoras visuales
    private void applyVisualEnhancements() {
        // Estilos del Panel Principal
        mainPanel.setBackground(new Color(230, 240, 250)); // Fondo azul claro suave
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30)); // Más padding alrededor

        // Si tienes JLabels para "Email" y "Password" en tu diseño, podrías estilizaros aquí:
        // Por ejemplo, si los llamaras `lblEmail` y `lblPassword` en tu .form:
        /*
        if (lblEmail != null) {
            lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblEmail.setForeground(new Color(70, 70, 70));
        }
        if (lblPassword != null) {
            lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblPassword.setForeground(new Color(70, 70, 70));
        }
        */

        // Estilos de los Campos de Texto (Email y Contraseña)
        Font textFieldFont = new Font("Arial", Font.PLAIN, 16);
        Color borderColor = new Color(150, 150, 200); // Borde azul grisáceo
        int borderWidth = 1;

        txtEmail.setFont(textFieldFont);
        txtEmail.setBorder(new LineBorder(borderColor, borderWidth, true)); // Borde redondeado
        txtEmail.setPreferredSize(new Dimension(250, 38)); // Tamaño preferido más grande
        txtEmail.setBackground(Color.WHITE); // Fondo blanco para los campos

        txtPassword.setFont(textFieldFont);
        txtPassword.setBorder(new LineBorder(borderColor, borderWidth, true)); // Borde redondeado
        txtPassword.setPreferredSize(new Dimension(250, 38)); // Tamaño preferido más grande
        txtPassword.setBackground(Color.WHITE); // Fondo blanco para los campos


        // Estilos de los Botones (Login y Salir)
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);
        Dimension buttonSize = new Dimension(150, 45); // Tamaño consistente y más grande

        btnLogin.setFont(buttonFont);
        btnLogin.setBackground(new Color(70, 130, 180)); // Azul acero
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false); // Elimina el borde de foco
        btnLogin.setPreferredSize(buttonSize);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding interno
        // Efecto hover para el botón de Login
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(90, 150, 200)); // Más claro al pasar el ratón
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(70, 130, 180)); // Vuelve al original
            }
        });


        btnSalir.setFont(buttonFont);
        btnSalir.setBackground(new Color(220, 90, 90)); // Rojo suave para salir
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false); // Elimina el borde de foco
        btnSalir.setPreferredSize(buttonSize);
        btnSalir.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding interno
        // Efecto hover para el botón de Salir
        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSalir.setBackground(new Color(240, 110, 110)); // Más claro al pasar el ratón
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSalir.setBackground(new Color(220, 90, 90)); // Vuelve al original
            }
        });

        // Configuración general del JDialog para un mejor aspecto
        setUndecorated(false); // Mantiene la barra de título por defecto para evitar problemas de cierre si no hay botones de control
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
                JOptionPane.showMessageDialog(this, // Cambiado de 'null' a 'this' para que el diálogo se centre en el LoginForm
                        "Correo electrónico o contraseña incorrectos.", // Mensaje más descriptivo
                        "Error de autenticación", // Título más claro
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            // Muestra cualquier error inesperado
            JOptionPane.showMessageDialog(this, // Cambiado de 'null' a 'this'
                    "Ocurrió un error inesperado: " + ex.getMessage(), // Mensaje más informativo
                    "Error del Sistema", // Corregido el error tipográfico
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Esto es útil para la depuración
        }
    }
}