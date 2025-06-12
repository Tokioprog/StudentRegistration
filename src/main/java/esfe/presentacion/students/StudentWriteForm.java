package esfe.presentacion.students;

import esfe.dominio.Career;
import esfe.dominio.Students;
import esfe.persistencia.CareerDAO;
import esfe.persistencia.StudentDAO;
import esfe.presentacion.users.MainForm;
import esfe.utils.CBOption;
import esfe.utils.CUD;

import javax.swing.*;
import java.util.ArrayList;
import java.awt.*; // Importación para Color, Font, Dimension, y GridBagLayout, GridBagConstraints
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Formulario Swing para crear, modificar o eliminar un estudiante.
 * Se comporta según el tipo de operación definido por el enum CUD.
 */
public class StudentWriteForm extends JDialog {
    // Componentes visuales (mantengo las declaraciones existentes)
    private JPanel mainPanel; // Este panel se configurará con GridBagLayout
    private JTextField txtCode;
    private JTextField txtFullName;
    private JTextField txtAge;
    private JComboBox<CBOption> cbCareer;
    private JButton btnOk;
    private JButton btnCancel;

    // Dependencias
    private CareerDAO careerDAO;
    private StudentDAO studentDAO;
    private MainForm mainForm;

    // Lógica de control
    private CUD cud; // Tipo de operación: CREATE, UPDATE, DELETE
    private Students currentStudent; // Estudiante actual (nuevo o a editar/eliminar)

    /**
     * Constructor del formulario.
     * @param mainForm Referencia al formulario principal
     * @param cud Tipo de operación a realizar (CREATE, UPDATE, DELETE)
     * @param student Objeto estudiante que será creado, editado o eliminado
     */
    public StudentWriteForm(MainForm mainForm, CUD cud, Students student) {
        this.cud = cud;
        this.currentStudent = student;
        this.mainForm = mainForm;

        // Inicialización de DAOs
        careerDAO = new CareerDAO();
        studentDAO = new StudentDAO();

        // Configuración del formulario
        // NO LLAMAMOS setContentPane(mainPanel) DIRECTAMENTE AQUÍ
        // mainPanel será construido y poblado en configurePanelLayout()
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 380); // Un poco más alto para el layout
        setResizable(false);
        setLocationRelativeTo(mainForm);

        // --- Configuración del Layout y Componentes ---
        configurePanelLayout(); // Nuevo método para configurar el mainPanel y sus componentes
        setContentPane(mainPanel); // Ahora establecemos el panel configurado

        init(); // Inicializa la interfaz según el tipo de operación (títulos, datos, etc.)

        // --- APLICACIÓN DE ESTILOS Y MEJORAS VISUALES ---
        applyVisualEnhancements();

        // Botón Cancelar
        btnCancel.addActionListener(s -> this.dispose());

