package esfe.presentacion.careers;

import esfe.dominio.Career;
import esfe.persistencia.CareerDAO;
import esfe.presentacion.users.MainForm;
import esfe.utils.CBOption;
import esfe.utils.CUD;

import javax.swing.*;

public class CareerWriteForm extends JDialog {
    private JTextField txtName;
    private JButton btnOk;
    private JButton btnCancel;
    private JPanel mainPanel;

    private CareerDAO careerDAO;     // DAO para interactuar con la base de datos
    private MainForm mainForm;       // Referencia al formulario principal
    private CUD cud;                 // Indica la operación: CREATE, UPDATE, DELETE
    private Career en;               // Objeto Career sobre el que se realizará la acción

    // Constructor
    public CareerWriteForm(MainForm mainForm, CUD cud, Career career) {
        this.cud = cud;
        this.en = career;
        this.mainForm = mainForm;
        this.careerDAO = new CareerDAO(); // Instancia el DAO para carreras

        // Configuración del formulario
        setContentPane(mainPanel);
        setModal(true);
        init();                      // Inicializa la vista y los datos
        pack();                      // Ajusta el tamaño
        setLocationRelativeTo(mainForm); // Centra respecto al formulario principal

        // Acción para botón Cancelar
        btnCancel.addActionListener(s -> this.dispose());

        // Acción para botón OK (Guardar o Eliminar)
        btnOk.addActionListener(s -> ok());
    }

    // Inicializa el formulario según la operación a realizar
    private void init() {
        switch (this.cud) {
            case CREATE:
                setTitle("Crear Carrera");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Carrera");
                btnOk.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Carrera");
                btnOk.setText("Eliminar");
                break;
        }

        // Llena el campo de texto con los datos actuales de la carrera
        setValuesControls(this.en);
    }

    // Asigna los valores del objeto Career a los campos visuales
    private void setValuesControls(Career career) {
        txtName.setText(career.getCareerName());

        // Si se va a eliminar, el campo no debe ser editable
        if (this.cud == CUD.DELETE) {
            txtName.setEditable(false);
        }
    }

    // Recupera los valores ingresados por el usuario y los valida
    private boolean getValuesControls() {
        boolean res = false;

        // Validación: el nombre no puede estar vacío
        if (txtName.getText().trim().isEmpty()) {
            return res;
        }
        // Validación adicional: en UPDATE y DELETE debe existir el ID de la carrera
        else if (this.cud != CUD.CREATE && this.en.getCareerId() == 0) {
            return res;
        }

        // Si pasa las validaciones, se asigna el valor al objeto Career
        res = true;
        this.en.setCareerName(txtName.getText());
        return res;
    }

    // Método que ejecuta la operación (crear, actualizar o eliminar)
    private void ok() {
        try {
            boolean res = getValuesControls(); // Validación y asignación de datos
            if (res) {
                boolean r = false;

                // Ejecuta la acción correspondiente en el DAO
                switch (this.cud) {
                    case CREATE:
                        Career career = careerDAO.create(this.en);
                        r = career.getCareerId() > 0;
                        break;
                    case UPDATE:
                        r = careerDAO.update(this.en);
                        break;
                    case DELETE:
                        r = careerDAO.delete(this.en);
                        break;
                }

                // Informa el resultado al usuario
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

