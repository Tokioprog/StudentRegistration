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
        setTitle("Buscar Estudiante");
        pack();
        setLocationRelativeTo(mainForm);

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
            DefaultTableModel emptyModel = new DefaultTableModel();
            tableStudents.setModel(emptyModel);
        });

        // Listener para el botón Modificar
        btnUpdate.addActionListener(s -> {
            Students student = getStudentFromTableRow(); // Obtiene el estudiante seleccionado en la tabla
            if (student != null) { // Si hay un estudiante seleccionado
                StudentWriteForm studentWriteForm = new StudentWriteForm(this.mainForm, CUD.UPDATE, student);
                studentWriteForm.setVisible(true);
                // Después de cerrar el formulario de escritura, limpia la tabla
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableStudents.setModel(emptyModel);
            }
        });

        // Listener para el botón Eliminar
        btnDelete.addActionListener(s -> {
            Students student = getStudentFromTableRow(); // Obtiene el estudiante seleccionado en la tabla
            if (student != null) { // Si hay un estudiante seleccionado
                StudentWriteForm studentWriteForm = new StudentWriteForm(this.mainForm, CUD.DELETE, student);
                studentWriteForm.setVisible(true);
                // Después de cerrar el formulario de escritura, limpia la tabla
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableStudents.setModel(emptyModel);
            }
        });
    }

    // Método para realizar la búsqueda de estudiantes
    private void search(String query) {
        try {
            ArrayList<Students> students = studentDAO.searchByFullName(query);
            createTable(students); // Actualiza la tabla con los resultados
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al buscar estudiantes: " + ex.getMessage(), // Mensaje más descriptivo
                    "ERROR", JOptionPane.ERROR_MESSAGE);
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
            // Añade una nueva fila y luego establece los valores
            model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getCode(),
                    student.getFullName(),
                    student.getAge(),
                    student.getCareer() != null ? student.getCareer().getCareerName() : "N/A" // Muestra el nombre de la carrera
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
            JOptionPane.showMessageDialog(null,
                    "Seleccione una fila de la tabla.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            int id = (int) this.tableStudents.getValueAt(filaSelect, 0); // Obtiene el ID de la fila seleccionada
            Students student = studentDAO.getById(id); // Busca el estudiante en la base de datos
            if (student == null || student.getStudentId() == 0) {
                JOptionPane.showMessageDialog(null,
                        "No se encontró ningún estudiante con el ID seleccionado.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return student;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al obtener estudiante de la tabla: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}