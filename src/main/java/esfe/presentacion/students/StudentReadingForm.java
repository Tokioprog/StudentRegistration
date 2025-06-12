package esfe.presentacion.students;

import esfe.dominio.Students;
import esfe.persistencia.StudentDAO;
import esfe.presentacion.users.MainForm;
import esfe.utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.*; // Importación para Color, Font, Dimension
import javax.swing.border.EmptyBorder; // Importación para padding
import javax.swing.border.LineBorder; // Importación para bordes

public class StudentReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JButton btnCreate;
    private JTable tableStudents;
    private JButton btnUpdate;
    private JButton btnDelete;
    private StudentDAO studentDAO;

    private MainForm mainForm;

    public StudentReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        studentDAO = new StudentDAO();

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Gestión de Estudiantes"); // Título mejorado
        setSize(800, 550); // Tamaño fijo para la ventana
        setResizable(true); // Permitimos redimensionar si la tabla es grande
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Cierra solo esta ventana
        setLocationRelativeTo(mainForm);

        // --- APLICACIÓN DE ESTILOS Y MEJORAS VISUALES ---
        applyVisualEnhancements();

        // Listener para buscar estudiantes al escribir en el campo de texto
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    // Si el campo de texto está vacío, limpia la tabla
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableStudents.setModel(emptyModel);
                }
            }
        });

        // Listener para el botón Crear
        btnCreate.addActionListener(s -> {
            StudentWriteForm studentWriteForm = new StudentWriteForm(this.mainForm, CUD.CREATE, new Students());
            studentWriteForm.setVisible(true);
            // Después de cerrar el formulario de escritura, limpia la tabla para forzar una nueva búsqueda si se desea
            // o se recarga con el texto actual si existe
            if (!txtName.getText().trim().isEmpty()) {
                search(txtName.getText());
            } else {
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableStudents.setModel(emptyModel);
            }
        });

        // Listener para el botón Modificar
        btnUpdate.addActionListener(s -> {
            Students student = getStudentFromTableRow(); // Obtiene el estudiante seleccionado en la tabla
            if (student != null) { // Si hay un estudiante seleccionado
                StudentWriteForm studentWriteForm = new StudentWriteForm(this.mainForm, CUD.UPDATE, student);
                studentWriteForm.setVisible(true);
                // Después de cerrar el formulario de escritura, limpia la tabla
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableStudents.setModel(emptyModel);
                }
            }
        });

        // Listener para el botón Eliminar
        btnDelete.addActionListener(s -> {
            Students student = getStudentFromTableRow(); // Obtiene el estudiante seleccionado en la tabla
            if (student != null) { // Si hay un estudiante seleccionado
                StudentWriteForm studentWriteForm = new StudentWriteForm(this.mainForm, CUD.DELETE, student);
                studentWriteForm.setVisible(true);
                // Después de cerrar el formulario de escritura, limpia la tabla
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableStudents.setModel(emptyModel);
                }
            }
        });
    }

    // Nuevo método para aplicar todas las mejoras visuales
    private void applyVisualEnhancements() {
        // Estilos del Panel Principal
        mainPanel.setBackground(new Color(240, 245, 250)); // Fondo azul muy claro
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding general

        // Estilo del Campo de Texto de búsqueda
        Font textFieldFont = new Font("Arial", Font.PLAIN, 16);
        Color borderColor = new Color(150, 150, 200);
        int borderWidth = 1;

        txtName.setFont(textFieldFont);
        txtName.setBorder(new LineBorder(borderColor, borderWidth, true)); // Borde redondeado
        txtName.setPreferredSize(new Dimension(250, 38)); // Tamaño preferido
        txtName.setBackground(Color.WHITE); // Fondo blanco para el campo

        // Estilos de los Botones de Acción
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(100, 38); // Tamaño consistente para botones

        // Botón Crear
        btnCreate.setFont(buttonFont);
        btnCreate.setBackground(new Color(60, 179, 113)); // Verde para Crear
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnCreate.setPreferredSize(buttonSize);
        applyHoverEffect(btnCreate, new Color(80, 199, 133)); // Hover más claro

        // Botón Actualizar
        btnUpdate.setFont(buttonFont);
        btnUpdate.setBackground(new Color(70, 130, 180)); // Azul acero para Actualizar
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setPreferredSize(buttonSize);
        applyHoverEffect(btnUpdate, new Color(90, 150, 200)); // Hover más claro

        // Botón Eliminar
        btnDelete.setFont(buttonFont);
        btnDelete.setBackground(new Color(200, 70, 70)); // Rojo para Eliminar
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setPreferredSize(buttonSize);
        applyHoverEffect(btnDelete, new Color(220, 90, 90)); // Hover más claro

        // Estilo de la Tabla
        tableStudents.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableStudents.setRowHeight(25); // Altura de fila para mejor lectura
        tableStudents.setGridColor(new Color(200, 200, 200)); // Color de las líneas de la cuadrícula
        tableStudents.setSelectionBackground(new Color(173, 216, 230)); // LightBlue para selección
        tableStudents.setSelectionForeground(Color.BLACK);

        // Estilo del encabezado de la tabla
        tableStudents.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableStudents.getTableHeader().setBackground(new Color(50, 100, 150)); // Azul oscuro
        tableStudents.getTableHeader().setForeground(Color.WHITE);
        tableStudents.getTableHeader().setReorderingAllowed(false); // No permitir reordenar columnas
        tableStudents.getTableHeader().setResizingAllowed(true); // Permitir redimensionar columnas
        // Nota: Asegúrate de que tu JTable esté dentro de un JScrollPane en tu diseño .form
        // para que los encabezados y el scroll funcionen correctamente.
    }

    // Método auxiliar para aplicar efecto hover a los botones
    private void applyHoverEffect(JButton button, Color hoverColor) {
        Color originalColor = button.getBackground();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }

    // Método para realizar la búsqueda de estudiantes
    private void search(String query) {
        try {
            ArrayList<Students> students = studentDAO.searchByFullName(query);
            createTable(students); // Actualiza la tabla con los resultados
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, // Cambiado de 'null' a 'this'
                    "Error al buscar estudiantes: " + ex.getMessage(),
                    "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para crear y poblar la JTable
    public void createTable(ArrayList<Students> students) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que las celdas de la tabla no sean editables
            }
        };
        model.addColumn("Id");
        model.addColumn("Código");
        model.addColumn("Nombre Completo");
        model.addColumn("Edad");
        model.addColumn("Carrera"); // Columna para la carrera

        this.tableStudents.setModel(model);

        for (int i = 0; i < students.size(); i++) {
            Students student = students.get(i);
            model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getCode(),
                    student.getFullName(),
                    student.getAge(),
                    student.getCareer() != null ? student.getCareer().getCareerName() : "N/A"
            });
        }
        hideCol(0); // Oculta la columna de ID
    }

    // Método para ocultar una columna de la tabla
    private void hideCol(int pColumna) {
        this.tableStudents.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableStudents.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tableStudents.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableStudents.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }

    // Método para obtener un objeto Students de la fila seleccionada en la tabla
    private Students getStudentFromTableRow() {
        int filaSelect = this.tableStudents.getSelectedRow();
        if (filaSelect == -1) {
            JOptionPane.showMessageDialog(this, // Cambiado de 'null' a 'this'
                    "Por favor, seleccione una fila de la tabla.",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            int id = (int) this.tableStudents.getValueAt(filaSelect, 0); // Obtiene el ID de la fila seleccionada
            Students student = studentDAO.getById(id); // Busca el estudiante en la base de datos
            if (student == null || student.getStudentId() == 0) {
                JOptionPane.showMessageDialog(this, // Cambiado de 'null' a 'this'
                        "No se encontró ningún estudiante con el ID seleccionado.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return student;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, // Cambiado de 'null' a 'this'
                    "Error al obtener estudiante de la tabla: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}