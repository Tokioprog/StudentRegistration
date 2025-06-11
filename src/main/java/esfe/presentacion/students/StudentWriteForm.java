package esfe.presentacion.students;

import esfe.dominio.Career;
import esfe.dominio.Students;
import esfe.persistencia.CareerDAO;
import esfe.persistencia.StudentDAO;
import esfe.presentacion.users.MainForm;
import esfe.utils.CBOption; // Asegúrate de tener tu clase CBOption correcta aquí
import esfe.utils.CUD;

import javax.swing.*;
import java.util.ArrayList;

public class StudentWriteForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtCode;
    private JTextField txtFullName;
    private JTextField txtAge;
    private JComboBox<CBOption> cbCareer; // Especifica el tipo genérico para el JComboBox
    private JButton btnOk;
    private JButton btnCancel;

    private CareerDAO careerDAO;
    private StudentDAO studentDAO;
    private MainForm mainForm;
    private CUD cud;
    private Students currentStudent; // Renombro 'en' a 'currentStudent' para mayor claridad

    public StudentWriteForm(MainForm mainForm, CUD cud, Students student) {
        this.cud = cud;
        this.currentStudent = student;
        this.mainForm = mainForm;
        careerDAO = new CareerDAO();
        studentDAO = new StudentDAO();
        setContentPane(mainPanel);
        setModal(true);
        // Es crucial llamar a init() aquí para cargar el ComboBox y configurar la UI antes de hacerla visible
        init();
        pack();
        setLocationRelativeTo(mainForm);

        // Listener para el botón Cancelar
        btnCancel.addActionListener(s -> this.dispose());
        // Listener para el botón OK/Guardar/Eliminar
        btnOk.addActionListener(s -> ok());
    }

    private void init() {
        // PASO 1: Cargar las carreras en el JComboBox. Esto debe hacerse antes de intentar seleccionar una.
        loadCareersIntoComboBox();

        // PASO 2: Configurar el formulario según la operación (CREATE, UPDATE, DELETE)
        switch (this.cud) {
            case CREATE:
                setTitle("Crear Estudiante");
                btnOk.setText("Guardar");
                // Si es CREATE, asegúrate de que el ComboBox tenga una opción seleccionada (ej. la primera)
                if (cbCareer.getItemCount() > 0) {
                    cbCareer.setSelectedIndex(0);
                }
                break;
            case UPDATE:
                setTitle("Modificar Estudiante");
                btnOk.setText("Guardar");
                // Carga los datos del estudiante existente en los controles para su edición
                setValuesControls(this.currentStudent);
                break;
            case DELETE:
                setTitle("Eliminar Estudiante");
                btnOk.setText("Eliminar");
                // Carga los datos para que el usuario los vea antes de eliminar
                setValuesControls(this.currentStudent);
                // Deshabilita los controles para evitar modificaciones antes de eliminar
                disableControls();
                break;
        }
    }

    // Método para cargar las carreras desde la base de datos en el JComboBox
    private void loadCareersIntoComboBox() {
        try {
            // Obtiene todas las carreras de la base de datos
            ArrayList<Career> careers = careerDAO.searchByName("");
            // Crea un nuevo modelo para el JComboBox
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();

            // Opcional: Añadir una opción por defecto al inicio, si no quieres que se seleccione automáticamente la primera
            // model.addElement(new CBOption("-- Seleccione una Carrera --", 0)); // Asumiendo que 0 no es un careerId válido

            // Itera sobre las carreras obtenidas y las añade al modelo del JComboBox como objetos CBOption
            for (Career career : careers) {
                model.addElement(new CBOption(career)); // Usa el constructor de CBOption que recibe un objeto Career
            }
            cbCareer.setModel(model); // Asigna el modelo al JComboBox
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar carreras: " + ex.getMessage() + "\n" +
                            "Asegúrese de que la base de datos esté accesible y tenga carreras.", // Mensaje más útil
                    "ERROR DE CARGA", JOptionPane.ERROR_MESSAGE);
            // Deshabilita el botón OK y el ComboBox si no se pueden cargar las carreras
            btnOk.setEnabled(false);
            cbCareer.setEnabled(false);
        }
    }

    // Método para establecer los valores de los controles basándose en el objeto Students
    private void setValuesControls(Students student) {
        txtCode.setText(student.getCode());
        txtFullName.setText(student.getFullName());
        txtAge.setText(String.valueOf(student.getAge()));

        // Seleccionar la carrera correcta en el JComboBox
        if (student.getCareer() != null && student.getCareer().getCareerId() > 0) {
            // Crea un objeto CBOption temporal para la carrera del estudiante.
            // Gracias al método equals() correctamente implementado en CBOption,
            // setSelectedItem() encontrará y seleccionará la opción correspondiente en el ComboBox.
            cbCareer.setSelectedItem(new CBOption(student.getCareer()));
        } else {
            // Si el estudiante no tiene carrera o es un ID inválido, seleccionar el primer elemento
            if (cbCareer.getItemCount() > 0) {
                cbCareer.setSelectedIndex(0);
            }
        }
    }

    // Método para deshabilitar los controles del formulario (útil para la operación DELETE)
    private void disableControls() {
        txtCode.setEditable(false);
        txtFullName.setEditable(false);
        txtAge.setEditable(false);
        cbCareer.setEnabled(false);
    }

    // Método para obtener los valores de los controles del formulario y realizar validaciones
    private boolean getValuesControls() {
        // Validación de campos de texto no vacíos
        if (txtCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo 'Código' es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo 'Nombre Completo' es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación y parseo de la edad
        int age;
        try {
            age = Integer.parseInt(txtAge.getText().trim());
            if (age <= 0) {
                JOptionPane.showMessageDialog(null, "La edad debe ser un número positivo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La edad debe ser un número válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación de la selección de carrera en el JComboBox
        CBOption selectedOption = (CBOption) cbCareer.getSelectedItem();
        // Asumiendo que un valor de 0 en CBOption.getValue() indica que no hay una selección válida
        if (selectedOption == null || selectedOption.getValue() == 0) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una Carrera.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Si todas las validaciones pasan, se procede a poblar el objeto currentStudent
        this.currentStudent.setCode(txtCode.getText().trim());
        this.currentStudent.setFullName(txtFullName.getText().trim());
        this.currentStudent.setAge(age);

        // Se crea una nueva instancia de Career con el ID seleccionado y se asigna al estudiante
        Career selectedCareer = new Career();
        selectedCareer.setCareerId(selectedOption.getValue());
        // Opcional: Si necesitas el nombre de la carrera para alguna lógica adicional, puedes asignarlo
        // selectedCareer.setCareerName(selectedOption.getDisplayText());
        this.currentStudent.setCareer(selectedCareer);

        return true; // Retorna true si todas las validaciones y el llenado del objeto fueron exitosos
    }

    // Método que se ejecuta al presionar el botón "OK" (Guardar/Eliminar)
    private void ok() {
        try {
            // Primero, valida los datos del formulario y actualiza el objeto currentStudent
            if (getValuesControls()) {
                boolean success = false;
                String message = "";

                // Ejecuta la operación CUD correspondiente
                switch (this.cud) {
                    case CREATE:
                        Students createdStudent = studentDAO.create(this.currentStudent);
                        if (createdStudent != null && createdStudent.getStudentId() > 0) {
                            success = true;
                            message = "Estudiante creado exitosamente.";
                        }
                        break;
                    case UPDATE:
                        success = studentDAO.update(this.currentStudent);
                        message = "Estudiante actualizado exitosamente.";
                        break;
                    case DELETE:
                        success = studentDAO.delete(this.currentStudent);
                        message = "Estudiante eliminado exitosamente.";
                        break;
                }

                // Muestra el resultado de la operación
                if (success) {
                    JOptionPane.showMessageDialog(null, message, "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose(); // Cierra el formulario después de una operación exitosa
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción o la operación falló.",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
            // Si getValuesControls() retorna false, ya habrá mostrado un JOptionPane con el mensaje de error.
        } catch (Exception ex) {
            // Captura y muestra cualquier excepción que ocurra durante el proceso
            JOptionPane.showMessageDialog(null,
                    "Error al procesar la operación: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime la pila de llamadas para facilitar la depuración
        }
    }
}