        // Botón OK (Guardar o Eliminar)
        btnOk.addActionListener(s -> ok());
    }

    /**
     * Configura el mainPanel con GridBagLayout y añade todos los componentes a él.
     */
    private void configurePanelLayout() {
        mainPanel = new JPanel(new GridBagLayout()); // Inicializa mainPanel con GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding alrededor de cada componente
        gbc.fill = GridBagConstraints.HORIZONTAL; // Los componentes se expandirán horizontalmente

        // --- Añadir JLabels y JTextFields ---
        // Código
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        gbc.anchor = GridBagConstraints.WEST; // Alinear a la izquierda
        mainPanel.add(new JLabel("Código:"), gbc);

        gbc.gridx = 1; // Columna 1
        gbc.gridy = 0; // Fila 0
        gbc.weightx = 1.0; // Permitir que el campo se expanda
        txtCode = new JTextField(); // Asegúrate de inicializar los componentes aquí
        mainPanel.add(txtCode, gbc);
        gbc.weightx = 0; // Resetear weightx para las etiquetas

        // Nombre Completo
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Nombre Completo:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        txtFullName = new JTextField();
        mainPanel.add(txtFullName, gbc);
        gbc.weightx = 0;

        // Edad
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Edad:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        txtAge = new JTextField();
        mainPanel.add(txtAge, gbc);
        gbc.weightx = 0;

        // Carrera
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Carrera:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        cbCareer = new JComboBox<>(); // Asegúrate de inicializar aquí
        mainPanel.add(cbCareer, gbc);
        gbc.weightx = 0;

        // --- Añadir Botones ---
        gbc.gridx = 0;
        gbc.gridy = 4; // Nueva fila para los botones
        gbc.gridwidth = 2; // Ocupar ambas columnas
        gbc.anchor = GridBagConstraints.CENTER; // Centrar los botones
        gbc.fill = GridBagConstraints.NONE; // No expandir los botones
        gbc.insets = new Insets(20, 8, 8, 8); // Más espacio superior para los botones

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Panel para centrar los botones
        btnOk = new JButton(); // Inicializar botones
        btnCancel = new JButton();
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        mainPanel.add(buttonPanel, gbc);
    }


    // Nuevo método para aplicar todas las mejoras visuales
    private void applyVisualEnhancements() {
        // Estilos del Panel Principal (ya configurado en configurePanelLayout)
        mainPanel.setBackground(new Color(245, 245, 250)); // Fondo muy claro
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30)); // Más padding alrededor del contenido total

        // Estilos de los JLabels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = new Color(70, 70, 70);
        // Iterar sobre los componentes del mainPanel para encontrar los JLabels
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setFont(labelFont);
                label.setForeground(labelColor);
            }
        }


        // Estilos de los Campos de Texto y ComboBox
        Font inputFont = new Font("Arial", Font.PLAIN, 16);
        Color borderColor = new Color(150, 150, 200);
        int borderWidth = 1;
        Dimension inputSize = new Dimension(280, 38); // Tamaño preferido para campos

        // Estilos de txtCode
        txtCode.setFont(inputFont);
        txtCode.setBorder(new LineBorder(borderColor, borderWidth, true));
        txtCode.setPreferredSize(inputSize);
        txtCode.setBackground(Color.WHITE);

        // Estilos de txtFullName
        txtFullName.setFont(inputFont);
        txtFullName.setBorder(new LineBorder(borderColor, borderWidth, true));
        txtFullName.setPreferredSize(inputSize);
        txtFullName.setBackground(Color.WHITE);

        // Estilos de txtAge
        txtAge.setFont(inputFont);
        txtAge.setBorder(new LineBorder(borderColor, borderWidth, true));
        txtAge.setPreferredSize(inputSize);
        txtAge.setBackground(Color.WHITE);

        // Estilos de cbCareer
        cbCareer.setFont(inputFont);
        cbCareer.setBackground(Color.WHITE);
        cbCareer.setBorder(new LineBorder(borderColor, borderWidth, true));
        cbCareer.setPreferredSize(inputSize);

        // Estilos de los Botones (OK y Cancelar)
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 15);
        Dimension buttonSize = new Dimension(120, 40);

        btnOk.setFont(buttonFont);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setPreferredSize(buttonSize);
        btnOk.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Colores y hover específicos para btnOk (depende del CUD)
        if (this.cud == CUD.DELETE) {
            btnOk.setBackground(new Color(200, 70, 70)); // Rojo para eliminar
            btnOk.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) { btnOk.setBackground(new Color(220, 90, 90)); }
                public void mouseExited(java.awt.event.MouseEvent evt) { btnOk.setBackground(new Color(200, 70, 70)); }
            });
        } else {
            btnOk.setBackground(new Color(60, 179, 113)); // Verde para guardar/actualizar
            btnOk.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) { btnOk.setBackground(new Color(80, 199, 133)); }
                public void mouseExited(java.awt.event.MouseEvent evt) { btnOk.setBackground(new Color(60, 179, 113)); }
            });
        }

        btnCancel.setFont(buttonFont);
        btnCancel.setBackground(new Color(150, 150, 150)); // Gris para cancelar
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(buttonSize);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        // Efecto hover para el botón Cancelar
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(new Color(170, 170, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(new Color(150, 150, 150));
            }
        });
    }

    /**
     * Inicializa el formulario según el tipo de operación.
     */
    private void init() {
        loadCareersIntoComboBox(); // Carga las carreras en el combo box

        // Configura el título y comportamiento según la operación
        switch (this.cud) {
            case CREATE:
                setTitle("Crear Nuevo Estudiante"); // Título más descriptivo
                btnOk.setText("Guardar");
                if (cbCareer.getItemCount() > 0) cbCareer.setSelectedIndex(0);
                break;

            case UPDATE:
                setTitle("Modificar Estudiante");
                btnOk.setText("Actualizar"); // Texto más claro
                setValuesControls(this.currentStudent);
                break;

            case DELETE:
                setTitle("Eliminar Estudiante");
                btnOk.setText("Eliminar");
                setValuesControls(this.currentStudent);
                disableControls(); // Deshabilita los controles para evitar modificaciones
                break;
        }
    }

    /**
     * Carga las carreras desde la base de datos en el combo box.
     */
    private void loadCareersIntoComboBox() {
        try {
            ArrayList<Career> careers = careerDAO.searchByName("");
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();

            for (Career career : careers) {
                model.addElement(new CBOption(career)); // CBOption encapsula Career
            }
            cbCareer.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar carreras: " + ex.getMessage() + "\nLas carreras no se pudieron cargar.",
                    "Error de Carga de Datos", JOptionPane.ERROR_MESSAGE);
            btnOk.setEnabled(false);
            cbCareer.setEnabled(false);
        }
    }

    /**
     * Establece los valores en los controles del formulario desde el objeto Students.
     */
    private void setValuesControls(Students student) {
        txtCode.setText(student.getCode());
        txtFullName.setText(student.getFullName());
        txtAge.setText(String.valueOf(student.getAge()));

        // Selecciona la carrera correspondiente en el combo box
        if (student.getCareer() != null && student.getCareer().getCareerId() > 0) {
            // Recorre los items del ComboBox para encontrar la opción correcta
            for (int i = 0; i < cbCareer.getItemCount(); i++) {
                CBOption option = cbCareer.getItemAt(i);
                if (option.getValue() == student.getCareer().getCareerId()) {
                    cbCareer.setSelectedItem(option);
                    break;
                }
            }
        } else if (cbCareer.getItemCount() > 0) {
            cbCareer.setSelectedIndex(0);
        }
    }

    /**
     * Deshabilita los controles del formulario (útil para DELETE).
     */
    private void disableControls() {
        txtCode.setEditable(false);
        txtFullName.setEditable(false);
        txtAge.setEditable(false);
        cbCareer.setEnabled(false);
        // Dar un feedback visual de que el campo está deshabilitado
        txtCode.setBackground(new Color(230, 230, 230));
        txtFullName.setBackground(new Color(230, 230, 230));
        txtAge.setBackground(new Color(230, 230, 230));
        cbCareer.setBackground(new Color(230, 230, 230));
    }

    /**
     * Obtiene los valores desde los controles, los valida y los asigna a currentStudent.
     * @return true si los datos son válidos; false en caso contrario.
     */
    private boolean getValuesControls() {
        if (txtCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Código' es obligatorio.", "Validación de Campos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Nombre Completo' es obligatorio.", "Validación de Campos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        int age;
        try {
            age = Integer.parseInt(txtAge.getText().trim());
            if (age <= 0) {
                JOptionPane.showMessageDialog(this, "La edad debe ser un número positivo.", "Validación de Campos", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número válido.", "Validación de Campos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        CBOption selectedOption = (CBOption) cbCareer.getSelectedItem();
        if (selectedOption == null || selectedOption.getValue() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una Carrera.", "Validación de Campos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Asignación a currentStudent
        this.currentStudent.setCode(txtCode.getText().trim());
        this.currentStudent.setFullName(txtFullName.getText().trim());
        this.currentStudent.setAge(age);

        Career selectedCareer = new Career();
        selectedCareer.setCareerId(selectedOption.getValue());
        this.currentStudent.setCareer(selectedCareer);

        return true;
    }

    /**
     * Ejecuta la acción según el tipo de operación (CREATE, UPDATE, DELETE).
     */
    private void ok() {
        try {
            if (getValuesControls()) {
                boolean success = false;
                String message = "";

                switch (this.cud) {
                    case CREATE:
                        Students createdStudent = studentDAO.create(this.currentStudent);
                        if (createdStudent != null && createdStudent.getStudentId() > 0) {
                            success = true;
                            message = "Estudiante creado exitosamente.";
                        } else {
                            message = "No se pudo crear el estudiante. Verifique los datos.";
                        }
                        break;

                    case UPDATE:
                        success = studentDAO.update(this.currentStudent);
                        message = "Estudiante actualizado exitosamente.";
                        if (!success) {
                            message = "No se pudo actualizar el estudiante.";
                        }
                        break;

                    case DELETE:
                        success = studentDAO.delete(this.currentStudent);
                        message = "Estudiante eliminado exitosamente.";
                        if (!success) {
                            message = "No se pudo eliminar el estudiante.";
                        }
                        break;
                }

                if (success) {
                    JOptionPane.showMessageDialog(this, message, "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, message, "Error de Operación", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar la operación: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}