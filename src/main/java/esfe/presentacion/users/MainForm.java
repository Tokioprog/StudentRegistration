package esfe.presentacion.users;

import esfe.dominio.User;
import esfe.presentacion.careers.CareerReadingForm;
import esfe.presentacion.students.StudentReadingForm;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder; // For padding in menus
import java.io.File; // For loading images if needed (not directly used but good for future)
import javax.imageio.ImageIO; // For loading images if needed (not directly used but good for future)

public class MainForm extends JFrame {
    private User userAutenticate;
    private JPanel panel1; // This panel isn't used in your current code, you could use it to organize content

    public User getUserAutenticate() {
        return userAutenticate;
    }

    public void setUserAutenticate(User userAutenticate) {
        this.userAutenticate = userAutenticate;
    }

    public MainForm() {
        setTitle("Sistema en java de escritorio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Set a custom background color for the content pane
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice Blue

        createMenu();
        addWelcomeMessage();
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Customize the MenuBar appearance
        menuBar.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        menuBar.setForeground(Color.WHITE); // Text color for the menu bar (not directly visible on JMenu)
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add some padding

        // --- Perfil Menu ---
        JMenu menuPerfil = new JMenu("Perfil");
        menuPerfil.setForeground(Color.WHITE); // Text color for the menu title
        menuPerfil.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Custom font
        menuBar.add(menuPerfil);

        // Menu items
        JMenuItem itemChangePassword = new JMenuItem("Cambiar contraseña");
        JMenuItem itemChangeUser = new JMenuItem("Cambiar de usuario");
        JMenuItem itemSalir = new JMenuItem("Salir");

        // Apply a consistent look to menu items
        customizeMenuItem(itemChangePassword);
        customizeMenuItem(itemChangeUser);
        customizeMenuItem(itemSalir);

        menuPerfil.add(itemChangePassword);
        menuPerfil.add(itemChangeUser);
        menuPerfil.addSeparator(); // Add a separator for better organization
        menuPerfil.add(itemSalir);

        itemChangePassword.addActionListener(e -> {
            ChangePasswordForm changePassword = new ChangePasswordForm(this);
            changePassword.setVisible(true);
        });

        itemChangeUser.addActionListener(e -> {
            LoginForm loginForm = new LoginForm(this);
            loginForm.setVisible(true);
        });

        itemSalir.addActionListener(e -> System.exit(0));

        // --- Mantenimiento Menu ---
        JMenu menuMantenimiento = new JMenu("Mantenimientos");
        menuMantenimiento.setForeground(Color.WHITE); // Text color for the menu title
        menuMantenimiento.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Custom font
        menuBar.add(menuMantenimiento);

        // Menu items
        JMenuItem itemUsers = new JMenuItem("Usuarios");
        JMenuItem itemCareers = new JMenuItem("Carreras");
        JMenuItem itemStudents = new JMenuItem("Estudiantes");

        // Apply a consistent look to menu items
        customizeMenuItem(itemUsers);
        customizeMenuItem(itemCareers);
        customizeMenuItem(itemStudents);

        menuMantenimiento.add(itemUsers);
        menuMantenimiento.add(itemCareers);
        menuMantenimiento.add(itemStudents);

        itemUsers.addActionListener(e -> {
            UserReadingForm userReadingForm = new UserReadingForm(this);
            userReadingForm.setVisible(true);
        });
        itemCareers.addActionListener(e -> {
            CareerReadingForm careerReadingForm = new CareerReadingForm(this);
            careerReadingForm.setVisible(true);
        });
        itemStudents.addActionListener(e -> {
            StudentReadingForm studentReadingForm = new StudentReadingForm(this);
            studentReadingForm.setVisible(true);
        });
    }

    private void addWelcomeMessage() {
        JLabel welcomeLabel = new JLabel("¡Bienvenido al Sistema de Registro Estudiantil!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Larger and bolder font
        welcomeLabel.setForeground(new Color(25, 25, 112)); // Midnight Blue for the text

        // Add a bit of padding around the label for better aesthetics
        welcomeLabel.setBorder(new EmptyBorder(50, 20, 50, 20));

        getContentPane().add(welcomeLabel, BorderLayout.CENTER);
    }

    // Helper method to customize menu items for consistency
    private void customizeMenuItem(JMenuItem item) {
        item.setBackground(Color.WHITE); // White background for items
        item.setForeground(Color.BLACK); // Black text for items
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Consistent font
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding

        // Add hover effect
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(220, 220, 220)); // Light gray on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(Color.WHITE); // Back to white on exit
            }
        });
    }
}